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
import com.mesh4j.sync.test.utils.TestHelper;

public class MergeWithACKMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(null, null);
		p.createMessage(null, item);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenItemIsNull(){
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(null, null);
		p.createMessage(new MockSyncSession(null), null);
	}
	
	@Test
	public void shouldCreateMessageWhenItemHasDiff(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), false));
		
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(new MockItemEncoding(item), null);
		
		Item itemChanged = item.clone();
		itemChanged.getSync().update("jmt1", new Date());
		IMessage message = p.createMessage(syncSession, itemChanged);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("T2", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(p.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test
	public void shouldCreateMessageWhenItemHasDiffAndIsDeleted(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), false));
		
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(new MockItemEncoding(item), null);
		
		Item itemChanged = item.clone();
		itemChanged.getSync().delete("jmt1", new Date());
		IMessage message = p.createMessage(syncSession, itemChanged);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("T1", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(p.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test
	public void shouldCreateMessageWhenItemHasNotDiff(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		
		MockSyncSession syncSession = new MockSyncSession(null, item);
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(new MockItemEncoding(item), null);
		
		IMessage message = p.createMessage(syncSession, item);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("T1", message.getData());
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
		
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(null, null);
		Message message = new Message("a", "a", syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(null, null);
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageWithConflicts(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.nowAddDays(-1), false));

		Item itemChanged = new Item(new NullContent("1"), new Sync("1", "tmj", new Date(), false));
				
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.setFullProtocol(false);
		
		ACKMergeMessageProcessor ack = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(new MockItemEncoding(itemChanged), ack);
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "F1", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		
		Assert.assertNotNull(response);
		Assert.assertEquals("T1", response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(ack.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());

	}
	
	@Test
	public void shouldProcessMessage(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.nowAddDays(-1), false));

		Item itemChanged = item.clone();
		itemChanged.getSync().update("mtj", new Date());
				
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.setFullProtocol(false);
		
		ACKMergeMessageProcessor ack = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		MergeWithACKMessageProcessor p = new MergeWithACKMessageProcessor(new MockItemEncoding(itemChanged), ack);
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "F1", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		
		Assert.assertNotNull(response);
		Assert.assertEquals("F1", response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(ack.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}
}
