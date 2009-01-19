package org.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.utils.DateHelper;


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
		Message message = new Message("a", "a", syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.endSyncWasCalled());
	}
	
	@Test
	public void shouldProcessMessageReturnsNoResponseBecauseSyncDateIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, "erkrnwfkwefk", syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertFalse(syncSession.endSyncWasCalled());

	}
	
	@Test
	public void shouldProcessMessageReturnsEndAck(){
		
		IMessageSyncProtocol messageSyncProtocol = new IMessageSyncProtocol(){
	
			@Override public IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveMessages) {
				Assert.fail();
				return null;
			}

			@Override
			public IMessage cancelSync(String sourceId, IEndpoint target) {
				Assert.fail();
				return null;
			}

			@Override
			public void endSync(ISyncSession syncSession, Date date) {
				syncSession.endSync(date);
			}

			@Override
			public ISyncSession getSyncSession(String sourceId, IEndpoint target) {
				Assert.fail();
				return null;
			}

			@Override
			public boolean isValidMessageProtocol(IMessage message) {
				Assert.fail();
				return false;
			}

			@Override
			public List<IMessage> processMessage(IMessage message) {
				Assert.fail();
				return null;
			}

			@Override
			public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
				Assert.fail();
			}
			
			@Override
			public void registerSource(IMessageSyncAdapter adapter) {
				Assert.fail();
			}

			@Override
			public void notifyBeginSync(ISyncSession syncSession) {
				Assert.fail();
			}

			@Override
			public void registerSyncAware(IMessageSyncAware syncAware) {
				Assert.fail();
			}

			@Override
			public void cancelSync(ISyncSession syncSession) {
				Assert.fail();				
			}

			@Override
			public IMessageSyncAdapter getSource(String sourceId) {
				Assert.fail();
				return null;
			}

			@Override
			public List<ISyncSession> getAllSyncSessions() {
				Assert.fail();
				return null;
			}
		};
		
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		
		ACKEndSyncMessageProcessor p = new ACKEndSyncMessageProcessor();
		p.setMessageSyncProtocol(messageSyncProtocol);
		
		Message message = new Message("a", p.getMessageType(), syncSession.getSessionId(), 0, DateHelper.formatDateTime(syncSession.createSyncDate()), syncSession.getTarget());
		List<IMessage> messages = p.process(syncSession, message);

		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, messages);
		Assert.assertTrue(syncSession.endSyncWasCalled());
		Assert.assertEquals(syncSession.createSyncDate(), syncSession.getLastSyncDate());
	}
}
