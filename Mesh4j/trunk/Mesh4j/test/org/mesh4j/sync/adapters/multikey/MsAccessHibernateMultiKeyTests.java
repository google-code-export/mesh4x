package org.mesh4j.sync.adapters.multikey;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateMsAccessToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
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

public class MsAccessHibernateMultiKeyTests {

	@Test
	public void shouldGetAll(){
		SplitAdapter adapter = makeAdapter();
		//RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();

		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		//assertItem("1,2", "1", "2", "jmt", items.get(0), rdfSchema, 1);
		//assertItem("1,1", "1", "1", "bia", items.get(1), rdfSchema, 1);
	}

	@Test
	public void shouldGet(){
		SplitAdapter adapter = makeAdapter();
		List<Item> items = adapter.getAll();
		TestHelper.assertItem(items.get(0), adapter);
	}
	
	@Test
	public void shouldAdd(){
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
	
		List<Item> items = adapter.getAll();
		int size = items.size();
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		
		String id2 = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id1", "1");
		properties.put("id2", id2);
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("1,"+id2, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, "1,"+id2);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(size +1, items.size());
		
		TestHelper.assertItem(item, adapter);
	}
	
	@Test
	public void shouldUpdate(){
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();

		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		int size = items.size();
		
		String id2 = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id1", "1");
		properties.put("id2", id2);
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("1,"+id2, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, "1,"+id2);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(size+1, items.size());
		size = items.size();
		
		properties = new HashMap<String, Object>();
		properties.put("id1", "1");
		properties.put("id2", id2);
		properties.put("name", "name"+IdGenerator.INSTANCE.newID());
		instance = rdfSchema.createNewInstanceFromProperties("1,"+id2, properties);
		
		identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, "1,"+id2);
		Item itemToUpdate = new Item(identifiableContent, item.getSync().clone().update("jmt", new Date(), false));
		adapter.update(itemToUpdate);	
		
		items = adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(size, items.size());
		
		TestHelper.assertItem(itemToUpdate, adapter);
	}
	
	@Test
	public void shouldDelete(){
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();

		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		int size = items.size();
		
		String id2 = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id1", "1");
		properties.put("id2", id2);
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties("1,"+id2, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, "1,"+id2);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		Item itemToDelete = item.clone();
		itemToDelete.getSync().delete("jmt", new Date());
		adapter.update(itemToDelete);	
		
		Item resultItem = adapter.get(item.getSyncId());
		Assert.assertTrue(resultItem.isDeleted());
		
		id2 = IdGenerator.INSTANCE.newID();
		properties = new HashMap<String, Object>();
		properties.put("id1", "1");
		properties.put("id2", id2);
		properties.put("name", "sol");
		instance = rdfSchema.createNewInstanceFromProperties("1,"+id2, properties);
		
		identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, "1,"+id2);
		item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		adapter.delete(item.getSyncId());

		resultItem = adapter.get(item.getSyncId());
		Assert.assertTrue(resultItem.isDeleted());
				
		items = adapter.getAll();
			
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(size + 2, items.size());
		
	}
	
	@Test
	public void shouldSync(){
		String fileName = TestHelper.fileName("msExcel_multikey_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);

		SplitAdapter adapter = makeAdapter();

		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
	}
	
	// PRIVATE
	
	private SplitAdapter makeAdapter() {
		try{
			String tableName = "mesh_multi_key";
			String fileName = TestHelper.fileName("msAccess_multikey_1_"+IdGenerator.INSTANCE.newID()+".mdb");
			String mdbFileName = getMsAccessFileNameToTest(fileName);
			
			MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://localhost:8008/mesh4x/feeds");
			
			SplitAdapter adapter = factory.createSyncAdapterFromFile(tableName, mdbFileName, tableName, NullIdentityProvider.INSTANCE);
			return adapter;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private String getMsAccessFileNameToTest(String fileName) {
		try{
			String localFileName = this.getClass().getResource("DevDB2003.mdb").getFile();
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	
	
}
