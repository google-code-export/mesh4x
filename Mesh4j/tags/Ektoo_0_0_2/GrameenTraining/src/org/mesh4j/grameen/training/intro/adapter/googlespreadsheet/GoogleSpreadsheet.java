package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

public class GoogleSpreadsheet implements IGoogleSpreadSheet {

	// MODEL VARIABLES
	private String spreadsheetFileId;
	private String username;
	private String password;
	private GSSpreadsheet<GSWorksheet> spreadsheet;

	private SpreadsheetService spreadsheetService;
	DocsService docService;
	private FeedURLFactory factory;

	private boolean dirty = false;

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

	/**
	 * initialize service and feedURL factory
	 */
	private void init() {
		this.spreadsheetService = GoogleSpreadsheetUtils.getSpreadsheetService(username, password);
		this.docService = GoogleSpreadsheetUtils.getDocService(username, password);
		this.factory = FeedURLFactory.getDefault();
	}

	/**
	 * load spreadsheet by spreadsheet file ID
	 * 
	 * @param spreadsheetFileId
	 * @param username
	 * @param password
	 */
	@SuppressWarnings("unchecked")
	public GoogleSpreadsheet(String spreadsheetFileId, String username,
			String password) {
		super();
		Guard.argumentNotNullOrEmptyString(spreadsheetFileId,
				"spreadsheetFileId");
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");

		this.spreadsheetFileId = spreadsheetFileId;
		this.username = username;
		this.password = password;

		init();

		try {
			this.spreadsheet = GoogleSpreadsheetUtils.getGSSpreadsheet(
					this.factory, this.spreadsheetService, spreadsheetFileId);
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
	@SuppressWarnings("unchecked")
	public GoogleSpreadsheet(int sheetIndex, String username, String password) {
		super();
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");

		init();

		try {
			this.spreadsheet = GoogleSpreadsheetUtils.getGSSpreadsheet(
					this.factory, this.spreadsheetService, sheetIndex);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public SpreadsheetService getSpreadsheetService() {
		return spreadsheetService;
	}

	public DocsService getDocService() {
		return docService;
	}

	public FeedURLFactory getFactory() {
		return factory;
	}

	@SuppressWarnings("unchecked")
	public GSSpreadsheet getGSSpreadsheet() {
		return this.spreadsheet;
	}

	/**
	 * get specific worksheet by sheet name/title
	 * 
	 * @param sheetName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSWorksheet getGSWorksheet(String sheetName) {
		return this.spreadsheet.getGSWorksheetBySheetName(sheetName);
	}

	/**
	 * get specific worksheet by sheetIndex
	 * 
	 * @param sheetIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSWorksheet getGSWorksheet(int sheetIndex) {
		return this.spreadsheet.getGSWorksheet(sheetIndex);
	}

	public void setDirty() {
		this.dirty = true;
	}

	/*
	 * public void addEntryToUpdate(GSCell toUpdate){
	 * batchFeed.getEntries().add(toUpdate.getCellEntry()); }
	 * 
	 * public void addEntryToUpdate(GSRow toUpdate){ for(IGSElement gsCell :
	 * toUpdate.getGsCells()){ if(gsCell.isDirty()){
	 * addEntryToUpdate((GSCell)gsCell); toUpdate.setDirty(); } } }
	 */
	/**
	 * transfer the current state of the spreadsheet in memory to spreadsheet
	 * file in web
	 */
	public void flush() {
		if (this.dirty)
			GoogleSpreadsheetUtils.flush(this.spreadsheetService,
					this.spreadsheet);
	}

	/**
	 * reload spreadsheet file to memory
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		try {
			this.spreadsheet = GoogleSpreadsheetUtils.getGSSpreadsheet(
					this.factory, this.spreadsheetService, spreadsheetFileId);
			this.dirty = false;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
