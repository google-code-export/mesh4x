package com.mesh4j.sync.adapters.feed;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class FeedAdapterTests {

	@Test
	public void  shouldBeReturnNewFeed(){
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, new Feed());
		Feed feed = repo.getFeed();
		
		Assert.assertNotNull(feed);
		Assert.assertTrue(feed.getItems().isEmpty());
	}
	
	@Test
	public void shouldBeReturnFeed(){
		Feed feedSource = new Feed();
		
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, feedSource);

		Feed feed = repo.getFeed();
		
		Assert.assertNotNull(feed);
		Assert.assertSame(feedSource, feed);
	}
	
	@Test
	public void shouldNotSupportMerge(){
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, new Feed());

		Assert.assertFalse(repo.supportsMerge());		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void shouldNotMerge(){
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, new Feed());
				
		repo.merge(new ArrayList<Item>());
	}
	
	@Test
	public void shouldBeAddItem(){
		Item item = new Item(null, new Sync("suncId123"));
		
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, new Feed());

		repo.add(item);
		
		Feed feed = repo.getFeed();
		
		Assert.assertEquals(1, feed.getItems().size());
		Assert.assertSame(item, feed.getItems().get(0));
	}

	
	@Test
	public void shouldBeDeleteItem(){
		Item item = new Item(null, new Sync("suncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item);
		
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, feed);

		repo.delete(item.getSyncId());
				
		Assert.assertEquals(0, feed.getItems().size());

	}
	
	@Test
	public void shouldGetItem(){
		Item item = new Item(null, new Sync("suncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item);
		
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, feed);

		Item resultItem = repo.get(item.getSyncId());
				
		Assert.assertSame(item, resultItem);
	}
	
	@Test 
	public void shouldGetAllWithoutSinceDate(){
		Item item0 = new Item(null, new Sync("suncId123"));
		Item item1 = new Item(null, new Sync("suncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item0);
		feed.addItem(item1);
		
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, feed);

		List<Item> results = repo.getAll(null, new NullFilter<Item>());
				
		Assert.assertEquals(2, results.size());
		Assert.assertSame(item0, results.get(0));
		Assert.assertSame(item1, results.get(1));
	}
	
	@Test 
	public void shouldGetAllWithSinceDate(){
		Date sinceDate = TestHelper.nowSubtractDays(1);
		Date twoDaysAgo = TestHelper.nowSubtractDays(2);
		Date now = TestHelper.now();
		
		Item item0 = new Item(null, new Sync("suncId123").update("jmt", twoDaysAgo));
		Item item1 = new Item(null, new Sync("suncId123").update("jmt", now));
		
		Feed feed = new Feed();
		feed.addItem(item0);
		feed.addItem(item1);
		
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, feed);

		List<Item> results = repo.getAll(sinceDate, new NullFilter<Item>());
				
		Assert.assertEquals(1, results.size());
		Assert.assertSame(item1, results.get(0));
	
	}

	@Test
	public void shouldReturnFriendlyName() {
		File file = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FeedAdapter repo = new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE, new Feed());

		Assert.assertFalse(FeedAdapter.class.getName() == repo.getFriendlyName());
	}
	
//	public void shouldNotUpdate(){

}
