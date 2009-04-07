package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gdata.client.spreadsheet.ListQuery;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
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
	private String tmpCellValue;
	private String columnTag;
	
	// BUSINESS METHODS	
	public GSCell(CellEntry cellEntry, GSRow<GSCell> parentRow, String columnTag){
		super();
		this.baseEntry = cellEntry;
		this.parentElement = parentRow;
		this.childElements = null;
		this.columnTag = columnTag;
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
	 * update content/value of this cell
	 * @param value
	 */
	public void updateCellValue(String value) {
		tmpCellValue = value;
		getCellEntry().changeInputValueLocal(value);
		
		if(columnTag!=null && columnTag.length()!=0)		
			((ListEntry)this.getParentRow().getBaseEntry()).getCustomElements().setValueLocal(this.columnTag, value);
		
		this.setDirty();
	} 	

	/**
	 * return content/value of this cell
	 * @param value
	 */
	public String getCellValue() {
		if (isDirty())
			return tmpCellValue;
		else
			return getCellEntry().getCell().getInputValue();
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
	public void populateParent_BLOCKED(/*SpreadsheetService service,*/
			WorksheetEntry worksheet) throws IOException, ServiceException {
		
		if(this.parentElement != null) return;
		
		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setStartIndex(((CellEntry)this.baseEntry).getCell().getRow()-1); 
		//why -1?: Google Spreadsheet's data row starts from it's 2nd row actually, 
		//1st row contains the column headers. 
		//So index of a row should be considered 1 less than row index returned from its cells.
		//What a weird API! 
		query.setMaxResults(1);

		//ListFeed feed = service.query(query, ListFeed.class);
		ListFeed feed = worksheet.getService().query(query, ListFeed.class);

		GSRow<GSCell> gsListEntry = new GSRow(
				feed.getEntries().get(0), ((CellEntry) this.baseEntry)
						.getCell().getRow() - 1);
		
		/*for (String tag : feed.getEntries().get(0).getCustomElements().getTags()) {
		      //out.print(entry.getCustomElements().getValue(tag)+"\t");
		      System.out.print(feed.getEntries().get(0).getCustomElements().getValue(tag)+" \t");
		}*/
		
		gsListEntry.populateClild_BLOCKED(/*service,*/ worksheet);
		
		this.parentElement =  gsListEntry;
	}


	public void refreshMe(){
		if(this.isDirty()){
			try {
				URL entryUrl = new URL(((WorksheetEntry) this
						.getParentElement().getParentElement().getBaseEntry())
						.getCellFeedUrl().toString()
						+ "/" + ((CellEntry) this.baseEntry).getId());

				this.baseEntry = this.baseEntry.getService().getEntry(entryUrl,
						CellEntry.class);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.dirty = false;
			this.deleteCandidate = false;
			
		}
	}
}
