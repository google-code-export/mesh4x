package org.mesh4j.sync.filter;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class FilterQueryInAdapterTests {

	@Test
	public void shouldSyncAdapterGetAllFilterByDynamicCondition(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("birthDate", "birth date", IRDFSchema.DEFAULT_LANGUAGE);
		
		RDFInstance rdfInstance1 = rdfSchema.createNewInstance("uri:urn:"+ IdGenerator.INSTANCE.newID());
		rdfInstance1.setProperty("name", "JMT");
		rdfInstance1.setProperty("birthDate", "1974-06-24T10:00:00Z");
		
		RDFInstance rdfInstance2 = rdfSchema.createNewInstance("uri:urn:"+ IdGenerator.INSTANCE.newID());
		rdfInstance2.setProperty("name", "MSA");
		rdfInstance2.setProperty("birthDate", "1976-12-23T10:00:00Z");
		
		RDFInstance rdfInstance3 = rdfSchema.createNewInstance("uri:urn:"+ IdGenerator.INSTANCE.newID());
		rdfInstance3.setProperty("name", "JIT");
		rdfInstance3.setProperty("birthDate", "1998-01-03T10:00:00Z");
		
		RDFInstance rdfInstance4 = rdfSchema.createNewInstance("uri:urn:"+ IdGenerator.INSTANCE.newID());
		rdfInstance4.setProperty("name", "BIT");
		rdfInstance4.setProperty("birthDate", "2003-12-12T10:00:00Z");
		
		Item item1 = makeItem(rdfInstance1.asElementRDFXML());
		Item item2 = makeItem(rdfInstance2.asElementRDFXML());
		Item item3 = makeItem(rdfInstance3.asElementRDFXML());
		Item item4 = makeItem(rdfInstance4.asElementRDFXML());
		
		List<Item> items = new ArrayList<Item>();
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
				
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("adapter", NullIdentityProvider.INSTANCE, items);
		Assert.assertNotNull(adapter);
		
		FilterQuery fq = new FilterQuery("name=JMT and birthDate=1974-06-24T10:00:00Z", rdfSchema);
		List<Item> result = adapter.getAll(fq);
		
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());

		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter("result", NullIdentityProvider.INSTANCE, result);
		TestHelper.assertItem(item1, inMemoryAdapter);
		
		fq = new FilterQuery("birthDate>1980-01-01T01:00:00Z", rdfSchema);
		result = adapter.getAll(fq);
		
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(2, result.size());

		inMemoryAdapter = new InMemorySyncAdapter("result", NullIdentityProvider.INSTANCE, result);
		TestHelper.assertItem(item3, inMemoryAdapter);
		TestHelper.assertItem(item4, inMemoryAdapter);
	}
	
	@Test
	public void shouldHttpSyncGetAllFilterByDynamicConditionWithIdentidiableID(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("birthDate", "birth date", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.setIdentifiablePropertyName("id");
		
		String id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance1 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance1.setProperty("id", id);
		rdfInstance1.setProperty("name", "JMT");
		rdfInstance1.setProperty("birthDate", DateHelper.parseW3CDateTime("1974-06-24T10:00:00Z"));
		
		id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance2 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance2.setProperty("id", id);
		rdfInstance2.setProperty("name", "MSA");
		rdfInstance2.setProperty("birthDate", DateHelper.parseW3CDateTime("1976-12-23T10:00:00Z"));
		
		id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance3 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance3.setProperty("id", id);
		rdfInstance3.setProperty("name", "JIT");
		rdfInstance3.setProperty("birthDate", DateHelper.parseW3CDateTime("1998-01-03T10:00:00Z"));
		
		id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance4 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance4.setProperty("id", id);
		rdfInstance4.setProperty("name", "BIT");
		rdfInstance4.setProperty("birthDate", DateHelper.parseW3CDateTime("2003-12-12T10:00:00Z"));
		
		Item item1 = makeItem(rdfInstance1.asElementRDFXML());
		Item item2 = makeItem(rdfInstance2.asElementRDFXML());
		Item item3 = makeItem(rdfInstance3.asElementRDFXML());
		Item item4 = makeItem(rdfInstance4.asElementRDFXML());
		
		List<Item> items = new ArrayList<Item>();
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
				
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, NullIdentityProvider.INSTANCE, rdfSchema, null);
		Assert.assertNotNull(adapter);
		
		List<Item> conflicts = adapter.merge(items);
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		FilterQuery fq = new FilterQuery("name=JMT and birthDate=1974-06-24T10:00:00Z", rdfSchema);
		List<Item> result = adapter.getAll(fq);
		
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());

		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter("result", NullIdentityProvider.INSTANCE, result);
		TestHelper.assertItem(item1, inMemoryAdapter);
		
		fq = new FilterQuery("birthDate>1980-01-01T01:00:00Z", rdfSchema);
		result = adapter.getAll(fq);
		
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(2, result.size());

		inMemoryAdapter = new InMemorySyncAdapter("result", NullIdentityProvider.INSTANCE, result);
		TestHelper.assertItem(item3, inMemoryAdapter);
		TestHelper.assertItem(item4, inMemoryAdapter);
	}
	
	@Test
	public void shouldHttpSyncGetAllFilterByDynamicConditionWithOutIdentifiableID(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("birthDate", "birth date", IRDFSchema.DEFAULT_LANGUAGE);
		
		String id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance1 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance1.setProperty("name", "JMT");
		rdfInstance1.setProperty("birthDate", DateHelper.parseW3CDateTime("1974-06-24T10:00:00Z"));
		Item item1 = makeItem(rdfInstance1.asElementRDFXML(), id);
		
		id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance2 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance2.setProperty("name", "MSA");
		rdfInstance2.setProperty("birthDate", DateHelper.parseW3CDateTime("1976-12-23T10:00:00Z"));
		Item item2 = makeItem(rdfInstance2.asElementRDFXML(), id);
		
		id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance3 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance3.setProperty("name", "JIT");
		rdfInstance3.setProperty("birthDate", DateHelper.parseW3CDateTime("1998-01-03T10:00:00Z"));
		Item item3 = makeItem(rdfInstance3.asElementRDFXML(), id);
		
		id = IdGenerator.INSTANCE.newID();
		RDFInstance rdfInstance4 = rdfSchema.createNewInstance("uri:urn:"+ id);
		rdfInstance4.setProperty("name", "BIT");
		rdfInstance4.setProperty("birthDate", DateHelper.parseW3CDateTime("2003-12-12T10:00:00Z"));
		Item item4 = makeItem(rdfInstance4.asElementRDFXML(), id);
		
		List<Item> items = new ArrayList<Item>();
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
				
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, NullIdentityProvider.INSTANCE, rdfSchema, null);
		Assert.assertNotNull(adapter);
		
		List<Item> conflicts = adapter.merge(items);
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		FilterQuery fq = new FilterQuery("name=JMT and birthDate=1974-06-24T10:00:00Z", rdfSchema);
		List<Item> result = adapter.getAll(fq);
		
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());

		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter("result", NullIdentityProvider.INSTANCE, result);
		TestHelper.assertItem(item1, inMemoryAdapter);  // TODO (JMT) content id must be rdf instance id when rdf schema has not identifiable properties
		
		fq = new FilterQuery("birthDate>1980-01-01T01:00:00Z", rdfSchema);
		result = adapter.getAll(fq);
		
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(2, result.size());

		inMemoryAdapter = new InMemorySyncAdapter("result", NullIdentityProvider.INSTANCE, result);
		TestHelper.assertItem(item3, inMemoryAdapter);
		TestHelper.assertItem(item4, inMemoryAdapter);
	}
	
	private Item makeItem(Element payload){
		String syncId = IdGenerator.INSTANCE.newID();
		String description = "Id: " + syncId + " Version: " + XMLHelper.canonicalizeXML(payload).hashCode();
		XMLContent content = new XMLContent(syncId, syncId, description, payload);
		Sync sync = new Sync(syncId, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		return item;
	}
	
	private Item makeItem(Element payload, String id){
		String syncId = IdGenerator.INSTANCE.newID();
		String description = "Id: " + syncId + " Version: " + XMLHelper.canonicalizeXML(payload).hashCode();
		XMLContent content = new XMLContent(id, id, description, payload);
		Sync sync = new Sync(syncId, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		return item;
	}
	
}
