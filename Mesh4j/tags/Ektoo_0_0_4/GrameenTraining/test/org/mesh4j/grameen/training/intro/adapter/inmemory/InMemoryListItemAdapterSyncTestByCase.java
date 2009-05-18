package org.mesh4j.grameen.training.intro.adapter.inmemory;

import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;


public class InMemoryListItemAdapterSyncTestByCase {

	private Item item1,item2,item3;
	
	
	@Before
	public void setUp() throws Exception {
		//creating content
		String id = "1";
		String title = "test";
		String desc = "this is test xml raw data";
		String rawDataAsXML = "<user><id>1</id><name>saiful</name></user>";
		//create sync object
		String syncId = IdGenerator.INSTANCE.newID();
		String by  ="raju";
		Date when = new Date();
		boolean isDeleted = false;
		
		//create new Item object with help of the content and sync instance
		item1 = createItem(id, title, desc, rawDataAsXML, syncId, when, isDeleted, by);

		//creating content
		id = "2";
		title = "test";
		desc = "this is test xml raw data";
		rawDataAsXML = "<user><id>1</id><name>juan</name></user>";
		//create sync object
		syncId = IdGenerator.INSTANCE.newID();
		by  ="marcelo";
		when = new Date();
		isDeleted = false;
		
		//create new Item object with help of the content and sync instance
		item2 = createItem(id, title, desc, rawDataAsXML, syncId, when, isDeleted, by);

		//creating content
		id = "3";
		title = "test";
		desc = "this is test xml raw data";
		rawDataAsXML = "<user><id>1</id><name>akther</name></user>";
		//create sync object
		syncId = IdGenerator.INSTANCE.newID();
		by  ="javed";
		when = new Date();
		isDeleted = false;
		
		//create new Item object with help of the content and sync instance
		item3 = createItem(id, title, desc, rawDataAsXML, syncId, when, isDeleted, by);
	}

	@After
	public void tearDown() throws Exception {
		//release the resources 
	}
	/**
	 * possible case would be like the following
	 * 
	 * Case 1:
	 * imagine 2 data sources named A and B
	 * add item 1 to A.
	 * sync A,B
	 * --- at this point A has item and B is empty
	 * sync process>select all time from A.
	 * send item1 to B.
	 * sync process>select all item from B.
	 * --empty list
	 * end sync
	 * 
	 * Case 2:
	 * imagine 2 data sources named  A and B.
	 * add item 1 and item 2 in A.
	 * sync A, B
	 * -- in this momment A has 2 items and B is empty.
	 * Start the sync process:
	 * sync process> select all item from A.
	 * send item1, item 2 to B.
	 * sync process> select all item from B
	 * -- empty list
	 * end sync
	 * -- in this moment A and B has the same items
	 * 
	 * Case 3:
	 * -- at this point A and B has the same items
	 * add a new item 3 to B
	 * sync A, B
	 * sync process>>select all item from A where last update >= last sync date
	 * -- empty list
	 * select all item from B where last update >= last sync date
	 * -- list with item 3
	 * sync A,B
	 * 
	 */
	
	
	@Test
	public void shouldSyncAfterDeleteItem(){
		//add item1 and item2 to SourceAdapter 
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		sourceAdapter.add(item1);
		
		
		//initially target adapter is empty
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		sourceAdapter.add(item2);
		
		//sync process starts
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(2, targetAdapter.getAll().size());
		Assert.assertEquals(2, sourceAdapter.getAll().size());
		
		sourceAdapter.delete(item1.getSyncId());
		
		Assert.assertEquals(1, sourceAdapter.getAll().size());
		Assert.assertEquals(2, targetAdapter.getAll().size());
		
		conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		Assert.assertEquals(2, targetAdapter.getAll().size());
		Assert.assertEquals(2, sourceAdapter.getAll().size());
		
	}
	
	@Test
	public void shouldGenerateConflict(){
		
		//add item1 and item2 to SourceAdapter 
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		
		Item item1Source = item1.clone();
		item1Source.getSync().update("marcelo", new Date());
		sourceAdapter.add(item1Source);
		
		
		//initially target adapter is empty
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		Item item1Target = item1.clone();		// same original item
		item1Target.getSync().update("juan", new Date());
		targetAdapter.add(item1Target);
		
		//sync process starts
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);;
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(1, conflicts.size());
	}
	
	@Test
	public void shouldGenerateConflicts(){
		
		//add item1 and item2 to SourceAdapter 
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		
		Item item1Source = item1.clone();
		item1Source.getSync().update("marcelo", new Date());
		sourceAdapter.add(item1Source);
		
		Item item2Source = item2.clone();
		item2Source.getSync().update("raju", new Date());
		sourceAdapter.add(item2Source);
		
		//initially target adapter is empty
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		Item item1Target = item1.clone();		// same original item
		item1Target.getSync().update("juan", new Date());
		targetAdapter.add(item1Target);
		
		Item item2Target = item2.clone();
		item2Target.getSync().update("javed", new Date());
		targetAdapter.add(item2Target);
		
		
		//sync process starts
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);;
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(2, conflicts.size());
		
	}
	
	@Test
	public void shouldNotGenerateConflictAsItem2(){
		
		//add item1 and item2 to SourceAdapter 
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		
		Item item1Source = item1.clone();
		item1Source.getSync().update("marcelo", new Date());
		sourceAdapter.add(item1Source);
		sourceAdapter.add(item2);
		
		try {
			Thread.currentThread().sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//initially target adapter is empty
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		Item item1Target = item1.clone();		// same original item
		item1Target.getSync().update("juan", new Date());
		targetAdapter.add(item1Target);
		targetAdapter.add(item2);
		
		System.out.println(item1Source.getSync().getLastUpdate().getWhen());
		System.out.println(item1Target.getSync().getLastUpdate().getWhen());
		
		//sync process starts
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);;
		List<Item> conflicts = syncEngine.synchronize();
		System.out.println(conflicts.get(0).getSync().getLastUpdate().getBy());
		
		Assert.assertEquals(conflicts.get(0).getLastUpdate().getBy(), item1Target.getLastUpdate().getBy());
		//Assert.assertNotSame(conflicts.get(0).getLastUpdate().getBy(), item2.getLastUpdate().getBy());
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(1, conflicts.size());
		Assert.assertEquals(2, sourceAdapter.getAll().size());
		Assert.assertEquals(2, targetAdapter.getAll().size());
		Assert.assertEquals(sourceAdapter.getAll().size(), targetAdapter.getAll().size());
		
	}
	
	
	@Test
	public void shouldSyncItem1(){
		
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		sourceAdapter.add(item1);
		System.out.println("authenticate "+sourceAdapter.getAuthenticatedUser());
		
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> confilicts = syncEngine.synchronize();
		
		Assert.assertNotNull(confilicts);
		Assert.assertTrue(confilicts.isEmpty());
		Assert.assertEquals(1, targetAdapter.getAll().size());
	}
	
	@Test
	public void shouldSyncItem1AndItem2(){
		
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		sourceAdapter.add(item1);
		sourceAdapter.add(item2);
		
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> confilicts = syncEngine.synchronize();
		
		Assert.assertEquals(2, targetAdapter.getAll().size());
		Assert.assertNotNull(confilicts);
		Assert.assertTrue(confilicts.isEmpty());
		
	}
	
	@Test
	public void shouldSyncItem1AndItem2AndItem3(){
		
		//add item1 and item2 to SourceAdapter 
		InMemoryListItemAdapter sourceAdapter = new InMemoryListItemAdapter("source",NullIdentityProvider.INSTANCE);
		sourceAdapter.add(item1);
		sourceAdapter.add(item2);
		
		//initially target adapter is empty
		InMemoryListItemAdapter targetAdapter = new InMemoryListItemAdapter("target",NullIdentityProvider.INSTANCE);
		
		//sync process starts
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflict = syncEngine.synchronize();
		
		Assert.assertEquals(2, targetAdapter.getAll().size());
		
		//now we are adding item3 to TargetAdapter
		targetAdapter.add(item3);
		
		Assert.assertEquals(3, targetAdapter.getAll().size());
		Assert.assertEquals(2, sourceAdapter.getAll().size());
		
		//sync process starts
		conflict = syncEngine.synchronize();
		Assert.assertEquals(3, sourceAdapter.getAll().size());
		Assert.assertEquals(3, targetAdapter.getAll().size());
		
	}
	
	
	
	
	private Item createItem(String id,String title,String desc,String rawXML,String syncId
			,Date when,boolean isDeleted,String by){
		//creating content
		Element payLoad = XMLHelper.parseElement(rawXML);
		IContent content = new XMLContent(id,title,desc,payLoad);
		//create sync object
		Sync sync = new Sync(syncId,by,when,isDeleted);
		//create new Item object with help of the content and sync instance
		Item item  = new Item(content,sync);
		return item;
	}
}
