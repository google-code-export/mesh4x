package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.*;

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
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet().getSpreadsheet();
		// GoogleSpreadsheetUtils.flush(sse, spreadsheetFileId);
		Assert.assertNotNull(sse);
	}
	
	@Deprecated
	public void shouldGetRow(){
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet().getSpreadsheet();		
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
	
	@Deprecated
	public void shouldGetMJCell() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet().getSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		int cellRowIndex = 2;
		int cellColIndex = 2;
		
		GSCell gsCell = GoogleSpreadsheetUtils.getGSCell(gss.getService(),
				wse, cellRowIndex, cellColIndex);
		
		Assert.assertNotNull(gsCell);
		Assert.assertEquals(cellColIndex, gsCell.getCellEntry().getCell().getCol());
		Assert.assertEquals(cellRowIndex, gsCell.getCellEntry().getCell().getRow());

		Assert.assertEquals("Sharif", gsCell.getCellEntry().getCell().getValue());
		
		
		Assert.assertNotNull(gsCell.getParentRow());		
		
		Assert.assertEquals(cellRowIndex - 1, gsCell.getParentRow().getRowIndex());
		Assert.assertEquals(4, gsCell.getParentRow().getGSCells().size());
		
		//this cell should be the same as the one contained in the child cell list of its parent at position colIndex  
		Assert.assertEquals(gsCell.getId(), ((GSCell) gsCell.getParentRow().getGSCell(cellColIndex)).getId());
		
		//get the parent row, pick 2 different child/cell, parent row ID of those two child should be same 
		Assert.assertEquals(((GSCell) gsCell.getParentRow().getGSCell(cellColIndex + 1)).getParentRow().getId(),
				((GSCell) gsCell.getParentRow().getGSCell(cellColIndex - 1)).getParentRow().getId());
		
		//get the parent row, pick 2 different child/cell, parent row index of those two child should be same 
		Assert.assertEquals(((GSCell) gsCell.getParentRow().getGSCell(cellColIndex+1)).getParentRow().getRowIndex(),
				((GSCell) gsCell.getParentRow().getGSCell(cellColIndex - 1)).getParentRow().getRowIndex());		
	}		

	@Deprecated
	public void shouldGetMJRow() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet().getSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		int rowIndex = 1;
		
		GSRow gsRow = GoogleSpreadsheetUtils.getGSRow(gss.getService(),
				wse, rowIndex);
		
		Assert.assertNotNull(gsRow);
		Assert.assertEquals(rowIndex, gsRow.getRowIndex());
		
		Assert.assertNotNull(gsRow.getGSCells());
		Assert.assertTrue(gsRow.getGSCells().size()>0);
		
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

	//@Test OK
	public void shouldBatchUpdateCells() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);		
		Assert.assertEquals(ws.getId(), ss.getChildElement("1").getId()); //get the first sheet from another method and check if they are equal
		
		GSCell gsCell_1 = ws.getGSCell(2, 1);		
		gsCell_1.updateCellValue("GSL-A219");		
		GoogleSpreadsheetUtils.processDirtyElementForBatchUpdate(gsCell_1);
		
		GSCell gsCell_2 = ws.getGSCell(3, 1);
		gsCell_2.updateCellValue("GSL-A218");		
		GoogleSpreadsheetUtils.processDirtyElementForBatchUpdate(gsCell_2);
		
		GoogleSpreadsheetUtils.flush(gss.getService(), ss);		
	}		
	

	//@Test OK 
	public void shouldBatchUpdateRows() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);		
		Assert.assertEquals(ws.getId(), ss.getChildElement("1").getId()); //get the first sheet from another method and check if they are equal
		
		GSRow gsRow_1 = ws.getGSRow(2);
		
		gsRow_1.updateCellValue("GSL-A21", 1);
		GoogleSpreadsheetUtils.processDirtyElementForBatchUpdate(gsRow_1);
		
		GSRow gsRow_2 = ws.getGSRow(3);
		gsRow_2.updateCellValue("GSL-A21", 1);
		GoogleSpreadsheetUtils.processDirtyElementForBatchUpdate(gsRow_2);
		
		GoogleSpreadsheetUtils.flush(gss.getService(), ss);		
	}		


	//@Test  OK   
	public void shouldAddNweRow() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);		
		Assert.assertEquals(ws.getId(), ss.getChildElement("1").getId()); //get the first sheet from another method and check if they are equal
		
		String [] values = {"new","new","new","new"};
		GSRow<GSCell> newGSRow = ws.createNewRow(values);
		ws.addNewRow(newGSRow);
		
		GoogleSpreadsheetUtils.processDirtyElementForBatchUpdate(newGSRow);
		
		GoogleSpreadsheetUtils.flush(gss.getService(), ss);		
	}		
	
	
	//@Test  OK   
	public void shouldDeleteRow() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);		
		Assert.assertEquals(ws.getId(), ss.getChildElement("1").getId()); //get the first sheet from another method and check if they are equal

		ws.deleteChildElement("7");		
		
		GoogleSpreadsheetUtils.flush(gss.getService(), ss);		
	}		
	
	
	
	@Test
	public void shouldRefreshRow() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);		
		Assert.assertEquals(ws.getId(), ss.getChildElement("1").getId()); //get the first sheet from another method and check if they are equal		
		
		//manually update a cell 2, 1 to specific value
		//"GSL-A219"
		
		
		//get a row that is going to be changed by batch update
		GSRow<GSCell> gsRow_1 = ws.getGSRow(2);
		
		gsRow_1.updateCellValue("GSL-A21", 1);
		GoogleSpreadsheetUtils.processDirtyElementForBatchUpdate(gsRow_1);
		
		GoogleSpreadsheetUtils.flush(gss.getService(), ss);
		
		
		Assert.assertTrue( gsRow_1.isDirty() );	//row is dirty	
		GSCell gsCell = gsRow_1.getGSCell(1);		
		Assert.assertTrue( gsCell.isDirty() ); //cell is dirty
		
		Assert.assertEquals(gsCell.getCellEntry().getCell().getValue(), "GSL-A219"); //content is dirty
		
		gsRow_1.refreshMe(); //reload data from feed

		Assert.assertFalse( gsRow_1.isDirty() );		
		GSCell gsCellAfterRefresh = gsRow_1.getGSCell(1);		
		Assert.assertFalse( gsCellAfterRefresh .isDirty() );
		
		Assert.assertEquals(gsCell.getCellEntry().getCell().getValue(), "GSL-A21"); //content should be updated
		
	}	
/*	
	@Test //TODO: need to resolve phantom row issue
	public void shouldBatchDeleteRow() throws IOException, ServiceException {
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet();
		WorksheetEntry wse = GoogleSpreadsheetUtils.getWorksheet(gss
				.getService(), sse, 0);
		Assert.assertNotNull(wse);
		
		GSRow gsRow_4 = GoogleSpreadsheetUtils.getGSRow(gss.getService(),
				wse, 4);
		for(IGSElement gsCell: gsRow_4.getGsCells()) {
			((GSCell)gsCell).getCellEntry().changeInputValueLocal("");
			gsCell.setDirty();
		
			BatchUtils.setBatchId(((GSCell)gsCell).getCellEntry(), ((GSCell)gsCell).getCellEntry().getId());
			BatchUtils.setBatchOperationType(((GSCell)gsCell).getCellEntry(),
					BatchOperationType.UPDATE);
		}
		
		gsRow_4.setDirty();
		gss.addEntryToUpdate(gsRow_4);
	
		GoogleSpreadsheetUtils.flush(gss.getService(), wse);
	}		
	
*/	
	private GSSpreadsheet<GSWorksheet> getSampleGoogleSpreadsheet() {
		return gss.getGSSpreadsheet();
	}

}
