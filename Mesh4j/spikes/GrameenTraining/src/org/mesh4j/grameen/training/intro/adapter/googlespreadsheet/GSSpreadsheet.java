package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class GSSpreadsheet extends WorksheetEntry {

	private SpreadsheetEntry spreadsheet;
	private Map<String, GSWorksheet> worksheetList = new LinkedHashMap<String, GSWorksheet>();

	
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
