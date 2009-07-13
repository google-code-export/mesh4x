package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelRDFContentAdapterWithBlankInSheetNameTests {

	@Test
	public void shouldCreate(){
		MsExcel excel = new MsExcel(this.getClass().getResource("excelWithBlankInSheetName.xlsx").getFile());
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, "my sheet", new String[]{"Code"}, null, "http://localhost:8080/mesh4x/feeds");
		
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID()+".xlsx");
		File file = new File(fileName);
		Assert.assertFalse(file.exists());
		
		SplitAdapter adapter = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), NullIdentityProvider.INSTANCE, rdfSchema);
		adapter.beginSync();
		adapter.endSync();
		
		Assert.assertTrue(file.exists());
		
		MsExcelContentAdapter contentAdapter = (MsExcelContentAdapter)adapter.getContentAdapter();
		
		RDFSchema newSchema = (RDFSchema)contentAdapter.getSchema();
		Assert.assertTrue(rdfSchema.isCompatible(newSchema));
		
		Workbook wb = contentAdapter.getWorkbook();
		Sheet sheet = wb.getSheet("my_sheet");
		Row row = sheet.getRow(0);

		Assert.assertEquals("Code", row.getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals("First Name", row.getCell(1).getRichStringCellValue().getString());
		Assert.assertEquals("Middle Name", row.getCell(2).getRichStringCellValue().getString());
		Assert.assertEquals("Last Name", row.getCell(3).getRichStringCellValue().getString());
		Assert.assertEquals("Country and Nationality", row.getCell(4).getRichStringCellValue().getString());
		Assert.assertEquals("Age", row.getCell(5).getRichStringCellValue().getString());
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldGetAll(){
		
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID()+".xlsx");
				
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((MsExcelContentAdapter)adapter.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		assertItem("P1", "juan", "Marcelo", "Tondato", 28, "Argentino", items.get(0), rdfSchema, 1);
		FileUtils.delete(fileName);
	}

	@Test
	public void shouldGet(){
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID()+".xlsx");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		List<Item> items = adapter.getAll();
		Item item = adapter.get(items.get(0).getSyncId());
		
		RDFSchema rdfSchema = (RDFSchema)((MsExcelContentAdapter)adapter.getContentAdapter()).getSchema();
		
		assertItem("P1", "juan", "Marcelo", "Tondato", 28, "Argentino", item, rdfSchema, 1);
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldAdd(){
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID()+".xlsx");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((MsExcelContentAdapter)adapter.getContentAdapter()).getSchema();
		MsExcelToRDFMapping mapping = (MsExcelToRDFMapping)((MsExcelContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		assertItem("P1", "juan", "Marcelo", "Tondato", 28, "Argentino", items.get(0), rdfSchema, 1);
		
		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("Code", id);
		properties.put("First_Name", "sol");
		properties.put("Middle_Name","bia");
		properties.put("Last_Name", "juani");
		properties.put("Age", 28);
		properties.put("Country_and_Nationality", "Argento");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("P1", "juan", "Marcelo", "Tondato", 28, "Argentino", items.get(0), rdfSchema, 1);
		assertItem(id, "sol", "bia", "juani", 28, "Argento", items.get(1), rdfSchema, 1);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldUpdate(){
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID()+".xlsx");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((MsExcelContentAdapter)adapter.getContentAdapter()).getSchema();
		MsExcelToRDFMapping mapping = (MsExcelToRDFMapping)((MsExcelContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		String id = items.get(0).getContent().getId();
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("Code", id);
		properties.put("First_Name", "sol");
		properties.put("Middle_Name","bia");
		properties.put("Last_Name", "juani");
		properties.put("Age", 30);
		properties.put("Country_and_Nationality", "Argento");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item item = new Item(identifiableContent, items.get(0).getSync().clone().update("jmt", new Date(), false));
		adapter.update(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		assertItem(id, "sol", "bia", "juani", 30, "Argento", items.get(0), rdfSchema, 2);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldDelete(){
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID()+".xlsx");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((MsExcelContentAdapter)adapter.getContentAdapter()).getSchema();
		MsExcelToRDFMapping mapping = (MsExcelToRDFMapping)((MsExcelContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());

		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("Code", id);
		properties.put("First_Name", "sol");
		properties.put("Middle_Name","bia");
		properties.put("Last_Name", "juani");
		properties.put("Age", 28);
		properties.put("Country_and_Nationality", "Argento");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		item = item.clone();
		item.getSync().delete("jmt", new Date());
		
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
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldSync(){
		String fileName = TestHelper.fileName("msExcel_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);

		SplitAdapter adapter = makeAdapter(fileName+".xlsx"); 

		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
		
		FileUtils.delete(fileName+".xlsx");
		FileUtils.delete(fileName+".xml");
	}
	
	// PRIVATE 
	private SplitAdapter makeAdapter(String fileName) {
		SplitAdapter adapter = MsExcelRDFSyncAdapterFactory.createSyncAdapter(
			makeMsExcel(fileName), 
			"my sheet",
			new String[]{"Code"},
			null,
			NullIdentityProvider.INSTANCE, 
			"http://localhost:8080/mesh4x/feeds");
		return adapter;
	}
	
	private MsExcel makeMsExcel(String fileName){
		try{
			String sourceFileName = this.getClass().getResource("excelWithBlankInSheetName.xlsx").getFile();
			FileUtils.copyFile(sourceFileName, fileName);
			return new MsExcel(fileName);
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private void assertItem(String id, String firstName, String middleName, String lastName, int age, String country, Item item, RDFSchema rdfSchema, int seq) {
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isDeleted());
		Assert.assertEquals(seq, item.getLastUpdate().getSequence());
		
		Assert.assertEquals(id, item.getContent().getId());
		
		RDFInstance instance = rdfSchema.createNewInstanceFromRDFXML(item.getContent().getPayload().asXML());
		Assert.assertEquals(id, instance.getId());
		Assert.assertEquals(id, instance.getPropertyValue("Code"));
		Assert.assertEquals(firstName, instance.getPropertyValue("First_Name"));
		Assert.assertEquals(middleName, instance.getPropertyValue("Middle_Name"));
		Assert.assertEquals(lastName, instance.getPropertyValue("Last_Name"));
		Assert.assertEquals(age, instance.getPropertyValue("Age"));
		Assert.assertEquals(country, instance.getPropertyValue("Country_and_Nationality"));

		
		
	}
}
