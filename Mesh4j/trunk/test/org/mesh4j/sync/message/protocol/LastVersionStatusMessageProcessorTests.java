package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;


public class LastVersionStatusMessageProcessorTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenSessionIsNull(){
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		mp.createMessage(null, new ArrayList<Item>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenItemsIsNull(){
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		mp.createMessage(new MockSyncSession(null), null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateMessageFailsWhenItemsIsEmpty(){
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		mp.createMessage(new MockSyncSession(null), new ArrayList<Item>());
	}
	
	@Test
	public void shouldCreateMessageDeletedItem(){
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), true)));
		items.add(new Item(new NullContent("2"), new Sync("2", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false)));
		
		MockSyncSession syncSession = new MockSyncSession(null);
		
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		IMessage message = mp.createMessage(syncSession, items);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("1~-1269158974~D~jmt~1201834861000|2~-702666385", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldCreateMessage(){
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false)));
		items.add(new Item(new NullContent("2"), new Sync("2", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false)));
		
		MockSyncSession syncSession = new MockSyncSession(null);
		
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		IMessage message = mp.createMessage(syncSession, items);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("1~-702666385|2~-702666385", message.getData());
		Assert.assertEquals(syncSession.getTarget(), message.getEndpoint());
		Assert.assertEquals(mp.getMessageType(), message.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, message.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), message.getSessionId());
	}
	
	@Test
	public void shouldProcessFailsWhenSessionIsNotOpen(){
		MockSyncSession syncSession = new MockSyncSession(null);
		
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, mp.process(syncSession, null));
	}
	
	@Test
	public void shouldProcessFailsWhenMessageTypeIsInvalid(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		Message message = new Message("a", "a", "a", 0, "a", null);
		
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, null);
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, mp.process(syncSession, message));		
	}	
	
	@Test
	public void shouldProcessThrowsGetItemForNew(){
		MockSyncSession syncSession = new MockSyncSession(null);
		syncSession.setOpen();
		
		GetForMergeMessageProcessor get = new GetForMergeMessageProcessor(new ItemEncoding(100), null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(get, null, null);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-702666385", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(get.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}

	@Test
	public void shouldProcessMarkConflicItemWhenItemWasChanged(){
		
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));

		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		syncSession.setOpen();
		syncSession.setFullProtocol(false);
		
		Assert.assertFalse(syncSession.hasConflict("1"));
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, end);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-73244", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
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
	public void shouldProcessMarkConflicItemWhenItemWasChangedFullProtocol(){
		
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));

		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		syncSession.setOpen();
		syncSession.setFullProtocol(true);
		
		Assert.assertFalse(syncSession.hasConflict("1"));
		
		GetForMergeMessageProcessor get = new GetForMergeMessageProcessor(new ItemEncoding(100), null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(get, null, null);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-73244", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(get.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		Assert.assertFalse(syncSession.hasConflict("1"));
	}
	
	@Test
	public void shouldProcessDeletedItem(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));

		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.setFullProtocol(false);
		
		EndSyncMessageProcessor end = new EndSyncMessageProcessor(null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, null, end);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-1269158974~D~jmt~1201834861000", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(end.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		
		Assert.assertTrue(item.isDeleted());
		Assert.assertEquals("jmt", item.getLastUpdate().getBy());
		Assert.assertEquals("1201834861000", String.valueOf(item.getLastUpdate().getWhen().getTime()));
	}	
	
	@Test
	public void shouldProcessDeletedItemFullProtocol(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));

		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.setFullProtocol(true);
		
		GetForMergeMessageProcessor get = new GetForMergeMessageProcessor(new ItemEncoding(100), null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(get, null, null);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-1269158974~D~jmt~1201834861000", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(get.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		
		Assert.assertFalse(item.isDeleted());
	}	
	
	@Test
	public void shouldProcessThrowsGetItemForUpdate(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));

		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		
		GetForMergeMessageProcessor get = new GetForMergeMessageProcessor(new ItemEncoding(100), null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(get, null, null);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-1269158955", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(get.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
	}
	
	@Test
	public void shouldProcessThrowsMergeForLocalChanges(){
		Item item = new Item(new NullContent("1"), new Sync("1", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));
		Item item2 = new Item(new NullContent("2"), new Sync("2", "jmt", TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1), false));

		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.setOpen();
		syncSession.addItem(item2);
		syncSession.setFullProtocol(false);
		
		MergeWithACKMessageProcessor merge = new MergeWithACKMessageProcessor(new ItemEncoding(100), null);
		LastVersionStatusMessageProcessor mp = new LastVersionStatusMessageProcessor(null, merge, null);
		
		Message message = new Message(IProtocolConstants.PROTOCOL, mp.getMessageType(), syncSession.getSessionId(), 0, "1~-1269158974~D~jmt~1201834861000", syncSession.getTarget());
		List<IMessage> messages = mp.process(syncSession, message);
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		
		IMessage response = messages.get(0);		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(syncSession.getTarget(), response.getEndpoint());
		Assert.assertEquals(merge.getMessageType(), response.getMessageType());
		Assert.assertEquals(IProtocolConstants.PROTOCOL, response.getProtocol());
		Assert.assertEquals(syncSession.getSessionId(), response.getSessionId());
		
		Assert.assertTrue(item.isDeleted());
		Assert.assertEquals("jmt", item.getLastUpdate().getBy());
		Assert.assertEquals("1201834861000", String.valueOf(item.getLastUpdate().getWhen().getTime()));
	}
}
