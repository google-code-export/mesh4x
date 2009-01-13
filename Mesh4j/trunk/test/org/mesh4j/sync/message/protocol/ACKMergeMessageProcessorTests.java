package org.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;


public class ACKMergeMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		ACKMergeMessageProcessor p = new ACKMergeMessageProcessor(null, null);
		p.createMessage(null, "1", true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsIfSyncIdIsNull(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(null, null);
		mp.createMessage(syncSession, null, true);
	}
	
	@Test
	public void shouldCreateMessage(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		IMessage message = mp.createMessage(syncSession, "1", true);

		Assert.assertNotNull(message);
		Assert.assertEquals("0|0|0|F1", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldCreateMessageWithConflicts(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.addConflict("1");
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		IMessage message = mp.createMessage(syncSession, "1", false);

		Assert.assertNotNull(message);
		Assert.assertEquals("0|0|0|T1", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldCreateMessageWithConflictsFullProtocol(){
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 0);
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", date, true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addConflict("1");
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		IMessage message = mp.createMessage(syncSession, "1", true);

		Assert.assertNotNull(message);
		Assert.assertEquals("0|0|0|T1T"+date.getTime()+"jmt", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldProcessMessageReturnNoResponseWhenSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", 0, "", null));
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageReturnNoResponseWhenMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), null);
		List<IMessage> messages = mp.process(syncSession, new Message("M", "kk", "1", 0, "", null));
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
	
	@Test
	public void shouldProcessMessageAddConflictFullProtocol(){
		String syncID = IdGenerator.INSTANCE.newID();
		
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		syncSession.setFullProtocol(true);
		
		syncSession.waitForAck(syncID);
		Assert.assertFalse(syncSession.isCompleteSync());
		Assert.assertFalse(syncSession.hasConflict(syncID));
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), new EndSyncMessageProcessor(null));
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), syncID, 0, "0|0|0|T"+syncID+"T1201834861000jmt", null));
		Assert.assertNotNull(messages);
		
		Assert.assertTrue(syncSession.hasConflict(syncID));
		Assert.assertTrue(syncSession.isCompleteSync());
	}
	
	@Test
	public void shouldProcessMessageAddConflict(){
		String syncID = IdGenerator.INSTANCE.newID();
		
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		syncSession.setFullProtocol(false);
		
		syncSession.waitForAck(syncID);
		Assert.assertFalse(syncSession.isCompleteSync());
		Assert.assertFalse(syncSession.hasConflict(syncID));
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), new EndSyncMessageProcessor(null));
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), syncID, 0, "0|0|0|T"+syncID, null));
		Assert.assertNotNull(messages);
		
		Assert.assertTrue(syncSession.hasConflict(syncID));
		Assert.assertTrue(syncSession.isCompleteSync());
	}
	
	@Test
	public void shouldProcessMessageReturnsEndWhenSyncIsComplete(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		syncSession.waitForAck("1");
		Assert.assertFalse(syncSession.isCompleteSync());
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), end);
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", 0, "0|0|0|F1", null));

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
		ACKMergeMessageProcessor mp = new ACKMergeMessageProcessor(new ItemEncoding(100), end);
		List<IMessage> messages = mp.process(syncSession, new Message("M", mp.getMessageType(), "1", 0, "0|0|0|F1", null));
		
		Assert.assertFalse(syncSession.isCompleteSync());	
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}
}
