package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;

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
public class GSRow<C> extends GSBaseElement<C>{
	
	// MODEL VARIABLES
	//all moved to base class
	
	
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
		return getChildElements();
	}

	/**
	 * get all the child {@link GSCell} contained in this {@link GSRow} in the
	 * form of {@link ArrayList}
	 * 
	 * @return
	 */
	
	public List<C> getGsCellsAsList() {
		return new ArrayList<C>(getChildElements().values());
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
	public C getGSCell(int colIndex){
		for(GSCell gsCell : ((GSRow<GSCell>)this).getChildElements().values()){
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
			this.childElements.put(key, (C) new GSCell(cell, (GSRow<GSCell>) this));
		}
	}
		
	/**
	 * Populate all the child {@link GSCell} of this {@link GSRow} from all available {@link CellEntry} in a {@link WorksheetEntry}
	 * by wrapping up each valid {@link CellEntry} with a {@link GSCell} 
	 * 
	 * @param cellList All cells in a worksheet
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public void populateClild(List<CellEntry> cellList) throws IOException,
			ServiceException {
		if (this.elementListIndex > 0) {
			// iterate over all cells, only cells of corresponding row will be
			// entered in the child list
			for (CellEntry cell : cellList) {
				if (cell.getCell().getRow() == this.elementListIndex) {
					String key = Integer.toString(cell.getCell().getCol());
					this.childElements.put( key, (C) new GSCell(cell, (GSRow<GSCell>) this));
				}
			}
		} else {
			// TODO:
		}
	}

	public void populateClildWithHeaderTag(List<CellEntry> cellList) throws IOException,
		ServiceException {
		if (this.elementListIndex > 0) {
			// iterate over all cells, only cells of corresponding row will be
			// entered in this filtered list
			List<CellEntry> filteredCellList = new ArrayList();
			for (CellEntry cell : cellList) {
				if (cell.getCell().getRow() == this.elementListIndex) {
					//String key = Integer.toString(cell.getCell().getCol());
					//this.childElements.put( key, (C) new GSCell(cell, (GSRow<GSCell>) this));
					filteredCellList.add(cell);
				}
			}
			
			//pick a cell corresponding to a header tag and put it in the child map 
			for (String tag : ((ListEntry) this.getBaseEntry())
					.getCustomElements().getTags()) {							
				String value = ((ListEntry) this.getBaseEntry())
					.getCustomElements().getValue(tag);
				
				for (CellEntry cell : filteredCellList) {
					if(cell.getCell().getValue().equals(value)){
						this.childElements.put( tag, (C) new GSCell(cell, (GSRow<GSCell>) this));
						//this cell
						filteredCellList.remove(cell);
						break;
					}
				}
				
			}
						
		} else {
			// TODO:
		}
	}
	
	/**
 	 * update content/value of a {@link CellEntry} identified by column index in this row
 	 * 
	 * @param value
	 * @param colIndex
	 */
	public void updateCellValue(String value, int colIndex) {
		GSCell cellToUpdate = (GSCell) getGSCell(colIndex); 
		cellToUpdate.getCellEntry().changeInputValueLocal(value);
		cellToUpdate.setDirty();
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
				.equals(value)) {
			cellToUpdate.getCellEntry().changeInputValueLocal(value);
			cellToUpdate.setDirty();
		}
	}

	public void refreshMe(){
		if(this.isDirty()){
			try {
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
			
			WorksheetEntry worksheet = (WorksheetEntry) this
				.getParentElement().getBaseEntry();
	
			try {
				this.getChildElements().clear();
				this.populateClild(worksheet);
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
