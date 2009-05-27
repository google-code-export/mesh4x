package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class GSWorksheet<C> extends GSBaseElement<C> {

	// MODEL VARIABLES

	
	// BUSINESS METHODS		
	public GSWorksheet(WorksheetEntry worksheet, int sheetIndex,
			GSSpreadsheet<?> parentElement) {
		super();
		this.baseEntry = worksheet;
		this.elementListIndex = sheetIndex;
		this.childElements = new LinkedHashMap<String, C>();
		this.parentElement = parentElement;
	}
	
	/**
	 * get the core {@link WorksheetEntry} object wrapped by this
	 * {@link GSWorksheet}
	 * @return
	 */
	public WorksheetEntry getWorksheetEntry() {
		return (WorksheetEntry) getBaseEntry();
	}

	/**
	 * get the parent/container {@link GSSpreadsheet} object of this {@link GSWorksheet}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSSpreadsheet getParentSpreadsheet() {
		return (GSSpreadsheet) getParentElement();
	}
	
	/**
	 * get all the child {@link GSRow} contained in this {@link GSSpreadsheet} 
	 * @return
	 */
	public Map<String, C> getGSRows() {
		return getNonDeletedChildElements();  
	}
	
	/**
	 * get a {@link GSRow} from this {@link GSWorksheet} by row index
	 * @param rowIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public C getGSRow(int rowIndex) {
		if(rowIndex < 1)
		 throw new IllegalArgumentException("rowIndex");
		
		for(GSRow gsRow : ((GSWorksheet<GSRow>)this).getChildElements().values()){
			if(gsRow.isDeleteCandidate()){ 
				rowIndex ++; //see notes 
				continue;
			}	
			// say 4 elements are identified by 1, 2, 3, 4 in an initial list, 
			// element 2 is marked deleted; now non-deleted elements are 1, 3, 4. 
			// so to get 2nd element u have to get one that is marked with 2+1 = 3 ;)  
			
			if(gsRow.getRowIndex() == rowIndex){
				return (C) gsRow;
			}			
		}
		return null;
	}	
	
	/**
	 * get a {@link GSRow} by key from this {@link GSWorksheet}
	 * 
	 * @param key
	 * @return
	 */
	public C getGSRow(String key){
		return getChildElement(key);
	}

	/**
	 * get the sheet index of this {@link GSWorksheet} in the container {@link GSSpreadsheet}
	 * @return
	 */
	public int getSheetIndex() {
		return getElementListIndex();
	}	
	
	/**
	 * get a {@link GSCell} from this {@link GSWorksheet} by row and column index 
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSCell getGSCell(int rowIndex, int colIndex) {
		for(GSRow gsRow : ((GSWorksheet<GSRow>)this).getNonDeletedChildElements().values()){
			if(gsRow.getRowIndex() == rowIndex){
				return (GSCell) gsRow.getGSCell(colIndex);
			}	
		}
		return null;		
	}	
		
	/**
	 * return the name/title of the core {@link WorksheetEntry} wrapped by this
	 * {@link GSWorksheet}
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getName() {
		return ((BaseEntry<WorksheetEntry>) this.baseEntry).getTitle()
				.getPlainText();
	}
	
	
	/**
	 * add a new row for the worksheet using cell/value map
	 * 
	 * @param values: cell value for each cell
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public GSRow<GSCell> createNewRow(LinkedHashMap<String, String> values) throws IOException, ServiceException {
		
		int newRowIndex = this.getChildElements().size() + 1;		
		ListEntry newRow = new ListEntry();		
		GSRow<GSCell> newGSRow = new GSRow(newRow, newRowIndex, this);
		
		GSRow<GSCell> headeRow = ((GSWorksheet<GSRow>) this).getGSRow(1);
		if(headeRow == null)
			throw new MeshException("Header row not available...");
		//int col = 1; 	// entries in values make sure actual ordering in spreadsheet, 
						// so we can assume they are in a position according to column order 1, 2, 3....   	
		
		for (String key : values.keySet()) {
			int colIndex = headeRow.getGSCell(key).elementListIndex;
			newGSRow.createNewCell(colIndex, key, values.get(key));
		}				
		
		this.addChildElement(newGSRow.getElementId(), (C) newGSRow);
		return newGSRow;
	}
			
	
    /**
     * create a new blank row in the position rowindex
     * 
     * @param rowIndex
     * @return
     */
    @SuppressWarnings("unchecked")
	public GSRow<GSCell> createNewRow(int rowIndex){
		if(rowIndex < 1)
			 throw new IllegalArgumentException("rowIndex");
    	
        ListEntry listEntry = new ListEntry();
		GSRow<GSCell> row = new GSRow(listEntry, rowIndex, this);
		
		//create empty cells using tags from header cells and add to this row
		GSRow<GSCell> headerRow = (GSRow<GSCell>) this.getGSRow(1);
		if(headerRow != null && headerRow.childElements.size() > 0){
			for (GSCell headerCell : headerRow.childElements.values()) {
				row.createNewCell(headerCell.getElementListIndex(),
						headerCell.getColumnTag(), "");
			}
		}
		this.addChildElement(row.getElementId(), (C) row);
        return row;
    }	  
    
	@SuppressWarnings("unchecked")
	@Override
	public void refreshMeFromFeed() throws IOException, ServiceException{
		if(this.isDirty()){
			
			List<ListEntry> rowList = GoogleSpreadsheetUtils
							.getAllRows((WorksheetEntry) this.baseEntry); // 1 http
																			// request
			List<CellEntry> cellList = GoogleSpreadsheetUtils
							.getAllCells((WorksheetEntry) this.baseEntry); // 1 http
																			// request
			
			if( rowList.size() > 0 && cellList.size() > 0 ){
				//get the header row and put it as the 1st row in the rowlist
				this.childElements.clear();
				GSRow<GSCell> gsListHeaderEntry = new GSRow(
						new ListEntry(), 1, this);
				gsListHeaderEntry.populateClildWithHeaderTag(cellList, (WorksheetEntry)this.baseEntry);				
				((GSWorksheet<GSRow>)this).getChildElements().put(gsListHeaderEntry.getElementId(), gsListHeaderEntry);			
				
				for (ListEntry row : rowList){
					//create a custom row object and populate its child
					GSRow<GSCell> gsListEntry = new GSRow(
							row, rowList.indexOf(row) + 2, this); //+2 because #1 position is occupied by list header entry 
					gsListEntry.populateClildWithHeaderTag(cellList, (WorksheetEntry)this.baseEntry);				
					
					//add a row to the custom worksheet object
					((GSWorksheet<GSRow>)this).getChildElements().put(gsListEntry.getElementId(), gsListEntry);
					//TODO: right now index has been used as key; mjrow.getId() could have used, this need to review
				}
			} // if
			
			
			this.dirty = false;
			this.deleteCandidate = false;
		}
 			
	}


}
