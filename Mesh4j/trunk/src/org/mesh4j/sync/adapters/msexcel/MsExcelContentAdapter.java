package org.mesh4j.sync.adapters.msexcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelContentAdapter implements IContentAdapter, ISyncAware {

	// MODEL VARIABLES
	private String fileName;
	private String sheetName;
	private String entityIdColumnName;
	
	private HSSFWorkbook workbook;
	private HSSFSheet worksheet;
	private int entityIdIndex;
	
	
	// BUSINESS METHODS
	public MsExcelContentAdapter(String sheetName, String entityIdColumnName, String fileName){
		super();
		
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNullOrEmptyString(entityIdColumnName, "entityIdColumnName");
		
		this.sheetName = sheetName;
		this.fileName = fileName;
		this.entityIdColumnName = entityIdColumnName;	
	}

	private void initialize() {
		try{
			this.workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(this.fileName);			
			this.worksheet = MsExcelUtils.getOrCreateSheetIfAbsent(this.workbook, this.sheetName);			
			HSSFRow row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(this.worksheet);						
			HSSFCell cell = MsExcelUtils.getOrCreateCellStringIfAbsent(row, this.entityIdColumnName);
			this.entityIdIndex = cell.getColumnIndex();
			
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private Element translate(HSSFRow row) {
		return MsExcelUtils.translate(this.worksheet, row, this.sheetName);
	}

	private void addRow(EntityContent entityContent) {
		HSSFRow row = this.worksheet.createRow(this.worksheet.getLastRowNum() +1);
		MsExcelUtils.updateRow(this.worksheet, row, entityContent.getPayload());		
	}
	
	// IContentAdapter methods

	@Override
	public void save(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.entityIdColumnName);
		HSSFRow row = MsExcelUtils.getRow(this.worksheet, this.entityIdIndex, entityContent.getId());
		if(row == null){
			this.addRow(entityContent);
		} else {
			MsExcelUtils.updateRow(this.worksheet, row, entityContent.getPayload());
		}
	}
	
	@Override
	public void delete(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.entityIdColumnName);
		HSSFRow row = MsExcelUtils.getRow(this.worksheet, this.entityIdIndex, entityContent.getId());
		if(row != null){
			this.worksheet.removeRow(row);
		}		
	}

	@Override
	public IContent get(String entityId) {
		HSSFRow row = MsExcelUtils.getRow(this.worksheet, this.entityIdIndex, entityId);
		if(row == null){
			return null;
		} else {
			Element payload = this.translate(row);
			return new EntityContent(payload, this.sheetName, entityId);
		}
	}

	@Override
	public List<IContent> getAll(Date since) { 	// TODO (JMT) Add filter by since date
		ArrayList<IContent> result = new ArrayList<IContent>();
		
		HSSFRow row;
		Element payload;
		IContent entityContent;
		for (int i = this.worksheet.getFirstRowNum()+1; i <= this.worksheet.getLastRowNum(); i++) {
			row = this.worksheet.getRow(i);
			payload = this.translate(row);
			entityContent = new EntityContent(payload, this.sheetName, this.entityIdColumnName);
			result.add(entityContent);
		}
		return result;
	}

	@Override
	public String getType() {
		return this.sheetName;
	}

	@Override
	public IContent normalize(IContent content) {
		return EntityContent.normalizeContent(content, this.sheetName, this.entityIdColumnName);
	}

	// ISyncAware methods

	@Override
	public void beginSync() {
		this.initialize();
	}

	@Override
	public void endSync() {
		MsExcelUtils.flush(this.workbook, this.fileName);		
	}
}
