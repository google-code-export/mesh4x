package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

/**
 * This class is to wrap a {@link SpreadsheetEntry}, also contains a list of reference
 * to {@link GSWorksheet} as the worksheets it contains
 * 
 * @author sharif
 * @Version 1.0, 29-03-09
 * 
 */
public class GSSpreadsheet implements IGSElement{

	// MODEL VARIABLES
	private SpreadsheetEntry spreadsheet; //represents the spreadsheet entry provided by google api
	private Map<String, GSWorksheet> worksheetList = new LinkedHashMap<String, GSWorksheet>(); //represents the map of worksheets it contains 
	private boolean dirty = false; //flag represents spreadsheet content changed
	private boolean deleteCandidate = false; //flag represents this spreadsheet is going to be deleted in next flush operation

	
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

	public IGSElement getParent() {
		return null;
	}
	
	public void setDeleteCandiddate(boolean isDeleteCandidate) {
		this.deleteCandidate = isDeleteCandidate;
	}
	
	public boolean isDeleteCandiddate() {
		return this.deleteCandidate;
	}

	public String getId() {
		return spreadsheet.getId();
	}

	public boolean isDirty() {
		return this.dirty;
	}	

	public List<IGSElement> getChilds() {
		Collection values = worksheetList.values();
		List childs = new ArrayList(values);			
		return childs; 		
	}	
}
