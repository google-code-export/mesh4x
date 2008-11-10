package com.mesh4j.sync.adapter.S3.emulator.test;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.S3.IS3Service;
import org.mesh4j.sync.adapters.S3.S3Adapter;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

import com.mesh4j.sync.adapter.S3.emulator.S3Service;

public class S3AdapterTest {

	@Test
	public void shouldGetAllReturnsEmptyResultsWhenBucketIsEmpty(){
		S3Service s3 = makeService();
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		List<Item> items = s3Adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(0, items.size());
	}
	
	@Test
	public void shouldGetAllReturnsListWithBucketElement() throws Exception{
		S3Service s3 = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3, "feeds", "myFeed."+item.getSyncId());
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		List<Item> items = s3Adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		Assert.assertTrue(item.equals(items.get(0)));
	}
	
	@Test
	public void shouldGetAllReturnsListWithBucketElementsOrderByDescByLastUpdateWhenValue() throws Exception{
		S3Service s3 = makeService();
		
		Item item = makeNewItem("0", "<payload><foo>bar</foo></payload>", TestHelper.makeDate(2008, 1, 1, 10, 15, 10, 0));
		this.write(item, s3, "feeds", "myFeed."+item.getSyncId());
		
		Item item1 = makeNewItem("1", "<payload><foo>bar</foo></payload>", TestHelper.makeDate(2008, 1, 1, 10, 20, 10, 0));
		this.write(item1, s3, "feeds", "myFeed."+item1.getSyncId());
		
		Item item2 = makeNewItem("2", "<payload><foo>bar</foo></payload>", TestHelper.makeDate(2008, 1, 1, 10, 25, 10, 0));
		this.write(item2, s3, "feeds", "myFeed."+item2.getSyncId());
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		List<Item> items = s3Adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());
		
		Assert.assertTrue(item.equals(items.get(2)));
		Assert.assertTrue(item1.equals(items.get(1)));
		Assert.assertTrue(item2.equals(items.get(0)));
	}
	
	@Test
	public void shouldGetAllMergeConflictsAndReturnsListWithBucketElementsOrderByDescByLastUpdateWhenValue() throws Exception{
		S3Service s3 = makeService();
		
		Item item = makeNewItem("0", "<payload><foo>bar</foo></payload>", TestHelper.makeDate(2008, 1, 1, 10, 10, 10, 0));
		this.write(item, s3, "feeds", "myFeed");
		
		item.getSync().update("kzu", TestHelper.makeDate(2008, 1, 1, 10, 15, 10, 0));
//System.out.println(item.getLastUpdate().getWhen());
		this.write(item, s3, "feeds", "myFeed");
		
		Item itemClone = item.clone();
		
		item.getSync().update("nico", TestHelper.makeDate(2008, 1, 1, 10, 20, 10, 0));
//System.out.println(item.getLastUpdate().getWhen());
		this.write(item, s3, "feeds", "myFeed");

		itemClone.getSync().update("jmt", TestHelper.makeDate(2008, 1, 1, 10, 25, 10, 0));
//System.out.println(itemClone.getLastUpdate().getWhen());
		this.write(itemClone, s3, "feeds", "myFeed");
		
		Item item1 = makeNewItem("1", "<payload><foo>bar</foo></payload>", TestHelper.makeDate(2008, 1, 1, 10, 30, 10, 0));
//System.out.println(item1.getLastUpdate().getWhen());
		this.write(item1, s3, "feeds", "myFeed."+item1.getSyncId());
		
		Item item1Clone = item1.clone();
		
		item1.getSync().update("nico", TestHelper.makeDate(2008, 1, 1, 10, 35, 10, 0));
//System.out.println(item1.getLastUpdate().getWhen());
		this.write(item1, s3, "feeds", "myFeed");
		
		item1Clone.getSync().update("jmt", TestHelper.makeDate(2008, 1, 1, 10, 40, 10, 0));
//System.out.println(item1Clone.getLastUpdate().getWhen());
		this.write(item1Clone, s3, "feeds", "myFeed");
		
		Item item2 = makeNewItem("2", "<payload><foo>bar</foo></payload>", TestHelper.makeDate(2008, 1, 1, 10, 45, 10, 0));
//System.out.println(item2.getLastUpdate().getWhen());
		this.write(item2, s3, "feeds", "myFeed."+item2.getSyncId());
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		List<Item> items = s3Adapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());
		
		Assert.assertFalse(items.get(0).hasSyncConflicts());
		Assert.assertTrue(item2.equals(items.get(0)));
		Assert.assertTrue(items.get(1).hasSyncConflicts());
		Assert.assertTrue(items.get(2).hasSyncConflicts());
		

	}
	
	
	@Test
	public void shouldReadItemReturnsNullBecauseItemDoesNotExists(){
		S3Service s3 = makeService();
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		Assert.assertNull(s3Adapter.get(IdGenerator.INSTANCE.newID()));
	}

	@Test
	public void shouldReadItemReturnsItemWhenItExistsInS3() throws Exception{
	
		S3Service s3 = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3, "feeds", "myFeed."+item.getSyncId());
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		Item addedItem = s3Adapter.get(item.getSyncId());
		
		Assert.assertNotNull(addedItem);
		Assert.assertEquals(item, addedItem);
	}
	
	@Test
	public void shouldReadItemReturnsLastItemVersionWhenItExistsInS3() throws Exception{
	
		S3Service s3 = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3, "feeds", "myFeed");
		
		item.getSync().update("kzu", new Date());
		this.write(item, s3, "feeds", "myFeed");
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		Item addedItem = s3Adapter.get(item.getSyncId());
		Assert.assertNotNull(addedItem);
		Assert.assertEquals(item, addedItem);
	}
	
	@Test
	public void shouldMergeConflicts() throws Exception{
		S3Service s3 = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3, "feeds", "myFeed");
		
		item.getSync().update("kzu", new Date());
		this.write(item, s3, "feeds", "myFeed");
		
		Item itemClone = item.clone();
		
		item.getSync().update("nico", new Date());
		this.write(item, s3, "feeds", "myFeed");
		
		itemClone.getSync().update("jmt", new Date());
		this.write(itemClone, s3, "feeds", "myFeed");
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		
		Item addedItem = s3Adapter.get(item.getSyncId());
		Assert.assertNotNull(addedItem);
		
		Assert.assertTrue(addedItem.hasSyncConflicts());
		
		Item result = MergeBehavior.resolveConflicts(addedItem, "jmt", new Date(), false);
		
		Assert.assertFalse(result.hasSyncConflicts());

	}
	
	@Test
	public void shouldSyncLocalItemWithS3ConflictItem() throws Exception{
		S3Service s3Service = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3Service, "feeds", "myFeed");
		
		item.getSync().update("kzu", new Date());
		this.write(item, s3Service, "feeds", "myFeed");
		
		Item itemClone = item.clone();
		
		item.getSync().update("nico", new Date());
		this.write(item, s3Service, "feeds", "myFeed");
		
		itemClone.getSync().update("jmt", new Date());
		this.write(itemClone, s3Service, "feeds", "myFeed");
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3Service, NullIdentityProvider.INSTANCE);
		
		Item s3Item = s3Adapter.get(item.getSyncId());
		Assert.assertNotNull(s3Item);
		
		Assert.assertTrue(s3Item.hasSyncConflicts());
				
		InMemorySyncAdapter localAdapter = new InMemorySyncAdapter("local", NullIdentityProvider.INSTANCE, item);
		InMemorySyncAdapter s3LocalAdapter = new InMemorySyncAdapter("s3", NullIdentityProvider.INSTANCE, s3Item);
		SyncEngine syncEngine = new SyncEngine(localAdapter, s3LocalAdapter);
		List<Item> conflicts = syncEngine.synchronize();

		Item resolvedConflicts = MergeBehavior.resolveConflicts(conflicts.get(0), "cibrax", new Date(), false);
		Assert.assertFalse(resolvedConflicts.hasSyncConflicts());
		
		this.write(resolvedConflicts, s3Service, "feeds", "myFeed");
		
		s3Item = s3Adapter.get(item.getSyncId());
		Assert.assertNotNull(s3Item);		
		Assert.assertFalse(s3Item.hasSyncConflicts());
		
	}
	
	@Test
	public void shouldPurgeBranchesDeleteOldHistoriesAndDoesNotDeleteLastUpdatedConflicts() throws Exception{
		S3Service s3Service = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3Service, "feeds", "myFeed");
		
		item.getSync().update("kzu", new Date());
		this.write(item, s3Service, "feeds", "myFeed");
		
		Item itemClone = item.clone();
		
		item.getSync().update("nico", new Date());
		this.write(item, s3Service, "feeds", "myFeed");
		
		itemClone.getSync().update("jmt", new Date());
		this.write(itemClone, s3Service, "feeds", "myFeed");
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3Service, NullIdentityProvider.INSTANCE);
		
		Assert.assertEquals(4, s3Adapter.getObjects(item.getSyncId()).size());
		Assert.assertEquals(2, s3Adapter.getBranches(item.getSyncId()).size());
		s3Adapter.purgeBranches(item.getSyncId());
		
		Thread.sleep(150);
		Assert.assertEquals(2, s3Adapter.getObjects(item.getSyncId()).size());
		Assert.assertEquals(2, s3Adapter.getBranches(item.getSyncId()).size());
	}
	
	@Test
	public void shouldPurgeBranchesDeleteOldHistories() throws Exception{
		S3Service s3Service = makeService();
		
		Item item = makeNewItem("<payload><foo>bar</foo></payload>");
		this.write(item, s3Service, "feeds", "myFeed");
		
		item.getSync().update("kzu", new Date());
		this.write(item, s3Service, "feeds", "myFeed");
		
		Item itemClone = item.clone();
		
		item.getSync().update("nico", new Date());
		this.write(item, s3Service, "feeds", "myFeed");
		
		itemClone.getSync().update("jmt", new Date());
		this.write(itemClone, s3Service, "feeds", "myFeed");
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3Service, NullIdentityProvider.INSTANCE);
		
		Item s3Item = s3Adapter.get(item.getSyncId());
		Assert.assertNotNull(s3Item);
		
		Assert.assertTrue(s3Item.hasSyncConflicts());
				
		InMemorySyncAdapter localAdapter = new InMemorySyncAdapter("local", NullIdentityProvider.INSTANCE, item);
		InMemorySyncAdapter s3LocalAdapter = new InMemorySyncAdapter("s3", NullIdentityProvider.INSTANCE, s3Item);
		SyncEngine syncEngine = new SyncEngine(localAdapter, s3LocalAdapter);
		List<Item> conflicts = syncEngine.synchronize();

		Item resolvedConflicts = MergeBehavior.resolveConflicts(conflicts.get(0), "cibrax", new Date(), false);
		Assert.assertFalse(resolvedConflicts.hasSyncConflicts());
		
		this.write(resolvedConflicts, s3Service, "feeds", "myFeed");
		Thread.sleep(100);
		
		s3Item = s3Adapter.get(item.getSyncId());
		Assert.assertNotNull(s3Item);		
		Assert.assertFalse(s3Item.hasSyncConflicts());
		
		Assert.assertEquals(5, s3Adapter.getObjects(item.getSyncId()).size());
		Assert.assertEquals(1, s3Adapter.getBranches(item.getSyncId()).size());
		s3Adapter.purgeBranches(item.getSyncId());
		
		Thread.sleep(150);
		Assert.assertEquals(1, s3Adapter.getObjects(item.getSyncId()).size());
		Assert.assertEquals(1, s3Adapter.getBranches(item.getSyncId()).size());
	}
	
	// PRIVATE METHODS
	
	private void write(Item item, IS3Service s3, String bucket, String objectPath) throws Exception{
		
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		String xmlItem = writer.writeAsXml(item);
		String oid = objectPath+"."+item.getSyncId()+"."+item.getLastUpdate().getSequence()+"."+item.getLastUpdate().getBy();
		
		s3.write(bucket, oid, xmlItem.getBytes());
		Thread.sleep(50);		
	}
	
	private Item makeNewItem(String payload)throws Exception{
		String syncId = IdGenerator.INSTANCE.newID();
		return makeNewItem(syncId, payload, new Date());
	}
	
	private Item makeNewItem(String syncId , String payload, Date date)throws Exception{
		Document doc = DocumentHelper.parseText(payload);
		
		IContent content = new XMLContent(syncId, "title", "desc", doc.getRootElement());
		Sync sync = new Sync(syncId, "jmt", date, false);
		Item item = new Item(content, sync);
		return item;
	}
	
	private S3Service makeService() {
		S3Service s3 = new S3Service();
		s3.addNode("1");
		return s3;
	}
}
