package org.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;


public class NoChangesMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		NoChangesMessageProcessor p = new NoChangesMessageProcessor(null, null);
		p.createMessage(null);
	}
	
	@Test
	public void shouldCreateMessage(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		IMessage message = ncp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertEquals(0, message.getData().length());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(ncp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		Message message = new Message("a", "a", syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}

	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(null, null);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);	
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseNoLocalChanges(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(end, merge);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());		
	}
	
	@Test
	public void shouldProcessMessageReturnsLocalChanges(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(end, merge);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(merge.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}

	@Test
	public void shouldProcessChangesReturnLocalChangesIfSessionHasLocalChangesAndShouldSendChanges(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setShouldSendChanges(true);
		syncSession.setOpen();
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(end, merge);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(merge.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}
	
	@Test
	public void shouldProcessChangesReturnEndSyncIfSessionHasLocalChangesAndShouldNotSendChanges(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setShouldSendChanges(false);
		syncSession.setOpen();
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(end, merge);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}
	
	@Test
	public void shouldProcessChangesReturnEndSyncIfSessionHasNotLocalChangesAndShouldSendChanges(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setShouldSendChanges(true);
		syncSession.setOpen();
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(end, merge);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());	
	}
	
	@Test
	public void shouldProcessChangesReturnEndSyncIfSessionHasNotLocalChangesAndShouldNotSendChanges(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setShouldSendChanges(false);
		syncSession.setOpen();
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		NoChangesMessageProcessor ncp = new NoChangesMessageProcessor(end, merge);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData().length());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());	
	}
	
	
}
