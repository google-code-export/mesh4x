package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;
/**
 * Basically implementation of CRUD operation in google spreadsheet through Mesh4x wrapper
 * API of GData API.
 * Content repository which actually responsible for applying CRUD operation
 * in google spread sheet.
 * @author Raju
 * @version 1.0,29/4/2009
 */
public class GoogleSpreadSheetContentAdapter implements IContentAdapter,ISyncAware{

	public final static String G_SPREADSHEET_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss";
	private String entityName = "";
	//this property is for identify each row in spreadsheet
	//but as because google API provides api to identify each row
	//we will not use this extra column id in spreadsheet like MsExcel Adapter
	private String idColumnName = "id";

	//Which actually represents the lastupdatecolumnName position of SpreadSheetToXMLMapper
	private int lastUpdateColumnIndex = -1;
	
	//represents the google spreadsheet
	private IGoogleSpreadSheet spreadSheet = null;
	
	//represents a specific sheet of a google spreadsheet
	private GSWorksheet<GSRow<GSCell>> workSheet;

	private ISpreadSheetToXMLMapper mapper;
	
	/**
	 * 
	 * @param spreadSheet the google spreadsheet
	 * @param sheetName the particular sheet name of a spreadsheet 
	 */
	public GoogleSpreadSheetContentAdapter(IGoogleSpreadSheet spreadSheet,GSWorksheet<GSRow<GSCell>> workSheet,
											ISpreadSheetToXMLMapper mapper){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNull(workSheet, "workSheet");
		
		this.spreadSheet = spreadSheet;
		this.workSheet = workSheet;
		this.mapper = mapper;
		
		//instead of providing sheet name as the entity name we using
		//type as the entity name.because with google spreadsheet it is possible
		//to create two worksheet(programatically) with the same name.
		this.entityName = mapper.getType();
//		this.lastUpdateColumnIndex = mapper.getLastUpdateColumnPosition();
		this.idColumnName = mapper.getIdColumnName();
		
	}
	
	@Override
	public void delete(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		EntityContent entityContent = EntityContent.normalizeContent(content, this.entityName, idColumnName);
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, mapper.getIdColumnPosition(), entityContent.getId());
		if(row != null){
			this.workSheet.deleteChildElement(row.getElementId());
		}
	}

	@Override
	public IContent get(String contentId) {
		Guard.argumentNotNullOrEmptyString(contentId, "contentId");
		//here contentId is entityid 
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, mapper.getIdColumnPosition(), contentId);
		if(row != null){
			Element payLoad = mapper.convertRowToXML(row, this.workSheet);
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
		System.out.println(this.workSheet.getGSRows().size());
		for(Map.Entry<String,GSRow<GSCell>> rowMap :this.workSheet.getGSRows().entrySet()){
			GSRow<GSCell> gsRow = rowMap.getValue();
			String entityId  = "";
			if(gsRow.getElementListIndex() > 1 ){
				if(gsRow != null && rowHasChanged(gsRow, since)){
					Element payLoad = mapper.convertRowToXML(gsRow, workSheet);
					 GSCell cell = gsRow.getGSCell(mapper.getIdColumnPosition());
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
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, mapper.getIdColumnPosition(), entityContent.getId());
		if(row == null){
			row = workSheet.createNewRow(workSheet.getChildElements().size() +1);
		}
		this.mapper.convertXMLElementToRow(workSheet, entityContent.getPayload(), row);
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
				GSCell cell = gsRow.getGSCell(mapper.getIdColumnPosition());
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

}
