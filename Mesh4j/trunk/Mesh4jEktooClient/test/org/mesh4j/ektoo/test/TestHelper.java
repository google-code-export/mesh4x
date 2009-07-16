package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.ServiceException;

public class TestHelper {

	public static String baseDirectoryForTest() {
		return "../../../tests/junit/";
	}

	public static String baseDirectoryRootForTest() {
		return "../../../tests/";
	}
	
	public static String fileName(String name) {
		return baseDirectoryForTest() + name;
	}

	public static File makeFileAndDeleteIfExists(String fileName) throws IOException{
		String myFileName = TestHelper.fileName(fileName);
		File file = new File(myFileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		return file;
	}

	public static void syncAndAssert(SyncEngine syncEngine) {
		List<Item> conflicts = syncEngine.synchronize();
	
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		if(syncEngine.getSource() instanceof ISyncAware){
			((ISyncAware) syncEngine.getSource()).beginSync();
		}
		if( syncEngine.getTarget() instanceof ISyncAware){
			((ISyncAware)  syncEngine.getTarget()).beginSync();
		}
		
		List<Item> sourceItems = syncEngine.getSource().getAll();
		List<Item> targetItems = syncEngine.getTarget().getAll();
		Assert.assertEquals(sourceItems.size(), targetItems.size());
		
		if(syncEngine.getTarget() instanceof ISupportMerge){
		
			for (Item targetItem : targetItems) {
				Item sourceItem = syncEngine.getSource().get(targetItem.getSyncId());
				boolean isOk = targetItem.equals(sourceItem);
				if(!isOk){
					System.out.println("Source: "+ sourceItem.getContent().getPayload().asXML());
					System.out.println("Target: "+ targetItem.getContent().getPayload().asXML());
					Assert.assertEquals(sourceItem.getContent().getPayload().asXML(), targetItem.getContent().getPayload().asXML());	
				}
				Assert.assertTrue(isOk);
			}
		} else {
			for (Item sourceItem : sourceItems) {
				Item targetItem = syncEngine.getTarget().get(sourceItem.getSyncId());
				boolean isOk = sourceItem.equals(targetItem);
				if(!isOk){
					System.out.println("Source: "+ sourceItem.getContent().getPayload().asXML());
					System.out.println("Target: "+ targetItem.getContent().getPayload().asXML());
					Assert.assertEquals(sourceItem.getContent().getPayload().asXML(), targetItem.getContent().getPayload().asXML());	
				}
				Assert.assertTrue(isOk);
			}
		}
	}

	public static Element makeElement(String xmlAsString) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlAsString);
		} catch (DocumentException e) {
			throw new IllegalArgumentException(e);
		}
		return doc.getRootElement();
	}

	public static Date now() {
		return new Date();
	}
	
	public static String createMsExcelFileForTest(File file, String excelTestWorksheetName, String idColumn, boolean addSampleRow){
		
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(excelTestWorksheetName);
		sheet.createRow(0);
		Row row = sheet.getRow(0);
		Cell cell;

		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,idColumn));
		
		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"name"));

		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"pass"));

		if(addSampleRow){
			row = sheet.createRow(1);	

			cell = row.createCell(0, Cell.CELL_TYPE_STRING);
			cell.setCellValue(getRichTextString(workbook,"G1"));

			cell = row.createCell(1, Cell.CELL_TYPE_STRING);
			cell.setCellValue(getRichTextString(workbook,"sharif"));

			cell = row.createCell(2, Cell.CELL_TYPE_STRING);
			cell.setCellValue(getRichTextString(workbook,"bangladesh"));
		}
		
		MsExcelUtils.flush(workbook, file.getAbsolutePath());

		return file.getAbsolutePath();
	}
	
	private static RichTextString getRichTextString(Workbook workbook,String value){
		return workbook.getCreationHelper().createRichTextString(value);
	}
	
	
	public static GoogleSpreadsheetToRDFMapping getSampleGoogleSpreadsheetToRDFMapping(
			String gssTestWorksheetName, String idColumn) {
		RDFSchema schema = new RDFSchema(gssTestWorksheetName, "http://localhost:8080/mesh4x/feeds/"
				+gssTestWorksheetName+"#", gssTestWorksheetName);
		schema.addStringProperty(idColumn, idColumn, IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("pass", "pass", IRDFSchema.DEFAULT_LANGUAGE);
		schema.setIdentifiablePropertyNames(Arrays.asList(new String[]{idColumn}));
		return new GoogleSpreadsheetToRDFMapping(schema);
	}
	
	public static GSRow<GSCell> addTestGssRow(GSWorksheet<GSRow> workSheet,
			String... fieldValues) throws IOException, ServiceException {
		GSRow<GSCell> headerRow = workSheet.getGSRow(1);
		Assert.assertNotNull("No header row available in the sheet", headerRow);	
		
		//add a new row 
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		for( Entry<String, GSCell> cellEntry : headerRow.getChildElements().entrySet()){
			values.put( cellEntry.getKey(), fieldValues[cellEntry.getValue().getElementListIndex()-1]);
		}
		return workSheet.createNewRow(values);
	}		
	
	public static void createMysqlTableForTest(String dbname, String username,
			String password, String dataTablename, String idColumn,	boolean addSampleData) {
		String url = "jdbc:mysql://localhost:3306/mysql";
		String drivername = "com.mysql.jdbc.Driver";
		
		String syncTableName = dataTablename+"_sync"; 
		
		//String dropDatabase = "DROP DATABASE IF EXISTS "+ dbname+"; ";
		String createDatabase =	"CREATE DATABASE "+dbname+"; ";
		String allowGrant = "GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP ON "+dbname+".* TO "+username+"@localhost IDENTIFIED BY '"+password+"';";
		
		String dropDataTable = "DROP TABLE IF EXISTS "+dbname+"."+dataTablename+"; ";
		String createDataTable=  "CREATE TABLE  "+dbname+"."+dataTablename+" ( " +
				""+idColumn+" varchar(50) NOT NULL, " +
				"name varchar(50) " + "default NULL," +
				"pass varchar(50) default NULL, PRIMARY KEY  USING BTREE ("+idColumn+") " +
				") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
	 
		String dropSyncTable = "DROP TABLE IF EXISTS "+dbname+"."+syncTableName+"; ";
		
		String createSyncTable = "CREATE TABLE  "+dbname+"."+syncTableName+" ( " +
				"  sync_id varchar(50) NOT NULL, " +
				"  entity_name varchar(50) default NULL, " +
				"  entity_id varchar(255) default NULL, " +
				"  entity_version varchar(50) default NULL, " +
				"  sync_data text, " +
				"  PRIMARY KEY  (sync_id) " +
				") ENGINE=InnoDB DEFAULT CHARSET=latin1;"; 
		
		String sampleData = "INSERT INTO "+dbname+"."+dataTablename+" VALUES ('P2','Saiful','geochat');";
		
		Connection con;
		Statement stmt;
		
		try {
			Class.forName(drivername);
			con = DriverManager.getConnection(url,username, password);
			stmt = con.createStatement();	 
			
			//stmt.addBatch(dropDatabase);
			stmt.addBatch(createDatabase);
			stmt.addBatch(allowGrant);
			
			stmt.addBatch(dropDataTable);
			stmt.addBatch(createDataTable);
			
			stmt.addBatch(dropSyncTable);
			stmt.addBatch(createSyncTable);
			
			if(addSampleData)
				stmt.addBatch(sampleData);

			stmt.executeBatch();
			
			stmt.close();
			con.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		 
	}
	
	public static IGoogleSpreadSheet getTestGoogleSpreadsheet(
			FeedURLFactory factory, SpreadsheetService service,
			DocsService docService, String gssTestSpreadsheetFileName,
			String gssTestWorksheetName, String idColumn) {
		try {
			GSSpreadsheet gss = GoogleSpreadsheetUtils.getOrCreateGSSpreadsheetIfAbsent(
					factory, service, docService, gssTestSpreadsheetFileName);
			
			IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(docService,
					service, factory, gssTestSpreadsheetFileName, gss);
			
			
			GSWorksheet<GSRow> workSheet = spreadSheet.getGSWorksheet(gssTestWorksheetName);
			if(workSheet == null){
				//this will create content/sync sheet when they are not available
				GoogleSpreadSheetRDFSyncAdapterFactory gsFactory = new GoogleSpreadSheetRDFSyncAdapterFactory(
						"http://localhost:8080/mesh4x/feeds");

				SplitAdapter sa = gsFactory.createSyncAdapter(spreadSheet,
						gssTestWorksheetName, new String[] { idColumn }, null,
						NullIdentityProvider.INSTANCE,
						TestHelper.getSampleGoogleSpreadsheetToRDFMapping(gssTestWorksheetName, idColumn).getSchema(),
						gssTestWorksheetName);
				
				spreadSheet = ((GoogleSpreadSheetContentAdapter)sa.getContentAdapter()).getSpreadSheet();
			}
			
			return spreadSheet;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
