package com.mesh4j.sync.feed;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.TestHelper;

public class FeedSyncTest {

	// TODO (JMT) test
	
	@Test
	public void shouldSyncTwoFeedRepositories(){
		Item item = new Item(null, new Sync("SyncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item);
		
		FeedRepository source = new FeedRepository();
		FeedRepository target = new FeedRepository(feed);
		
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
		
		Item item0 = new Item(null, new Sync("SyncId0").addHistory(new History("jmt", now, 1)));
		Item item1 = new Item(null, new Sync("SyncId1").addHistory(new History("jmt", now, 1)));
		Item item2 = new Item(null, new Sync("SyncId2").addHistory(new History("jmt", now, 1)));
		
		Feed feed1 = new Feed();
		feed1.addItem(item0);
		feed1.addItem(item1);
		
		Feed feed2 = new Feed();
		feed2.addItem(item2);
		
		FeedRepository source = new FeedRepository(feed1);
		FeedRepository target = new FeedRepository(feed2);
		
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
		
		FeedRepository source = new FeedRepository(feed1);
		FeedRepository target = new FeedRepository(feed2);
		
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
		
		FeedRepository source = new FeedRepository(feed1);
		FeedRepository target = new FeedRepository(feed2);
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflictItems = syncEngine.synchronize();
		
		Assert.assertEquals(1, conflictItems.size());
				
	}
}
