package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

/**
 * This class is to wrap a {@link ListEntry}, also contains a list of references
 * to {@link GSCellEntry} as the cells it contains.
 * 
 * @author sharif
 * @Version 1.0, 29-03-09
 * 
 */
public class GSListEntry implements IGSElement{
	
	// MODEL VARIABLES
	private ListEntry rowEntry; //represents the row entry provided by google api
	private List <IGSElement> gsCells; //represents the list of cells it contains
	private int rowIndex; //represents index of the row in the worksheet; starting from 1
	private boolean dirty = false; //flag represents row content changed
	private boolean deleteCandidate = false; //flag represents this row is going to be deleted in next flush operation

	
	//Note:Google Spreadsheet's data row starts from it's 2nd row actually, 1st row contains the column headers. 
	//So index of a row should be considered 1 less than row index returned from its cells.
	//What a weird API! 
	
	// BUSINESS METHODS
	public GSListEntry(List<IGSElement> gsCells, ListEntry rowEntry, int rowIndex) {
		super();
		this.gsCells = gsCells;
		this.rowEntry = rowEntry;
		this.rowIndex = rowIndex;
	}	
	
	public GSListEntry(ListEntry rowEntry, int rowIndex) {
		super();
		this.gsCells = new LinkedList<IGSElement>();
		this.rowEntry = rowEntry;
		this.rowIndex = rowIndex;
	}	

	public ListEntry getRowEntry() {
		return rowEntry;
	}

	public List<IGSElement> getGsCells() {
		if (gsCells == null)
			gsCells = new LinkedList<IGSElement>();
		return gsCells;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
		
	public void setDirty() {
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * get the cell at column position colIndex 
	 * 
	 * @param colIndex
	 * @return
	 */
	public GSCellEntry getGsCell(int colIndex){
		if (colIndex < 1 )
			throw new IllegalArgumentException("Column Index should be greater than 0");
		return (GSCellEntry)gsCells.get(colIndex-1);
	}
	
	/**
 	 * return the unique row/listEntry id specified for that row in the spreadsheet 
 	 * 
	 * @return
	 */
	public String getId(){
		return this.rowEntry.getId();
	}
	
	/**
	 * populate the cells entries contained in this row.
	 * 
	 * @param service
	 * @param worksheet
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void populateClild(SpreadsheetService service,
			WorksheetEntry worksheet) throws IOException, ServiceException{
		
		if(this.gsCells != null && this.gsCells.size() > 0) return;
			
		if(this.rowIndex == 0){
			ListFeed lFeed = service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
	
			//int rowIndex = lFeed.getEntries().contains(this.rowEntry);
			//TODO: unfortunately this is not working :(; may be need to override equals method... 
			
			int rowIndex=1; 
			for (; rowIndex <= lFeed.getEntries().size(); rowIndex++){
				if(lFeed.getEntries().get(rowIndex-1).getId().equals(this.rowEntry.getId()))
					break;
			}
	
			this.rowIndex = rowIndex;
		}
		
		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setMinimumRow(this.rowIndex + 1); //cell row# is 1 more that row#
		query.setMaximumRow(this.rowIndex + 1);
		
		CellFeed cFeed = service.query(query, CellFeed.class);		
		
		for(CellEntry entry:cFeed.getEntries()){
			getGsCells().add(new GSCellEntry(entry, this));
		}
	}
	
	
	public void populateClild(List<CellEntry> cellList) throws IOException, ServiceException{
		if(this.rowIndex > 0){
			for(CellEntry cell : cellList){
				if( cell.getCell().getRow()-1 == this.rowIndex)
					getGsCells().add(new GSCellEntry(cell, this));
			}
		}else{
			//TODO:
		}	
	}

	public IGSElement getParent() {
		// TODO: this should be the container worksheet 
		return null;
	}

	public void setDeleteCandiddate(boolean isDeleteCandidate) {
		this.deleteCandidate = isDeleteCandidate;
	}
	
	public boolean isDeleteCandiddate() {
		return this.deleteCandidate;
	}	
	
	public List<IGSElement> getChilds() {
		return this.getGsCells();
	}
	
}
