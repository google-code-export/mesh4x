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

public class MergeMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNullWithSyncID(){
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		p.createMessage(null, "1", new int[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenItemIsNullWithSyncID(){
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		String syncId = null;
		p.createMessage(new MockSyncSession(null), syncId, new int[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenDiffIsNullWithSyncID(){
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		p.createMessage(new MockSyncSession(null), "2", null);
	}
	
	@Test
	public void shouldCreateMessageWithSyncID(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), false));
		
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		
		MergeMessageProcessor p = new MergeMessageProcessor(new MockItemEncoding(item), null);

		IMessage message = p.createMessage(syncSession, "1", new int[]{1, 3});
		
		Assert.assertNotNull(message);
		Assert.assertEquals("2", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(p.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		p.createMessage(null, item, new int[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenItemIsNull(){
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		Item item = null;
		p.createMessage(new MockSyncSession(null), item, new int[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenDiffIsNull(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		p.createMessage(new MockSyncSession(null), item, null);
	}
	
	@Test
	public void shouldCreateMessageWhenItemHasDiff(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), false));
		
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		
		MergeMessageProcessor p = new MergeMessageProcessor(new MockItemEncoding(item), null);
		
		Item itemChanged = item.clone();
		itemChanged.getSync().update("jmt1", new Date());
		IMessage message = p.createMessage(syncSession, itemChanged, new int[]{1, 3});
		
		Assert.assertNotNull(message);
		Assert.assertEquals("2", message.getData());
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
		MergeMessageProcessor p = new MergeMessageProcessor(new MockItemEncoding(item), null);
		
		IMessage message = p.createMessage(syncSession, item, new int[0]);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("1", message.getData());
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
		
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
		Message message = new Message("a", "a", syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		MergeMessageProcessor p = new MergeMessageProcessor(null, null);
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
		syncSession.waitForAck("1");
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		MergeMessageProcessor p = new MergeMessageProcessor(new MockItemEncoding(itemChanged), end);
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertTrue(syncSession.hasConflict("1"));

	}
	
	@Test
	public void shouldProcessMessageWaitForAcks(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.nowAddDays(-1), false));

		Item itemChanged = item.clone();
		itemChanged.getSync().update("mtj", new Date());
				
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.waitForAck("3");
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		MergeMessageProcessor p = new MergeMessageProcessor(new MockItemEncoding(itemChanged), end);
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.hasConflict("1"));
	}
	
	@Test
	public void shouldProcessMessageCompleteSync(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.nowAddDays(-1), false));

		Item itemChanged = item.clone();
		itemChanged.getSync().update("mtj", new Date());
				
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.waitForAck("1");
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		MergeMessageProcessor p = new MergeMessageProcessor(new MockItemEncoding(itemChanged), end);
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertFalse(syncSession.hasConflict("1"));
	}
}
