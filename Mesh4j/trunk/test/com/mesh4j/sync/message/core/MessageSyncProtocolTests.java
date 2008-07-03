package com.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.core.repository.SyncSessionFactory;
import com.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import com.mesh4j.sync.message.protocol.CancelSyncMessageProcessor;
import com.mesh4j.sync.message.protocol.MockMessageSyncAdapter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.validations.MeshException;


public class MessageSyncProtocolTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenPrefixIsNull(){
		new MessageSyncProtocol(null, new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenPrefixIsEmpty(){
		new MessageSyncProtocol("", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenInitialMsgIsNull(){
		new MessageSyncProtocol("M", null, new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenCancelMsgIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), null, new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenRepositoryIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), null, new ArrayList<IMessageProcessor>());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenMsgProcessorsIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), null);		
	}
	
	@Test
	public void shouldValidMessageReturnsFalseIfMsgIsNull(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
		syncProtocol.isValidMessageProtocol(null);
	}

	@Test
	public void shouldValidMessageReturnsFalseIfMsgHasInvalidMsgType(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
		Assert.assertFalse(syncProtocol.isValidMessageProtocol(new Message("J", "a", "a", "a", new SmsEndpoint("a"))));
	}

	@Test
	public void shouldProcessMsgReturnsNoResponseWhenSessionIsNotNullAndMsgTypeIsNotInitialMsg(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new ArrayList<IMessageProcessor>());
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, syncProtocol.processMessage(new Message("J", "a", "a", "a", new SmsEndpoint("a"))));	
	}
	
	@Test(expected=MeshException.class)
	public void shouldBeginSyncFailsWhenSessionIsOpen(){
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory();
		syncSessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		syncSessionFactory.createSession("a", "123", "123", true, true, new Date(), new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>());
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.beginSync("123", new SmsEndpoint("123"), true);
	}
	
	@Test(expected=MeshException.class)
	public void shouldCancelSyncFailsWhenSessionIsNull(){
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory();
		syncSessionFactory.registerSource(new MockMessageSyncAdapter("123"));

		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
	}
	
	@Test(expected=MeshException.class)
	public void shouldCancelSyncFailsWhenSessionIsClosed(){
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory();
		syncSessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSession = syncSessionFactory.createSession("a", "123", "123", true, true, new Date(), new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>());
		syncSession.endSync(new Date());
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
	}
	
	@Test
	public void shouldCancelSync(){
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory();
		syncSessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSession = syncSessionFactory.createSession("a", "123", "123", true, true, new Date(), new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>());
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
		
		Assert.assertFalse(syncSession.isOpen());
	}

	// TODO (JMT) MEshSMS: Tests beginSync and ProcessMessages when there are not an adapter register for the sourceID 
	// TODO (JMT) MEshSMS: Tests SyncSession creation guards
	// TODO (JMT) MEshSMS: Tests SyncSessionFactory createSession and readSession return null when there are not an adapter register for the sourceID
	
}
