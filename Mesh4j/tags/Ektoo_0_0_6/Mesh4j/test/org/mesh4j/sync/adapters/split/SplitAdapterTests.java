package org.mesh4j.sync.adapters.split;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;

public class SplitAdapterTests {
	// TODO (JMT) test

	@Test
	public void shouldGetAllItems(){
		// create item
		Element payload = DocumentHelper.createElement("EXAMPLE");
		IContent content = new XMLContent("1", "title", "desc", payload);
		
		List<Item> items;
		
		// create split adapter
		ISyncRepository syncRepo = new MockSyncRepository();
		IContentAdapter contentRepo = new MockContentAdapter();
		
		SplitAdapter split = new SplitAdapter(syncRepo, contentRepo, NullIdentityProvider.INSTANCE);
		
		items = split.getAll();
		Assert.assertEquals(0, items.size());
		
		// user add a new content
		contentRepo.save(content);
		Assert.assertNotNull(contentRepo.get(content.getId()));
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
		
		// future reads no change sync data
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());

		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
	
	}
	
	@Test
	public void shouldDetectNewItems(){
		
		// create item
		Element payload = DocumentHelper.createElement("EXAMPLE");
		IContent content = new XMLContent("1", "title", "desc", payload);
		
		List<Item> items;
		
		// create split adapter
		ISyncRepository syncRepo = new MockSyncRepository();
		IContentAdapter contentRepo = new MockContentAdapter();
		
		SplitAdapter split = new SplitAdapter(syncRepo, contentRepo, NullIdentityProvider.INSTANCE);
		
		items = split.getAll();
		Assert.assertEquals(0, items.size());
		
		contentRepo.save(content);
		Assert.assertNotNull(contentRepo.get(content.getId()));
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
		
		// future reads no change sync data
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
	}
	
	
	@Test
	public void shouldDetectUpdatedItems(){
		
		// create item
		Element payload = DocumentHelper.createElement("EXAMPLE");
		IContent content = new XMLContent("1", "title", "desc", payload);
		
		List<Item> items;
		
		// create split adapter
		ISyncRepository syncRepo = new MockSyncRepository();
		IContentAdapter contentRepo = new MockContentAdapter();
		
		SplitAdapter split = new SplitAdapter(syncRepo, contentRepo, NullIdentityProvider.INSTANCE);
		
		items = split.getAll();
		Assert.assertEquals(0, items.size());
		
		contentRepo.save(content);
		Assert.assertNotNull(contentRepo.get(content.getId()));
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
		
		// update content
		payload = DocumentHelper.createElement("EXAMPLE_UPDATED");
		content = new XMLContent("1", "title", "desc", payload);
		contentRepo.save(content);
		Assert.assertNotNull(contentRepo.get(content.getId()));
		Assert.assertEquals("EXAMPLE_UPDATED", contentRepo.get(content.getId()).getPayload().getName());
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(2, items.get(0).getSync().getUpdates());
	
		
		// future reads no change sync data
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(2, items.get(0).getSync().getUpdates());
	
	}
	
	@Test
	public void shouldDetectDeletedItems(){
		
		// create item
		Element payload = DocumentHelper.createElement("EXAMPLE");
		IContent content = new XMLContent("1", "title", "desc", payload);
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		
		Item item = new Item(content, sync);
		
		List<Item> items;
		
		// create split adapter
		ISyncRepository syncRepo = new MockSyncRepository();
		IContentAdapter contentRepo = new MockContentAdapter();
		
		SplitAdapter split = new SplitAdapter(syncRepo, contentRepo, NullIdentityProvider.INSTANCE);
		
		items = split.getAll();
		Assert.assertEquals(0, items.size());
		
		// add item
		split.add(item);
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
		Assert.assertNotNull(contentRepo.get(content.getId()));
		
		// delete item
		split.delete(item.getSyncId());

		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).isDeleted());
		Assert.assertEquals(NullContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(2, items.get(0).getSync().getUpdates());
		Assert.assertNull(contentRepo.get(content.getId()));
		
			
		// future reads no change sync data
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).isDeleted());
		Assert.assertEquals(NullContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(2, items.get(0).getSync().getUpdates());
		Assert.assertNull(contentRepo.get(content.getId()));

	}
	
	@Test
	public void shouldDetectLazaroItems(){
		
		// create item
		Element payload = DocumentHelper.createElement("EXAMPLE");
		IContent content = new XMLContent("1", "title", "desc", payload);
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		
		Item item = new Item(content, sync);
		
		List<Item> items;
		
		// create split adapter
		ISyncRepository syncRepo = new MockSyncRepository();
		IContentAdapter contentRepo = new MockContentAdapter();
		
		SplitAdapter split = new SplitAdapter(syncRepo, contentRepo, NullIdentityProvider.INSTANCE);
		
		items = split.getAll();
		Assert.assertEquals(0, items.size());
		
		// add item
		split.add(item);
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(1, items.get(0).getSync().getUpdates());
		Assert.assertNotNull(contentRepo.get(content.getId()));
		
		// delete item
		split.delete(item.getSyncId());

		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).isDeleted());
		Assert.assertEquals(NullContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(2, items.get(0).getSync().getUpdates());
		Assert.assertNull(contentRepo.get(content.getId()));
		
		// Lazaro - add item again
		contentRepo.save(content);
		Assert.assertNotNull(contentRepo.get(content.getId()));
		
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(3, items.get(0).getSync().getUpdates());
		
		// future reads no change sync data
		items = split.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(items.get(0).isDeleted());
		Assert.assertEquals(XMLContent.class.getName(), items.get(0).getContent().getClass().getName());
		Assert.assertEquals(3, items.get(0).getSync().getUpdates());

	}
	

}
