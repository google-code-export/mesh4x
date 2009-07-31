package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

public class SyncAdapterBuilderTest {

	private static SpreadsheetService service;
	private static DocsService docService;
	private static FeedURLFactory factory;
	
	private static String gssTestWorksheetName = "user";
	private static String gssTestSpreadsheetFileName = "SyncAdapterBuilderTest";
	private static String gssTestUsername = "gspreadsheet.test@gmail.com";
	private static String gssTestPassword = "java123456";
	
	private static String mysqlTestDBName = "mesh4x_ektoo_db";
	private static String mysqlTestTableName = gssTestWorksheetName;
	private static String mysqlTestUsername = "root";
	private static String mysqlTestPassword = "admin";
	
	private static String excelTestWorksheetName = gssTestWorksheetName;
	private static String accessTestTableName = gssTestWorksheetName;
	
	private static String idColumn = "id";
	
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
	public void ShouldCreateFolderSyncAdapter() throws IOException{
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter folderAdapter = adapterBuilder.createFolderAdapter(TestHelper.baseDirectoryForTest() + "sourcefolder");
		Assert.assertNotNull(folderAdapter);
		Assert.assertNotNull(folderAdapter.getAll());
	}	
	
	@Test(expected=MeshException.class)
	public void shouldNotCreatePlainXMLBasedGoogleSpreadSheetAdapterWhenSpreadsheetWithThegivenNameNotAvailable(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"ajaira spreadsheet", gssTestUsername, gssTestPassword,
				new String[] {idColumn}, gssTestWorksheetName, gssTestWorksheetName );
	
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapterA = adapterBuilder.createPlainXMLBasedGoogleSpreadSheetAdapter(spreadSheetInfo);
		Assert.assertNull(syncAdapterA);
	}

	@Test
	public void shouldCreatePlainXMLBasedGoogleSpreadSheetAdapterWhenSpreadsheetWithTheGivenNameAvailable() throws Exception{
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
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapter = adapterBuilder.createPlainXMLBasedGoogleSpreadSheetAdapter(spreadSheetInfo);
		Assert.assertNotNull(syncAdapter);
	}
	
	@SuppressWarnings("unchecked")
	@Test //ok
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOwnRDFSchemaIfDataRowAvailable(){
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
		
		GSWorksheet<GSRow> syncWorkSheet = gss.getGSWorksheet(gssTestWorksheetName+"_sync");
		if(syncWorkSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: syncWorkSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					syncWorkSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			TestHelper.addTestGssRow(workSheet, "P1", "Sharif", "mesh4x");
		} catch (Exception e) {
			throw new MeshException(e);
		} 
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		//test setup done	
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, null);		
		
		Assert.assertNotNull(syncAdapter);
		Assert.assertEquals(1, syncAdapter.getAll().size());
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)((SplitAdapter)syncAdapter).getContentAdapter()).getSchema();
		Assert.assertNotNull(rdfSchema);
		Assert.assertEquals(3, rdfSchema.getPropertyCount());
	}	

	@Test(expected=MeshException.class) //ok
	public void shouldNotCreateRdfBasedGoogleSpreadSheetAdapterUsingOwnRDFSchemaIfNoDataRowAvailable(){
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
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());			
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		//create the adapter now
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, null);		
		Assert.assertNull(syncAdapter);
	}	
	
	@Test //ok
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetAvailableAndWorksheetAvailableWithCompatibleSchema(){
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
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		
		RDFSchema sourceSchema = new RDFSchema("user",
				"http://localhost:8080/mesh4x/feeds/user#", "user");
		sourceSchema.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("pass", "pass", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.setIdentifiablePropertyName("id");
		
		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) sourceSchema);
		Assert.assertNotNull(syncAdapter);
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)
				((SplitAdapter)syncAdapter).getContentAdapter()).getSchema();
		Assert.assertNotNull(rdfSchema);
		Assert.assertTrue(sourceSchema.isCompatible(rdfSchema));		
	}		

	
	@Test(expected=MeshException.class) //ok
	public void shouldNotCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetAvailableAndWorksheetAvailableWithNonCompatibleSchema(){
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
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		
		RDFSchema sourceSchema = new RDFSchema("user",
				"http://localhost:8080/mesh4x/feeds/user#", "user");
		sourceSchema.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("age", "age", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("country", "country", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.setIdentifiablePropertyName("id");
		
		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) sourceSchema);
		Assert.assertNotNull(syncAdapter);
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)
				((SplitAdapter)syncAdapter).getContentAdapter()).getSchema();
		Assert.assertNotNull(rdfSchema);
		Assert.assertTrue(sourceSchema.isCompatible(rdfSchema));	
	}	
	
	@Test //ok
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetAvailableButWorksheetNotAvailable(){
		//prepare/update the spreadsheet for this specific test
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
		GSWorksheet<GSRow> syncWorkSheet = gss.getGSWorksheet(gssTestWorksheetName+"_sync");
		if(syncWorkSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: syncWorkSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					syncWorkSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());
		//test setup done	
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				gssTestSpreadsheetFileName, gssTestUsername, gssTestPassword,
				new String[] { idColumn }, gssTestWorksheetName, gssTestWorksheetName);		
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		
		RDFSchema sourceSchema = new RDFSchema("user", "http://localhost:8080/mesh4x/feeds/user#", "user");
		sourceSchema.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.addStringProperty("pass", "pass", IRDFSchema.DEFAULT_LANGUAGE);
		sourceSchema.setIdentifiablePropertyName("id");

		SplitAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) sourceSchema);
		Assert.assertNotNull(syncAdapter);
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)syncAdapter.getContentAdapter()).getSchema();
		IIdentifiableMapping mapping = (IIdentifiableMapping)((GoogleSpreadSheetContentAdapter)syncAdapter.getContentAdapter()).getMapper();
		Assert.assertNotNull(rdfSchema);
		Assert.assertTrue(sourceSchema.isCompatible(rdfSchema));

		ISyncAdapter syncAdapterA = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
		Assert.assertNotNull(syncAdapterA);
		
		Assert.assertEquals(0, syncAdapter.getAll().size());
		syncAdapter.add(makeRDFItem(sourceSchema, mapping));
		Assert.assertEquals(1, syncAdapter.getAll().size());
	}
	
	@Test
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetNotAvailable(){		
		GoogleSpreadsheetToRDFMapping mapping = TestHelper.getSampleGoogleSpreadsheetToRDFMapping(gssTestWorksheetName, idColumn);
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"i was not here earlier",
				gssTestUsername,
				gssTestPassword,
				(String [])mapping.getSchema().getIdentifiablePropertyNames().toArray(),
				mapping.getType(),
				mapping.getType()
				);
			
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		SplitAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, mapping.getSchema());
		Assert.assertNotNull(syncAdapter);
		syncAdapter.beginSync();
		syncAdapter.endSync();
		
		IGoogleSpreadSheet gss = ((GoogleSpreadSheetContentAdapter)syncAdapter.getContentAdapter()).getSpreadSheet();
		
		GSWorksheet ws = gss.getGSSpreadsheet().getGSWorksheetBySheetName(gssTestWorksheetName);
		Assert.assertNotNull(ws);
		ws = gss.getGSSpreadsheet().getGSWorksheetBySheetName(gssTestWorksheetName+"_sync");
		Assert.assertNotNull(ws);
		
		GoogleSpreadsheetUtils.deleteSpreadsheetDoc(spreadSheetInfo.getGoogleSpreadSheetName(), docService);
	}	
	
	@Test
	public void shouldCreateMsAccessAdapter() throws Exception{
	    ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
	    String sourceFileName = this.getClass().getResource("aktoo.mdb").getFile();
	    ISyncAdapter syncAdapter = adapterBuilder.createMsAccessAdapter(sourceFileName, "mesh_example");
	    Assert.assertNotNull(syncAdapter);
	}
	
	@Test
	public void shouldCreateExcelAdapter() throws DocumentException{
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		File sourceContentFile = new File(TestHelper.baseDirectoryForTest() + "source_"+IdGenerator.INSTANCE.newID()+".xls");
		String contentFilePathAsString = TestHelper.createMsExcelFileForTest(sourceContentFile, excelTestWorksheetName, idColumn, true);
		SplitAdapter excelAdapter = adapterBuilder.createMsExcelAdapter(contentFilePathAsString, excelTestWorksheetName, new String[]{idColumn}, false);
		Assert.assertNotNull(excelAdapter);
	}
	
	@Test
	public void ShouldCreateMySqlAdapter() throws DocumentException{
		// prepare/update the mysql for this specific test
		TestHelper.createMysqlTableForTest(mysqlTestDBName, mysqlTestUsername,
				mysqlTestPassword, mysqlTestTableName, idColumn, true);
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter mysqlAdapter =  builder.createMySQLAdapter(mysqlTestUsername, mysqlTestPassword,"localhost" ,3306, mysqlTestDBName, mysqlTestTableName);
		Assert.assertNotNull(mysqlAdapter);
	}
	
	@Test
	public void ShouldCreateFeedAdapter() {
		File file = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+ ".xml"));
		String link = "http://localhost:8080/mesh4x/feeds";	
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter feedAdapter = builder.createFeedAdapter("myFeed", "my data feed", link, file.getAbsolutePath(), RssSyndicationFormat.INSTANCE);
		Assert.assertNotNull(feedAdapter);
		Assert.assertEquals(0, feedAdapter.getAll().size());
	}
	
	@Test
	public void ShouldCreateKMLAdapter(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter kmlAdapter = builder.createKMLAdapter(TestHelper.baseDirectoryForTest() + "kmlDummyForSync.kml");
		Assert.assertNotNull(kmlAdapter);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfSheetNameEmptyOrNull()	{
		String contentFile = TestHelper.fileName("contentFile.xls");
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(contentFile, "", new String[]{"id"}, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdEmptyOrNull(){
		String contentFile = TestHelper.fileName("contentFile.xls");
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(contentFile, "user", new String[]{"id"}, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReturnExceptionIfContenFileIsNullOrEmpty(){
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(null, "user", new String[]{"id"}, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReturnExceptionIfRDFIsNullOrEmpty(){
		String contentFile = TestHelper.fileName("contentFile.xls");
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(contentFile, null);
	}
	

//	private GoogleSpreadsheetToRDFMapping createSampleGoogleSpreadsheetToRDFMapping() {
//		RDFSchema schema = new RDFSchema(gssTestWorksheetName, "http://localhost:8080/mesh4x/feeds/user#", gssTestWorksheetName);
//		schema.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
//		schema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
//		schema.addStringProperty("pass", "pass", IRDFSchema.DEFAULT_LANGUAGE);
//		schema.setIdentifiablePropertyName("id");
//		RDFInstance rdfInstance = schema.createNewInstance("uri:urn:id");
//
//		long millis = System.currentTimeMillis();
//		String name = "Name: " + millis;
//		String code = "P3";
//		String pass = "mesh4j";
//
//		rdfInstance.setProperty("id", code);
//		rdfInstance.setProperty("name", name);
//		rdfInstance.setProperty("pass", pass);
//
//		return new GoogleSpreadsheetToRDFMapping(schema);
//	}		
	
	private Item getItem(IIdentifiableMapping mapping) throws DocumentException {
		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" +
				"<id>"+id+"</id>" +
				"<name>Marcelo</name>" +
				"<pass>Buensaires</pass>" +
				"</user>";
				
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new IdentifiableContent(payload, mapping, id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}

	private Item makeRDFItem(IRDFSchema schema, IIdentifiableMapping mapping){		
		String id = IdGenerator.INSTANCE.newID();		
		String rawDataAsXML = "<user>" +
				"<id>"+id+"</id>" +
				"<name>Raju</name>" +
				"<pass>25</pass>" +
				"</user>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		payload = schema.getInstanceFromPlainXML(id, payload, ISchema.EMPTY_FORMATS);
		IContent content = new IdentifiableContent(payload, mapping, id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
}
