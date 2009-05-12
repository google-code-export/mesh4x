package org.mesh4j.ektoo.test;

import java.io.File;
import java.util.Date;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class SyncAdapterBuilderTest {

	
	@Test
	public void shouldCreateGoogleSpreadSheetAdapter(){
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"pLUqch-enpf1-GcqnD6qjSA",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter syncAdapterA = adapterBuilder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
		
		Assert.assertEquals(0,syncAdapterA.getAll().size());
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
		Assert.assertEquals(0, kmlAdapter.getAll().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfSheetNameEmptyOrNull()
	{
		// TODO remove folder harcode
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("", "id", contentFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdEmptyOrNull(){
		// TODO remove folder harcode
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "", contentFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfContenFileIsNull(){
		// TODO remove folder harcode
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", "");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfSyncFileIsNull(){
		// TODO remove folder harcode
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdentityIsNull(){
		// TODO remove folder harcode
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdGeneratorIsNull(){
		// TODO remove folder harcode
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile);
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
	private Item makeRDFItem(IRDFSchema schema){
		
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
