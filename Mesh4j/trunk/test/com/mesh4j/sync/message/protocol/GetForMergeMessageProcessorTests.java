package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;

public class GetForMergeMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));

		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		p.createMessage(null, item);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull1(){
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		p.createMessage(null, "1");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSyncIdIsNull(){
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		String syncId = null;
		p.createMessage(new MockSyncSession(null), syncId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenItemIsNull(){
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		Item item = null;
		p.createMessage(new MockSyncSession(null), item);
	}

	@Test
	public void shouldCreateMessage(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		
		MockSyncSession syncSession = new MockSyncSession(null);
		Assert.assertFalse(syncSession.isPendingAck("1"));
		
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(new MockItemEncoding(item), null);
		IMessage message = p.createMessage(syncSession, item);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("1|1~2", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(p.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		
		Assert.assertTrue(syncSession.isPendingAck("1"));
		Assert.assertFalse(syncSession.isCompleteSync());
	}
	
	@Test
	public void shouldCreateMessageWhenItemIsNew(){
				
		MockSyncSession syncSession = new MockSyncSession(null);
		Assert.assertFalse(syncSession.isPendingAck("1"));
		
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		IMessage message = p.createMessage(syncSession, "1");
		
		Assert.assertNotNull(message);
		Assert.assertEquals("1", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(p.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
		
		Assert.assertTrue(syncSession.isPendingAck("1"));
		Assert.assertFalse(syncSession.isCompleteSync());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseWhenSessionIsNonOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		List<IMessage> messages = p.process(syncSession, new Message("m", p.getMessageType(), "1", "1", new SmsEndpoint("1")));
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages); 
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseWhenMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(null, null);
		List<IMessage> messages = p.process(syncSession, new Message("m", "a", "1", "1", new SmsEndpoint("1")));
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages); 	}
	
	@Test
	public void shouldProcessMessageNewItem(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		
		MergeMessageProcessor merge = new MergeMessageProcessor(new MockItemEncoding(item), null);
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(new MockItemEncoding(item), merge);
		List<IMessage> messages = p.process(syncSession, new Message("m", p.getMessageType(), "1", "1", new SmsEndpoint("1")));
		
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertEquals("1", response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(merge.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());

	}
	
	@Test
	public void shouldProcessMessageUpdateItem(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();

		MergeMessageProcessor merge = new MergeMessageProcessor(new MockItemEncoding(item), null);
		GetForMergeMessageProcessor p = new GetForMergeMessageProcessor(new MockItemEncoding(item), merge);
		List<IMessage> messages = p.process(syncSession, new Message("m", p.getMessageType(), "1", "1|67~30", new SmsEndpoint("1")));
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertEquals("2", response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(merge.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}
		
}
