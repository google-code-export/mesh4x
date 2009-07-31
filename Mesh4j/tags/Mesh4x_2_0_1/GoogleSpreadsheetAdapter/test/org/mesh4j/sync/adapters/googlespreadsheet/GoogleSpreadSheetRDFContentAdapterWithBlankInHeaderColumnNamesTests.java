package org.mesh4j.sync.adapters.googlespreadsheet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.ServiceException;


/**
 * @author sharif uddin
 *
 */
public class GoogleSpreadSheetRDFContentAdapterWithBlankInHeaderColumnNamesTests {

	private static SpreadsheetService service;
	private static DocsService docService;
	private static FeedURLFactory factory;
	
	private static final String SHEET_NAME = "user";
	String spreadsheetFileName = "GoogleSpreadSheetRDFContentAdapterBlankInColumnHeaderTest";
	
	@BeforeClass
	public static void setUp() throws Exception {
		String username = "gspreadsheet.test@gmail.com";
		String password = "java123456";
		service = GoogleSpreadsheetUtils.getSpreadsheetService(username, password);
		docService = GoogleSpreadsheetUtils.getDocService(username, password);
		factory = FeedURLFactory.getDefault();		
	}
	
	@Test
	public void shouldCreate(){
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);
		adapter.beginSync();
		adapter.endSync();
		
		GoogleSpreadSheetContentAdapter contentAdapter = (GoogleSpreadSheetContentAdapter) adapter
				.getContentAdapter();
		
		GSWorksheet<GSRow<GSCell>> gws = contentAdapter.getWorkSheet();
		GSRow<GSCell> row = gws.getGSRow(1);

		Assert.assertEquals("User Id", row.getGSCell(1).getCellValue());
		Assert.assertEquals("First Name", row.getGSCell(2).getCellValue());
		Assert.assertEquals("Last Name", row.getGSCell(3).getCellValue());
		Assert.assertEquals("Pass", row.getGSCell(4).getCellValue());
	}
	
	@Test
	public void shouldGet(){
		
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();

		//prepare/update the spreadsheet for this specific test
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(SHEET_NAME);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			addTestRow(workSheet, "P1", "Sharif", "Uddin", "mesh4x");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		
		gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		assertItem("P1", "Sharif", "Uddin", "mesh4x", items.get(0), rdfSchema, 1);
	}
	
	@Test
	public void shouldGetAll() throws IOException, ServiceException{
		
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();

		//prepare/update the spreadsheet for this specific test
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(SHEET_NAME);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		addTestRow(workSheet, "P1", "Sharif", "Uddin", "mesh4x");
		addTestRow(workSheet, "P2", "Saiful", "Islam", "geochat");	
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		
		gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("P1", "Sharif", "Uddin", "mesh4x", items.get(0), rdfSchema, 1);
		assertItem("P2", "Saiful", "Islam", "geochat", items.get(1), rdfSchema, 1);
	}
	
	@Test
	public void shouldAdd(){
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();

		//prepare/update the spreadsheet for this specific test
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(SHEET_NAME);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			addTestRow(workSheet, "P1", "Sharif", "Uddin", "mesh4x");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		
		gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getSchema();
		GoogleSpreadsheetToRDFMapping mapping = (GoogleSpreadsheetToRDFMapping)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getMapper();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		assertItem("P1", "Sharif", "Uddin", "mesh4x", items.get(0), rdfSchema, 1);
		
		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("User_Id", id);
		properties.put("First_Name", "Saiful");
		properties.put("Last_Name", "Islam");
		properties.put("Pass", "geochat");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "sharif", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		
		assertItem("P1", "Sharif", "Uddin", "mesh4x", items.get(0), rdfSchema, 1);
		assertItem(id, "Saiful", "Islam", "geochat", items.get(1), rdfSchema, 1);
	}
	
	@Test
	public void shouldUpdate(){
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();

		//prepare/update the spreadsheet for this specific test
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(SHEET_NAME);
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		GSWorksheet<GSRow> syncWorkSheet = gss.getGSWorksheet(SHEET_NAME+"_sync");
		if(syncWorkSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: syncWorkSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					syncWorkSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			addTestRow(workSheet, "P1", "Sharif", "Uddin", "mesh4x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		
		gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getSchema();
		GoogleSpreadsheetToRDFMapping mapping = (GoogleSpreadsheetToRDFMapping)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getMapper();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		String id = items.get(0).getContent().getId();
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("User_Id", id);
		properties.put("First_Name", "Saiful");
		properties.put("Last_Name", "Islam");
		properties.put("Pass", "geochat");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, id);
		Item item = new Item(identifiableContent, items.get(0).getSync().clone().update("sharif", new Date(), false));
		adapter.update(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		assertItem(id, "Saiful", "Islam", "geochat", items.get(0), rdfSchema, 2);
	}
	
	@Test
	public void shouldDelete(){
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();

		//prepare/update the spreadsheet for this specific test
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(SHEET_NAME);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			addTestRow(workSheet, "P1", "Sharif", "Uddin", "mesh4x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		
		gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getSchema();
		GoogleSpreadsheetToRDFMapping mapping = (GoogleSpreadsheetToRDFMapping)((GoogleSpreadSheetContentAdapter)adapter.getContentAdapter()).getMapper();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());

		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("User_Id", id);
		properties.put("First_Name", "Saiful");
		properties.put("Last_Name", "Islam");
		properties.put("Pass", "geochat");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "sharif", new Date(), false));
		adapter.add(item);	
		
		item = item.clone();
		item.getSync().delete("sharif", new Date());
		
		adapter.update(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertTrue(items.get(1).isDeleted());
				
		adapter.delete(items.get(0).getSyncId());
				
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertTrue(items.get(0).isDeleted());
		Assert.assertTrue(items.get(1).isDeleted());
	}
	
	@Test
	public void shouldSync(){
		IGoogleSpreadSheet gss = getSampleGoogleSpreadsheet();

		//prepare/update the spreadsheet for this specific test
		GSWorksheet<GSRow> workSheet = gss.getGSWorksheet(SHEET_NAME);
		
		if(workSheet.getChildElements().size() > 1){
			for(Map.Entry<String, GSRow> rowEntry: workSheet.getChildElements().entrySet()){
				if(rowEntry.getValue().getElementListIndex() > 1)
					workSheet.deleteChildElement(rowEntry.getKey());
			}
		}
		
		try {
			addTestRow(workSheet, "P1", "Sharif", "Uddin", "mesh4x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GoogleSpreadsheetUtils.flush(service, gss.getGSSpreadsheet());	
		
		gss = getSampleGoogleSpreadsheet();
		SplitAdapter adapter = makeAdapter(gss);		

		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);

		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		TestHelper.assertSync(syncEngine);
		
		FileUtils.delete(fileName);
	}

	private IGoogleSpreadSheet getSampleGoogleSpreadsheet() {
		try {
			GSSpreadsheet gss = GoogleSpreadsheetUtils.getOrCreateGSSpreadsheetIfAbsent(
					factory, service, docService, spreadsheetFileName);
			
			IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(docService,
					service, factory, spreadsheetFileName, gss);
			
			return spreadSheet;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return null;
	}
		
	private GoogleSpreadsheetToRDFMapping getSampleGoogleSpreadsheetToRDFMapping() {
		RDFSchema schema = new RDFSchema(SHEET_NAME, "http://localhost:8080/mesh4x/feeds/"
				+SHEET_NAME+"#", SHEET_NAME);
		schema.addStringProperty("User_Id", "User Id", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("First_Name", "First Name", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("Last_Name", "Last Name", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("Pass", "Pass", IRDFSchema.DEFAULT_LANGUAGE);
		schema.setIdentifiablePropertyNames(Arrays.asList(new String[]{"User Id"}));

		return new GoogleSpreadsheetToRDFMapping(schema);
	}
	
	private SplitAdapter makeAdapter(IGoogleSpreadSheet spreadSheet) {
		GoogleSpreadSheetRDFSyncAdapterFactory factory = new GoogleSpreadSheetRDFSyncAdapterFactory(
				"http://localhost:8080/mesh4x/feeds");

		return factory.createSyncAdapter(spreadSheet, SHEET_NAME,
				new String[] { "User Id" }, null,
				NullIdentityProvider.INSTANCE,
				getSampleGoogleSpreadsheetToRDFMapping().getSchema(),
				SHEET_NAME);
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
	
	private void assertItem(String id, String firstName, String lastName, String pass, Item item, RDFSchema rdfSchema, int seq) {
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isDeleted());
		Assert.assertEquals(seq, item.getLastUpdate().getSequence());
		
		Assert.assertEquals(id, item.getContent().getId());
		
		RDFInstance instance = rdfSchema.createNewInstanceFromRDFXML(item.getContent().getPayload().asXML());
		Assert.assertEquals(id, instance.getId());
		Assert.assertEquals(id, instance.getPropertyValue("User_Id"));
		Assert.assertEquals(firstName, instance.getPropertyValue("First_Name"));
		Assert.assertEquals(lastName, instance.getPropertyValue("Last_Name"));
		Assert.assertEquals(pass, instance.getPropertyValue("Pass"));
	}	
}
