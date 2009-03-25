package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.MJCellEntry;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.MJListEntry;


import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
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
	
	//@Test
	public void shouldGetMJCell() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		int cellRowIndex = 2;
		int cellColIndex = 2;
		
		MJCellEntry mjCell = GoogleSpreadsheetUtils.getMJCell(gss.getService(),
				wse, cellRowIndex, cellColIndex);
		
		Assert.assertNotNull(mjCell);
		Assert.assertEquals(cellColIndex, mjCell.getCellEntry().getCell().getCol());
		Assert.assertEquals(cellRowIndex, mjCell.getCellEntry().getCell().getRow());

		Assert.assertEquals("Sharif", mjCell.getCellEntry().getCell().getValue());
		
		
		Assert.assertNotNull(mjCell.getParentRow());		
		
		Assert.assertEquals(cellRowIndex - 1, mjCell.getParentRow().getRowIndex());
		Assert.assertEquals(4, mjCell.getParentRow().getMjCells().size());
		
		//this cell should be the same as the one contained in the child cell list of its parent at position colIndex  
		Assert.assertEquals(mjCell.getId(), mjCell.getParentRow().getMjCell(cellColIndex).getId());
		
		//get the parent row, pick 2 different child/cell, parent row ID of those two child should be same 
		Assert.assertEquals(mjCell.getParentRow().getMjCell(cellColIndex + 1).getParentRow().getId(),
				mjCell.getParentRow().getMjCell(cellColIndex - 1).getParentRow().getId());
		
		//get the parent row, pick 2 different child/cell, parent row index of those two child should be same 
		Assert.assertEquals(mjCell.getParentRow().getMjCell(cellColIndex+1).getParentRow().getRowIndex(),
				mjCell.getParentRow().getMjCell(cellColIndex - 1).getParentRow().getRowIndex());		
	}		

	//@Test
	public void shouldGetMJRow() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		int rowIndex = 1;
		
		MJListEntry mjRow = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, rowIndex);
		
		Assert.assertNotNull(mjRow);
		Assert.assertEquals(rowIndex, mjRow.getRowIndex());
		
		Assert.assertNotNull(mjRow.getMjCells());
		Assert.assertTrue(mjRow.getMjCells().size()>0);
		
/*		for (String tag : mjRow.getRowEntry().getCustomElements().getTags()) {
		      //out.print(entry.getCustomElements().getValue(tag)+"\t");
		      System.out.print(mjRow.getRowEntry().getCustomElements().getValue(tag)+" \t");
		}    		    
		System.out.println("");
*/		    
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

	@Test
	public void shouldBatchUpdateCells() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		MJCellEntry mjCell_1 = GoogleSpreadsheetUtils.getMJCell(gss.getService(),
				wse, 2, 1);
		mjCell_1.getCellEntry().changeInputValueLocal("GSL-A21");
		BatchUtils.setBatchId(mjCell_1.getCellEntry(), "1");
		BatchUtils.setBatchOperationType(mjCell_1.getCellEntry(), BatchOperationType.UPDATE);
		gss.addEntryToUpdate(mjCell_1);
		
		MJCellEntry mjCell_2 = GoogleSpreadsheetUtils.getMJCell(gss.getService(),
				wse, 3, 1);
		mjCell_2.getCellEntry().changeInputValueLocal("GSL-A21");
		BatchUtils.setBatchId(mjCell_2.getCellEntry(), "2");
		BatchUtils.setBatchOperationType(mjCell_2.getCellEntry(), BatchOperationType.UPDATE);
		gss.addEntryToUpdate(mjCell_2);
		
		MJCellEntry mjCell_3 = GoogleSpreadsheetUtils.getMJCell(gss.getService(),
				wse, 4, 1);
		mjCell_3.getCellEntry().changeInputValueLocal("GSL-A21");
		BatchUtils.setBatchId(mjCell_3.getCellEntry(), "3");
		BatchUtils.setBatchOperationType(mjCell_3.getCellEntry(), BatchOperationType.UPDATE);
		gss.addEntryToUpdate(mjCell_3);

		MJCellEntry mjCell_4 = GoogleSpreadsheetUtils.getMJCell(gss.getService(),
				wse, 5, 1);
		mjCell_4.getCellEntry().changeInputValueLocal("GSL-A21");
		BatchUtils.setBatchId(mjCell_4.getCellEntry(), "3");
		BatchUtils.setBatchOperationType(mjCell_4.getCellEntry(), BatchOperationType.UPDATE);
		gss.addEntryToUpdate(mjCell_4);
		
		GoogleSpreadsheetUtils.flush(gss.getService(), wse, gss.getBatchFeed());
		
	}		
	
	@Test //TODO: Need to review the code for dirty checking, avoid adding duplicate cell entry   
	public void shouldBatchUpdateRows() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		MJListEntry mjRow_1 = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, 1);

		mjRow_1.getMjCell(1).getCellEntry().changeInputValueLocal("GSL-A219");
		mjRow_1.getMjCell(1).setDirty();
		
		BatchUtils.setBatchId(mjRow_1.getMjCell(1).getCellEntry(), mjRow_1.getMjCell(1).getCellEntry().getId());
		BatchUtils.setBatchOperationType(mjRow_1.getMjCell(1).getCellEntry(),
				BatchOperationType.UPDATE);
		
		mjRow_1.setDirty();
		gss.addEntryToUpdate(mjRow_1);
	
		MJListEntry mjRow_2 = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, 2);
		mjRow_2.getMjCell(1).getCellEntry().changeInputValueLocal("GSL-A218");
		mjRow_2.getMjCell(1).setDirty();
		
		BatchUtils.setBatchId(mjRow_2.getMjCell(1).getCellEntry(), mjRow_2.getMjCell(1).getCellEntry().getId());
		BatchUtils.setBatchOperationType(mjRow_2.getMjCell(1).getCellEntry(),
				BatchOperationType.UPDATE);
		gss.addEntryToUpdate(mjRow_2);


		MJListEntry mjRow_3 = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, 3);
		mjRow_3.getMjCell(1).getCellEntry().changeInputValueLocal("GSL-A217");
		mjRow_3.getMjCell(1).setDirty();
		
		BatchUtils.setBatchId(mjRow_3.getMjCell(1).getCellEntry(), mjRow_3.getMjCell(1).getCellEntry().getId());
		BatchUtils.setBatchOperationType(mjRow_3.getMjCell(1).getCellEntry(),
				BatchOperationType.UPDATE);
		
		gss.addEntryToUpdate(mjRow_3);

		MJListEntry mjRow_4 = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, 4);
		mjRow_4.getMjCell(1).getCellEntry().changeInputValueLocal("GSL-A216");
		mjRow_4.getMjCell(1).setDirty();
		
		BatchUtils.setBatchId(mjRow_4.getMjCell(1).getCellEntry(), mjRow_4.getMjCell(1).getCellEntry().getId());
		BatchUtils.setBatchOperationType(mjRow_4.getMjCell(1).getCellEntry(),
				BatchOperationType.UPDATE);
		gss.addEntryToUpdate(mjRow_4);
		
		GoogleSpreadsheetUtils.flush(gss.getService(), wse, gss.getBatchFeed());
	}		

	
	@Test //TODO: need to resolve phantom row issue
	public void shouldBatchDeleteRow() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		MJListEntry mjRow_4 = GoogleSpreadsheetUtils.getMJRow(gss.getService(),
				wse, 4);
		for(MJCellEntry mjCell: mjRow_4.getMjCells()) {
			mjCell.getCellEntry().changeInputValueLocal("");
			mjCell.setDirty();
		
			BatchUtils.setBatchId(mjCell.getCellEntry(), mjCell.getCellEntry().getId());
			BatchUtils.setBatchOperationType(mjCell.getCellEntry(),
					BatchOperationType.UPDATE);
		}
		
		mjRow_4.setDirty();
		gss.addEntryToUpdate(mjRow_4);
	
		GoogleSpreadsheetUtils.flush(gss.getService(), wse, gss.getBatchFeed());
	}		
	
	
	private SpreadsheetEntry getSampleGoogleSpreadsheet() {
		return gss.getSpreadsheet();
	}

}
