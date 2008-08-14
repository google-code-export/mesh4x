package org.mesh4j.sync.adapters.feed;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.AbstractSyncEngineTest;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;


public class FeedSyncTest extends AbstractSyncEngineTest{
	
	@Test
	public void shouldSyncTwoFeedRepositories(){
		
		
		Element element = TestHelper.makeElement("<payload><user><id>SyncId123</id><name>SyncId123</name><pass>123</pass></user></payload>");
		
		XMLContent content = new XMLContent("SyncId123", "SyncId123", "SyncId123", element);
		Item item = new Item(content, new Sync("SyncId123", "jmt", TestHelper.now(), false));
		
		Feed feed = new Feed();
		feed.addItem(item);
		
		File fileSource = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter source = new FeedAdapter(fileSource, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed);
		
		File fileTarget = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter target = new FeedAdapter(fileTarget, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, new Feed());
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflictItems = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflictItems.size());
		Assert.assertEquals(1, source.getFeed().getItems().size());
		Assert.assertEquals(source.getFeed().getItems().size(), target.getFeed().getItems().size());
		Assert.assertEquals(source.getFeed().getItems().get(0), target.getFeed().getItems().get(0));
				
	}
	
	@Test
	public void shouldMergeItems(){
		Date now = TestHelper.now();
		
		Item item0 = new Item(null, new Sync("SyncId0").update("jmt", now));
		Item item1 = new Item(null, new Sync("SyncId1").update("jmt", now));
		Item item2 = new Item(null, new Sync("SyncId2").update("jmt", now));
		
		Feed feed1 = new Feed();
		feed1.addItem(item0);
		feed1.addItem(item1);
		
		Feed feed2 = new Feed();
		feed2.addItem(item2);
		
		File fileSource = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter source = new FeedAdapter(fileSource, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed1);
		
		File fileTarget = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter target = new FeedAdapter(fileTarget, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed2);
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflictItems = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflictItems.size());
		Assert.assertEquals(3, source.getFeed().getItems().size());
		Assert.assertEquals(source.getFeed().getItems().size(), target.getFeed().getItems().size());
		Assert.assertEquals(source.getFeed().getItems().get(0), target.getFeed().getItems().get(1));
		Assert.assertEquals(source.getFeed().getItems().get(1), target.getFeed().getItems().get(2));
		Assert.assertEquals(source.getFeed().getItems().get(2), target.getFeed().getItems().get(0));
				
	}
	
	@Test
	public void shouldBeResolveConflicts(){
		
		Date oneDayAgo = TestHelper.nowSubtractDays(1);
		Date now = TestHelper.now();
		
		Sync sync = new Sync("SyncId0").update("jmt", oneDayAgo);
		
		Item item0 = new Item(null, sync);
		Item item01 = new Item(null, sync.clone().update("jmt", now));
		
		Feed feed1 = new Feed();
		feed1.addItem(item0);
		
		Feed feed2 = new Feed();
		feed2.addItem(item01);
		
		File fileSource = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter source = new FeedAdapter(fileSource, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed1);
		
		File fileTarget = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter target = new FeedAdapter(fileTarget, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed2);
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflictItems = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflictItems.size());
		Assert.assertEquals(1, source.getFeed().getItems().size());
		Assert.assertEquals(source.getFeed().getItems().size(), target.getFeed().getItems().size());
		Assert.assertEquals(source.getFeed().getItems().get(0), target.getFeed().getItems().get(0));	
	}
	
	@Test
	public void shouldBeNotifyConflicts(){
		Date oneDayAgo = TestHelper.nowSubtractDays(1);
		Date now = TestHelper.now();
		
		Sync sync = new Sync("SyncId0").update("jmt", oneDayAgo);
		
		Item item01 = new Item(null, sync.clone().update("jmt", now));
		Item item0 = new Item(null, sync.update("jmt1", now));
		
		Feed feed1 = new Feed();
		feed1.addItem(item0);
		
		Feed feed2 = new Feed();
		feed2.addItem(item01);
		
		File fileSource = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter source = new FeedAdapter(fileSource, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed1);
		
		File fileTarget = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		FeedAdapter target = new FeedAdapter(fileTarget, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed2);
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflictItems = syncEngine.synchronize();
		
		Assert.assertEquals(1, conflictItems.size());
				
	}

	@Override
	protected ISyncAdapter makeLeftRepository(Item... items) {
		Feed feed = new Feed(items);
		File file = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		return new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed);
	}
	
	@Override
	protected ISyncAdapter makeRightRepository(Item... items) {
		Feed feed = new Feed(items);
		File file = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));
		return new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed);
	}

	@Override
	protected String getUserName(Item item) {
		return item.getContent().getPayload().element("user").element("name").getText();
	}

}
