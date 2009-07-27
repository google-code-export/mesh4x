package org.mesh4j.sync.adapters.msaccess;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccess;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessContentAdapter;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessJackcessSyncAdapterFactory;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessToRDFMapping;
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

public class MsAccess2000RDFContentAdapterWithBlankInHeaderColumnNamesTests {

	@Test
	public void shouldGetAll(){
		
		String fileName = TestHelper.fileName("msAccess_blanks"+IdGenerator.INSTANCE.newID()+".mdb");
				
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		List<Item> items = adapter.getAll();
		
		RDFSchema rdfSchema = (RDFSchema)((MsAccessContentAdapter)adapter.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		assertItem("2", "2", "jmt", items.get(1), rdfSchema, 1);
		FileUtils.delete(fileName);
	}

	@Test
	public void shouldGet(){
		String fileName = TestHelper.fileName("msAccess_blanks"+IdGenerator.INSTANCE.newID()+".mdb");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		List<Item> items = adapter.getAll();
		Item item = adapter.get(items.get(0).getSyncId());
		
		RDFSchema rdfSchema = (RDFSchema)((MsAccessContentAdapter)adapter.getContentAdapter()).getSchema();
		
		assertItem("1", "1", "bia", item, rdfSchema, 1);
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldAdd(){
		String fileName = TestHelper.fileName("msAccess_blanks"+IdGenerator.INSTANCE.newID()+".mdb");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((MsAccessContentAdapter)adapter.getContentAdapter()).getSchema();
		MsAccessToRDFMapping mapping = (MsAccessToRDFMapping)((MsAccessContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		assertItem("2", "2", "jmt", items.get(1), rdfSchema, 1);
		
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("My_Code", "3");
		properties.put("my_Comment", "3");
		properties.put("my_name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("3", properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, "3");
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		assertItem("2", "2", "jmt", items.get(1), rdfSchema, 1);		
		assertItem("3", "3", "sol", items.get(2), rdfSchema, 1);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldUpdate(){
		String fileName = TestHelper.fileName("msAccess_blanks"+IdGenerator.INSTANCE.newID()+".mdb");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((MsAccessContentAdapter)adapter.getContentAdapter()).getSchema();
		MsAccessToRDFMapping mapping = (MsAccessToRDFMapping)((MsAccessContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		assertItem("2", "2", "jmt", items.get(1), rdfSchema, 1);
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("My_Code", "2");
		properties.put("my_Comment", "2");
		properties.put("my_name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("2", properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping, "2");
		Item item = new Item(identifiableContent, items.get(1).getSync().clone().update("jmt", new Date(), false));
		adapter.update(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		assertItem("2", "2", "sol", items.get(1), rdfSchema, 2);
		
		FileUtils.delete(fileName);
	}
	
	@Test
	public void shouldDelete(){
		String fileName = TestHelper.fileName("msAccess_blanks"+IdGenerator.INSTANCE.newID()+".mdb");
		
		SplitAdapter adapter = makeAdapter(fileName);
		adapter.beginSync();
		
		RDFSchema rdfSchema = (RDFSchema)((MsAccessContentAdapter)adapter.getContentAdapter()).getSchema();
		
		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		assertItem("1", "1", "bia", items.get(0), rdfSchema, 1);
		assertItem("2", "2", "jmt", items.get(1), rdfSchema, 1);
		
		Item item = items.get(1).clone();
		item.getSync().delete("jmt", new Date());
		
		adapter.update(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertTrue(items.get(1).isDeleted());
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
		String fileName = TestHelper.fileName("msAccess_multikey_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);
		
		SplitAdapter adapter = makeAdapter(fileName);

		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
	}
	
	// PRIVATE 

	private SplitAdapter makeAdapter(String fileName) {
		return MsAccessJackcessSyncAdapterFactory.createSyncAdapter(
			makeMsAccess(fileName), 
			"mytable",
			NullIdentityProvider.INSTANCE, 
			"http://localhost:8080/mesh4x/feeds");
	}
	
	private MsAccess makeMsAccess(String fileName){
		try{
			String sourceFileName = this.getClass().getResource("msAccess2000WithBlankInHeader.mdb").getFile();
			FileUtils.copyFile(sourceFileName, fileName);
			return new MsAccess(fileName);
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
		Assert.assertEquals(id, instance.getPropertyValue("My_Code"));
		Assert.assertEquals(comment, instance.getPropertyValue("my_Comment"));
		Assert.assertEquals(name, instance.getPropertyValue("my_name"));
		
	}
}
