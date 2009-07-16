package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

public class InterRepositorySyncTest {
	
	private static SpreadsheetService service;
	private static DocsService docService;
	private static FeedURLFactory factory;
	
	private static String gssTestWorksheetName = "mesh_example";
	private static String gssTestSpreadsheetFileName1 = "InterRepositorySyncTest1";
	private static String gssTestSpreadsheetFileName2 = "InterRepositorySyncTest2";
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
		GoogleSpreadsheetUtils.deleteSpreadsheetDoc(gssTestSpreadsheetFileName1, docService);
		GoogleSpreadsheetUtils.deleteSpreadsheetDoc(gssTestSpreadsheetFileName2, docService);
	}
	
	@Test
	public void ShouldSyncFolderToFolder(){
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceFolderAdapter = adapterBuilder.createFolderAdapter(TestHelper.baseDirectoryForTest() + "sourcefolder");
		ISyncAdapter targetFolderAdapter = adapterBuilder.createFolderAdapter(TestHelper.baseDirectoryForTest() + "targetfolder");
		SyncEngine syncEngine = new SyncEngine(sourceFolderAdapter,targetFolderAdapter);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
	}
	
	@Test
	public void ShouldSyncRssToRssWithoutRDFAssumeSameSchema(){
		
		String link = "";
		Element element = TestHelper.makeElement("<payload><user><id>SyncId123</id><name>SyncId123</name><pass>123</pass></user></payload>");
		XMLContent content = new XMLContent("SyncId123", "SyncId123", "SyncId123", element);
		Item item = new Item(content, new Sync("SyncId123", "jmt", TestHelper.now(), false));
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		 
		File rssSourceFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_source.xml"));
		ISyncAdapter sourceRSSAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssSourceFile.getAbsolutePath(), RssSyndicationFormat.INSTANCE);
		
		sourceRSSAdapter.add(item);
		
		File rssTargetFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_target.xml"));
		ISyncAdapter targetRSSAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssTargetFile.getAbsolutePath(), RssSyndicationFormat.INSTANCE);
		
		Assert.assertEquals(1, sourceRSSAdapter.getAll().size());
		Assert.assertEquals(0, targetRSSAdapter.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(sourceRSSAdapter,targetRSSAdapter);
		List<Item> listOfConflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(1, sourceRSSAdapter.getAll().size());
		Assert.assertEquals(1, targetRSSAdapter.getAll().size());
		
	}
	
	@Test
	public void ShouldSyncAtomToAtomWithoutRDFAssumeSameSchema(){
		
		String link = "";
		Element element = TestHelper.makeElement("<payload><user><id>SyncId123</id><name>SyncId123</name><pass>123</pass></user></payload>");
		XMLContent content = new XMLContent("SyncId123", "SyncId123", "SyncId123", element);
		Item item = new Item(content, new Sync("SyncId123", "jmt", TestHelper.now(), false));
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		 
		File rssSourceFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_source.xml"));
		ISyncAdapter sourceAtomAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssSourceFile.getAbsolutePath(), AtomSyndicationFormat.INSTANCE);
		
		sourceAtomAdapter.add(item);
		
		File rssTargetFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_target.xml"));
		ISyncAdapter targetAtomAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssTargetFile.getAbsolutePath(), AtomSyndicationFormat.INSTANCE);
		
		Assert.assertEquals(1, sourceAtomAdapter.getAll().size());
		Assert.assertEquals(0, targetAtomAdapter.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(sourceAtomAdapter,targetAtomAdapter);
		List<Item> listOfConflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(1, sourceAtomAdapter.getAll().size());
		Assert.assertEquals(1, targetAtomAdapter.getAll().size());
		
	}
	
	@Test
	public void ShouldSyncKmlToKmlWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceKamlAdapter = builder.createKMLAdapter(TestHelper.fileName("kmlSyncTestsGround.kml"));
		ISyncAdapter targetKamlAdapter = builder.createKMLAdapter(TestHelper.fileName("kmlSyncTestsPlacemark.kml"));
		
		SyncEngine syncEngine = new SyncEngine(sourceKamlAdapter,targetKamlAdapter);
		List<Item> listOfConflicts = syncEngine.synchronize();
			
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToExcelWithoutRDFAssumeSameSchema() throws Exception{
		//prepare/update the spreadsheet for this specific test
		IGoogleSpreadSheet gss = TestHelper.getTestGoogleSpreadsheet(factory, service,
				docService, gssTestSpreadsheetFileName1,
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
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName1, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		ISyncAdapter sourceAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, null);
		
		IRDFSchema rdfSchema = (IRDFSchema) ((GoogleSpreadSheetContentAdapter)((SplitAdapter)sourceAsGoogleSpreadsheet).getContentAdapter()).getSchema();
		
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".xls");
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.createMsExcelFileForTest(targetContentFile, excelTestWorksheetName, idColumn, true), rdfSchema);

		SyncEngine engine = new SyncEngine(sourceAsGoogleSpreadsheet, targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToGoogleSpreadSheet() throws Exception{
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		//prepare/update the spreadsheet for this specific test
		IGoogleSpreadSheet gssSource = TestHelper.getTestGoogleSpreadsheet(factory, service,
				docService, gssTestSpreadsheetFileName1,
				gssTestWorksheetName, idColumn);
		GSWorksheet<GSRow> workSheetSource = gssSource.getGSWorksheet(gssTestWorksheetName);
		
		if(workSheetSource.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheetSource.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheetSource.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			TestHelper.addTestGssRow(workSheetSource, "P1", "Sharif", "mesh4x");
		} catch (Exception e) {
			throw new MeshException(e);
		} 
		
		GoogleSpreadsheetUtils.flush(service, gssSource.getGSSpreadsheet());	
		//test setup done
		
		GoogleSpreadSheetInfo spreadSheetInfoSource = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName1, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		ISyncAdapter sourceAsGoogleSpreadSheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfoSource, null);
		
		IRDFSchema rdfSchemaSource = (IRDFSchema) ((GoogleSpreadSheetContentAdapter)((SplitAdapter)sourceAsGoogleSpreadSheet).getContentAdapter()).getSchema();
		
		//prepare/update the spreadsheet for this specific test
		IGoogleSpreadSheet gssTarget = TestHelper.getTestGoogleSpreadsheet(factory, service,
				docService, gssTestSpreadsheetFileName2,
				gssTestWorksheetName, idColumn);
		GSWorksheet<GSRow> workSheetTarget = gssTarget.getGSWorksheet(gssTestWorksheetName);
		
		if(workSheetTarget.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheetTarget.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheetTarget.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			TestHelper.addTestGssRow(workSheetTarget, "P2", "Raju", "geochat");
		} catch (Exception e) {
			throw new MeshException(e);
		} 
		
		GoogleSpreadsheetUtils.flush(service, gssTarget.getGSSpreadsheet());	
		//test setup done
		
		GoogleSpreadSheetInfo spreadSheetInfoTarget = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName2, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		
		ISyncAdapter targetAsGoogleSpreadSheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfoTarget, rdfSchemaSource);
		
		SyncEngine engine = new SyncEngine(sourceAsGoogleSpreadSheet,targetAsGoogleSpreadSheet);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	
	@Test
	public void ShouldSyncExcelToExcelWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		File sourceContentFile = new File(TestHelper.baseDirectoryForTest() + "sourec_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(TestHelper.createMsExcelFileForTest(sourceContentFile, 
				excelTestWorksheetName, idColumn, true), excelTestWorksheetName, new String[] {idColumn}, false);
		
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.createMsExcelFileForTest(targetContentFile, 
				excelTestWorksheetName, idColumn, false), excelTestWorksheetName, new String[] {idColumn}, false);

		SyncEngine engine = new SyncEngine(sourceAsExcel,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncAccessToAccessWithoutRDFAssumeSameSchema() throws IOException{
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		String sourceFileName = this.getClass().getResource("aktoo.mdb").getFile();
		File sourceContentFile = new File(TestHelper.baseDirectoryForTest() + "source_"+IdGenerator.INSTANCE.newID()+".mdb");
		FileUtils.copyFile(sourceFileName, sourceContentFile.getAbsolutePath());
		ISyncAdapter sourceAsAccess = builder.createMsAccessAdapter(sourceContentFile.getAbsolutePath() , accessTestTableName);
		
		String targetFileName = this.getClass().getResource("aktoo.mdb").getFile();
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".mdb");
		FileUtils.copyFile(targetFileName, targetContentFile.getAbsolutePath());
		ISyncAdapter targetAsAccess = builder.createMsAccessAdapter(targetContentFile.getAbsolutePath() , accessTestTableName);
		
		SyncEngine engine = new SyncEngine(sourceAsAccess,targetAsAccess);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncMysqlToMysqlWithoutRDFAssumeSameSchema(){
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		FileUtils.cleanupDirectory(builder.getBaseDirectory());
		
		// prepare/update the mysql for this specific test
		TestHelper.createMysqlTableForTest(mysqlTestDBName+"_source", mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, true);
		
		SplitAdapter sourceAsMySql =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword, "localhost" ,3306, mysqlTestDBName+"_source", mysqlTestTableName);
		
		// prepare/update the mysql for this specific test
		TestHelper.createMysqlTableForTest(mysqlTestDBName+"_target", mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, false);

		SplitAdapter targetAsMySql =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword, "localhost" ,3306, mysqlTestDBName+"_target", mysqlTestTableName);
		
		SyncEngine engine = new SyncEngine(sourceAsMySql,targetAsMySql);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}

	@Test
	public void ShouldSyncMySQLToCloud(){	
			
		String meshName = "Mysql";
		String feedName = "user";
		String url = "http://localhost:8080/mesh4x/feeds";

		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		FileUtils.cleanupDirectory(builder.getBaseDirectory());
		
		// prepare/update the mysql for this specific test
		TestHelper.createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, true);

		SplitAdapter sourceAsMySql =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword, "localhost" ,3306, mysqlTestDBName, mysqlTestTableName);
		
		SplitAdapter sourceAdapter = (SplitAdapter) sourceAsMySql;

		ISyncAdapter targetAdapter = builder.createHttpSyncAdapter(url, meshName, feedName, null);

		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);

		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(sourceAdapter.getAll().size(), targetAdapter
				.getAll().size());
	}
}
