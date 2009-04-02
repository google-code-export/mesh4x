package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

/**
 * This class is to wrap a {@link CellEntry}, also contains a reference
 * {@link GSListEntry} as a parent/container row of this cell
 * 
 * @author sharif
 * @Version 1.0, 29-03-09
 * 
 */
@SuppressWarnings("unchecked")
public class GSCell extends GSBaseElement {
	
	// MODEL VARIABLES
	//all moved to base class
	
	// BUSINESS METHODS	
	public GSCell(CellEntry cellEntry, GSRow<GSCell> parentRow){
		super();
		this.baseEntry = cellEntry;
		this.parentElement = parentRow;
		this.childElements = null;
	}	
		
	/**
	 * get the core {@link CellEntry} object wrapped by this {@link GSCell}
	 * @return
	 */
	public CellEntry getCellEntry() {
		return (CellEntry) getBaseEntry();
	}
	
	/**
	 * get the parent/container {@link GSRow} object of this {@link GSCell}
	 * @return
	 */	
	public GSRow<GSCell> getParentRow() {
		return (GSRow) getParentElement();
	}

	/**
	 * get the row index of this {@link GSCell} in the container {@link GSRow}
	 * @return
	 */
	public int getRowIndex() {
		return getCellEntry().getCell().getRow();
	}
	
	/**
	 * get the column index of this {@link GSCell} in the container {@link GSRow}
	 * @return
	 */
	public int getColIndex() {
		return getCellEntry().getCell().getCol();
	}	
	
	/**
	 * populates the the {@link GSRow} that contains this {@link GSCell}
	 * Note: this method involves a http request    
	 * 
	 * @param service
	 * @param worksheet
	 * @throws IOException
	 * @throws ServiceException
	 */
	@Deprecated
	public void populateParent(SpreadsheetService service,
			WorksheetEntry worksheet) throws IOException, ServiceException {
		
		if(this.parentElement != null) return;
		
		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setStartIndex(((CellEntry)this.baseEntry).getCell().getRow()-1); 
		//why -1?: Google Spreadsheet's data row starts from it's 2nd row actually, 
		//1st row contains the column headers. 
		//So index of a row should be considered 1 less than row index returned from its cells.
		//What a weird API! 
		query.setMaxResults(1);

		ListFeed feed = service.query(query, ListFeed.class);

		GSRow<GSCell> gsListEntry = new GSRow(
				feed.getEntries().get(0), ((CellEntry) this.baseEntry)
						.getCell().getRow() - 1);
		
		/*for (String tag : feed.getEntries().get(0).getCustomElements().getTags()) {
		      //out.print(entry.getCustomElements().getValue(tag)+"\t");
		      System.out.print(feed.getEntries().get(0).getCustomElements().getValue(tag)+" \t");
		}*/
		
		gsListEntry.populateClild(service, worksheet);
		
		this.parentElement =  gsListEntry;
	}

}
