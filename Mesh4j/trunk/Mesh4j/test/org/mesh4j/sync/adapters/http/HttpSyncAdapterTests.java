package org.mesh4j.sync.adapters.http;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;


public class HttpSyncAdapterTests {

	// MODEL VARIABLES
	private HttpSyncAdapter httpAdapter;
	
	// BUSINESS METHODS

	@Before
	public void setUp() throws MalformedURLException{
		String path = "http://localhost:9090/mesh4x/feeds/myMesh/myFeed";
		this.httpAdapter = new HttpSyncAdapter(path, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
	}

	@Test
	public void shouldUploadMesh(){
		String path = "http://localhost:9090/mesh4x/feeds";
		this.httpAdapter = new HttpSyncAdapter(path, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		
		// add mesh
		this.httpAdapter.uploadMeshDefinition("myMesh", RssSyndicationFormat.NAME, "my mesh", "", "");
		
		// add feed
		this.httpAdapter.uploadMeshDefinition("myMesh/myFeed", RssSyndicationFormat.NAME, "my description", "my schema", "my mappings");
		
		// update feed
		this.httpAdapter.uploadMeshDefinition("myMesh/myFeed", RssSyndicationFormat.NAME, "my description223", "my schema1 ", "my mappings2");
	}
	
	@Test
	public void shouldExecuteGetAll(){
		List<Item> items = this.httpAdapter.getAll();
		Assert.assertNotNull(items);
	}
	
	@Test
	public void shouldExecuteGetAllSince(){
		List<Item> items = this.httpAdapter.getAllSince(TestHelper.now());
		Assert.assertNotNull(items);
	}

	
	@Test
	public void shouldExecuteMerge() throws Exception{
		
		Date now = TestHelper.now();
		Thread.sleep(500);
		int size = this.httpAdapter.getAll().size();
		int size1 = this.httpAdapter.getAllSince(now).size();
		
		String syncId = IdGenerator.INSTANCE.newID();		

		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar").setText("fooBar");
		
		XMLContent content = new XMLContent(syncId, "myTitle", "myDesc", payload);
		Sync sync = new Sync(syncId, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		
		Feed feed = new Feed(item);
				
		List<Item> result = this.httpAdapter.merge(feed.getItems());
		Assert.assertEquals(0, result.size());
		
		List<Item> items = this.httpAdapter.getAll();
		Assert.assertEquals(size + 1, items.size());
		
		items = this.httpAdapter.getAllSince(now);
		Assert.assertEquals(size1 + 1, items.size());
	}

	
	@Test
	public void shouldObtainsSchema(){
		String schema = this.httpAdapter.getSchema();
		Assert.assertNotNull(schema);
	}

	@Test
	public void shouldObtainsMappings(){
		String mappings = this.httpAdapter.getMappings();
		Assert.assertNotNull(mappings);
	}
	
}
