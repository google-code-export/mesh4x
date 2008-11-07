package com.mesh4j.sync.adapter.S3.test;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.S3.S3Adapter;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.concurrent.command.ConcurrentCommandExecutor;
import org.mesh4j.sync.test.utils.concurrent.command.ConcurrentWorkerCommand;

import com.mesh4j.sync.adapter.S3.emulator.ObjectData;
import com.mesh4j.sync.adapter.S3.emulator.S3Service;

public class S3MultiNodesTest {

	@Test
	public void shouldReadItemReturnsNullBecauseItemDoesNotExists(){
		S3Service s3 = makeService(100);
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);
		Assert.assertNull(s3Adapter.get(IdGenerator.INSTANCE.newID()));
	}

	@Test
	public void shouldReadItemReturnsItemWhenItExistsInS3() throws Exception{
	
		S3Service s3 = makeService(100);
		
		Item item = makeNewItem(IdGenerator.INSTANCE.newID(), "<payload><foo>bar</foo></payload>", "jmt");
		this.write(item, s3, "feeds", "myFeed."+item.getSyncId());
		
		Thread.sleep(500);
		
		S3Adapter s3Adapter = new S3Adapter("feeds", "myFeed", s3, NullIdentityProvider.INSTANCE);		
		Item addedItem = s3Adapter.get(item.getSyncId());
		
		Assert.assertNotNull(addedItem);
		Assert.assertEquals(item, addedItem);
	}
	
	@Test
	public void emulate() throws Exception{
		
		boolean showAll = true;
		int execTimes = 30;
		int maxDelay = 100;
		int s3ReplicationDelay = 500;
		
		String bucket = "feeds";
		String objectPath = "myFeed";
		S3Service s3 = makeService(s3ReplicationDelay);
		S3Adapter s3Adapter = new S3Adapter(bucket, objectPath, s3, NullIdentityProvider.INSTANCE);

		// Create 15 items
		int size = 15;
		String[] availableSyncIds = new String[size];
		for (int i = 0; i < size; i++) {
			String syncId = IdGenerator.INSTANCE.newID();
			availableSyncIds[i] = syncId;
			
			Item newItem = makeNewItem(syncId, "<payload><foo>bar_"+syncId+"</foo></payload>", "admin");
			this.write(newItem, s3, bucket, objectPath);
		}		
		printNodeStatus(showAll, bucket, objectPath, s3);
		
		// Emulate sync process with 6 clients
		ConcurrentCommandExecutor executor = new ConcurrentCommandExecutor();
		executor.execute(
			new SyncClient("jmt", s3, bucket, objectPath, showAll, execTimes, maxDelay, 0),
			new SyncClient("nico", s3, bucket, objectPath, showAll, execTimes, maxDelay, 10),
			new SyncClient("juan", s3, bucket, objectPath, showAll, execTimes, maxDelay, 35),
			new SyncClient("kzu", s3, bucket, objectPath, showAll, execTimes, maxDelay, 50),
			new SyncClient("brian", s3, bucket, objectPath, showAll, execTimes, maxDelay, 75),
			new SyncClient("cibrax", s3, bucket, objectPath, showAll, execTimes, maxDelay, 150)
		);
		
		while(s3.isPropagatingChanges()){
			Thread.sleep(50);
		}		
		printNodeStatus(showAll, bucket, objectPath, s3);
		printItemStatus(s3Adapter, availableSyncIds, true);	// purge old histories
		
		while(s3.isPropagatingChanges()){
			Thread.sleep(50);
		}

		// Show status after purge old histories
		System.out.println("Purge........");
		printNodeStatus(showAll, bucket, objectPath, s3);		
		printItemStatus(s3Adapter, availableSyncIds, false);
	}

	private void printNodeStatus(boolean showAll, String bucket,
			String objectPath, S3Service s3) {
		for (int i = 0; i < 10; i++) {
			printData(s3, String.valueOf(i), bucket, objectPath, showAll);	
		}
	}

	private void printItemStatus(S3Adapter s3Adapter, String[] availableSyncIds, boolean purge) {
		for (String syncId : availableSyncIds) {
			System.out.println("SyncId: " + syncId);
			for (int i = 0; i < 10; i++) {
				String node = String.valueOf(i);
				Item item = s3Adapter.get(syncId, node);
				if(item == null){
					System.out.println("node: " + i + " item: null");
				} else {
					System.out.println("node: " + i + " item: " + item.getSync().getUpdates() + " deleted: " + item.isDeleted() + " conflicts: " + item.hasSyncConflicts());
					for (History h : item.getSync().getUpdatesHistory()) {
						System.out.println("\t" +h.getBy() + "\t" +h.getSequence() + "\t" + h.getWhen());
					}
					if(purge){
						if(item.hasSyncConflicts()){
							Item itemResult = MergeBehavior.resolveConflicts(item, "admin", new Date(), item.isDeleted());
							s3Adapter.update(itemResult);
						} 
						s3Adapter.purgeBranches(syncId);
					}
				}
			}			
		}
	}

	private void printData(S3Service s3, String node, String bucket, String objectPath, boolean showAll) {
		List<ObjectData> objs = s3.readObjectsStartsWith(node, bucket, objectPath);
		
		System.out.println("Node: " + node + " objects: " + objs.size());
		if(showAll){
			for (ObjectData obj : objs) {
				System.out.println(obj.getId());
			}
		}
	}
	
	// PRIVATE METHODS
	private class MockIdentityProvider implements IIdentityProvider{
		
		private String user = "admin";
		
		public MockIdentityProvider(String user) {
			super();
			this.user = user;
		}

		@Override
		public String getAuthenticatedUser() {
			return this.user;
		}
		
	}
	
	private class SyncClient extends ConcurrentWorkerCommand{

		// MODEL VARIABLES
		private S3Adapter s3Adapter;
		private InMemorySyncAdapter memoryAdapter;
		private SyncEngine syncEngine;
		private Random random = new Random();
		private String user;
		private int executionTimes;
		private int maxDelay;
		private boolean showAll;
		
		// BUSINESS METHODS
		public SyncClient(String user, S3Service s3, String bucket, String objectPath, boolean showAll, int executionTimes, int maxDelay, long delay) {
			super(new Object[0], delay);
			
			this.s3Adapter = new S3Adapter(bucket, objectPath, s3, new MockIdentityProvider(user));
			this.memoryAdapter = new InMemorySyncAdapter("in memory "+user, NullIdentityProvider.INSTANCE, this.s3Adapter.getAll());
			this.syncEngine = new SyncEngine(this.memoryAdapter, this.s3Adapter);
			this.user = user;
			this.executionTimes = executionTimes;
			this.maxDelay = maxDelay;
			this.showAll = showAll;
		}

		@Override
		public Object execute() throws Exception {
			for (int i = 0; i < this.executionTimes; i++) {
				int action = this.random.nextInt(3);
				try{
					if(action == 0){
						delete(i);
					} else if(action == 1){
						update(i);
					} else{
						add(i);
					}
					
					List<Item> conflicts = this.syncEngine.synchronize();
					for (Item conflictItem : conflicts) {
						Item resolvedItem = MergeBehavior.resolveConflicts(conflictItem, this.user, new Date(), conflictItem.isDeleted());
						this.memoryAdapter.update(resolvedItem);
						this.s3Adapter.update(resolvedItem);
					}
					Thread.sleep(this.random.nextInt(this.maxDelay));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			return "ok";
		}

		private void add(int i) throws Exception {
			if(showAll){
				System.out.println("ADD: " + this.user);
			}
			
			Item item = makeNewItem(String.valueOf(i)+ "_" + IdGenerator.INSTANCE.newID(), "<payload><foo>bar_"+i+"</foo></payload>", this.user);
			this.memoryAdapter.add(item);
		}
		
		private void delete(int i) throws Exception {
			Item item = getRandomItem();
			if(!item.isDeleted()){
				if(showAll){
					System.out.println("DELETE: " + this.user + " syncID: " + item.getSyncId());
				}
				this.memoryAdapter.delete(item.getSyncId());
			} else {
				if(showAll){
					System.out.println("NO DELETE: " + this.user + " syncID: " + item.getSyncId());
				}
			}

		}
		
		private void update(int i) throws Exception {
			Item item = getRandomItem();
			if(!item.isDeleted()){
				if(showAll){
					System.out.println("UPDATE: " + this.user + " syncID: " + item.getSyncId());
				}
				item.getSync().update(this.user, new Date());
				this.memoryAdapter.update(item);
			} else {
				if(showAll){
					System.out.println("NO UPDATE: " + this.user + " syncID: " + item.getSyncId());
				}
			}
		}

		private Item getRandomItem() {
			List<Item> all = this.memoryAdapter.getAll();
			Item item = all.get(this.random.nextInt(all.size() -1));
			return item;
		}
	}
	
	private void write(Item item, S3Service s3, String bucket, String objectPath) throws Exception{
		
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		String xmlItem = writer.writeAsXml(item);
		String oid = objectPath+"."+item.getSyncId()+"."+item.getLastUpdate().getSequence()+"."+item.getLastUpdate().getBy();
				
		s3.writeAndFastReplicate(bucket, oid, xmlItem.getBytes());

	}
	
	private Item makeNewItem(String syncId, String payload, String user)throws Exception{
		Document doc = DocumentHelper.parseText(payload);
	
		IContent content = new XMLContent(syncId, "title", "desc", doc.getRootElement());
		Sync sync = new Sync(syncId, user, new Date(), false);
		Item item = new Item(content, sync);
		return item;
	}
	
	private S3Service makeService(int s3Delay) {
		S3Service s3 = new S3Service(s3Delay);
		s3.addNode("0");
		s3.addNode("1");
		s3.addNode("2");
		s3.addNode("3");
		s3.addNode("4");
		s3.addNode("5");
		s3.addNode("6");
		s3.addNode("7");
		s3.addNode("8");
		s3.addNode("9");
		return s3;
	}


}
