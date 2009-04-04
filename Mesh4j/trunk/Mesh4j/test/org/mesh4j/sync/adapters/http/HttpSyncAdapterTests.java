package org.mesh4j.sync.adapters.http;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;


public class HttpSyncAdapterTests {

	// BUSINESS METHODS

	@Test
	public void shouldUploadMesh(){
		String path = "http://localhost:8080/mesh4x/feeds";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, "myMesh", RssSyndicationFormat.NAME, "my mesh", null, null);
		
		// add feed
		Element schemaElement = XMLHelper.parseElement("<mySchema><id>33</id></mySchema>");
		ISchema schema = new Schema(schemaElement);
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		HttpSyncAdapter.uploadMeshDefinition(path, "myMesh/myFeed", RssSyndicationFormat.NAME, "my description", schema, mapping);
		
		// update feed
		Element schemaElement1 = XMLHelper.parseElement("<mySchema><id>CODE</id><name>NAME</name></mySchema>");
		ISchema schema1 = new Schema(schemaElement1);
		
		Element elementMappings1 = XMLHelper.parseElement("<mappings><title>title</title><desc>desc</desc></mappings>");
		IMapping mapping1 = new Mapping(elementMappings1);

		HttpSyncAdapter.uploadMeshDefinition(path, "myMesh/myFeed", RssSyndicationFormat.NAME, "my description223", schema1, mapping1);
	}
	
	@Test
	public void shouldExecuteGetAll(){
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		List<Item> items = httpAdapter.getAll();
		Assert.assertNotNull(items);
	}

	private HttpSyncAdapter makeAdapter(String path) {
		return new HttpSyncAdapter(path, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, ContentWriter.INSTANCE, ContentReader.INSTANCE);
	}
	
	@Test
	public void shouldExecuteGetAllSince(){
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		List<Item> items = httpAdapter.getAllSince(TestHelper.now());
		Assert.assertNotNull(items);
	}

	
	@Test
	public void shouldExecuteMerge() throws Exception{
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		
		Date now = TestHelper.now();
		Thread.sleep(500);
		int size = httpAdapter.getAll().size();
		int size1 = httpAdapter.getAllSince(now).size();
		
		String syncId = IdGenerator.INSTANCE.newID();		

		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar").setText("fooBar");
		
		XMLContent content = new XMLContent(syncId, "myTitle", "myDesc", payload);
		Sync sync = new Sync(syncId, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		
		Feed feed = new Feed(item);
				
		List<Item> result = httpAdapter.merge(feed.getItems());
		Assert.assertEquals(0, result.size());
		
		List<Item> items = httpAdapter.getAll();
		Assert.assertEquals(size + 1, items.size());
		
		items = httpAdapter.getAllSince(now);
		Assert.assertEquals(size1 + 1, items.size());
	}

	
	@Test
	public void shouldObtainsSchema(){
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		String schema = HttpSyncAdapter.getSchema(path);
		Assert.assertNotNull(schema);
	}

	@Test
	public void shouldObtainsMappings(){
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		String mappings = HttpSyncAdapter.getMappings(path);
		Assert.assertNotNull(mappings);
	}
	
}
