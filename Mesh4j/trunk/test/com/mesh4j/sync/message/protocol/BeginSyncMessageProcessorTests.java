package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.message.core.SyncSessionFactory;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.DateHelper;

public class BeginSyncMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		BeginSyncMessageProcessor p = new BeginSyncMessageProcessor(null, null);
		p.createMessage(null);
	}
	
	@Test
	public void shouldCreateMessageWithOutSinceDate(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		IMessage message = mp.createMessage(syncSession);

		Assert.assertNotNull(message);
		Assert.assertEquals(syncSession.getSourceId(), message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertNull(syncSession.getLastSyncDate());
	}
	
	@Test
	public void shouldProcessMessageWithOutSinceDateWithOutChanges(){
		
		MockSyncSession syncSession = new MockSyncSession(null);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		
		String data = syncSession.getSourceId();
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(ncp.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertTrue(syncSession.getAllWasCalled());
		Assert.assertNull(syncSession.getLastSyncDate());

	}
	
	@Test
	public void shouldProcessMessageWithOutSinceDateWithChanges(){
		
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		
		String data = syncSession.getSourceId();
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(lvp.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertTrue(syncSession.getAllWasCalled());
		Assert.assertNull(syncSession.getLastSyncDate());
				
	}

	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, "aaaaa", syncSession.getSessionId(), "dsds", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.beginSyncWasCalled());
		Assert.assertFalse(syncSession.getAllWasCalled());

	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), "dsds", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.beginSyncWasCalled());
		Assert.assertFalse(syncSession.getAllWasCalled());
	}
	
	@Test
	public void shouldCreateMessageWithSinceDate(){
		
		Date date = new Date();
		MockSyncSession syncSession = new MockSyncSession(date);
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		IMessage message = mp.createMessage(syncSession);

		Assert.assertNotNull(message);
		Assert.assertEquals(syncSession.getSourceId()+"|"+DateHelper.formatDateTime(date), message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertEquals(date, syncSession.getLastSyncDate());


	}
	
	@Test
	public void shouldProcessMessageWithSinceDateWithOutChanges(){
		
		Date date = new Date();
		MockSyncSession syncSession = new MockSyncSession(date);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		
		String data = syncSession.getSourceId();
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(ncp.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertTrue(syncSession.getAllWasCalled());
		Assert.assertEquals(date, syncSession.getLastSyncDate());
	}
	
	@Test
	public void shouldProcessMessageWithSinceDateWithChanges(){
		
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		
		String data = syncSession.getSourceId();
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(lvp.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertTrue(syncSession.getAllWasCalled());
		Assert.assertEquals(date, syncSession.getLastSyncDate());
				
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsIfSessionFactoryIsNull(){
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		mp.createSession(null, "123", new SmsEndpoint("sms:1"), false);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsIfSourceIDIsNull(){
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		mp.createSession(new SyncSessionFactory(), null, new SmsEndpoint("sms:1"), false);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsIfEndPointIsNull(){
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		mp.createSession(new SyncSessionFactory(), "123", null, false);
	}
	
	@Test
	public void shouldCreateSession(){
		String sourceID = "1234";
		IEndpoint endpoint = new SmsEndpoint("sms:1");
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		
		SyncSessionFactory sessionFac = new SyncSessionFactory();
		sessionFac.registerSource(new MockMessageSyncAdapter(sourceID));
		
		ISyncSession syncSession = mp.createSession(sessionFac, sourceID, endpoint, false);
		
		Assert.assertNotNull(syncSession);
		Assert.assertNotNull(syncSession.getSessionId());
		Assert.assertFalse(syncSession.isOpen());
		Assert.assertNotNull(syncSession.getAll());
		Assert.assertEquals(0, syncSession.getAll().size());
		Assert.assertNotNull(syncSession.getSnapshot());
		Assert.assertEquals(0, syncSession.getSnapshot().size());
		Assert.assertNull(syncSession.getLastSyncDate());
		Assert.assertEquals(sourceID, syncSession.getSourceId());
		Assert.assertEquals(endpoint, syncSession.getTarget());

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFromMessageFailsIfSessionFactoryIsNull(){
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		mp.createSession(null, new Message("a", "1", "a", "a", new SmsEndpoint("sms:1")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFromMessageFailsIfMessageIsNull(){
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		mp.createSession(new SyncSessionFactory(), null);
	}
	
	@Test
	public void shouldDoesNotCreateSessionFromMessageIfMessageTypeIsInvalid(){
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		ISyncSession syncSession = mp.createSession(new SyncSessionFactory(), new Message("a", "a", "a", "a", new SmsEndpoint("sms:1")));
		Assert.assertNull(syncSession);
	}
	
	@Test
	public void shouldCreateSessionFromMessageWithOutSinceDate(){

		String sourceID = "1234";
		IEndpoint endpoint = new SmsEndpoint("sms:1");

		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), "12", sourceID, endpoint);
		
		SyncSessionFactory sessionFac = new SyncSessionFactory();
		sessionFac.registerSource(new MockMessageSyncAdapter(sourceID));
		
		ISyncSession syncSession = mp.createSession(sessionFac, message);
		
		Assert.assertNotNull(syncSession);
		Assert.assertNotNull(syncSession.getSessionId());
		Assert.assertEquals("12", syncSession.getSessionId());
		Assert.assertFalse(syncSession.isOpen());
		Assert.assertNotNull(syncSession.getAll());
		Assert.assertEquals(0, syncSession.getAll().size());
		Assert.assertNotNull(syncSession.getSnapshot());
		Assert.assertEquals(0, syncSession.getSnapshot().size());
		Assert.assertNull(syncSession.getLastSyncDate());
		Assert.assertEquals(sourceID, syncSession.getSourceId());
		Assert.assertEquals(endpoint, syncSession.getTarget());
	}
	
	@Test
	public void shouldCreateSessionFromMessageWithSinceDate(){

		String sourceID = "1234";
		IEndpoint endpoint = new SmsEndpoint("sms:1");

		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null);
		
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), "12", sourceID+"|"+DateHelper.formatDateTime(new Date()), endpoint);
		
		SyncSessionFactory sessionFac = new SyncSessionFactory();
		sessionFac.registerSource(new MockMessageSyncAdapter(sourceID));
		
		ISyncSession syncSession = mp.createSession(sessionFac, message);
		
		Assert.assertNotNull(syncSession);
		Assert.assertNotNull(syncSession.getSessionId());
		Assert.assertEquals("12", syncSession.getSessionId());
		Assert.assertFalse(syncSession.isOpen());
		Assert.assertNotNull(syncSession.getAll());
		Assert.assertEquals(0, syncSession.getAll().size());
		Assert.assertNotNull(syncSession.getSnapshot());
		Assert.assertEquals(0, syncSession.getSnapshot().size());
		Assert.assertNull(syncSession.getLastSyncDate());
		Assert.assertEquals(sourceID, syncSession.getSourceId());
		Assert.assertEquals(endpoint, syncSession.getTarget());
	}

}
