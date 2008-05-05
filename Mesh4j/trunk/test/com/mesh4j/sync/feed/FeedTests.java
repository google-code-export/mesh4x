package com.mesh4j.sync.feed;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;

public class FeedTests {

	@Test
	public void shouldBeReturnsEmptyItemListWhenFeeIsNew(){
		Feed feed = new Feed();
		List<Item> feedItems = feed.getItems();
		Assert.assertTrue(feedItems.isEmpty());
	}
	
	@Test
	public void shouldBeDeleteNotFailedWithNullArgument(){
		Feed feed = new Feed();
		feed.deleteItem(null);
	}
	
	@Test
	public void shouldBeDeleteNotFailedIfItemIsNotInFeedList(){
		Item item = new Item(null, new Sync("syncId123"));
		
		Feed feed = new Feed();
		feed.deleteItem(item);
	}	
	
	@Test
	public void shouldBeAddItemWhenFeedIsNew(){
		Item item = new Item(null, new Sync("syncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item);
		
		List<Item> feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(1 == feedItems.size());
		Assert.assertTrue(item.equals(feedItems.get(0)));

	}
	
	@Test
	public void shouldBeAddItemWhenFeedItemListIsNotEmpty(){
		Item item0 = new Item(null, new Sync("syncId123"));
		Item item1 = new Item(null, new Sync("syncId1234"));
		
		Feed feed = new Feed();
		feed.addItem(item0);
		feed.addItem(item1);
		
		List<Item> feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(2 == feedItems.size());
		Assert.assertTrue(item0.equals(feedItems.get(0)));
		Assert.assertTrue(item1.equals(feedItems.get(1)));
	}
	
	@Test
	public void shouldBeRemoveItemIfFeedItemListHas1Item(){
		Item item0 = new Item(null, new Sync("syncId123"));
		
		Feed feed = new Feed();
		feed.addItem(item0);
		
		List<Item> feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(1 == feedItems.size());
		Assert.assertTrue(item0.equals(feedItems.get(0)));
		
		feed.deleteItem(item0);		
		feedItems = feed.getItems();
		Assert.assertTrue(feedItems.isEmpty());
	}
	
	@Test
	public void shouldBeFirstRemoveItemIfFeedItemListHasItems(){
		Item item0 = new Item(null, new Sync("syncId123"));
		Item item1 = new Item(null, new Sync("syncId1234"));
		
		Feed feed = new Feed();
		feed.addItem(item0);
		feed.addItem(item1);
		
		List<Item> feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(2 == feedItems.size());
		Assert.assertTrue(item0.equals(feedItems.get(0)));
		Assert.assertTrue(item1.equals(feedItems.get(1)));
		
		feed.deleteItem(item0);		
		feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(1 == feedItems.size());
		Assert.assertTrue(item1.equals(feedItems.get(0)));

	}
	
	@Test
	public void shouldBeRemoveItemIfFeedItemListHasItems(){
		Item item0 = new Item(null, new Sync("syncId123"));
		Item item1 = new Item(null, new Sync("syncId1234"));
		
		Feed feed = new Feed();
		feed.addItem(item0);
		feed.addItem(item1);
		
		List<Item> feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(2 == feedItems.size());
		Assert.assertTrue(item0.equals(feedItems.get(0)));
		Assert.assertTrue(item1.equals(feedItems.get(1)));
		
		feed.deleteItem(item1);		
		feedItems = feed.getItems();
		Assert.assertFalse(feedItems.isEmpty());
		Assert.assertTrue(1 == feedItems.size());
		Assert.assertTrue(item0.equals(feedItems.get(0)));

	}
	
	
	@Test
	public void shouldGetItemBySyncIDReturnsNull(){
		Feed feed = new Feed();
		Item item = feed.getItemBySyncId("1232");
		Assert.assertNull(item);
	}
	
	@Test
	public void shouldGetItemBySyncIDReturnsItem(){
		Item item0 = new Item(null, new Sync("1232")); 
		Item item1 = new Item(null, new Sync("1233"));
		
		Feed feed = new Feed(item0, item1);
		Item item = feed.getItemBySyncId("1232");
		Assert.assertNotNull(item);
		Assert.assertSame(item0, item);
	}
}
