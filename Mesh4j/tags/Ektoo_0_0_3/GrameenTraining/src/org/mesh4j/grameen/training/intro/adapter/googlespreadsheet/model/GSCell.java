package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.validations.Guard;

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
	
	//CONSTANTS 
	public static final int CELL_TYPE_TEXT 		= 0;
	public static final int CELL_TYPE_LONG 		= 1;
	public static final int CELL_TYPE_DOUBLE 	= 2;
	public static final int CELL_TYPE_DATE 		= 3;
    public static final int CELL_TYPE_BOOLEAN 	= 4;
    public static final int CELL_TYPE_BLANK 	= 5;
	public static final int CELL_TYPE_UNKNOWN 	= -1;
	
	public static final int BOOLEAN_CELL_VALE_AS_YES_NO = 10;
	public static final int BOOLEAN_CELL_VALE_AS_TRUE_FALSE = 11;

	// MODEL VARIABLES	
	private String tmpCellValue;
	private String columnTag;
	
	// BUSINESS METHODS	
	public GSCell(CellEntry cellEntry, GSRow<GSCell> parentRow, String columnTag){
		
		Guard.argumentNotNull(cellEntry, "cellEntry");
		Guard.argumentNotNull(parentRow, "parentRow");
		Guard.argumentNotNull(columnTag, "columnTag");
		
		this.baseEntry = cellEntry;
		this.parentElement = parentRow;
		this.childElements = null;
		this.columnTag = columnTag;
		
		if(cellEntry.getCell() != null)
			this.elementListIndex = cellEntry.getCell().getCol();
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

	
	public String getColumnTag() {
		return columnTag;
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


	@Override
	public void refreshMeFromFeed(){
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
	
	/**
	 * return the cell value as appropriate object type with respect to cell type
	 * 
	 * @return
	 */
	public Object getCellValueAsType(){
		int cellType = this.getCellType();
		String cellValue = getCellValue();
		switch (cellType) {
			case CELL_TYPE_BOOLEAN:
				if (cellValue.equalsIgnoreCase("true")
						|| cellValue.equalsIgnoreCase("yes"))
					return Boolean.valueOf(true);
				else
					return Boolean.valueOf(false);
			case CELL_TYPE_DATE:
				return new Date(Long.parseLong(cellValue));
			case CELL_TYPE_LONG:
				return Long.valueOf(cellValue);
			case CELL_TYPE_DOUBLE:
				return Double.valueOf(cellValue);
			default:
				return cellValue; //this including text & unknown type 
		}
	}
	
	
	/**
	 * return the cell value as appropriate object type with respect to cell type
	 * 
	 * @return
	 */
	public void setCellValueAsType(Object cellValue){
		int cellType = this.getCellType();		
		setCellValueAsType(cellValue, cellType);
	}
	
	public void setCellValueAsType(Object cellValue, int cellType) {

		switch (cellType) {
		case CELL_TYPE_BOOLEAN:
			if (getBooleanDataFormat() == BOOLEAN_CELL_VALE_AS_TRUE_FALSE){
				updateCellValue(cellValue.toString());
			}
			else{
				if((Boolean)cellValue)
					updateCellValue("Yes");
				else
					updateCellValue("No");
			}	
			break;
		case CELL_TYPE_DATE:		
			SimpleDateFormat df = new SimpleDateFormat(
					GoogleSpreadSheetContentAdapter.G_SPREADSHEET_DATE_FORMAT);
			updateCellValue(df.format(cellValue));
			break;
		case CELL_TYPE_LONG:			
			updateCellValue(cellValue.toString());
			break;
		case CELL_TYPE_DOUBLE:			
			updateCellValue(cellValue.toString());
			break;
		default:		
			updateCellValue(String.valueOf(cellValue));
	}		
	}
	
	private int getBooleanDataFormat() {
		// TODO: need to come up with a solution for get/set the format(yes/no
		// or true/false) for boolean type data in spreadsheet
		return BOOLEAN_CELL_VALE_AS_TRUE_FALSE;
	}

	/**
	 * return the content type of a cell
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public int getCellType() {
		Double cellDoubleValue = ((CellEntry) this.baseEntry).getCell()
				.getDoubleValue();
		String cellStringValue = ((CellEntry) this.baseEntry).getCell()
				.getValue();

		if (((CellEntry) this.baseEntry).getCell().getValue() == null)
			return CELL_TYPE_BLANK;
		else if (Double.isNaN(cellDoubleValue)) {
			if (cellStringValue.equalsIgnoreCase("true")
					|| cellStringValue.equalsIgnoreCase("false")
					|| cellStringValue.equalsIgnoreCase("yes")
					|| cellStringValue.equalsIgnoreCase("no"))
				return CELL_TYPE_BOOLEAN;
			else
				return CELL_TYPE_TEXT;

		} else {

			try {
				Double.parseDouble(cellStringValue);
				try{
					 Long.parseLong(cellStringValue);
					 return CELL_TYPE_LONG;
				}catch (Exception e){
					 return CELL_TYPE_DOUBLE;
				}
			} catch (Exception e) {
				try {
					// if its a valid date formated, parsing will be ok
					Date.parse(cellStringValue);

					// the minimum date google spreadsheet supports is 1/1/0100,
					// which is evaluated as double value -657436.0
					if (cellDoubleValue >= -657436.0)
						return CELL_TYPE_DATE;
					else
						return CELL_TYPE_TEXT;
				} catch (Exception ee) {
					return CELL_TYPE_UNKNOWN;
				}
			}
		}
	}

}
