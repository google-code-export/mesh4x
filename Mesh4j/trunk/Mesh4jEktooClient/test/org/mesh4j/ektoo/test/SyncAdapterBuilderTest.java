package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;

public class SyncAdapterBuilderTest {

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
				"pLUqch-enpf1-GcqnD6qjSA",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);
	
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapterA = adapterBuilder.createPlainXMLBasedGoogleSpreadSheetAdapter(spreadSheetInfo);
		Assert.assertNull(syncAdapterA);
	}

	@Test
	public void shouldCreatePlainXMLBasedGoogleSpreadSheetAdapterWhenSpreadsheetWithTheGivenNameAvailable() throws Exception{
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"spreadsheettest2",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);

		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapter = adapterBuilder.createPlainXMLBasedGoogleSpreadSheetAdapter(spreadSheetInfo);
		Assert.assertNotNull(syncAdapter);
	}
	
	@Test //ok
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOwnRDFSchemaIfDataRowAvailable(){
			GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
					"spreadsheettest2",
					"gspreadsheet.test@gmail.com",
					"java123456",
					"id",
					"user",
					"user"
					);
			
			//prepare spreadsheet for test (make sure at least one data row available in the sheet)
			IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetName(), 
					spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
			
			Assert.assertNotNull(spreadSheet.getGSSpreadsheet());
			
			GSWorksheet<GSRow<GSCell>> worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName());
			if( worksheet!= null )
				spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));

			worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName()+"_sync");
			if( worksheet!= null )
				spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));
		
			GSWorksheet<GSRow<GSCell>> newWorksheet = GoogleSpreadsheetUtils.getOrCreateWorkSheetIfAbsent(spreadSheet.getGSSpreadsheet(), spreadSheetInfo.getSheetName());
			
			//create the header row
			GSRow<GSCell> headerRow = GoogleSpreadsheetUtils.getOrCreateHeaderRowIfAbsent(newWorksheet);
			GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "id");
			GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "name");
			GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "pass");
		
			GSRow<GSCell> dataRow = newWorksheet.createNewRow(2);
			dataRow.createNewCell(headerRow.getChildElement("id").getColIndex(), "id", "gsl-a123");
			dataRow.createNewCell(headerRow.getChildElement("name").getColIndex(), "name", "sharif");
			dataRow.createNewCell(headerRow.getChildElement("pass").getColIndex(), "pass", "mesh4x");				

			spreadSheet.setDirty();
			spreadSheet.flush();			
			
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
			GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
					"spreadsheettest2",
					"gspreadsheet.test@gmail.com",
					"java123456",
					"id",
					"user",
					"user"
					);
			//prepare spreadsheet for test (remove all data rows from spreadsheet)
			IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetName(), 
					spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
			
			GSWorksheet worksheet = GoogleSpreadsheetUtils.getOrCreateWorkSheetIfAbsent(spreadSheet.getGSSpreadsheet(), spreadSheetInfo.getSheetName());
			//clean up data row if any
			int rowCount = worksheet.getChildElements().size();
			
			if(rowCount > 1){
				//remove all the data row 
				for (int i = 2; i <= rowCount; i++){
					worksheet.deleteChildElement(String.valueOf(i));
				}
			}
			
			spreadSheet.setDirty();
			spreadSheet.flush();		
		
			//create the adapter now
			ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
			ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, null);		
			Assert.assertNull(syncAdapter);
	}	
	
	@Test //ok
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetAvailableAndWorksheetAvailableWithCompatibleSchema(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"spreadsheettest2",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user",
				"user"
				);
		
		//prepare spreadsheet for test (make sure at least one data row available in the sheet)
		IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetName(), 
				spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
		
		Assert.assertNotNull(spreadSheet.getGSSpreadsheet());
		
		GSWorksheet<GSRow<GSCell>> worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName());
		if( worksheet!= null )
			spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));

		worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName()+"_sync");
		if( worksheet!= null )
			spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));
	
		GSWorksheet<GSRow<GSCell>> newWorksheet = GoogleSpreadsheetUtils.getOrCreateWorkSheetIfAbsent(spreadSheet.getGSSpreadsheet(), spreadSheetInfo.getSheetName());
		
		//create the header row
		GSRow<GSCell> headerRow = GoogleSpreadsheetUtils.getOrCreateHeaderRowIfAbsent(newWorksheet);
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "id");
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "name");
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "pass");
	
		GSRow<GSCell> dataRow = newWorksheet.createNewRow(2);
		dataRow.createNewCell(headerRow.getChildElement("id").getColIndex(), "id", "gsl-a123");
		dataRow.createNewCell(headerRow.getChildElement("name").getColIndex(), "name", "sharif");
		dataRow.createNewCell(headerRow.getChildElement("pass").getColIndex(), "pass", "mesh4x");	
		
		spreadSheet.setDirty();
		spreadSheet.flush();		
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		
		RDFSchema sourceSchema = new RDFSchema("user",
				"http://localhost:8080/mesh4x/feeds/user#", "user");
		sourceSchema.addStringProperty("id", "id", "en");
		sourceSchema.addStringProperty("name", "Name", "en");
		sourceSchema.addStringProperty("pass", "pass", "en");

		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) sourceSchema);
		Assert.assertNotNull(syncAdapter);
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)
				((SplitAdapter)syncAdapter).getContentAdapter()).getSchema();
		Assert.assertNotNull(rdfSchema);
		Assert.assertTrue(sourceSchema.isCompatible(rdfSchema));		
	}		

	
	@Test(expected=MeshException.class) //ok
	public void shouldNotCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetAvailableAndWorksheetAvailableWithNonCompatibleSchema(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"spreadsheettest2",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user",
				"user"
				);
		
		//prepare spreadsheet for test (make sure at least one data row available in the sheet)
		IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetName(), 
				spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
		
		Assert.assertNotNull(spreadSheet.getGSSpreadsheet());
		
		GSWorksheet<GSRow<GSCell>> worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName());
		
		if(worksheet != null)
			spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));
		
		GSWorksheet<GSRow<GSCell>> newWorksheet = GoogleSpreadsheetUtils.getOrCreateWorkSheetIfAbsent(spreadSheet.getGSSpreadsheet(), spreadSheetInfo.getSheetName());
		
		//create/change the header row
		GSRow<GSCell> headerRow = GoogleSpreadsheetUtils.getOrCreateHeaderRowIfAbsent(newWorksheet);
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "id");
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "name");
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "age");
		GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, "phone");
	
		//add data
		GSRow<GSCell> dataRow = newWorksheet.createNewRow(newWorksheet.getChildElements().size()+1);
		dataRow.createNewCell(headerRow.getChildElement("id").getColIndex(), "id", "gsl-a123");
		dataRow.createNewCell(headerRow.getChildElement("name").getColIndex(), "name", "sharif");
		dataRow.createNewCell(headerRow.getChildElement("age").getColIndex(), "age", "13");
		dataRow.createNewCell(headerRow.getChildElement("phone").getColIndex(), "phone", "12345");	
			
		spreadSheet.setDirty();
		spreadSheet.flush();		
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		
		RDFSchema sourceSchema = new RDFSchema("user",
				"http://localhost:8080/mesh4x/feeds/user#", "user");
		sourceSchema.addStringProperty("id", "id", "en");
		sourceSchema.addStringProperty("name", "Name", "en");
		sourceSchema.addStringProperty("pass", "pass", "en");

		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) sourceSchema);
		Assert.assertNull(syncAdapter);
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)((SplitAdapter)syncAdapter).getContentAdapter()).getSchema();
		Assert.assertNotNull(rdfSchema);
		Assert.assertFalse(sourceSchema.isCompatible(rdfSchema));		
	}	
	
	@Test //ok
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetAvailableButWorksheetNotAvailable(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"spreadsheettest2",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user",
				"user"
				);
		
		//prepare spreadsheet for test (make no worksheet available with the name contained in sheetName variable)
		IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetName(), 
				spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
		
		Assert.assertNotNull(spreadSheet.getGSSpreadsheet());
		
		GSWorksheet<GSRow<GSCell>> worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName());
		if( worksheet!= null )
			spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));

		worksheet = spreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName()+"_sync");
		if( worksheet!= null )
			spreadSheet.getGSSpreadsheet().deleteChildElement(String.valueOf(worksheet.getElementListIndex()));
		
		spreadSheet.setDirty();
		spreadSheet.flush();		
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		
		RDFSchema sourceSchema = new RDFSchema("user", "http://localhost:8080/mesh4x/feeds/user#", "user");
		sourceSchema.addStringProperty("id", "id", "en");
		sourceSchema.addStringProperty("name", "Name", "en");
		sourceSchema.addStringProperty("pass", "pass", "en");

		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) sourceSchema);
		Assert.assertNotNull(syncAdapter);
		
		IRDFSchema rdfSchema = (IRDFSchema)((GoogleSpreadSheetContentAdapter)((SplitAdapter)syncAdapter).getContentAdapter()).getSchema();
		Assert.assertNotNull(rdfSchema);
		Assert.assertTrue(sourceSchema.isCompatible(rdfSchema));

		ISyncAdapter syncAdapterA = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
		Assert.assertNotNull(syncAdapterA);
		
		Assert.assertEquals(0, syncAdapter.getAll().size());
		syncAdapter.add(makeRDFItem(sourceSchema));
		Assert.assertEquals(1, syncAdapter.getAll().size());
	}
	
	
	@Test
	public void shouldCreateRdfBasedGoogleSpreadSheetAdapterUsingOtherRDFWhenSpreadsheetNotAvailable(){		
		IGoogleSpreadsheetToXMLMapping mapping = createSampleGoogleSpreadsheetToRDFMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"no spreadsheet",
				"gspreadsheet.test@gmail.com",
				"java123456",
				mapping.getIdColumnName(),
				mapping.getSheetName(),
				mapping.getType()
				);
			
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapter = adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, mapping.getSchema());
		Assert.assertNotNull(syncAdapter);
	}	
	
	
	@Test
	public void shouldCreateMsAccessAdapter() throws Exception{
	    ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
	    ISyncAdapter syncAdapter = adapterBuilder.createMsAccessAdapter(TestHelper.baseDirectoryForTest() + "ektoo.mdb", "ektoo");
	    
	   Assert.assertEquals(0, syncAdapter.getAll().size());
	}
	
	@Test
	public void shouldCreateExcelAdapter() throws DocumentException{
	
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls","user", "id");
		
		Assert.assertEquals(0, excelAdapter.getAll().size());
 		
		excelAdapter.add(getItem());
		
		
		Assert.assertEquals(1, excelAdapter.getAll().size());
		
	}
	
	@Test
	public void ShouldCreateMySqlAdapter() throws DocumentException{
		String userName = "root";
		String password = "test1234";
		String tableName = "user";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter mysqlAdapter =  builder.createMySQLAdapter(userName, password,"localhost" ,3306,"mesh4xdb",tableName);
		
		Assert.assertNotNull(mysqlAdapter);
//		Assert.assertEquals(0, mysqlAdapter.getAll().size());
		
//		mysqlAdapter.add(getItem());
		Assert.assertEquals(1, mysqlAdapter.getAll().size());
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
	public void shouldGenarateExceptionIfSheetNameEmptyOrNull()
	{
		String contentFile = TestHelper.fileName("contentFile.xls");
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(contentFile, "", "id");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdEmptyOrNull(){
		
		String contentFile = TestHelper.fileName("contentFile.xls");
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(contentFile, "user", "");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReturnNUllIfContenFileIsNullOrEmpty(){
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		adapterBuilder.createMsExcelAdapter(null, "user", "id");
	}
	
	private static final String SHEET_NAME = "user";
	private static final String COLUMN_PASS = "Pass";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_ID = "Id";
		
	private GoogleSpreadsheetToRDFMapping createSampleGoogleSpreadsheetToRDFMapping() {

		String sheetName = SHEET_NAME;

		RDFSchema schema = new RDFSchema(sheetName, "http://localhost:8080/mesh4x/feeds/user#", sheetName);
		schema.addStringProperty("id", "id", "en");
		schema.addStringProperty("name", "Name", "en");
		schema.addStringProperty("pass", "pass", "en");

		RDFInstance rdfInstance = schema.createNewInstance("uri:urn:id");

		long millis = System.currentTimeMillis();
		String name = "Name: " + millis;
		String code = "GSL-A219";
		String pass = "mesh4j";

		rdfInstance.setProperty(COLUMN_ID, code);
		rdfInstance.setProperty(COLUMN_NAME, name);
		rdfInstance.setProperty(COLUMN_PASS, pass);

		return new GoogleSpreadsheetToRDFMapping(schema, SHEET_NAME, "id", null, new DocsService(""));
	}		
	
	private Item getItem() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
/*		String rawDataAsXML = "<user>" +
								"<id>"+id+"</id>" +
								"<age>25</age>" +
								"<name>Marcelo</name>" +
								"<city>Buens aires</city>" +
								"<country>Argentina</country>" +
								"</user>";*/
		String rawDataAsXML = "<user>" +
				"<id>"+id+"</id>" +
				"<name>Marcelo</name>" +
				"<pass>Buensaires</pass>" +
				"</user>";
				
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}

	private Item makeRDFItem(IRDFSchema schema){		
		String id = IdGenerator.INSTANCE.newID();		
		String rawDataAsXML = "<user>" +
				"<id>"+id+"</id>" +
				"<name>Raju</name>" +
				"<pass>25</pass>" +
				"</user>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		payload = schema.getInstanceFromPlainXML(id, payload, ISchema.EMPTY_FORMATS);
		IContent content = new EntityContent(payload, "ektoo", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
}
