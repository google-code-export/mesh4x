package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

/**
 * Represent the google spreadsheet  to the Mesh4j.
 * @author Raju
 * @version 1.0,29/4/2009
 */
public interface IGoogleSpreadSheet {
	
	public SpreadsheetService getService();
	public void setDirty();
	public void flush(WorksheetEntry worksheet);
}
