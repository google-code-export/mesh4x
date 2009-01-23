package org.mesh4j.sync.message.core.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.message.core.SyncSession;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;


public class SyncSessionFactoryTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenTargetIsNull(){
		
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.createSession("1", 0, "123", null, true, true, true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.createSession(null, 0, "123", new SmsEndpoint("123"), true, true, true);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIsEmpty(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.createSession("", 0, "123", new SmsEndpoint("123"), true, true, true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIDIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.createSession("1", 0, null, new SmsEndpoint("123"), true, true, true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIDIsEmpty(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.createSession("1", 0, "", new SmsEndpoint("123"), true, true, true);
	}

	@Test
	public void shouldCreateSessionReturnsFeedAdapterWhenSourceIDDoesNotRegistered(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		
		IMessageSyncAdapter adapter = ((SyncSession)factory.createSession("1", 0, "kml:123", new SmsEndpoint("123"), true, true, true)).getSyncAdapter();
		Assert.assertEquals(MessageSyncAdapter.class.getName(), adapter.getClass().getName());
		Assert.assertEquals(FeedAdapter.class.getName(), ((MessageSyncAdapter)adapter).getSyncAdapter().getClass().getName());
		
	}
	
	@Test
	public void shouldBasicCreateSessionReturnsFeedAdapterWhenSourceIDDoesNotRegistered(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		
		IMessageSyncAdapter adapter = ((SyncSession)factory.createSession(
			"1", 0, "kml:123", "333", true, true, true, true, false, false, new Date(), null, null, 0, 0, new ArrayList<Item>(), 
			new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0, 
			null, 0, 0, 0))
				.getSyncAdapter();
		
		Assert.assertEquals(MessageSyncAdapter.class.getName(), adapter.getClass().getName());
		Assert.assertEquals(FeedAdapter.class.getName(), ((MessageSyncAdapter)adapter).getSyncAdapter().getClass().getName());

	}

	@Test
	public void shouldCreateSession(){
		String sourceID = "kml:123";
		String sessionID = IdGenerator.INSTANCE.newID();
		SmsEndpoint endpoint = new SmsEndpoint("123");
		
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.registerSource(new InMemoryMessageSyncAdapter(sourceID));
		
		ISyncSession syncSession = factory.createSession(sessionID, 0, sourceID, endpoint, true, true, true);
		Assert.assertNotNull(syncSession);
		Assert.assertNull(syncSession.getLastSyncDate());
		Assert.assertEquals(sessionID, syncSession.getSessionId());
		Assert.assertEquals(0, syncSession.getVersion());
		Assert.assertEquals(0, syncSession.getSnapshot().size());
		Assert.assertEquals(sourceID, syncSession.getSourceId());
		Assert.assertEquals(endpoint, syncSession.getTarget());
		Assert.assertTrue(syncSession.isCompleteSync());
		Assert.assertTrue(syncSession.isFullProtocol());
		Assert.assertTrue(syncSession.shouldSendChanges());
		Assert.assertTrue(syncSession.shouldReceiveChanges());
		Assert.assertFalse(syncSession.isOpen());
		Assert.assertFalse(syncSession.isBroken());
		Assert.assertFalse(syncSession.isCancelled());
		Assert.assertEquals(InMemoryMessageSyncAdapter.SOURCE_TYPE, syncSession.getSourceType());
		Assert.assertEquals(0, syncSession.getNumberOfAddedItems());
		Assert.assertEquals(0, syncSession.getNumberOfUpdatedItems());
		Assert.assertEquals(0, syncSession.getNumberOfDeletedItems());
		Assert.assertEquals(0, syncSession.getTargetNumberOfAddedItems());
		Assert.assertEquals(0, syncSession.getTargetNumberOfAddedItems());
		Assert.assertEquals(0, syncSession.getTargetNumberOfAddedItems());
		Assert.assertNull(syncSession.getTargetSourceType());
	}
	
	@Test
	public void shouldCreateSessionFromRepository(){
		String endpoint = "022345434623";
		String sourceId = "kml:myFeed";
		String sessionId = IdGenerator.INSTANCE.newID();
		int version = 6;
		Date lastSyncDate = new Date();
		Date startDate = new Date();
		Date endDate = new Date();
		
		boolean open = false;
		boolean cancelled = true;
		boolean broken = true;
		boolean full = false;
		boolean shouldSend = true;
		boolean shouldReceive = false;

		List<Item> current = new ArrayList<Item>(); 
		
		IContent content = new XMLContent("1", "1", "1", XMLHelper.parseElement("<foo>bar</foo>"));
		Sync sync = new Sync("1");
		Item item = new Item(content, sync);
		
		List<Item> snapshot = new ArrayList<Item>();
		snapshot.add(item);
		
		List<String> conflicts = new ArrayList<String>(); 
		conflicts.add(IdGenerator.INSTANCE.newID());
		
		List<String> acks = new ArrayList<String>();
		acks.add(IdGenerator.INSTANCE.newID());
		
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, createMessageSyncAdapterFactory());
		factory.registerSource(new InMemoryMessageSyncAdapter(sourceId));
		
		ISyncSession syncSession = factory.createSession(sessionId, version, sourceId, endpoint, full, shouldSend, shouldReceive, open, broken, cancelled, 
			startDate, endDate, lastSyncDate, 1, 2, current, snapshot, conflicts, acks, 1, 2, 3, "mySource", 4, 5, 6);
		
		Assert.assertNotNull(syncSession);
		Assert.assertEquals(sessionId, syncSession.getSessionId());
		Assert.assertEquals(version, syncSession.getVersion());
		Assert.assertEquals(sourceId, syncSession.getSourceId());
		Assert.assertEquals(InMemoryMessageSyncAdapter.SOURCE_TYPE, syncSession.getSourceType());
		Assert.assertEquals(endpoint, syncSession.getTarget().getEndpointId());
		Assert.assertEquals(full, syncSession.isFullProtocol());
		Assert.assertEquals(shouldSend, syncSession.shouldSendChanges());
		Assert.assertEquals(shouldReceive, syncSession.shouldReceiveChanges());
		Assert.assertEquals(open, syncSession.isOpen());
		Assert.assertEquals(broken, syncSession.isBroken());
		Assert.assertEquals(cancelled, syncSession.isCancelled());		
		Assert.assertNotNull(syncSession.getLastSyncDate());
		Assert.assertEquals(startDate, syncSession.getStartDate());
		Assert.assertEquals(endDate, syncSession.getEndDate());
		Assert.assertEquals(lastSyncDate, syncSession.getLastSyncDate());
		
		Assert.assertEquals(snapshot.size(), syncSession.getSnapshot().size());
		Assert.assertEquals(current.size(), syncSession.getCurrentSnapshot().size());
		Assert.assertEquals(acks.size(), syncSession.getAllPendingACKs().size());
		Assert.assertEquals(conflicts.size(), syncSession.getConflictsSyncIDs().size());
		Assert.assertFalse(syncSession.isCompleteSync());

		Assert.assertEquals(1, syncSession.getNumberOfAddedItems());
		Assert.assertEquals(2, syncSession.getNumberOfUpdatedItems());
		Assert.assertEquals(3, syncSession.getNumberOfDeletedItems());
		Assert.assertEquals(4, syncSession.getTargetNumberOfAddedItems());
		Assert.assertEquals(5, syncSession.getTargetNumberOfUpdatedItems());
		Assert.assertEquals(6, syncSession.getTargetNumberOfDeletedItems());
		Assert.assertEquals("mySource", syncSession.getTargetSourceType());
		
		Assert.assertEquals(1, syncSession.getLastNumberInMessages());
		Assert.assertEquals(2, syncSession.getLastNumberOutMessages());
	}

	private MessageSyncAdapterFactory createMessageSyncAdapterFactory() {
		OpaqueFeedSyncAdapterFactory feedFactory = new OpaqueFeedSyncAdapterFactory(TestHelper.baseDirectoryForTest());
		KMLDOMLoaderFactory kmlFactory = new KMLDOMLoaderFactory(TestHelper.baseDirectoryForTest());
		
		MessageSyncAdapterFactory msgSyncAdapter = new MessageSyncAdapterFactory(feedFactory, false, kmlFactory);
		return msgSyncAdapter;
	}

}
