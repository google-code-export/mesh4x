package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.IOException;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class GSCellEntry{
	
	// MODEL VARIABLES
	private CellEntry cellEntry;
	private GSListEntry parentRow;
	private boolean dirty = false;

	// BUSINESS METHODS	
	public GSCellEntry(CellEntry cellEntry, GSListEntry parentRow) {
		super();
		this.cellEntry = cellEntry;
		this.parentRow = parentRow;
	}	
	
	public CellEntry getCellEntry() {
		return cellEntry;
	}

	public GSListEntry getParentRow() {
		return parentRow;
	}

	/**
	 * return the unique cell id specified for that cell in the spreadsheet 
	 *   
	 * @return
	 */
	public String getId(){
		return this.cellEntry.getId();
	}

	public void setDirty() {
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * populates the the row that contains this Cell 
	 * 
	 * @param service
	 * @param worksheet
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void populateParent(SpreadsheetService service,
			WorksheetEntry worksheet) throws IOException, ServiceException {
		
		if(this.parentRow != null) return;
		
		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setStartIndex(this.cellEntry.getCell().getRow()-1); 
		//why -1?: Google Spreadsheet's data row starts from it's 2nd row actually, 
		//1st row contains the column headers. 
		//So index of a row should be considered 1 less than row index returned from its cells.
		//What a weird API! 
		query.setMaxResults(1);

		ListFeed feed = service.query(query, ListFeed.class);

		GSListEntry gsListEntry = new GSListEntry(feed.getEntries().get(0),
				this.cellEntry.getCell().getRow()-1);
		
		/*for (String tag : feed.getEntries().get(0).getCustomElements().getTags()) {
		      //out.print(entry.getCustomElements().getValue(tag)+"\t");
		      System.out.print(feed.getEntries().get(0).getCustomElements().getValue(tag)+" \t");
		}*/
		
		gsListEntry.populateClild(service, worksheet);
		
		this.parentRow = gsListEntry;
	}
}
