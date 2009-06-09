package org.mesh4j.sync.adapters.msexcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.ISupportReadSchema;
import org.mesh4j.sync.ISupportWriteSchema;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelContentAdapter implements IIdentifiableContentAdapter, ISyncAware, ISupportReadSchema, ISupportWriteSchema {

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
			Workbook workbook = excel.getWorkbook();			
			Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, this.sheetName);			
			Row row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);						
			Cell cell = MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, this.mapping.getIdColumnName());
			this.entityIdIndex = cell.getColumnIndex();
			
			if(this.mapping.getLastUpdateColumnName() != null){
				cell = MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, this.mapping.getLastUpdateColumnName());
				this.lastUpdateIndex = cell.getColumnIndex();
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	protected Element translate(Row row) {
		return this.mapping.convertRowToXML(getWorkbook(), getSheet(), row);
	}
	
	protected void updateRow(Row row, EntityContent entityContent) {
		this.mapping.appliesXMLToRow(getWorkbook(), getSheet(), row, entityContent.getPayload());
	}

	private void addRow(EntityContent entityContent) {
		int index = getSheet().getPhysicalNumberOfRows();
		if(numberOfPhantomRows > 0){
			index = index - this.numberOfPhantomRows;
			this.numberOfPhantomRows--;
		} 
		
		Row row = getSheet().createRow(index);
		this.updateRow(row, entityContent);		
	}
	
	public Workbook getWorkbook() {
		return this.excel.getWorkbook();
	}
	
	public Sheet getSheet() {
		return getWorkbook().getSheet(this.sheetName);
	}
	
	public int getNumberOfPhantomRows() {
		return numberOfPhantomRows;
	}
	
	// IContentAdapter methods

	@Override
	public void save(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.mapping.getIdColumnName());
		Row row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityContent.getId());
		if(row == null){
			this.addRow(entityContent);
		} else {
			this.updateRow(row, entityContent);
		}
	}
	
	@Override
	public void delete(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, this.mapping.getIdColumnName());
		Row row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityContent.getId());
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
		Row row = MsExcelUtils.getRow(getSheet(), this.entityIdIndex, entityId);
		if(row == null){
			return null;
		} else {
			Element payload = this.translate(row);
			return new EntityContent(payload, this.sheetName, this.mapping.getIdColumnName(), entityId);
		}
	}

	@Override
	public List<IContent> getAll(Date since) {
		ArrayList<IContent> result = new ArrayList<IContent>();
		
		Row row;
		Element payload;
		IContent entityContent;
		for (int i = getSheet().getFirstRowNum()+1; i <= getSheet().getLastRowNum(); i++) {
			row = getSheet().getRow(i);
			
			if(row != null && this.hasChanged(row, since)){
				String entityID = this.mapping.getIdColumnValue(getSheet(), row);
				if(entityID != null){
					payload = this.translate(row);
					entityContent = new EntityContent(payload, this.sheetName, this.mapping.getIdColumnName(), entityID);
					result.add(entityContent);
				}
			}
		}
		return result;
	}

	private boolean hasChanged(Row row, Date since) {
		if(this.lastUpdateIndex == -1){
			return true;
		} else {
			Date lastUpdate = this.mapping.getLastUpdateColumnValue(getSheet(), row);
			if(lastUpdate == null){
				return true;
			} else {
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
			Row row = getSheet().getRow(i);
			
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

	@Override
	public ISchema getSchema() {
		return mapping.getSchema();
	}

	public IMsExcelToXMLMapping getMapping(){
		return this.mapping;
	}

	@Override
	public void writeDataSourceFromSchema() {
		this.mapping.createDataSource(this.excel);
	}
}
