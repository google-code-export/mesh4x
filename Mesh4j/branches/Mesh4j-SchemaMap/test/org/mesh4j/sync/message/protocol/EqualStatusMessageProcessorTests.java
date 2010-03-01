package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MockInMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;


public class EqualStatusMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		EqualStatusMessageProcessor p = new EqualStatusMessageProcessor(null);
		p.createMessage(null);
	}
	
	@Test
	public void shouldCreateMessage(){
		
		MockInMemoryMessageSyncAdapter adapter = new MockInMemoryMessageSyncAdapter("myadapter", new ArrayList<Item>());
		MockSyncSession syncSession = new MockSyncSession(null);
		MockSyncProtocol syncProtocol = new MockSyncProtocol(adapter, syncSession); 
		
		EqualStatusMessageProcessor ncp = new EqualStatusMessageProcessor(null);
		ncp.setMessageSyncProtocol(syncProtocol);
		IMessage message = ncp.createMessage(syncSession);
		
		Assert.assertNotNull(message);
		Assert.assertEquals(syncProtocol.getSourceType(), message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(ncp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		EqualStatusMessageProcessor ncp = new EqualStatusMessageProcessor(null);
		Message message = new Message("a", "a", syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
	}

	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		EqualStatusMessageProcessor ncp = new EqualStatusMessageProcessor(null);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);	
	}
	
	@Test
	public void shouldProcessMessageReturnsEndSync(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		EqualStatusMessageProcessor ncp = new EqualStatusMessageProcessor(end);
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
	public void shouldProcessChangeSyncSessionTargetSourceType(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setShouldSendChanges(false);
		syncSession.setOpen();
		
		EndSyncMessageProcessor end =  new EndSyncMessageProcessor(null);
		EqualStatusMessageProcessor ncp = new EqualStatusMessageProcessor(end);
		Message message = new Message("a", ncp.getMessageType(), syncSession.getSessionId(), 0, "mySource", syncSession.getTarget());
		
		Assert.assertNull(syncSession.getTargetSourceType());	
		
		List<IMessage> messages = ncp.process(syncSession, message);
		Assert.assertEquals(1, messages.size());
		
		Assert.assertEquals("mySource", syncSession.getTargetSourceType());	
	}
	
}
