package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;


import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.AuthenticationException;


public class GoogleSpreadsheet {

	// MODEL VARIABLES
	private String spreadsheetFileId;
	private SpreadsheetEntry spreadsheet;
	private boolean dirty = false;
	
	private SpreadsheetService service;
	private FeedURLFactory factory;
	
	
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
	public GoogleSpreadsheet(String spreadsheetFileId, String username,
			String password) {
		super();
		Guard.argumentNotNullOrEmptyString(spreadsheetFileId,
				"spreadsheetFileId");
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");

		this.spreadsheetFileId = spreadsheetFileId;

		this.service = new SpreadsheetService("Mesh4j");

		try {
			this.service.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			throw new MeshException(e);
		}

		this.factory = FeedURLFactory.getDefault();

		try {
			this.spreadsheet = GoogleSpreadsheetUtils.getSpreadsheet(
					this.factory, this.service, spreadsheetFileId);
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

	public SpreadsheetEntry getSpreadsheet() {
		return this.spreadsheet;
	}
		
	public void setDirty() {
		this.dirty = true;		
	}

	public void flush() {
		if(this.dirty){
			GoogleSpreadsheetUtils.flush(this.spreadsheet, this.spreadsheetFileId);
			this.dirty = false;
		}	
	}
}
