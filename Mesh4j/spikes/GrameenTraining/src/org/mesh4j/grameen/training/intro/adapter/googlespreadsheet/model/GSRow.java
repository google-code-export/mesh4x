package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;

import com.google.gdata.client.spreadsheet.CellQuery;
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
public class GSRow<C> extends GSBaseElement<C>{
	
	// MODEL VARIABLES
	
	
	// BUSINESS METHODS
	public GSRow(Map<String, C> gsCells,
			ListEntry rowEntry, int rowIndex,
			GSWorksheet<GSRow<GSCell>> parentElement) {
		super();
		this.childElements = gsCells;
		this.baseEntry = rowEntry;
		this.elementListIndex = rowIndex;
		this.parentElement = parentElement;
	}	
	
	@Deprecated
	public GSRow(ListEntry rowEntry, int rowIndex) {
		super();
		this.childElements = new LinkedHashMap<String, C>();
		this.baseEntry = rowEntry;
		this.elementListIndex = rowIndex;
	}	

	public GSRow(ListEntry rowEntry, int rowIndex,
			GSWorksheet<GSRow<GSCell>> parentElement) {
		super();
		this.childElements = new LinkedHashMap<String, C>();
		this.baseEntry = rowEntry;
		this.elementListIndex = rowIndex;
		this.parentElement = parentElement;
	}	
	
	/**
	 * get the core {@link ListEntry} object wrapped by this {@link GSRow}
	 * @return
	 */
	public ListEntry getRowEntry() {
		return (ListEntry) getBaseEntry();
	}

	/**
	 * get the parent/container {@link GSWorksheet} object of this {@link GSRow}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSWorksheet<GSRow<GSCell>> getParentWorksheet() {
		return (GSWorksheet) getParentElement();
	}
	
	/**
	 * get all the child {@link GSCell} contained in this {@link GSRow} 
	 * @return
	 */
	public Map<String, C> getGSCells() {
		return getNonDeletedChildElements();
	}

	/**
	 * get all the child {@link GSCell} contained in this {@link GSRow} in the
	 * form of {@link ArrayList}
	 * 
	 * @return
	 */
	
	public List<C> getGsCellsAsList() {
		return new ArrayList<C>(getNonDeletedChildElements().values());
	}
	
	/**
	 * get the row index of this {@link GSRow} in the container {@link GSWorksheet}
	 * @return
	 */
	public int getRowIndex() {
		return getElementListIndex();
	}

	/**
	 * get the {@link GSCell} from this {@link GSRow} by colIndex 
	 * 
	 * @param colIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public C getGSCell(int colIndex){
		for(GSCell gsCell : ((GSRow<GSCell>)this).getNonDeletedChildElements().values()){
			if(gsCell.getCellEntry().getCell().getCol() == colIndex){
				return (C) gsCell;
			}			
		}
		return null;
	}

	/**
	 * get the {@link GSCell} from this {@link GSRow} by key/header tag 
	 * 
	 * @param key
	 * @return
	 */
	public C getGSCell(String key){
		return getChildElement(key);
	}
	
	/**
	 * populate the {@link GSCell} entries from the feed to be contained in this {@link GSRow}.
 	 * Note: this method involves a http request
 	 *     
	 * @param service
	 * @param worksheet
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public void populateClild(/*SpreadsheetService service,*/
			WorksheetEntry worksheet) throws IOException, ServiceException{
		
		if(getGSCells() != null && getGSCells().size() > 0) return;
			
		if(this.elementListIndex == 0){
			//ListFeed lFeed = service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
			ListFeed lFeed = worksheet.getService().getFeed(worksheet.getListFeedUrl(), ListFeed.class);
	
			//int rowIndex = lFeed.getEntries().contains(this.rowEntry);
			//TODO: unfortunately this is not working :(; may be need to override equals method... 
			
			int rowIndex=2; 
			for (; rowIndex <= lFeed.getEntries().size(); rowIndex++){
				if(lFeed.getEntries().get(rowIndex-1).getId().equals(((IGSElement)this.baseEntry).getId()))
					break;
			}
	
			this.elementListIndex = rowIndex;
		}
		
		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setMinimumRow(this.elementListIndex ); //cell row# is 1 more that row#
		query.setMaximumRow(this.elementListIndex );
		
		//CellFeed cFeed = service.query(query, CellFeed.class);
		CellFeed cFeed = worksheet.getService().query(query, CellFeed.class);
		
		for(CellEntry cell:cFeed.getEntries()){
			//getGsCells().add(new GSCell(cell, this));
			String key = Integer.toString(cell.getCell().getCol());
			this.childElements.put(key, (C) new GSCell(cell, (GSRow<GSCell>) this, "TODO: have to provide tag"));
		}
	}
		

	@SuppressWarnings("unchecked")
	public void populateClildWithHeaderTag(List<CellEntry> cellList, WorksheetEntry ws) throws IOException,
		ServiceException {
		if (this.elementListIndex > 0) {
			// iterate over all cells, only cells of corresponding row will be
			// entered in this filtered list
			List<CellEntry> filteredCellList = new ArrayList<CellEntry>();
			for (CellEntry cell : cellList) {
				if (cell.getCell().getRow() == this.elementListIndex) {
					//String key = Integer.toString(cell.getCell().getCol());
					//this.childElements.put( key, (C) new GSCell(cell, (GSRow<GSCell>) this));
					filteredCellList.add(cell);
				}
			}
			
			//pick a cell corresponding to a header tag and put it in the child map
			/*if(((ListEntry) this.getBaseEntry())
					.getCustomElements().getTags().size() == 0) {*/
			if(this.elementListIndex == 1) {
				for (CellEntry cell : cellList) {
					if (cell.getCell().getRow() == this.elementListIndex) {
										
						//the gdata api doesn't provide column tag for cell's of first row
						//that why it is generated from the cell's value
						
						String cellValue = cell.getCell().getValue();
						//TODO: need to review later if cellValue is null 
						/*if(cellValue == null || cellValue.length() ==0){							
							cellValue = "Column"+cell.getCell().getCol();
							cell.changeInputValueLocal(cellValue);
						}*/

						String key = extractCellHeadetTag(cellValue);						
						this.childElements.put(key, (C) new GSCell(cell,
								(GSRow<GSCell>) this, key)); 
					}
				}
				
			}else{

				for (String tag : ((ListEntry) this.getBaseEntry())
						.getCustomElements().getTags()) {							
					String value = ((ListEntry) this.getBaseEntry())
						.getCustomElements().getValue(tag);
					
					for (CellEntry cell : filteredCellList) {
						if(cell.getCell().getValue().equals(value)){
							this.childElements.put( tag, (C) new GSCell(cell, (GSRow<GSCell>) this, tag));
							//this cell
							filteredCellList.remove(cell);
							break;
						}
					}
				}//for
				
			}
						
		} else {
			// TODO:
		}
	}
	
	private String extractCellHeadetTag(String value) {
		StringBuffer tag = new StringBuffer("");
		for(char c: value.toLowerCase().toCharArray()){
			if(Character.isLetterOrDigit(c)){
				tag.append(c);
			}
		}		
		return tag.toString();
	}

	/**
 	 * update content/value of a {@link CellEntry} identified by column index in this row
 	 * 
	 * @param value
	 * @param colIndex
	 */
	@Deprecated
	public void updateCellValue(String value, int colIndex) {
		GSCell cellToUpdate = (GSCell) getGSCell(colIndex); 
		if (!cellToUpdate.getCellEntry().getCell().getInputValue()
				.equals(value)) 
			cellToUpdate.updateCellValue(value);
	}

	/**
	 * update content/value of a {@link CellEntry} identified by key in this row
	 * 
	 * @param value
	 * @param key
	 */
	public void updateCellValue(String value, String key) {
		GSCell cellToUpdate = (GSCell) getGSCell(key); 		
		if (!cellToUpdate.getCellEntry().getCell().getInputValue()
				.equals(value)) 
			cellToUpdate.updateCellValue(value);
	}

	/**
	 * return the content/value of a cell 
	 * 
	 * @param value
	 * @param key
	 * @return
	 */
	public String getCellValue(String value, String key) {
		GSCell cell = (GSCell) getGSCell(key); 
		return cell.getCellValue();
	}	
	
	/**
	 * this will create a new cell at column position col and add it to its child  
	 * elements with key 'key', any existing cell element with that key will be replaced
	 *  
	 * @param col
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSCell createNewCell(int col, String key, String value) {
		if(col < 1)
			 throw new IllegalArgumentException("colIndex");
		
		CellEntry newCell = new CellEntry(this.elementListIndex, col, ""); //this is not supported for batch update :(
		GSCell newGSCell = new GSCell(newCell, (GSRow<GSCell>) this, key);
		newGSCell.updateCellValue(value);
		this.addChildElement(key, (C) newGSCell);
		return newGSCell;
	}
	    
	@SuppressWarnings("deprecation")
	@Override
	public void refreshMeFromFeed() throws IOException, ServiceException{
		if(this.isDirty()){
				if (this.baseEntry.getId() == null) { //if it is a newly added row
					this.baseEntry = GoogleSpreadsheetUtils
							.getListEntryFromFeed(
									(WorksheetEntry) this.parentElement.baseEntry,
									this.elementListIndex);
				} else {
					URL entryUrl = new URL(((ListEntry) this.baseEntry).getId());

					this.baseEntry = this.baseEntry.getService().getEntry(
							entryUrl, ListEntry.class);
				}
			
			WorksheetEntry worksheet = (WorksheetEntry) this
				.getParentElement().getBaseEntry();
	
				this.getChildElements().clear();
				this.populateClild(worksheet);
			
			
			this.dirty = false;
			this.deleteCandidate = false;
						
		}
	}
	
}
