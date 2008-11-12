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
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelContentAdapter implements IContentAdapter, ISyncAware {

	// MODEL VARIABLES
	private MsExcel excel;
	private String sheetName;
	private String entityIdColumnName;
	private String lastUpdateColumnName;
	
	private int lastUpdateIndex;
	private int entityIdIndex;
	
		
	// BUSINESS METHODS
	public MsExcelContentAdapter(MsExcel excel, String sheetName, String entityIdColumnName){
		this(excel, sheetName, entityIdColumnName, null);
	}
	
	public MsExcelContentAdapter(MsExcel excel, String sheetName, String entityIdColumnName, String lastUpdateColumnName){
		super();
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(entityIdColumnName, "entityIdColumnName");
		if(lastUpdateColumnName != null){
			Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		}		
		
		this.sheetName = sheetName;
		this.entityIdColumnName = entityIdColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
		this.excel = excel;
		
		this.initialize();
	}


	private void initialize() {
		try{
			HSSFWorkbook workbook = excel.getWorkbook();			
			HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, this.sheetName);			
			HSSFRow row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);						
			HSSFCell cell = MsExcelUtils.getOrCreateCellStringIfAbsent(row, this.entityIdColumnName);
			this.entityIdIndex = cell.getColumnIndex();
			
			if(this.lastUpdateColumnName != null){
				cell = MsExcelUtils.getOrCreateCellStringIfAbsent(row, this.lastUpdateColumnName);
				this.lastUpdateIndex = cell.getColumnIndex();
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private Element translate(HSSFRow row) {
		return MsExcelUtils.translate(getSheet(), row, this.sheetName);
	}

	private void addRow(EntityContent entityContent) {
		HSSFRow row = getSheet().createRow(getSheet().getPhysicalNumberOfRows());
		MsExcelUtils.updateRow(getSheet(), row, entityContent.getPayload());		
	}
	
	public HSSFWorkbook getWorkbook() {
		return this.excel.getWorkbook();
	}
	
	public HSSFSheet getSheet() {
		return getWorkbook().getSheet(this.sheetName);
	}
	
	// IContentAdapter methods

	@Override
	public void save(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.entityIdColumnName);
		HSSFRow row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityContent.getId());
		if(row == null){
			this.addRow(entityContent);
		} else {
			MsExcelUtils.updateRow(getSheet(), row, entityContent.getPayload());
		}
	}
	
	@Override
	public void delete(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.entityIdColumnName);
		HSSFRow row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityContent.getId());
		if(row != null){
			getSheet().removeRow(row);
		}		
	}

	@Override
	public IContent get(String entityId) {
		HSSFRow row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityId);
		if(row == null){
			return null;
		} else {
			Element payload = this.translate(row);
			return new EntityContent(payload, this.sheetName, entityId);
		}
	}

	@Override
	public List<IContent> getAll(Date since) {
		ArrayList<IContent> result = new ArrayList<IContent>();
		
		HSSFRow row;
		HSSFCell cell;
		Element payload;
		IContent entityContent;
		for (int i = getSheet().getFirstRowNum()+1; i <= getSheet().getLastRowNum(); i++) {
			row = getSheet().getRow(i);
			
			if(this.hasChanged(row, since)){
				payload = this.translate(row);
				
				cell = MsExcelUtils.getCell(getSheet(), row, this.entityIdColumnName);
				entityContent = new EntityContent(payload, this.sheetName, cell.getRichStringCellValue().getString());
				result.add(entityContent);
			}
		}
		return result;
	}

	private boolean hasChanged(HSSFRow row, Date since) {
		if(lastUpdateColumnName == null){
			return true;
		} else {
			HSSFCell cell = row.getCell(this.lastUpdateIndex);
			if(cell == null){
				return true;
			} else {
				Date lastUpdate = DateHelper.parseW3CDateTime(cell.getRichStringCellValue().getString());
				return since.compareTo(lastUpdate) <= 0;
			}
		}
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
		this.excel.setDirty();
	}

	@Override
	public void endSync() {
		this.excel.flush();		
	}
}
