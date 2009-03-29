package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

/**
 * This class is to wrap a {@link SpreadsheetEntry}, also contains a list of reference
 * to {@link GSWorksheet} as the worksheets it contains
 * 
 * @author sharif
 * @Version 1.0, 29-03-09
 * 
 */
public class GSSpreadsheet{

	// MODEL VARIABLES
	private SpreadsheetEntry spreadsheet; //represents the spreadsheet entry provided by google api
	private Map<String, GSWorksheet> worksheetList = new LinkedHashMap<String, GSWorksheet>(); //represents the map of worksheets it contains 

	
	// BUSINESS METHODS	
	public GSSpreadsheet(SpreadsheetEntry spreadsheet) {
		super();
		this.spreadsheet = spreadsheet;
		this.worksheetList = new LinkedHashMap<String, GSWorksheet>();
	}

	public SpreadsheetEntry getSpreadsheet() {
		return spreadsheet;
	}

	public Map<String, GSWorksheet> getWorksheetList() {
		return worksheetList;
	}	
	
}
