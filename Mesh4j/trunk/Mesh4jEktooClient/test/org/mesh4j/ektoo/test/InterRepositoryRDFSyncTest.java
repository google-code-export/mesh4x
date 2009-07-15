package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.adapters.IdentifiableContent;
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
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.ServiceException;

public class InterRepositoryRDFSyncTest {
	
	private static SpreadsheetService service;
	private static DocsService docService;
	private static FeedURLFactory factory;
	
	private static String gssTestWorksheetName = "mesh_example";
	private static String gssTestSpreadsheetFileName = "InterRepositoryRDFSyncTest";
	private static String gssTestUsername = "gspreadsheet.test@gmail.com";
	private static String gssTestPassword = "java123456";
	
	private static String mysqlTestDBName = "mesh4x_ektoo_db";
	private static String mysqlTestTableName = gssTestWorksheetName;
	private static String mysqlTestUsername = "root";
	private static String mysqlTestPassword = "admin";
	
	private static String excelTestWorksheetName = gssTestWorksheetName;
	private static String accessTestTableName = gssTestWorksheetName;
	
	private static String idColumn = "uid";
	
	@BeforeClass
	public static void setUp() throws Exception {
		service = GoogleSpreadsheetUtils.getSpreadsheetService(gssTestUsername, gssTestPassword);
		docService = GoogleSpreadsheetUtils.getDocService(gssTestUsername, gssTestPassword);
		factory = FeedURLFactory.getDefault();		
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryForTest());
		//GoogleSpreadsheetUtils.deleteSpreadsheetDoc(gssTestSpreadsheetFileName, docService);
	}
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToExcelByRDFAndMustCreateTargetSchema() throws Exception{
		//prepare/update the spreadsheet for this specific test
		IGoogleSpreadSheet gss = getTestGoogleSpreadsheet();
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(gssTestWorksheetName);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			addTestRow(workSheet, "P1", "Sharif", "mesh4x");
		} catch (Exception e) {
			throw new MeshException(e);
		} 
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		//test setup done
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		SplitAdapter sourceAsGoogleSpreadSheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, null);
		RDFSchema rdfSchema = (RDFSchema)((GoogleSpreadSheetContentAdapter)sourceAsGoogleSpreadSheet.getContentAdapter()).getSchema();
		
		File contentFile = new File(TestHelper.baseDirectoryForTest() + "msExcel_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(contentFile.getAbsolutePath(), rdfSchema);
		
		SyncEngine syncEngine = new SyncEngine(sourceAsGoogleSpreadSheet, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);
	}
	
	@Test
	public void ShouldSyncMsExcelToMsExcelByRDFAndMustCreateTargetSchema(){

		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		File sourceContentFile = new File(TestHelper.baseDirectoryForTest() + "source_"+IdGenerator.INSTANCE.newID()+".xls");
		String contentFilePathAsString = createMsExcelFileForTest(sourceContentFile);
		
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(contentFilePathAsString, excelTestWorksheetName, new String[]{idColumn}, true);

		SplitAdapter splitAdapterSource = ((SplitAdapter)sourceAsExcel);
		ISchema sourceSchema = ((MsExcelContentAdapter)splitAdapterSource.getContentAdapter()).getSchema();
		IRDFSchema rdfSchema = (IRDFSchema)sourceSchema;
		
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(targetContentFile.getAbsolutePath(), rdfSchema);
		
		SyncEngine syncEngine = new SyncEngine(sourceAsExcel, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);
	}
	
	@Test
	public void ShouldSyncMySQLToExcelByRDF(){
		// prepare/update the mysql for this specific test
		createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, true);

		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword, "localhost" ,3306, mysqlTestDBName, mysqlTestTableName);
		
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsMySql;
		ISchema sourceSchema = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping().getSchema();
		
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(targetContentFile.getAbsolutePath(), (IRDFSchema)sourceSchema);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);
	}
	
	@Test
	public void ShouldSyncMySQLToExistingGoogleSpreadsheetByRDF(){
		// prepare/update the mysql for this specific test
		createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, true);

		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		FileUtils.cleanupDirectory(builder.getBaseDirectory());
		
		SplitAdapter sourceAsMySql =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword, "localhost" ,3306, mysqlTestDBName, mysqlTestTableName);
		
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)sourceAsMySql.getContentAdapter()).getMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		SplitAdapter targetAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) mapping.getSchema());
		
		targetAsGoogleSpreadsheet.add(makeNewItem(((GoogleSpreadSheetContentAdapter)targetAsGoogleSpreadsheet.getContentAdapter()).getMapper()));
		
		SyncEngine syncEngine = new SyncEngine(sourceAsMySql, targetAsGoogleSpreadsheet);
		TestHelper.syncAndAssert(syncEngine);
	}

	@Test
	public void ShouldSyncMySQLToNonExistingGoogleSpreadsheetByRDF(){
		// prepare/update the mysql for this specific test
		createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, false);
		
		IGoogleSpreadSheet gss = getTestGoogleSpreadsheet();
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(gssTestWorksheetName);
		if(workSheet != null){
			try {
				workSheet.getBaseEntry().delete();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		}
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		FileUtils.cleanupDirectory(builder.getBaseDirectory());
		
		SplitAdapter sourceAsMySql =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword, "localhost" ,3306, mysqlTestDBName, mysqlTestTableName);
		
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)sourceAsMySql.getContentAdapter()).getMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		SplitAdapter targetAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) mapping.getSchema());
		
		targetAsGoogleSpreadsheet.add(makeNewItem(((GoogleSpreadSheetContentAdapter)targetAsGoogleSpreadsheet.getContentAdapter()).getMapper()));
		
		SyncEngine syncEngine = new SyncEngine(sourceAsMySql, targetAsGoogleSpreadsheet);
		TestHelper.syncAndAssert(syncEngine);
	}

	@Test
	public void ShouldSyncMsAccessToExcelByRDF() throws IOException{
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		String sourceFileName = this.getClass().getResource("aktoo.mdb").getFile();
		File sourceContentFile = new File(TestHelper.baseDirectoryForTest() + "source_"+IdGenerator.INSTANCE.newID()+".mdb");
		FileUtils.copyFile(sourceFileName, sourceContentFile.getAbsolutePath());
		
		ISyncAdapter sourceAsAccess = builder.createMsAccessAdapter(sourceContentFile.getAbsolutePath() , accessTestTableName);
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsAccess;
		
		//IRDFSchema sourceSchema = MsAccessRDFSchemaGenerator.extractRDFSchema(TestHelper.baseDirectoryForTest() +"ektoo.mdb", "ektoo", rdfBaseURl);
		
		ISchema sourceSchema = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping().getSchema();
		
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".xls");
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(targetContentFile.getAbsolutePath(), (IRDFSchema)sourceSchema);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);
	}
	
	
	//PRIVATE METHODS
	
	private Item makeNewItem(IIdentifiableMapping mapping){
		try{
			String id = IdGenerator.INSTANCE.newID();
			
			String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:"+gssTestWorksheetName+"=\"http://localhost:8080/mesh4x/feeds/"+gssTestWorksheetName+"#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"> " +
			  "<"+gssTestWorksheetName+":"+gssTestWorksheetName+" rdf:about=\"uri:urn:P3\">"+
			    "<"+gssTestWorksheetName+":"+idColumn+" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">P3</"+gssTestWorksheetName+":"+idColumn+">"+
			    "<"+gssTestWorksheetName+":name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">iklas</"+gssTestWorksheetName+":name>"+
			    "<"+gssTestWorksheetName+":pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">hummm</"+gssTestWorksheetName+":pass>"+
			  "</"+gssTestWorksheetName+":"+gssTestWorksheetName+">"+
			"</rdf:RDF>";
			
			Element payload = DocumentHelper.parseText(xml).getRootElement();
			
			IContent content = new IdentifiableContent(payload, mapping, id);
			Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "sharif", new Date(), false);
			return new Item(content, sync);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private void createMysqlTableForTest(String dbname, String username, String password, String dataTablename, boolean addSampleData){
		String url = "jdbc:mysql://localhost:3306/mysql";
		String drivername = "com.mysql.jdbc.Driver";
		
		String syncTableName = dataTablename+"_sync"; 
		
		String dropDatabase = "DROP DATABASE IF EXISTS "+ dbname+"; ";
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
			
			stmt.addBatch(dropDatabase);
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

	private String createMsExcelFileForTest(File file){
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

		//add sample data
		row = sheet.createRow(1);	

		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"P2"));

		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"raju"));

		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"dhaka"));

		MsExcelUtils.flush(workbook, file.getAbsolutePath());

		return file.getAbsolutePath();
	}
	
	private RichTextString getRichTextString(Workbook workbook,String value){
		return workbook.getCreationHelper().createRichTextString(value);
	}
	
	private IGoogleSpreadSheet getTestGoogleSpreadsheet() {
		try {
			GSSpreadsheet gss = GoogleSpreadsheetUtils.getOrCreateGSSpreadsheetIfAbsent(
					factory, service, docService, gssTestSpreadsheetFileName);
			
			IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(docService,
					service, factory, gssTestSpreadsheetFileName, gss);
			
			
			GSWorksheet<GSRow> workSheet = spreadSheet.getGSWorksheet(gssTestWorksheetName);
			if(workSheet == null){
				//this will create content/sync sheet when they are not available
				GoogleSpreadSheetRDFSyncAdapterFactory factory = new GoogleSpreadSheetRDFSyncAdapterFactory(
						"http://localhost:8080/mesh4x/feeds");

				SplitAdapter sa = factory.createSyncAdapter(spreadSheet,
						gssTestWorksheetName, new String[] { idColumn }, null,
						NullIdentityProvider.INSTANCE,
						getSampleGoogleSpreadsheetToRDFMapping().getSchema(),
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
	
	private GoogleSpreadsheetToRDFMapping getSampleGoogleSpreadsheetToRDFMapping() {
		RDFSchema schema = new RDFSchema(gssTestWorksheetName, "http://localhost:8080/mesh4x/feeds/"
				+gssTestWorksheetName+"#", gssTestWorksheetName);
		schema.addStringProperty(idColumn, idColumn, IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("pass", "pass", IRDFSchema.DEFAULT_LANGUAGE);
		schema.setIdentifiablePropertyNames(Arrays.asList(new String[]{idColumn}));
		return new GoogleSpreadsheetToRDFMapping(schema);
	}
	
	private GSRow<GSCell> addTestRow(GSWorksheet<GSRow> workSheet,
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
	
}
