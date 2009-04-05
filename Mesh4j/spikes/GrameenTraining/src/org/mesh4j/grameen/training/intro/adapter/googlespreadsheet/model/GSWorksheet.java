package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class GSWorksheet<C> extends GSBaseElement<C> {

	// MODEL VARIABLES
	//all moved to base class
	
	// BUSINESS METHODS	
	@Deprecated
	public GSWorksheet(WorksheetEntry worksheet, int sheetIndex) {
		super();
		this.baseEntry = worksheet;
		this.elementListIndex = sheetIndex;
		this.childElements = new LinkedHashMap<String, C>();
	}
	
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
		return getChildElements();  
	}
	
	/**
	 * get a {@link GSRow} from this {@link GSWorksheet} by row index
	 * @param rowIndex
	 * @return
	 */
	public C getGSRow(int rowIndex) {
		return getChildElement(Integer.toString(rowIndex));
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
		GSRow<GSCell> gsRow = (GSRow) getChildElement(Integer.toString(rowIndex));
		return (GSCell) gsRow.getChildElement(Integer
				.toString(colIndex));
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
	 * add a new row to the spreadsheet
	 * 
	 * @param rowToAdd
	 */
	public void addNewRow(GSRow rowToAdd) {		
		int newRowIndex = this.getChildElements().size() + 2;
		rowToAdd.elementListIndex= newRowIndex;
		((GSWorksheet<GSRow>) this).addChildElement(
				Integer.toString(rowToAdd.getRowIndex()), rowToAdd);
		this.setDirty();
	}	
	
	/**
	 * generate a new row for the worksheet 
	 * 
	 * @param values: cell value for each cell
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public GSRow<GSCell> createNewRow(String[] values) throws IOException, ServiceException {

		int noOfColumns = ((GSWorksheet<GSRow>) this).getGSRow(1)
				.getChildElements().size();

		if (values.length < noOfColumns) {
			// TODO: throw exception
			return null;
		}
		int newRowIndex = this.getChildElements().size() + 1;
		ListEntry newRow = new ListEntry();
		GSRow<GSCell> newGSRow = new GSRow(newRow, newRowIndex, this);
		
		for (int col = 1; col <= noOfColumns; col++) {
			
		    String batchId = "R" + newRowIndex + "C" + col;		    
		    URL entryUrl = new URL( ((WorksheetEntry)this.getBaseEntry()).getCellFeedUrl().toString() + "/" + batchId);
		    
		    CellEntry newCell = ((WorksheetEntry)this.getBaseEntry()).getService().getEntry(entryUrl, CellEntry.class);			
			//CellEntry newCell = new CellEntry(newRowIndex, col, values[col - 1]); //this is not supported for batch update :(

		    GSCell newGSCell = new GSCell(newCell, newGSRow);
			newGSCell.updateCellValue(values[col - 1]);
			newGSRow.addChildElement(Integer.toString(col), newGSCell);
		}

		return newGSRow;
	}

	@Override
	public void refreshMe() {
		// TODO Auto-generated method stub
		
	}


}
