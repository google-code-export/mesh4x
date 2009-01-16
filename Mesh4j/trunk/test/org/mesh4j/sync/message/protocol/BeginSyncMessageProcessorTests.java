package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MockInMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.utils.DateHelper;


public class BeginSyncMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		BeginSyncMessageProcessor p = new BeginSyncMessageProcessor(null, null, null);
		p.createMessage(null);
	}
	
	@Test
	public void shouldCreateMessageWithOutSinceDate(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null, null);
		IMessage message = mp.createMessage(syncSession);

		Assert.assertNotNull(message);
		Assert.assertEquals(syncSession.getSourceId()+"|T|T|T|0", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		//Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertNull(syncSession.getLastSyncDate());
	}
	
	@Test
	public void shouldProcessMessageWithOutSinceDateWithOutChanges(){
		
		MockInMemoryMessageSyncAdapter adapter = new MockInMemoryMessageSyncAdapter("myadapter", new ArrayList<Item>());
		MockSyncSession syncSession = new MockSyncSession(null);
		MockSyncProtocol syncProtocol = new MockSyncProtocol(adapter, syncSession); 
		
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		ncp.setMessageSyncProtocol(syncProtocol);
		
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		lvp.setMessageSyncProtocol(syncProtocol);
		
		String data = syncSession.getSourceId() + "|mock|T|T|T|0";
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, null);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(adapter.getSourceType(), response.getData());
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
		MockInMemoryMessageSyncAdapter adapter = new MockInMemoryMessageSyncAdapter("myadapter", new ArrayList<Item>());
		MockSyncProtocol syncProtocol = new MockSyncProtocol(adapter, syncSession); 
				
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		ncp.setMessageSyncProtocol(syncProtocol);
		
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		lvp.setMessageSyncProtocol(syncProtocol);
		
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
		esp.setMessageSyncProtocol(syncProtocol);
		
		String data = syncSession.getSourceId()+ "|mock|T|T|T|0";
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(adapter.getSourceType(), LastVersionStatusMessageProcessor.getSourceType(response.getData()));
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
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null, null);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, "aaaaa", syncSession.getSessionId(), 0, "dsds", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.beginSyncWasCalled());
		Assert.assertFalse(syncSession.getAllWasCalled());

	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null, null);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "dsds", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.beginSyncWasCalled());
		Assert.assertFalse(syncSession.getAllWasCalled());
	}
	
	@Test
	public void shouldCreateMessageWithSinceDate(){
		
		Date date = new Date();
		MockSyncSession syncSession = new MockSyncSession(date);
		
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(null, null, null);
		IMessage message = mp.createMessage(syncSession);

		Assert.assertNotNull(message);
		Assert.assertEquals(syncSession.getSourceId()+"|T|T|T|0|"+DateHelper.formatDateTime(date), message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		//Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertEquals(date, syncSession.getLastSyncDate());


	}
	
	@Test
	public void shouldProcessMessageWithSinceDateWithOutChanges(){
		
		Date date = new Date();
		MockSyncSession syncSession = new MockSyncSession(date);
		MockInMemoryMessageSyncAdapter adapter = new MockInMemoryMessageSyncAdapter("myadapter", new ArrayList<Item>());
		MockSyncProtocol syncProtocol = new MockSyncProtocol(adapter, syncSession); 

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		ncp.setMessageSyncProtocol(syncProtocol);
		
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		lvp.setMessageSyncProtocol(syncProtocol);
		
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
		esp.setMessageSyncProtocol(syncProtocol);
		
		String data = syncSession.getSourceId()+"|mock|T|T|T|0";
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(adapter.getSourceType(), response.getData());
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
		MockInMemoryMessageSyncAdapter adapter = new MockInMemoryMessageSyncAdapter("myadapter", new ArrayList<Item>());
		MockSyncProtocol syncProtocol = new MockSyncProtocol(adapter, syncSession); 
		
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		ncp.setMessageSyncProtocol(syncProtocol);
		
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		lvp.setMessageSyncProtocol(syncProtocol);
		
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
		esp.setMessageSyncProtocol(syncProtocol);
		
		String data = syncSession.getSourceId()+"|mock|T|T|T|0";
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, data, syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(adapter.getSourceType(), LastVersionStatusMessageProcessor.getSourceType(response.getData()));
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(lvp.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertTrue(syncSession.beginSyncWasCalled());
		Assert.assertTrue(syncSession.getAllWasCalled());
		Assert.assertEquals(date, syncSession.getLastSyncDate());
				
	}
	
	@Test
	public void shouldEncodeFullProtocol(){
		
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);
		syncSession.setFullProtocol(true);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
				
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = mp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertTrue(mp.getFullProtocol(message.getData()));
			
	}

	@Test
	public void shouldEncodeNonFullProtocol(){
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);
		syncSession.setFullProtocol(false);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
				
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = mp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertFalse(mp.getFullProtocol(message.getData()));		
	}
	
	@Test
	public void shouldEncodeSendChanges(){
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);
		syncSession.setShouldSendChanges(true);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
				
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = mp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertTrue(mp.getSendChanges(message.getData()));		
	}
	
	@Test
	public void shouldEncodeNoSendChanges(){
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);
		syncSession.setShouldSendChanges(false);
		
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
				
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = mp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertFalse(mp.getReceiveChanges(message.getData()));  // the value was inverted to receive because is the point of view according to endpoint sync session	
	}
	
	@Test
	public void shouldEncodeReceiveChanges(){
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);
		syncSession.setShouldReceiveChanges(true);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
				
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = mp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertTrue(mp.getReceiveChanges(message.getData()));		
	}
	
	@Test
	public void shouldEncodeNonReceiveChanges(){
		Date date = new Date();
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(date, item);
		syncSession.setShouldReceiveChanges(false);

		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null); 
		LastVersionStatusMessageProcessor lvp = new LastVersionStatusMessageProcessor(null, null, null);
		EqualStatusMessageProcessor esp = new EqualStatusMessageProcessor(null);
				
		BeginSyncMessageProcessor mp = new BeginSyncMessageProcessor(ncp, lvp, esp);
		IMessage message = mp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertFalse(mp.getSendChanges(message.getData()));		// the value was inverted to send because is the point of view according to the endpoint sync session
	}
	
}
