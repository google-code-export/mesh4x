package org.mesh4j.sync.adapters.msexcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelContentAdapter implements IIdentifiableContentAdapter, ISyncAware {

	// MODEL VARIABLES
	private IMsExcel excel;
	private IMsExcelToXMLMapping mapping;
	private int numberOfPhantomRows = 0;
		
	// BUSINESS METHODS
	public MsExcelContentAdapter(IMsExcel excel, IMsExcelToXMLMapping mapping){
		super();
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNull(mapping, "mapping");
		
		this.excel = excel;
		this.mapping = mapping;
		
		this.initialize();
	}


	private void initialize() {
		try{
			Workbook workbook = excel.getWorkbook();			
			Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, this.getSheetName());			
			Row row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);						
			this.mapping.initializeHeaderRow(workbook, sheet, row);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	protected Element translate(Row row) {
		return this.mapping.convertRowToXML(getWorkbook(), getSheet(), row);
	}
	
	protected void updateRow(Row row, IdentifiableContent entityContent) {
		this.mapping.appliesXMLToRow(getWorkbook(), getSheet(), row, entityContent.getPayload());
	}

	private void addRow(IdentifiableContent entityContent) {
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
		return getWorkbook().getSheet(this.getSheetName());
	}
	
	public int getNumberOfPhantomRows() {
		return numberOfPhantomRows;
	}
	
	// IContentAdapter methods

	@Override
	public void save(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		Row row =this.mapping.getRow(getSheet(), entityContent.getId());
		if(row == null){
			this.addRow(entityContent);
		} else {
			this.updateRow(row, entityContent);
		}
	}
	
	@Override
	public void delete(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		Row row = this.mapping.getRow(getSheet(), entityContent.getId());
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
		Row row = this.mapping.getRow(getSheet(), entityId);
		if(row == null){
			return null;
		} else {
			Element payload = this.translate(row);
			return new IdentifiableContent(payload, this.mapping, entityId);
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
				String entityID = this.mapping.getId(getSheet(), row);
				if(entityID != null){
					payload = this.translate(row);
					entityContent = new IdentifiableContent(payload, this.mapping, entityID);
					result.add(entityContent);
				}
			}
		}
		return result;
	}

	private boolean hasChanged(Row row, Date since) {
		Date lastUpdate = this.mapping.getLastUpdate(getSheet(), row);
		if(lastUpdate == null){
			return true;
		} else {
			return since.compareTo(lastUpdate) <= 0;
		}
	}

	@Override
	public String getType() {
		return this.mapping.getType();
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
		return this.getType();
	}

	@Override
	public String getID(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
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
}
