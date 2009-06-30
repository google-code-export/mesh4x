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
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class HibernateMultiKeyTests {

	//@Test
	public void shouldTest() throws Exception{
		SplitAdapter adapter = makeAdapter();
		
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		for (Item item : items) {
			System.out.println(item.getContent().getPayload().asXML());
			System.out.println(mapping.convertXMLToRow(item.getContent().getPayload()).asXML());
		}
		
		String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:mesh_multi_key=\"http://localhost:8080/mesh4x/feeds/mesh_multi_key#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><mesh_multi_key:mesh_multi_key rdf:about=\"uri:urn:1-3\"><mesh_multi_key:id1 rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</mesh_multi_key:id1><mesh_multi_key:id2 rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">3</mesh_multi_key:id2><mesh_multi_key:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">sol</mesh_multi_key:name></mesh_multi_key:mesh_multi_key></rdf:RDF>";
		Item item = new Item(new IdentifiableContent(XMLHelper.parseElement(xml), mapping, "1-3"), new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);
	}

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
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
	
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
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();

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
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();

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
		String connectionUri = "jdbc:mysql://localhost:3306/mesh4xdb";

		SplitAdapter adapter = HibernateSyncAdapterFactory.createHibernateAdapter(
				connectionUri,
				"root",
				"",
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class, 
				"mesh_multi_key", 
				"http://localhost:8080/mesh4x/feeds",
				TestHelper.baseDirectoryForTest(),
				NullIdentityProvider.INSTANCE);
		return adapter;
	}
}
