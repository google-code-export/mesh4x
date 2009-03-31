package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class GSWorksheet implements IGSElement{

	// MODEL VARIABLES
	private WorksheetEntry worksheet;  //represents the worksheet entry provided by google api
	private Map<String, GSRow> rowList = new LinkedHashMap<String, GSRow>(); //represents the map of rows this worksheet contains
	private int sheetIndex; //represents the order index of this worksheet in the container spreadsheet 
	private boolean dirty = false; //flag represents worksheet content changed
	private boolean deleteCandidate = false; //flag represents this worksheet is going to be deleted in next flush operation
	
	
	// BUSINESS METHODS	
	public GSWorksheet(WorksheetEntry worksheet, int sheetIndex) {
		super();
		this.worksheet = worksheet;
		this.sheetIndex = sheetIndex;
		this.rowList = new LinkedHashMap<String, GSRow>();
	}
	
	public int getSheetIndex() {
		return sheetIndex;
	}

	public WorksheetEntry getWorksheet() {
		return worksheet;
	}
	public Map<String, GSRow> getRowList() {
		return rowList;
	}

	public String getId(){
		return this.worksheet.getId();
	}

	public IGSElement getParent() {
		// TODO: this should be the container spreadsheet
		return null;
	}

	public void setDeleteCandidate() {
		this.deleteCandidate = true;
	}
	
	public boolean isDeleteCandiddate() {
		return this.deleteCandidate;
	}
	
	/*
	 * code blocked by sharif, 30-03-09. 
	 * 
	 * please use the method addChildEntry(IGSElement element) instead
	 * also make sure ((GSRow)element).getRowIndex() returns its proper position
	 * 
	 * public void add(GSRow listEntry){
		this.getRowList().put(String.valueOf(getRowList().size() +1), listEntry);
	}*/
	
	public boolean isDirty() {
		return this.dirty;
	}	

	public void setDirty() {
		this.dirty = true;
	}	
	
	public List<IGSElement> getChilds() {
		Collection values = rowList.values();
		List childs = new ArrayList(values);			
		return childs;  
	}
	
	public void addChildEntry(IGSElement element) {
		element.setDirty();
		String key = Integer.toString(((GSRow)element).getRowIndex());
		this.rowList.put(key, (GSRow)element); 
	}

	public void deleteChildEntry(String key) {
		this.rowList.get(key).setDirty();
		this.rowList.get(key).setDeleteCandidate();
	}

	public IGSElement getChildEntry(String key) {
		return this.rowList.get(key);
	}

	public void updateChildEntry(String key, IGSElement element) {
		element.setDirty();
		this.rowList.put(key, (GSRow)element);
	}
	public String getName(){
		return this.getWorksheet().getTitle().getPlainText();
	}
}
