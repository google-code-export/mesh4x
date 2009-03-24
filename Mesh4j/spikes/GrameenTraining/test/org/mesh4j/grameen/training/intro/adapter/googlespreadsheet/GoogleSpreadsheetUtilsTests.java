package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.MJCellEntry;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.MJListEntry;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.test.utils.TestHelper;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class GoogleSpreadsheetUtilsTests {
	
	private GoogleSpreadsheet gss;
	
	@Before
	public void setUp() throws Exception {
		String spreadsheetFileId = "pvQrTFmc5F8tXD89WRNiBVw";
		
		String username = "sharif.uddin.ku@gmail.com";
		String password = "sharif123";
		this.gss = new GoogleSpreadsheet(spreadsheetFileId,
				username, password);
	}
	
	//@Test
	public void shouldLoadSpreadsheetWhenFileExist()
			throws FileNotFoundException, IOException {		
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		// GoogleSpreadsheetUtils.flush(sse, spreadsheetFileId);
		Assert.assertNotNull(sse);
	}

	
	public void shouldGetRow(){
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();		
		WorksheetEntry wse = null;
		ListEntry row = null;

		try {
			wse = GoogleSpreadsheetUtils.getWorksheet(gss.getService(), sse, 0);
			row = GoogleSpreadsheetUtils.getRow(gss.getService(), wse, "firstname", "Sharif");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotNull(row);
	}
	
	@Test
	public void shouldGetMJCell() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		int rowIndex = 2;
		int colIndex = 2;
		
		MJCellEntry mjCell = GoogleSpreadsheetUtils.getMJCell(gss.getService(),
				wse, rowIndex, colIndex);
		
		Assert.assertNotNull(mjCell);
		Assert.assertEquals(colIndex, mjCell.getCellEntry().getCell().getCol());
		Assert.assertEquals(rowIndex, mjCell.getCellEntry().getCell().getRow());

		Assert.assertEquals("Sharif", mjCell.getCellEntry().getCell().getValue());
		
		
		Assert.assertNotNull(mjCell.getParentRow());		
		
		Assert.assertEquals(rowIndex, mjCell.getParentRow().getRowIndex());
		Assert.assertEquals(4, mjCell.getParentRow().getMjCells().size());
		
		//this cell should be the same as the one contained in the child cell list of its parent at position colIndex  
		Assert.assertEquals(mjCell.getId(), mjCell.getParentRow().getMjCell(colIndex).getId());
		
		//get the parent row, pick 2 different child/cell, parent row ID of those two child should be same 
		Assert.assertEquals(mjCell.getParentRow().getMjCell(colIndex+1).getParentRow().getId(),
				mjCell.getParentRow().getMjCell(colIndex - 1).getParentRow().getId());
		
		//get the parent row, pick 2 different child/cell, parent row index of those two child should be same 
		Assert.assertEquals(mjCell.getParentRow().getMjCell(colIndex+1).getParentRow().getRowIndex(),
				mjCell.getParentRow().getMjCell(colIndex - 1).getParentRow().getRowIndex());		
	}		

	@Test
	public void shouldGetMJRow() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		int rowIndex = 2;
		
		MJListEntry mjRow = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, rowIndex);
		
		Assert.assertNotNull(mjRow);
		Assert.assertEquals(rowIndex, mjRow.getRowIndex());
		
		Assert.assertNotNull(mjRow.getMjCells());
		Assert.assertTrue(mjRow.getMjCells().size()>0);
		
		/*//row/list's rowIndex will be 1 less than cells rowIndex!
		Assert.assertEquals(rowIndex - 1, mjCell.getParentRow().getRowIndex());
		Assert.assertEquals(4, mjCell.getParentRow().getMjCells().size());
		
		//this cell should be the same as the one contained in the child cell list of its parent at position colIndex  
		Assert.assertEquals(mjCell.getId(), mjCell.getParentRow().getMjCell(colIndex).getId());
		
		//get the parent row, pick 2 different child/cell, parent row ID of those two child should be same 
		Assert.assertEquals(mjCell.getParentRow().getMjCell(colIndex+1).getParentRow().getId(),
				mjCell.getParentRow().getMjCell(colIndex - 1).getParentRow().getId());
		
		//get the parent row, pick 2 different child/cell, parent row index of those two child should be same 
		Assert.assertEquals(mjCell.getParentRow().getMjCell(colIndex+1).getParentRow().getRowIndex(),
				mjCell.getParentRow().getMjCell(colIndex - 1).getParentRow().getRowIndex());		*/
	}		

	
	
	private SpreadsheetEntry getSampleGoogleSpreadsheet() {
		return gss.getSpreadsheet();
	}

}
