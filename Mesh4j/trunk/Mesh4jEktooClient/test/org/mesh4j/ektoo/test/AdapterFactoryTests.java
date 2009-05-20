package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.GoogleSpreadsheetToPlainXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MSExcelToPlainXMLMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;

@Deprecated
public class AdapterFactoryTests {

	

	//@Test
	public void shouldCreateHttpAdapter() {
		String url = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter adapter = new HttpSyncAdapter(url,
				RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE, ContentWriter.INSTANCE,
				ContentReader.INSTANCE);
		// Assert.assertEquals(6, adapter.getAll());
		Assert.assertTrue(adapter.getAll().size() > 0);
	}

//	@Test
	public void shouldCreateMsAccessAdapterWithRDF() throws Exception {
		MsAccessSyncAdapterFactory adapterFactory = new MsAccessSyncAdapterFactory(
				TestHelper.baseDirectoryForTest(),
				"http://mesh4x/feeds/grammen");
		SplitAdapter syncAdapter = adapterFactory.createSyncAdapterFromFile(
				"ektoo", TestHelper.baseDirectoryForTest() + "\\Ektoo.mdb",
				"ektoo");

		HibernateContentAdapter contentAdapter = (HibernateContentAdapter) syncAdapter
				.getContentAdapter();
		HibernateToRDFMapping mapping = (HibernateToRDFMapping) contentAdapter
				.getMapping();
		IRDFSchema schema = mapping.getSchema();
		Assert.assertEquals(0, syncAdapter.getAll().size());

		syncAdapter.add(makeRDFItem(schema));

		Assert.assertEquals(1, syncAdapter.getAll().size());
	}

//	@Test
	public void shouldCreateMsAccessAdapterWithoutRDF() throws Exception {
		MsAccessSyncAdapterFactory adapterFactory = new MsAccessSyncAdapterFactory(
				TestHelper.baseDirectoryForTest(), null);
		SplitAdapter syncAdapter = adapterFactory.createSyncAdapterFromFile(
				"aktoo", TestHelper.baseDirectoryForTest() + "\\aktoo.mdb",
				"aktoo");

		Assert.assertEquals(0, syncAdapter.getAll().size());

		syncAdapter.add(getEntityItem());

		Assert.assertEquals(1, syncAdapter.getAll().size());
	}

//	@Test
	public void shouldCreateMsExcelAdapter() throws IOException,
			DocumentException {

		SplitAdapter excelAdapter = createMsExcelAdapter("user", "id",
				"excelA.xls", "syncA.xls", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);

		Assert.assertEquals(0, excelAdapter.getAll().size());

		excelAdapter.add(getItem());

		Assert.assertEquals(1, excelAdapter.getAll().size());
	}

//	@Test
	public void shouldCreateFeedAdapter() {
		File file = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()
				+ ".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE,
				NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, new Feed());
		Feed feed = repo.getFeed();

		Assert.assertNotNull(feed);
		Assert.assertTrue(feed.getItems().isEmpty());
	}

//	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateGoogleSpreadSheetAdapter() throws DocumentException {

		String idColumName = "id";
		String userName = "gspreadsheet.test@gmail.com";
		String passWord = "java123456";
		String GOOGLE_SPREADSHEET_FIELD = "peo4fu7AitTo8e3v0D8FCew";

		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD, userName, passWord);
		GSWorksheet sourceRepo = spreadsheet.getGSWorksheet(1);

		IGoogleSpreadsheetToXMLMapping mapper = new GoogleSpreadsheetToPlainXMLMapping("user", idColumName,
													null,sourceRepo.getName(), 
													spreadsheet.getDocsService());
				
		
		SplitAdapter spreadSheetAdapter = GoogleSpreadsheetUtils
			.createGoogleSpreadSheetAdapter(spreadsheet, mapper,
												NullIdentityProvider.INSTANCE,
												IdGenerator.INSTANCE);

		int size = spreadSheetAdapter.getAll().size();
		spreadSheetAdapter.add(getItem());

		Assert.assertEquals(size + 1,spreadSheetAdapter.getAll().size());

	}
	

	private Item makeRDFItem(IRDFSchema schema) {

		String id = IdGenerator.INSTANCE.newID();

		// String rawDataAsXML =
		// "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:aktoo=\"http://mesh4x/feeds/grammen/aktoo#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"
		// +
		// "<aktoo:aktoo rdf:about=\"uri:urn:"+ id +"\">"+
		// "<aktoo:Name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">jose</aktoo:Name>"
		// +
		// "<aktoo:Age rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">30</aktoo:Age>"
		// +
		//"<aktoo:ID rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+
		// id +"</aktoo:ID>"+
		// "</aktoo:aktoo>"+
		// "</rdf:RDF>";
		//		
		String rawDataAsXML = "<aktoo>" + "<ID>" + id + "</ID>"
				+ "<Name>Raju</Name>" + "<Age>25</Age>" + "</aktoo>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		payload = schema.getInstanceFromPlainXML(id, payload,
				ISchema.EMPTY_FORMATS);

		IContent content = new EntityContent(payload, "aktoo", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(),
				false);
		return new Item(content, sync);
	}

	private Item getEntityItem() throws DocumentException {

		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<aktoo>" + "<ID>" + id + "</ID>"
				+ "<Name>Raju</Name>" + "<Age>25</Age>" + "</aktoo>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		System.out.println("xml as:" + payload.asXML());
		IContent content = new EntityContent(payload, "aktoo", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(),
				false);
		return new Item(content, sync);
	}

	private Item getItem() throws DocumentException {

		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" + 
								"<id>" + id + "</id>"
								+"<name>Raju</name>" + 
								"<age>25</age>" + 
								"<city>Dhaka</city>"
								+ "<country>Bangladesh</country>" +
								"</user>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(),
				false);
		return new Item(content, sync);
	}

	private SplitAdapter createMsExcelAdapter(String sheetName,
			String idColumnName, String contentFileName, String syncFileName,
			IIdentityProvider identityProvider, IdGenerator idGenerator)
			throws IOException {

		MsExcel contentExcel = null;
		MsExcel syncExcel = null;
		if (contentFileName.equals(syncFileName)) {
			File file = TestHelper.makeFileAndDeleteIfExists(contentFileName);
			contentExcel = new MsExcel(file.getAbsolutePath());
			syncExcel = contentExcel;
		} else {
			File fileData = TestHelper
					.makeFileAndDeleteIfExists(contentFileName);
			File fileSync = TestHelper.makeFileAndDeleteIfExists(syncFileName);

			contentExcel = new MsExcel(fileData.getAbsolutePath());
			syncExcel = new MsExcel(fileSync.getAbsolutePath());
		}

		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(syncExcel,sheetName+"_sync",
				identityProvider, idGenerator);
		MSExcelToPlainXMLMapping mapper = new MSExcelToPlainXMLMapping(
				idColumnName, null);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(
				contentExcel, mapper, sheetName);

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter,
				identityProvider);
		return splitAdapter;
	}
}
