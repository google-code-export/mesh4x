package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

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
		GoogleSpreadsheetUtils.deleteSpreadsheetDoc(gssTestSpreadsheetFileName, docService);
	}
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToExcelByRDFAndMustCreateTargetSchema() throws Exception{
		//prepare/update the spreadsheet for this specific test
		IGoogleSpreadSheet gss = TestHelper.getTestGoogleSpreadsheet(factory, service,
				docService, gssTestSpreadsheetFileName,
				gssTestWorksheetName, idColumn);
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(gssTestWorksheetName);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			TestHelper.addTestGssRow(workSheet, "P1", "Sharif", "mesh4x");
		} catch (Exception e) {
			throw new MeshException(e);
		} 
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		//test setup done
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder();
		
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

		ISyncAdapterBuilder builder = new SyncAdapterBuilder();
		
		File sourceContentFile = new File(TestHelper.baseDirectoryForTest() + "source_"+IdGenerator.INSTANCE.newID()+".xls");
		String contentFilePathAsString = TestHelper.createMsExcelFileForTest(sourceContentFile, excelTestWorksheetName, idColumn, true);
		
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
		TestHelper.createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, true);

		ISyncAdapterBuilder builder = new SyncAdapterBuilder( );
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
		TestHelper.createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, true);

		ISyncAdapterBuilder builder = new SyncAdapterBuilder();
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
		TestHelper.createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, false);
		
		IGoogleSpreadSheet gss = TestHelper.getTestGoogleSpreadsheet(factory, service,
				docService, gssTestSpreadsheetFileName,
				gssTestWorksheetName, idColumn);
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(gssTestWorksheetName);
		if(workSheet != null){
			try {
				workSheet.getBaseEntry().delete();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		}
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder();
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
		ISyncAdapterBuilder builder = new SyncAdapterBuilder();
		
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
	
}
