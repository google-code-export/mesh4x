package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class GSWorksheet extends WorksheetEntry {

	private WorksheetEntry worksheet;
	private Map<String, GSListEntry> rowList = new LinkedHashMap<String, GSListEntry>();
	private int sheetIndex;
	
	
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
