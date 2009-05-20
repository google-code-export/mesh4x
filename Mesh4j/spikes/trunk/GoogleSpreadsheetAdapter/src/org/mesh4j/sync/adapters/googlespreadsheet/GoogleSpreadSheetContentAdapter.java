package org.mesh4j.sync.adapters.googlespreadsheet;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.ISupportReadSchema;
import org.mesh4j.sync.ISupportWriteSchema;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;
/**
 * Basically implementation of CRUD operation in google spreadsheet through Mesh4x wrapper
 * API of GData API.
 * Content repository which actually responsible for applying CRUD operation
 * in google spread sheet.
 * @author Raju
 * @version 1.0,29/4/2009
 */
public class GoogleSpreadSheetContentAdapter implements IContentAdapter,ISyncAware, ISupportReadSchema, ISupportWriteSchema{

	public final static String G_SPREADSHEET_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss";
	private String entityName = "";
	//this property is for identify each row in spreadsheet
	//but as because google API provides api to identify each row
	//we will not use this extra column id in spreadsheet like MsExcel Adapter
	private String idColumnName = "id";

	//Which actually represents the lastupdatecolumnName position of SpreadSheetToXMLMapper
	private int lastUpdateColumnIndex = -1;
	//represents the id column name index or position in repository
	private int entityIdIndex = -1;
	
	//represents the google spreadsheet
	private IGoogleSpreadSheet spreadSheet = null;
	
	//represents a specific sheet of a google spreadsheet
	private GSWorksheet<GSRow<GSCell>> workSheet;

	private IGoogleSpreadsheetToXMLMapping mapper;
	
	/**
	 * 
	 * @param spreadSheet the google spreadsheet
	 * @param sheetName the particular sheet name of a spreadsheet 
	 */
	//TODO(raju)  no need to pass the workSheet instance, rather pass the worksheet name
	// just pick the particular worksheet from IGoogleSpreadSheet
	public GoogleSpreadSheetContentAdapter(IGoogleSpreadSheet spreadSheet, IGoogleSpreadsheetToXMLMapping mapper){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNull(spreadSheet.getGSSpreadsheet(), "spreadSheet.gssSpreadsheet");
		Guard.argumentNotNull(mapper, "mapper");
		
		this.spreadSheet = spreadSheet;
		this.mapper = mapper;
		
		//instead of providing sheet name as the entity name we are using
		//type as the entity name.because with google spreadsheet it is possible
		//to create two worksheet(programatically) with the same name.
		this.entityName = mapper.getType();
		this.idColumnName = mapper.getIdColumnName();
		this.workSheet = GoogleSpreadsheetUtils.getOrCreateContentSheetIfAbsent(
						spreadSheet.getGSSpreadsheet(),	mapper);
			//this.spreadSheet.getGSWorksheet(mapper.getSheetName());
		
		init();
	}
	
	
	 /** 
	 * Find out the id column position(entityIdIndex) of the entity from the 
	 * worksheet. 
	 * **/
	 
	private void init(){
		
		for(Map.Entry<String, GSRow<GSCell>> rowMap:workSheet.getGSRows().entrySet()){
			GSCell cell = GoogleSpreadsheetUtils.getCell(rowMap.getValue(), mapper.getIdColumnName());
			entityIdIndex = cell.getColIndex();
			
			if(this.mapper.getLastUpdateColumnName() != null && !this.mapper.getLastUpdateColumnName().equals("")){
				cell = GoogleSpreadsheetUtils.getCell(rowMap.getValue(), mapper.getLastUpdateColumnName());
				lastUpdateColumnIndex = cell.getColIndex();		
			}
			break;
		}
	}
	
	@Override
	public void delete(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		EntityContent entityContent = EntityContent.normalizeContent(content, this.entityName, idColumnName);
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, entityIdIndex, entityContent.getId());
		if(row != null){
			this.workSheet.deleteChildElement(row.getElementId());
		}
	}

	@Override
	public IContent get(String contentId) {
		Guard.argumentNotNullOrEmptyString(contentId, "contentId");
		//here contentId is entityid 
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, entityIdIndex, contentId);
		if(row != null){
			Element payLoad = mapper.convertRowToXML(row);
			return new EntityContent(payLoad,this.entityName,contentId);
		}
		return null;
	}
	
	@Override
	public List<IContent> getAll(Date since) {
//		Guard.argumentNotNull(since, "since");
		
		List<IContent> listOfAll = new LinkedList<IContent>();
		//we will not count first row,since this is header row
		//and here counting is starts from 1 not zero.so header row index will be 1
		//System.out.println(this.workSheet.getGSRows().size());
		for(Map.Entry<String,GSRow<GSCell>> rowMap :this.workSheet.getGSRows().entrySet()){
			GSRow<GSCell> gsRow = rowMap.getValue();
			String entityId  = "";
			if(gsRow.getElementListIndex() > 1 ){
				if(gsRow != null && rowHasChanged(gsRow, since)){
					Element payLoad = mapper.convertRowToXML(gsRow);
					 GSCell cell = gsRow.getGSCell(entityIdIndex);
					 if(cell != null){
						 entityId = cell.getCellValue();
					 }
					 //TODO handle the else condition.
	   		    entityId = cell.getCellValue();
				EntityContent entityContent = new EntityContent(payLoad,this.entityName,entityId);
				listOfAll.add(entityContent);
			}
		}
	}
		return listOfAll;
	}
	
	
	
	private boolean rowHasChanged(GSRow<GSCell> row,Date since){
		if(lastUpdateColumnIndex == -1){
			return true;
		} else {
			GSCell cell = row.getGSCell(lastUpdateColumnIndex);
			if(cell == null){
				return true;
			} else {
				String dateTimeAsString = cell.getCellValue();
				Date lasUpdateDateTime = GoogleSpreadsheetUtils.normalizeDate(dateTimeAsString, G_SPREADSHEET_DATE_FORMAT);
				return since.compareTo(lasUpdateDateTime) <= 0;
			}
		}
	}
	
	@Override
	public String getType() {
		return this.entityName;
	}

	@Override
	public void save(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		EntityContent entityContent = EntityContent.normalizeContent(content, this.entityName, idColumnName);
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, entityIdIndex, entityContent.getId());
		if(row == null){
			row = workSheet.createNewRow(workSheet.getChildElements().size() +1);
		}
		this.mapper.applyXMLElementToRow(workSheet, row, entityContent.getPayload());
	}
	
	private void addRow(EntityContent entityContent){
//		GSRow<GSCell> row = this.mapper.convertXMLElementToRow(this.workSheet,entityContent.getPayload());
//		this.workSheet.addChildElement(row.getElementId(), row);
	}
	
	@SuppressWarnings("unused")
	private void printTest(){
		for(Map.Entry<String,GSRow<GSCell>> rowMap :this.workSheet.getGSRows().entrySet()){
			GSRow<GSCell> gsRow = rowMap.getValue();
			String entityId  = "";
			if(gsRow.getRowIndex() > 1 ){
				GSCell cell = gsRow.getGSCell(entityIdIndex);
				if(cell != null){
					String value = cell.getCellValue();
					System.out.println("cell value " + value);
				}else{
					System.out.println("cell is null");
				}
			}
		}
	}
	private void updateRow(EntityContent entityContent,GSRow<GSCell> rowTobeUpdated){
//		GSRow row = this.mapper.normalizeRow(workSheet, entityContent.getPayload(), rowTobeUpdated);
//		this.workSheet.updateChildElement(row.getElementId(), row);
	}
	
	@Override
	public void beginSync() {
		this.spreadSheet.setDirty();
	}
	
	@Override
	public void endSync() {
		this.spreadSheet.flush();
	}
	
	@Override
	public ISchema getSchema() {
		return mapper.getSchema();
	}
	
	@Override
	public void writeDataSourceFromSchema() {
		// TODO (sharif/raju)
//		try {
//			this.mapper.createDataSource(GoogleSpreadSheetRDFSyncAdapterFactory.DEFAULT_NEW_SPREADSHEET_FILENAME);
//		} catch (Exception e) {
//			throw new MeshException(e);
//		}		
	}
}
