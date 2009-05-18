package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

/**
 * Represent the google spreadsheet  to the Mesh4j.
 * @author Raju
 * @version 1.0,29/4/2009
 */
public interface IGoogleSpreadSheet {
	
	public GSSpreadsheet<GSWorksheet> getGSSpreadsheet();
	public SpreadsheetService getSpreadsheetService();
	public DocsService getDocsService();
	public GSWorksheet getGSWorksheet(String sheetName);
	public GSWorksheet getGSWorksheet(int sheetIndex);
	public void setDirty();
	public void flush();
	public void refresh();
}
