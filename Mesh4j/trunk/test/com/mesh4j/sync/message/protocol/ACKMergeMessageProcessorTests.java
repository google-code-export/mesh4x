package com.mesh4j.sync.message.protocol;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.core.Message;

public class ACKMergeMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		ACKMergeMessageProcessor p = new ACKMergeMessageProcessor(null);
		p.createMessage(null, "1", true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsIfSyncIdIsNull(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(null);
		mp.createMessage(syncSession, null, true);
	}
	
	@Test
	public void shouldCreateMessage(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(null);
		IMessage message = mp.createMessage(syncSession, "1", true);

		Assert.assertNotNull(message);
		Assert.assertEquals("T1", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldCreateMessageWithConflicts(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(null);
		IMessage message = mp.createMessage(syncSession, "1", false);

		Assert.assertNotNull(message);
		Assert.assertEquals("F1", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldProcessMessageReturnNoResponseWhenSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(null);
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", "", null));
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageReturnNoResponseWhenMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(null);
		List<IMessage> messages = mp.process(syncSession, new Message("M", "kk", "1", "", null));
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageAddConflict(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		syncSession.waitForAck("1");
		Assert.assertFalse(syncSession.isCompleteSync());
		Assert.assertFalse(syncSession.hasConflict("1"));
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new EndSyncMessageProcessor(null));
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", "T1", null));
		Assert.assertNotNull(messages);
		
		Assert.assertTrue(syncSession.hasConflict("1"));
		Assert.assertTrue(syncSession.isCompleteSync());
	}
	
	@Test
	public void shouldProcessMessageReturnsEndWhenSyncIsComplete(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		syncSession.waitForAck("1");
		Assert.assertFalse(syncSession.isCompleteSync());
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(end);
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", "F1", null));

		Assert.assertTrue(syncSession.isCompleteSync());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());

	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseWhenSyncIsNotComplete(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		syncSession.waitForAck("2");
		Assert.assertFalse(syncSession.isCompleteSync());
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(end);
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", "F1", null));
		
		Assert.assertFalse(syncSession.isCompleteSync());	
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
}
