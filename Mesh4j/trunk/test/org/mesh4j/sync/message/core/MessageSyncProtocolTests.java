package org.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.OpaqueFeedSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.SyncSessionFactory;
import org.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.CancelSyncMessageProcessor;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.MeshException;

public class MessageSyncProtocolTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenPrefixIsNull(){
		new MessageSyncProtocol(null, new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenPrefixIsEmpty(){
		new MessageSyncProtocol("", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenInitialMsgIsNull(){
		new MessageSyncProtocol("M", null, new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenCancelMsgIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), null, new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenRepositoryIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), null, new ArrayList<IMessageProcessor>());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenMsgProcessorsIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), null);		
	}
	
	@Test
	public void shouldValidMessageReturnsFalseIfMsgIsNull(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
		syncProtocol.isValidMessageProtocol(null);
	}

	@Test
	public void shouldValidMessageReturnsFalseIfMsgHasInvalidMsgType(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
		Assert.assertFalse(syncProtocol.isValidMessageProtocol(new Message("J", "a", "a", 0, "a", new SmsEndpoint("a"))));
	}

	@Test
	public void shouldProcessMsgReturnsNoResponseWhenSessionIsNotNullAndMsgTypeIsNotInitialMsg(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, syncProtocol.processMessage(new Message("J", "a", "a", 0, "a", new SmsEndpoint("a"))));	
	}
	
	
	public void shouldBeginSyncReturnNullWhenSessionIsOpen(){
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));
		
		syncSessionFactory.createSession("a", 0, "123", "123", true, true, true, true, false, new Date(), new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		Assert.assertNull(syncProtocol.beginSync("123", new SmsEndpoint("123"), true, true, true));
	}
	
	@Test
	public void shouldBeginSyncUseFeedAdapterWhenSourceIDIsNotRegistered(){
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(new OpaqueFeedSyncAdapterFactory(""), true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		
		SmsEndpoint endpoint = new SmsEndpoint("123");
		Message message = (Message)syncProtocol.beginSync("MySourceType:123", endpoint, true, true, true);
		String sourceId = syncProtocol.getInitialMessage().getSourceId(message.getData());
		SyncSession syncSession = (SyncSession)syncProtocol.getSyncSession(sourceId, endpoint);

		Assert.assertEquals(MessageSyncAdapter.class.getName(), syncSession.getSyncAdapter().getClass().getName());
		Assert.assertEquals(FeedAdapter.class.getName(), ((MessageSyncAdapter)syncSession.getSyncAdapter()).getSyncAdapter().getClass().getName());

	}
	
	@Test
	public void shouldProcessMessageReturnNoResponseWhenSourceIDIsNotRegistered(){
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, syncProtocol.processMessage(new Message("M", "1", "1", 0, "a|T|T|T|0", new SmsEndpoint("sms:1"))));
	}
	
	@Test(expected=MeshException.class)
	public void shouldCancelSyncFailsWhenSessionIsNull(){
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));

		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
	}
	
	@Test(expected=MeshException.class)
	public void shouldCancelSyncFailsWhenSessionIsClosed(){
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));
		
		ISyncSession syncSession = syncSessionFactory.createSession("a", 0, "123", "123", true, true, true, true, true, new Date(), new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0);
		syncSession.endSync(new Date());
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
	}
	
	@Test
	public void shouldCancelSync(){
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));
		
		ISyncSession syncSession = syncSessionFactory.createSession("a", 0, "123", "123", true, true, true, true, true, new Date(), new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
		
		Assert.assertFalse(syncSession.isOpen());
	}
}
