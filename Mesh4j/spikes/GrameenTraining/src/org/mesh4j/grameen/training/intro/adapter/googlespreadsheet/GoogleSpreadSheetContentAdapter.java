package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;
import java.util.List;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;
/**
 * Content repository which actually responsible for applying CRUD operation
 * in google spread sheet.
 * @author Raju
 * @version 1.0,29/4/2009
 */
public class GoogleSpreadSheetContentAdapter implements IContentAdapter,ISyncAware{

	private String type = "";
	private String sheetName = "";
	private IGoogleSpreadSheet spreadSheet = null;
	private GSWorksheet workSheet;
	private String idColumnName = "id";
	
	/**
	 * 
	 * @param spreadSheet the google spreadsheet
	 * @param sheetName the particular sheet name of a spreadsheet 
	 */
	public GoogleSpreadSheetContentAdapter(IGoogleSpreadSheet spreadSheet,GSWorksheet workSheet,String type){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		this.spreadSheet = spreadSheet;
		this.workSheet = workSheet;
		this.type = type;
		//right now we are planning to give the entity name as the title of the each sheet
		sheetName = workSheet.getWorksheet().getTitle().getPlainText();
	}
	@Override
	public void delete(IContent content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IContent get(String contentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IContent> getAll(Date since) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public void save(IContent content) {
		Guard.argumentNotNull(content, "content");
		EntityContent entityContent = EntityContent.normalizeContent(content, this.sheetName, idColumnName);
		//now find out the row from the spreadsheet
		//now convert this entiyconte
	}
	
	private void addRow(EntityContent entityContent){
		
		//this.workSheet.add(listEntry);
	}
	private void updateRow(EntityContent entityContent){
		
	}
	@Override
	public void beginSync() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endSync() {
		// TODO Auto-generated method stub
		
	}

}
