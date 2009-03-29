package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class GSWorksheet{

	// MODEL VARIABLES
	private WorksheetEntry worksheet;  //represents the worksheet entry provided by google api
	private Map<String, GSListEntry> rowList = new LinkedHashMap<String, GSListEntry>(); //represents the map of rows this worksheet contains
	private int sheetIndex; //represents the order index of this worksheet in the container spreadsheet 
	
	
	// BUSINESS METHODS	
	public GSWorksheet(WorksheetEntry worksheet, int sheetIndex) {
		super();
		this.worksheet = worksheet;
		this.sheetIndex = sheetIndex;
		this.rowList = new LinkedHashMap<String, GSListEntry>();
	}
	
	public int getSheetIndex() {
		return sheetIndex;
	}

	public WorksheetEntry getWorksheet() {
		return worksheet;
	}
	public Map<String, GSListEntry> getRowList() {
		return rowList;
	}

	public String getId(){
		return this.worksheet.getId();
	}	
	
}
