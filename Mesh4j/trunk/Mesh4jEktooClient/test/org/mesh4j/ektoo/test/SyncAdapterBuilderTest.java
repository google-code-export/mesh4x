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
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

public class SyncAdapterBuilderTest {

	
	
	@Test
	public void ShouldCreateFolderSyncAdapter() throws IOException{
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter folderAdapter = adapterBuilder.createFolderAdapter(TestHelper.baseDirectoryForTest() + "sourcefolder");
		Assert.assertNotNull(folderAdapter);
		Assert.assertNotNull(folderAdapter.getAll());
	}
	
	/**
	 * This test assumes you have a worksheet named "user_source" in your spreadsheet
	 * with the provided gmail account(gspreadsheet.test@gmail.com)
	 */
	@Test
	public void shouldCreateGoogleSpreadSheetAdapter(){
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		
		IGoogleSpreadSheet gss = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetId(), spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
		IRDFSchema rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(gss, spreadSheetInfo.getSheetName(), rdfUrl);
			
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapterA = adapterBuilder.createGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
		Assert.assertNotNull(syncAdapterA);
		
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
	
	
	
	private Item getItem() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" +
								"<id>"+id+"</id>" +
								"<age>25</age>" +
								"<name>Marcelo</name>" +
								"<city>Buens aires</city>" +
								"<country>Argentina</country>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
	protected Item makeRDFItem(IRDFSchema schema){
		
		String id = IdGenerator.INSTANCE.newID();
		
		String rawDataAsXML = "<ektoo>" +
		"<ID>"+id+"</ID>" +
		"<Name>Raju</Name>" +
		"<Age>25</Age>" +
		"</ektoo>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		payload = schema.getInstanceFromPlainXML(id, payload, ISchema.EMPTY_FORMATS);
		
		IContent content = new EntityContent(payload, "ektoo", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
}
