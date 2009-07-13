package org.mesh4j.sync.adapters.googlespreadsheet;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
/**
 * Basically implementation of CRUD operation in google spreadsheet through Mesh4x wrapper
 * API of GData API.
 * Content repository which actually responsible for applying CRUD operation
 * in google spread sheet.
 * @author Raju
 * @version 1.0,29/4/2009
 */
public class GoogleSpreadSheetContentAdapter implements IContentAdapter, ISyncAware, IIdentifiableContentAdapter{

	// MODEL VARIABLES
	private IGoogleSpreadSheet spreadSheet = null;		//represents the google spreadsheet
	private GSWorksheet<GSRow<GSCell>> workSheet;		//represents a specific sheet of a google spreadsheet
	private IGoogleSpreadsheetToXMLMapping mapper;

	// BUSINESS METHODS
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
		this.workSheet = GoogleSpreadsheetUtils.getOrCreateContentSheetIfAbsent(spreadSheet.getGSSpreadsheet(),	mapper);

	}
	
	@Override
	public void delete(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapper);
		GSRow<GSCell> row = this.mapper.getRow(this.workSheet, entityContent.getId());
		if(row != null){
			this.workSheet.deleteChildElement(row.getElementId());
		}
	}

	@Override
	public IContent get(String contentId) {
		Guard.argumentNotNullOrEmptyString(contentId, "contentId");

		GSRow<GSCell> row = this.mapper.getRow(this.workSheet, contentId);
		if(row != null){
			Element payLoad = mapper.convertRowToXML(row);
			return new IdentifiableContent(payLoad, this.mapper, contentId);
		}
		return null;
	}
	
	@Override
	public List<IContent> getAll(Date since) {
		List<IContent> listOfAll = new LinkedList<IContent>();
		//we will not count first row,since this is header row
		//and here counting is starts from 1 not zero.so header row index will be 1
		//System.out.println(this.workSheet.getGSRows().size());
		for(Map.Entry<String,GSRow<GSCell>> rowMap :this.workSheet.getGSRows().entrySet()){
			GSRow<GSCell> gsRow = rowMap.getValue();
			if(gsRow != null && gsRow.getElementListIndex() > 1 && rowHasChanged(gsRow, since)){
				String entityId = this.mapper.getId(gsRow);
				if(entityId != null){
					Element payLoad = mapper.convertRowToXML(gsRow);
					IdentifiableContent entityContent = new IdentifiableContent(payLoad,this.mapper, entityId);
					listOfAll.add(entityContent);
				}
			}
		}
		return listOfAll;
	}
	
	
	
	private boolean rowHasChanged(GSRow<GSCell> row, Date since){
		Date lastUpdate = this.mapper.getLastUpdate(row);
		if(lastUpdate == null){
			return true;
		} else {
			return since.compareTo(lastUpdate) <= 0;
		}
	}
	
	@Override
	public String getType() {
		return this.mapper.getType();
	}

	@Override
	public void save(IContent content) {
		Guard.argumentNotNull(content, "content");
		
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapper);
		GSRow<GSCell> row = this.mapper.getRow(workSheet, entityContent.getId());
		if(row == null){
			row = workSheet.createNewRow(workSheet.getChildElements().size() +1);
		}
		this.mapper.applyXMLElementToRow(workSheet, row, entityContent.getPayload());
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
	
	public IGoogleSpreadsheetToXMLMapping getMapper() {
		return this.mapper;
	}

	@Override
	public String getID(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapper);
		if(entityContent == null){
			return null;
		} else {
			return entityContent.getId();
		}
	}

	public GSWorksheet<GSRow<GSCell>> getWorkSheet() {
		return workSheet;
	}
}
