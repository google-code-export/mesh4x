package com.mesh4j.sync.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.TestHelper;

public class FeedRepositoryTests {

	@Test
	public void  shouldBeReturnNewFeed(){
		FeedRepository repo = new FeedRepository();
		Feed feed = repo.getFeed();
		
		Assert.assertNotNull(feed);
		Assert.assertTrue(feed.getItems().isEmpty());
	}
	
	@Test
	public void shouldBeReturnFeed(){
		Feed feedSource = new Feed();
		
		FeedRepository repo = new FeedRepository(feedSource);
		Feed feed = repo.getFeed();
		
		Assert.assertNotNull(feed);
		Assert.assertSame(feedSource, feed);
	}
	
	@Test
	public void shouldNotSupportMerge(){
		FeedRepository repo = new FeedRepository();

		Assert.assertFalse(repo.supportsMerge());		
	}
	
	@Test
	public void shouldNotMerge(){
		FeedRepository repo = new FeedRepository();
		
		List<Item> itemsSource = new ArrayList<Item>();
		Item item = new Item(null, new Sync("suncId123"));
		itemsSource.add(item);
		
		List<Item> result = repo.merge(itemsSource);
		Assert.assertSame(itemsSource, result);		
	}
	
	@Test
	public void shouldBeAddItem(){
		Item item = new Item(null, new Sync("suncId123"));
		
		FeedRepository repo = new FeedRepository();
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
		
		FeedRepository repo = new FeedRepository(feed);
		repo.delete(item.getSyncId());
				
		Assert.assertEquals(0, feed.getItems().size());

	}
	
	@Test
	public void shouldGetItem(){
		Item item = new Item(null, new Sync("suncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item);
		
		FeedRepository repo = new FeedRepository(feed);
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
		
		FeedRepository repo = new FeedRepository(feed);
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
		
		FeedRepository repo = new FeedRepository(feed);
		List<Item> results = repo.getAll(sinceDate, new NullFilter<Item>());
				
		Assert.assertEquals(1, results.size());
		Assert.assertSame(item1, results.get(0));
	
	}

//	public String getFriendlyName() {	
//	public void shouldNotUpdate(){

}
