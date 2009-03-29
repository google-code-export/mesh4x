package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
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
	
	/**
	 * 
	 * @param spreadSheet the google spreadsheet
	 * @param sheetName the particular sheet name of a spreadsheet 
	 */
	public GoogleSpreadSheetContentAdapter(IGoogleSpreadSheet spreadSheet,String sheetName){
		
		this.spreadSheet = spreadSheet;
		this.sheetName = sheetName;
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
		return null;
	}

	@Override
	public void save(IContent content) {
		// TODO Auto-generated method stub
		
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
