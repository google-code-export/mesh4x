package org.mesh4j.sync.adapters.csv;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.csv.CSVContentAdapter;
import org.mesh4j.sync.adapters.csv.CSVFile;
import org.mesh4j.sync.adapters.csv.CSVSyncAdapterFactory;
import org.mesh4j.sync.adapters.csv.CSVToRDFMapping;
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

public class CSVSyncAdapterTests {

	@Test
	public void shouldCreate(){
		RDFSchema rdfSchema = new RDFSchema("sheet1", "http://localhost:8080/mesh4x/feeds/sheet1#", "sheet1");
		rdfSchema.addStringProperty("id1", "id1", "en");
		rdfSchema.addStringProperty("comment", "comment", "en");
		rdfSchema.addStringProperty("name", "name", "en");
		rdfSchema.setIdentifiablePropertyNames(Arrays.asList(new String[]{"id1"}));
				
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID()+".csv");
		File file = new File(fileName);
		Assert.assertFalse(file.exists());
		
		SplitAdapter adapter = CSVSyncAdapterFactory.createSyncAdapter(fileName, NullIdentityProvider.INSTANCE, rdfSchema);
		adapter.beginSync();
		adapter.endSync();
		
		Assert.assertTrue(file.exists());
		
		CSVContentAdapter contentAdapter = (CSVContentAdapter)adapter.getContentAdapter();
		
		RDFSchema newSchema = (RDFSchema)contentAdapter.getSchema();
		Assert.assertTrue(rdfSchema.isCompatible(newSchema));
		
		CSVFile csvfile = contentAdapter.getCSVFile();

		String[] columnNames = csvfile.getHeader().getColumnNames();
		Assert.assertEquals("id1", columnNames[0]);
		Assert.assertEquals("comment", columnNames[1]);
		Assert.assertEquals("name", columnNames[2]);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldGetAll(){
		
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID()+".csv");
				
		SplitAdapter adapter = makeAdapter(fileName);
		
		adapter.beginSync();
		List<Item> items = adapter.getAll();
		adapter.endSync();
		
		RDFSchema rdfSchema = (RDFSchema)((CSVContentAdapter)adapter.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("2", "2", "jmt", items.get(0), rdfSchema, 1);
		assertItem("1", "1", "bia", items.get(1), rdfSchema, 1);
		FileUtils.delete(fileName);
	}

	@Test
	public void shouldGet(){
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID()+".csv");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		List<Item> items = adapter.getAll();
		Item item = adapter.get(items.get(1).getSyncId());
		
		RDFSchema rdfSchema = (RDFSchema)((CSVContentAdapter)adapter.getContentAdapter()).getSchema();
		
		assertItem("1", "1", "bia", item, rdfSchema, 1);
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldAdd(){
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID()+".csv");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((CSVContentAdapter)adapter.getContentAdapter()).getSchema();
		CSVToRDFMapping mapping = (CSVToRDFMapping)((CSVContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("2", "2", "jmt", items.get(0), rdfSchema, 1);
		assertItem("1", "1", "bia", items.get(1), rdfSchema, 1);
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id1", "3");
		properties.put("comment", "3");
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("3", properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, "3");
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		adapter.endSync();
		
		adapter.beginSync();		
		items = adapter.getAll();
		adapter.endSync();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());
		assertItem("2", "2", "jmt", items.get(0), rdfSchema, 1);
		assertItem("1", "1", "bia", items.get(1), rdfSchema, 1);
		assertItem("3", "3", "sol", items.get(2), rdfSchema, 1);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldUpdate(){
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID()+".csv");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((CSVContentAdapter)adapter.getContentAdapter()).getSchema();
		CSVToRDFMapping mapping = (CSVToRDFMapping)((CSVContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("2", "2", "jmt", items.get(0), rdfSchema, 1);
		assertItem("1", "1", "bia", items.get(1), rdfSchema, 1);
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id1", "2");
		properties.put("comment", "2");
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("2", properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, "2");
		Item item = new Item(identifiableContent, items.get(0).getSync().clone().update("jmt", new Date(), false));
		adapter.update(item);	
		adapter.endSync();
		
		adapter.beginSync();
		items = adapter.getAll();

		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("2", "2", "sol", items.get(0), rdfSchema, 2);
		assertItem("1", "1", "bia", items.get(1), rdfSchema, 1);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldDelete(){
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID()+".csv");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((CSVContentAdapter)adapter.getContentAdapter()).getSchema();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("2", "2", "jmt", items.get(0), rdfSchema, 1);
		assertItem("1", "1", "bia", items.get(1), rdfSchema, 1);
		
		Item item = items.get(0).clone();
		item.getSync().delete("jmt", new Date());
		
		adapter.update(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertTrue(items.get(1).isDeleted());
		Assert.assertFalse(items.get(0).isDeleted());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		
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
		String fileName = TestHelper.fileName("CSV_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);
		

		SplitAdapter adapter = CSVSyncAdapterFactory.createSyncAdapter(
			makeCSVFile(fileName+".csv"), 
			new String[]{"id1"},
			null,
			NullIdentityProvider.INSTANCE, 
			"http://localhost:8080/mesh4x/feeds");

		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
	}
	
	// PRIVATE 
	
	private SplitAdapter makeAdapter(String fileName) {
		SplitAdapter adapter = CSVSyncAdapterFactory.createSyncAdapter(
			makeCSVFile(fileName), 
			new String[]{"id1"},
			null,
			NullIdentityProvider.INSTANCE, 
			"http://localhost:8080/mesh4x/feeds");
		return adapter;
	}
	
	private String makeCSVFile(String fileName){
		try{
			String sourceFileName = this.getClass().getResource("CSV_example.csv").getFile();
			FileUtils.copyFile(sourceFileName, fileName);
			return fileName;
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private void assertItem(String id, String comment, String name, Item item, RDFSchema rdfSchema, int seq) {
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isDeleted());
		Assert.assertEquals(seq, item.getLastUpdate().getSequence());
		
		Assert.assertEquals(id, item.getContent().getId());
		
		RDFInstance instance = rdfSchema.createNewInstanceFromRDFXML(item.getContent().getPayload().asXML());
		Assert.assertEquals(id, instance.getId());
		Assert.assertEquals(id, instance.getPropertyValue("id1"));
		Assert.assertEquals(comment, instance.getPropertyValue("comment"));
		Assert.assertEquals(name, instance.getPropertyValue("name"));
		
	}
}
