package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.DateHelper;

public class ACKEndSyncMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		p.createMessage(null, new Date());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSinceDateIsNull(){
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		p.createMessage(new MockSyncSession(null), null);
	}
	
	@Test
	public void shouldCreateMessage(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		IMessage message = p.createMessage(syncSession, syncSession.createSyncDate());
		
		Assert.assertNotNull(message);
		Assert.assertEquals(DateHelper.formatDateTime(syncSession.createSyncDate()), message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(p.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}	
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		Message message = new Message("a", "a", syncSession.getSessionId(), "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSyncDateIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), "erkrnwfkwefk", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.endSyncWasCalled());

	}
	
	@Test
	public void shouldProcessMessageReturnsEndAck(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), DateHelper.formatDateTime(syncSession.createSyncDate()), syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);

		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertTrue(syncSession.endSyncWasCalled());
		Assert.assertEquals(syncSession.createSyncDate(), syncSession.getLastSyncDate());
	}
}
