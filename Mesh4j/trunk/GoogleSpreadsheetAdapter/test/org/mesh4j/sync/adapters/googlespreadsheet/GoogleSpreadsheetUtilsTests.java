package org.mesh4j.sync.adapters.googlespreadsheet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;

public class GoogleSpreadsheetUtilsTests {
	
	private static SpreadsheetService service;
	private static DocsService docService;
	private static FeedURLFactory factory;

	String testCellValue = "New Cell";

	
	@BeforeClass
	public static void setUp() throws Exception {
		String username = "gspreadsheet.test@gmail.com";
		String password = "java123456";
		service = GoogleSpreadsheetUtils.getSpreadsheetService(username, password);
		docService = GoogleSpreadsheetUtils.getDocService(username, password);
		factory = FeedURLFactory.getDefault();		
	}

	public void shouldLoadSpreadsheetWhenFileExist()
			throws FileNotFoundException, IOException {		
		SpreadsheetEntry sse = getSampleGoogleSpreadsheet().getSpreadsheet();
		// GoogleSpreadsheetUtils.flush(sse, spreadsheetFileId);
		Assert.assertNotNull(sse);
	}
	
	@Test
	public void shouldCreatNewSpreadsheet() throws IOException,
			ServiceException {
		GSSpreadsheet<?> spreadsheet = null;
		String newSpreadsheetName = "new test spreadsheet";
		
		spreadsheet = GoogleSpreadsheetUtils.getOrCreateGSSpreadsheetIfAbsent(
				factory, service, docService, newSpreadsheetName, TestHelper.baseDirectoryForTest());

		Assert.assertNotNull(spreadsheet);
		Assert.assertNotNull(spreadsheet.getBaseEntry());

		System.out.println(spreadsheet.getBaseEntry().getTitle().getPlainText()
				+ " : "
				+ spreadsheet.getId().substring(spreadsheet.getId().lastIndexOf("/") + 1));
		
		Assert.assertNotNull(spreadsheet.getBaseEntry().getId());
		Assert.assertEquals(newSpreadsheetName, spreadsheet.getBaseEntry().getTitle()
				.getPlainText());
		
	}
		
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldBatchUpdateCells() throws IOException, ServiceException {
		
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);	
		
		GSRow<GSCell> newGSRow = addTestRow(ws);
		
		GoogleSpreadsheetUtils.flush(service, ss);			
		//a new test row added
		
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); //get the first sheet	
		
		GSRow<GSCell> gsRow = ws.getGSRow(newGSRow.getElementListIndex());		
		
		String updatedCellValue1 = "Updated Cell 1";
		String updatedCellValue2 = "Updated Cell 2";
		
		GSCell gsCellToUpdate1 = gsRow.getGSCell(1);		
		gsCellToUpdate1.updateCellValue(updatedCellValue1);		
		
		GSCell gsCellToUpdate2 = gsRow.getGSCell(2);
		gsCellToUpdate2.updateCellValue(updatedCellValue2);		
		
		GoogleSpreadsheetUtils.flush(service, ss);			
		
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); //get the first sheet	
		
		gsRow = ws.getGSRow(newGSRow.getElementListIndex());					
		GSCell gsCell1 = gsRow.getGSCell(1);			
		GSCell gsCell2 = gsRow.getGSCell(2);
		
		Assert.assertNotNull(gsCell1);
		Assert.assertNotNull(gsCell2);
		Assert.assertEquals(updatedCellValue1, gsCell1.getCellValue());
		Assert.assertEquals(updatedCellValue2, gsCell2.getCellValue());
		
		//test success, so remove the test row from the sheet
		ws.deleteChildElement(String.valueOf(newGSRow.getElementListIndex()));
		GoogleSpreadsheetUtils.flush(service, ss);	
	}			

	@Test 
	@SuppressWarnings("unchecked")
	public void shouldBatchUpdateRows() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);	
		
		GSRow<GSCell> newGSRow1 = addTestRow(ws);
		GSRow<GSCell> newGSRow2 = addTestRow(ws);	
		
		GoogleSpreadsheetUtils.flush(service, ss);			
		//a new test row added
		
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); //get the first sheet	
		
		GSRow<GSCell> headerRow = ws.getGSRow(1);
		GSRow<GSCell> gsRowToUpdate1 = ws.getGSRow(newGSRow1.getElementListIndex());	
		GSRow<GSCell> gsRowToUpdate2 = ws.getGSRow(newGSRow2.getElementListIndex());	
		
		String keyToCellForUpdate = headerRow.getGSCells().keySet().iterator().next();
		
		String updatedCellValue1 = "Updated Cell 1";
		String updatedCellValue2 = "Updated Cell 2";
		
		gsRowToUpdate1.updateCellValue(updatedCellValue1, keyToCellForUpdate);
		
		gsRowToUpdate2.updateCellValue(updatedCellValue2, keyToCellForUpdate);
		
		GoogleSpreadsheetUtils.flush(service, ss);	
		
		ss=null;ws=null;
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); //get the first sheet
		
		GSRow<GSCell> gsRowUpdated1 = ws.getGSRow(newGSRow1.getElementListIndex());	
		GSRow<GSCell> gsRowUpdated2 = ws.getGSRow(newGSRow2.getElementListIndex());	
		
		Assert.assertNotNull(gsRowUpdated1);
		Assert.assertNotNull(gsRowUpdated2);
		
		Assert.assertEquals(updatedCellValue1, gsRowUpdated1.getGSCell(keyToCellForUpdate).getCellValue());
		Assert.assertEquals(updatedCellValue2, gsRowUpdated2.getGSCell(keyToCellForUpdate).getCellValue());
		
		
		//test success, now remove the test rows from the sheet
		ws.deleteChildElement(String.valueOf(gsRowUpdated1.getElementListIndex()));
		ws.deleteChildElement(String.valueOf(gsRowUpdated2.getElementListIndex()));
		GoogleSpreadsheetUtils.flush(service, ss);			
	}		


	@SuppressWarnings("unchecked")
	@Test   
	public void shouldAddNweRow() throws IOException, ServiceException {
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);	
		
		int rowCountBeforeAdd = ws.getChildElements().size();
			
		GSRow<GSCell> newGSRow = addTestRow(ws);		
		
		//check row count
		Assert.assertEquals(rowCountBeforeAdd + 1, ws.getChildElements().size());

		GoogleSpreadsheetUtils.flush(service, ss);	

		ss=null;ws=null;
		
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertEquals(rowCountBeforeAdd + 1, ws.getChildElements().size());
		
		GSRow<GSCell> gsRow = ws.getGSRow(newGSRow.getElementListIndex());
		Assert.assertNotNull(gsRow);
		
		for(GSCell cell : gsRow.getChildElements().values()){
			Assert.assertEquals(testCellValue, cell.getCellValue());
		}
		
		//test success, so remove the test row from the sheet
		ws.deleteChildElement(String.valueOf(newGSRow.getElementListIndex()));
		GoogleSpreadsheetUtils.flush(service, ss);	
	}		
	
		
	@SuppressWarnings("unchecked")
	@Test   
	public void shouldDeleteRow() throws IOException, ServiceException {
		
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		GSWorksheet<GSRow> ws = ss.getGSWorksheet(1); //get the first sheet
		
		Assert.assertNotNull(ws);	
		
		int rowCountBeforeAdd = ws.getChildElements().size();
		
		GSRow<GSCell> newGSRow = addTestRow(ws);		
		
		GoogleSpreadsheetUtils.flush(service, ss);			
		
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); //get the first sheet	
		
		int rowCountBeforeDelete = ws.getChildElements().size();
		Assert.assertEquals(rowCountBeforeDelete - 1 , rowCountBeforeAdd);
		
		//remove the row from the sheet
		ws.deleteChildElement(String.valueOf(newGSRow.getElementListIndex()));
		
		GoogleSpreadsheetUtils.flush(service, ss);			
			
		ss = getSampleGoogleSpreadsheet();
		ws = ss.getGSWorksheet(1); 
		
		Assert.assertEquals(rowCountBeforeDelete - 1, ws.getChildElements().size());
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void ShouldCreateSyncSheetIfAbsent(){
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		String syncSheetName = "sync_info";
		GSWorksheet syncSheet = ss.getGSWorksheetBySheetName(syncSheetName);
		
		if(syncSheet != null){
			ss.deleteChildElement(String.valueOf(syncSheet.getElementListIndex()));	
			GoogleSpreadsheetUtils.flush(service, ss);
		}	
		
		ss = null; syncSheet = null;
		ss = getSampleGoogleSpreadsheet();
		syncSheet = ss.getGSWorksheetBySheetName(syncSheetName);
		
		Assert.assertNull(syncSheet);
		
		GSWorksheet newSyncSheet = GoogleSpreadsheetUtils.getOrCreateSyncSheetIfAbsent(ss, syncSheetName);	
		
		Assert.assertNotNull(newSyncSheet);
		
		GoogleSpreadsheetUtils.flush(service, ss);
		
		ss = null; syncSheet = null;
		ss = getSampleGoogleSpreadsheet();
		syncSheet = ss.getGSWorksheetBySheetName(syncSheetName);		
		Assert.assertNotNull(syncSheet);
		
		Assert.assertEquals(newSyncSheet.getId(), syncSheet.getId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ShouldCreateContentSheetIfAbsent(){
		GSSpreadsheet<GSWorksheet> ss = getSampleGoogleSpreadsheet();
		String contentSheetName = SHEET_NAME;
		GSWorksheet contentSheet = ss.getGSWorksheetBySheetName(contentSheetName);
		
		if(contentSheet != null){
			ss.deleteChildElement(String.valueOf(contentSheet.getElementListIndex()));	
			GoogleSpreadsheetUtils.flush(service, ss);
			
			ss = null; contentSheet = null;
			ss = getSampleGoogleSpreadsheet();
			contentSheet = ss.getGSWorksheetBySheetName(contentSheetName);		
		}	
		
		Assert.assertNull(contentSheet);	
		
		GSWorksheet newSyncSheet = GoogleSpreadsheetUtils.getOrCreateContentSheetIfAbsent(ss, createSampleGoogleSpreadsheetToRDFMapping());	
		
		Assert.assertNotNull(newSyncSheet);
		
		GoogleSpreadsheetUtils.flush(service, ss);
		
		ss = null; contentSheet = null;
		ss = getSampleGoogleSpreadsheet();
		contentSheet = ss.getGSWorksheetBySheetName(contentSheetName);		
		Assert.assertNotNull(contentSheet);
		
		Assert.assertEquals(newSyncSheet.getId(), contentSheet.getId());
	}	
	
	private static final String SHEET_NAME = "Person";
	private static final String COLUMN_DATE_ONSET = "DateOnset";
	private static final String COLUMN_ILL = "Ill";
	private static final String COLUMN_SEX = "Sex";
	private static final String COLUMN_AGE = "Age";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_ID = "Id";


	private GoogleSpreadsheetToRDFMapping createSampleGoogleSpreadsheetToRDFMapping() {

		String sheetName = SHEET_NAME;

		RDFSchema schema = new RDFSchema(sheetName,
				"http://mesh4x/googlespreadsheet/" + sheetName + "#", sheetName);
		schema.addStringProperty(COLUMN_ID, "id", "en");
		schema.addStringProperty(COLUMN_NAME, "name", "en");
		schema.addIntegerProperty(COLUMN_AGE, "age", "en");
		schema.addStringProperty(COLUMN_SEX, "sex", "en");
		schema.addBooleanProperty(COLUMN_ILL, "ill", "en");
		schema.addDateTimeProperty(COLUMN_DATE_ONSET, "dateOnset", "en");
		schema.setIdentifiablePropertyName(COLUMN_ID);
		
		RDFInstance rdfInstance = schema.createNewInstance("uri:urn:GSL-A219");

		long millis = System.currentTimeMillis();
		String name = "Name: " + millis;
		String code = "GSL-A219";
		Long age = 1l;
		String sex = "sex: " + millis;
		Boolean ill = true;
		Date dateOnset = new Date();

		rdfInstance.setProperty(COLUMN_ID, code);
		rdfInstance.setProperty(COLUMN_NAME, name);
		rdfInstance.setProperty(COLUMN_AGE, age);
		rdfInstance.setProperty(COLUMN_SEX, sex);
		rdfInstance.setProperty(COLUMN_ILL, ill);
		rdfInstance.setProperty(COLUMN_DATE_ONSET, dateOnset);

		return new GoogleSpreadsheetToRDFMapping(schema);
	}	
	
	@SuppressWarnings("unchecked")
	private GSRow<GSCell> addTestRow(GSWorksheet<GSRow> ws) throws IOException,
			ServiceException {
		GSRow<GSCell> headerRow = ws.getGSRow(1);		
		Assert.assertNotNull("No header row available in the sheet", headerRow);	
		
		//add a new row 
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		for(String key : headerRow.getChildElements().keySet()){
			values.put( key, testCellValue);
		}
		
		return ws.createNewRow(values);
	}		
	
	@SuppressWarnings("unchecked")
	private GSSpreadsheet getSampleGoogleSpreadsheet() {
		//String spreadsheetFileId = "pvQrTFmc5F8tXD89WRNiBVw";
		String spreadsheetFileName = "gsl employee";
	
		try {
		
			return GoogleSpreadsheetUtils.getGSSpreadsheet(
					factory, service, spreadsheetFileName);
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
