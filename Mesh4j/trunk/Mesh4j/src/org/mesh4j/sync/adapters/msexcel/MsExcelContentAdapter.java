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
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelContentAdapter implements IIdentifiableContentAdapter, ISyncAware {

	// MODEL VARIABLES
	private IMsExcel excel;
	private IMsExcelToXMLMapping mapping;
	
	private String sheetName;
	
	private int lastUpdateIndex = -1;
	private int entityIdIndex = -1;
	private int numberOfPhantomRows = 0;
		
	// BUSINESS METHODS
	public MsExcelContentAdapter(IMsExcel excel, IMsExcelToXMLMapping mapping, String sheetName){
		super();
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNull(mapping, "mapping");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		this.sheetName = sheetName;
		this.excel = excel;
		this.mapping = mapping;
		
		this.initialize();
	}


	private void initialize() {
		try{
			HSSFWorkbook workbook = excel.getWorkbook();			
			HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, this.sheetName);			
			HSSFRow row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);						
			HSSFCell cell = MsExcelUtils.getOrCreateCellStringIfAbsent(row, this.mapping.getIdColumnName());
			this.entityIdIndex = cell.getColumnIndex();
			
			if(this.mapping.getLastUpdateColumnName() != null){
				cell = MsExcelUtils.getOrCreateCellStringIfAbsent(row, this.mapping.getLastUpdateColumnName());
				this.lastUpdateIndex = cell.getColumnIndex();
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	protected Element translate(HSSFRow row) {
		return this.mapping.convertRowToXML(getWorkbook(), getSheet(), row);
	}
	
	protected void updateRow(HSSFRow row, EntityContent entityContent) {
		this.mapping.appliesXMLToRow(getWorkbook(), getSheet(), row, entityContent.getPayload());
	}

	private void addRow(EntityContent entityContent) {
		int index = getSheet().getPhysicalNumberOfRows();
		if(numberOfPhantomRows > 0){
			index = index - this.numberOfPhantomRows;
			this.numberOfPhantomRows--;
		} 
		
		HSSFRow row = getSheet().createRow(index);
		this.updateRow(row, entityContent);		
	}
	
	public HSSFWorkbook getWorkbook() {
		return this.excel.getWorkbook();
	}
	
	public HSSFSheet getSheet() {
		return getWorkbook().getSheet(this.sheetName);
	}
	
	public int getNumberOfPhantomRows() {
		return numberOfPhantomRows;
	}
	
	// IContentAdapter methods

	@Override
	public void save(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.mapping.getIdColumnName());
		HSSFRow row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityContent.getId());
		if(row == null){
			this.addRow(entityContent);
		} else {
			this.updateRow(row, entityContent);
		}
	}
	
	@Override
	public void delete(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.mapping.getIdColumnName());
		HSSFRow row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityContent.getId());
		if(row != null){
			this.numberOfPhantomRows++;
			getSheet().removeRow(row);
			if(row.getRowNum() < getSheet().getLastRowNum()){
				getSheet().shiftRows(row.getRowNum()+1, getSheet().getLastRowNum(), -1);
			}
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
			
			if(row != null && this.hasChanged(row, since)){
				cell = MsExcelUtils.getCell(getSheet(), row, this.mapping.getIdColumnName());
				if(cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK){
					payload = this.translate(row);		
					entityContent = new EntityContent(payload, this.sheetName, cell.getRichStringCellValue().getString());
					result.add(entityContent);
				}
			}
		}
		return result;
	}

	private boolean hasChanged(HSSFRow row, Date since) {
		if(this.lastUpdateIndex == -1){
			return true;
		} else {
			HSSFCell cell = row.getCell(this.lastUpdateIndex);
			if(cell == null){
				return true;
			} else {
				Date lastUpdate = (Date)MsExcelUtils.getCellValue(cell);
				return since.compareTo(lastUpdate) <= 0;
			}
		}
	}

	@Override
	public String getType() {
		return this.sheetName;
	}

	// ISyncAware methods

	@Override
	public void beginSync() {
		this.numberOfPhantomRows = 0;
		for (int i = getSheet().getFirstRowNum()+1; i <= getSheet().getLastRowNum(); i++) {
			HSSFRow row = getSheet().getRow(i);
			
			if(MsExcelUtils.isPhantomRow(row)){
				this.numberOfPhantomRows++;
				if(i < getSheet().getLastRowNum()){
					getSheet().shiftRows(i+1, getSheet().getLastRowNum(), -1);
				}
			}
		}
		
		this.excel.setDirty();
	}

	@Override
	public void endSync() {
		this.excel.flush();		
	}
	
	public String getSheetName(){
		return this.sheetName;
	}


	@Override
	public String getID(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.mapping.getIdColumnName());
		if(entityContent == null){
			return null;
		} else {
			return entityContent.getId();
		}
	}
}
