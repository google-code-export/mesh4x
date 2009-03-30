package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;


import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCellEntry;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSListEntry;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;


public class GoogleSpreadsheet implements IGoogleSpreadSheet{

	// MODEL VARIABLES
	//private String spreadsheetFileId;
	private GSSpreadsheet spreadsheet;
	
	private SpreadsheetService service;
	private FeedURLFactory factory;
	

	
	private boolean dirty = false;
	
	private CellFeed batchFeed;  //TODO: need to guard against duplicate entry
	
	public static final String VISIBILITY_PRIVATE = "private";
	public static final String VISIBILITY_PUBLIC = "public";
	
	public static final String PROJECTION_PUBLIC = "full";
	public static final String PROJECTION_VALUES = "values";	
	public static final String PROJECTION_BASIC = "basic";

	public static final String DOC_ROOT = "http://spreadsheets.google.com/feeds";

	public static final String DOC_ELEMENT_TYPE_CELL = "cells";
	public static final String DOC_ELEMENT_TYPE_ROW = "list";
	public static final String DOC_ELEMENT_TYPE_WORKSHEET = "worksheets";
	public static final String DOC_ELEMENT_TYPE_SPREADSHEET = "spreadsheets";
	
		
	// BUSINESS METHODS
	
	private void init(String username, String password) {

		this.service = new SpreadsheetService("Mesh4j");
		this.service.setProtocolVersion(SpreadsheetService.Versions.V1);

		try {
			this.service.setUserCredentials(username, password);

		} catch (AuthenticationException e) {
			throw new MeshException(e);
		}

		this.factory = FeedURLFactory.getDefault();
		this.batchFeed = new CellFeed();
	}	
	
	/**
	 * load spreadsheet by spreadsheet file ID
	 * 
	 * @param spreadsheetFileId
	 * @param username
	 * @param password
	 */
	public GoogleSpreadsheet(String spreadsheetFileId, String username,
			String password) {
		super();
		Guard.argumentNotNullOrEmptyString(spreadsheetFileId,
				"spreadsheetFileId");
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");

		init(username, password);
		
		try {
			this.spreadsheet = GoogleSpreadsheetUtils.getGSSpreadsheet(
					this.factory, this.service, spreadsheetFileId);
		} catch (Exception e) {
			throw new MeshException(e);
		}	
	}	
	
	/**
	 * load spreadsheet by sheet index
	 * 
	 * @param sheetIndex
	 * @param username
	 * @param password
	 */
	public GoogleSpreadsheet(int sheetIndex, String username,
			String password) {
		super();
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");

		init(username, password);
		
		try {
			this.spreadsheet = GoogleSpreadsheetUtils.getGSSpreadsheet(
					this.factory, this.service, sheetIndex);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
	}
	
	public SpreadsheetService getService() {
		return service;
	}

	public FeedURLFactory getFactory() {
		return factory;
	}

	public GSSpreadsheet getGSSpreadsheet() {
		return this.spreadsheet;
	}

	public CellFeed getBatchFeed() {
		return batchFeed;
	}

	public void setDirty() {
		this.dirty = true;		
	}

	public void addEntryToUpdate(GSCellEntry toUpdate){
		batchFeed.getEntries().add(toUpdate.getCellEntry());
	}

	public void addEntryToUpdate(GSListEntry toUpdate){		
		for(GSCellEntry gsCell : toUpdate.getGsCells()){
			if(gsCell.isDirty()){
				addEntryToUpdate(gsCell);
				toUpdate.setDirty();
			}	
		}
	}
	
	public void flush(GSWorksheet worksheet) {
		if(this.dirty){
			GoogleSpreadsheetUtils.flush(this.service, worksheet, this.batchFeed);
			this.dirty = false;
		}	
	}
	
	public void refresh(){
		//TODO: need to think more abt it
	}

}
