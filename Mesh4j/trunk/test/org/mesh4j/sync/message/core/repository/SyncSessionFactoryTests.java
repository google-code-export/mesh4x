package org.mesh4j.sync.message.core.repository;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncTest;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.message.core.SyncSession;
import org.mesh4j.sync.model.Item;


public class SyncSessionFactoryTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenTargetIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		factory.createSession("1", 0, "123", null, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		factory.createSession(null, 0, "123", new SmsEndpoint("123"), true);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIsEmpty(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		factory.createSession("", 0, "123", new SmsEndpoint("123"), true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIDIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		factory.createSession("1", 0, null, new SmsEndpoint("123"), true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIDIsEmpty(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		factory.createSession("1", 0, "", new SmsEndpoint("123"), true);
	}

	@Test
	public void shouldCreateSessionReturnsFeedAdapterWhenSourceIDDoesNotRegistered(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		
		IMessageSyncAdapter adapter = ((SyncSession)factory.createSession("1", 0, "123", new SmsEndpoint("123"), true)).getSyncAdapter();
		Assert.assertEquals(MessageSyncAdapter.class.getName(), adapter.getClass().getName());
		Assert.assertEquals(FeedAdapter.class.getName(), ((MessageSyncAdapter)adapter).getSyncAdapter().getClass().getName());
		
	}
	
	@Test
	public void shouldBasicCreateSessionReturnsFeedAdapterWhenSourceIDDoesNotRegistered(){
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		
		IMessageSyncAdapter adapter = ((SyncSession)factory.createSession("1", 0, "123", "333", true, true, null, new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>())).getSyncAdapter();
		Assert.assertEquals(MessageSyncAdapter.class.getName(), adapter.getClass().getName());
		Assert.assertEquals(FeedAdapter.class.getName(), ((MessageSyncAdapter)adapter).getSyncAdapter().getClass().getName());

	}


	@Test
	public void shouldCreateSession(){
		String sourceID = "123";
		String sessionID = IdGenerator.INSTANCE.newID();
		SmsEndpoint endpoint = new SmsEndpoint("123");
		
		SyncSessionFactory factory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, new MessageSyncAdapterFactory("", false));
		factory.registerSource(new InMemoryMessageSyncAdapter(sourceID));
		
		ISyncSession syncSession = factory.createSession(sessionID, 0, sourceID, endpoint, true);
		Assert.assertNotNull(syncSession);
		Assert.assertNull(syncSession.getLastSyncDate());
		Assert.assertEquals(sessionID, syncSession.getSessionId());
		Assert.assertEquals(0, syncSession.getVersion());
		Assert.assertEquals(0, syncSession.getSnapshot().size());
		Assert.assertEquals(sourceID, syncSession.getSourceId());
		Assert.assertEquals(endpoint, syncSession.getTarget());
		Assert.assertTrue(syncSession.isCompleteSync());
		Assert.assertTrue(syncSession.isFullProtocol());
		Assert.assertFalse(syncSession.isOpen());
	}
}
