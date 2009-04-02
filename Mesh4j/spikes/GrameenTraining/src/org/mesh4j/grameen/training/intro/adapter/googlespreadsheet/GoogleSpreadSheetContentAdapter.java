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
import org.mesh4j.sync.utils.DateHelper;
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

	private String type = "";
	private String sheetName = "";
	//this property is for identify each row in spreadsheet
	//but as because google API provides api to identify each row
	//we will not use this extra column id in spreadsheet like MsExcel Adapter
	private String idColumnName = "id";

	//Which actually represents the lastupdatecolumnName position of SpreadSheetToXMLMapper
	private int lastUpdateColumnIndex = -1;
	
	//represents the google spreadsheet
	private IGoogleSpreadSheet spreadSheet = null;
	
	//represents a specific sheet of a google spreadsheet
	private GSWorksheet workSheet;

	private ISpreadSheetToXMLMapper mapper;
	
	/**
	 * 
	 * @param spreadSheet the google spreadsheet
	 * @param sheetName the particular sheet name of a spreadsheet 
	 */
	public GoogleSpreadSheetContentAdapter(IGoogleSpreadSheet spreadSheet,GSWorksheet workSheet,
											ISpreadSheetToXMLMapper mapper,String type){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		this.spreadSheet = spreadSheet;
		this.workSheet = workSheet;
		this.mapper = mapper;
		this.type = type;
		//right now we are planning to give the entity name as the title of the each sheet
		this.sheetName = workSheet.getWorksheet().getTitle().getPlainText();
		this.lastUpdateColumnIndex = mapper.getLastUpdateColumnPosition();
		
	}
	
	@Override
	public void delete(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, idColumnName);
		GSRow row = this.workSheet.getGSRow(Integer.parseInt(entityContent.getId()));
		if(row != null){
			this.workSheet.deleteChildEntry(entityContent.getId());	
		}
	}

	@Override
	public IContent get(String contentId) {
		Guard.argumentNotNullOrEmptyString(contentId, "contentId");
		
		GSRow row = this.workSheet.getGSRow(Integer.parseInt(contentId));
		if(row != null){
			Element payLoad = mapper.convertRowToXML(row, this.workSheet);
			return new EntityContent(payLoad,this.sheetName,contentId);
		}
		return null;
	}

	@Override
	public List<IContent> getAll(Date since) {
		Guard.argumentNotNull(since, "content");
		
		List<IContent> listOfAll = new LinkedList<IContent>();
		for(Map.Entry<String,GSRow> rowMap :this.workSheet.getRowList().entrySet()){
			GSRow gsRow = rowMap.getValue();
			
			if(gsRow != null && rowHasChanged(gsRow, since)){
				Element payLoad = mapper.convertRowToXML(gsRow, workSheet);
				EntityContent entityContent = new EntityContent(
						payLoad,this.sheetName,String.valueOf(gsRow.getRowIndex()));
				listOfAll.add(entityContent);
			}
//			 for (String columnHeader : gsRow.getRowEntry().getCustomElements().getTags()){
//				 if(columnHeader != null && !columnHeader.equals("")){
//					if(columnHeader.equalsIgnoreCase(lastUpdateColumn)){
//						String dateTimeAsString = gsRow.getRowEntry().getCustomElements().getValue(columnHeader);
//						System.out.println("Date is :" + dateTimeAsString);
//						Date convertedDate = DateHelper.parseDateTime(dateTimeAsString);
//						isRowChanged(gsRow, convertedDate);
//						
//					}
//				 }
//			 }
		}
		
		return listOfAll;
	}
	private boolean rowHasChanged(GSRow row,Date since){
		if(lastUpdateColumnIndex == -1){
			return true;
		} else {
			GSCell cell = row.getGsCell(lastUpdateColumnIndex);
			if(cell == null){
				return true;
			} else {
				String dateTimeAsString = cell.getCellEntry().getCell().getValue();
				Date lasUpdateDate = DateHelper.parseDateTime(dateTimeAsString);
				return since.compareTo(lasUpdateDate) <= 0;
			}
		}
	}
	
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void save(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, idColumnName);
		//now find out the row from the spreadsheet
		//here entity id is row index of the each row,since google spreadsheet has row index 
		//implementation so right we don't need to put extra id column in the spreadsheet to
		//identify the each row or item
		//TODO We need to do the test to prove the  above concept. 
		GSRow row = this.workSheet.getGSRow(Integer.parseInt(entityContent.getId()));
		if(row == null){
			addRow(entityContent);
		}else{
			updateRow(entityContent);
		}
	}
	
	private void addRow(EntityContent entityContent){
		int rowIndex = this.workSheet.getRowList().size() + 1;
		GSRow row = this.mapper.convertXMLElementToRow(entityContent.getPayload(), rowIndex );
		this.workSheet.addChildEntry(row);
		
	}
	private void updateRow(EntityContent entityContent){
		int rowIndex = Integer.parseInt(entityContent.getId());
		GSRow row = this.mapper.convertXMLElementToRow(entityContent.getPayload(), rowIndex);
		this.workSheet.updateChildEntry(entityContent.getId(), row);
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
