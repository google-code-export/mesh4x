package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.message.channel.sms.SmsChannel;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.core.SyncSessionFactory;
import com.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.IdGenerator;

public class MessageSyncEngineTests {

	@Test
	public void shouldSync(){
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
//		itemsA.add(createNewItem());
//		itemsA.add(createNewItem());
//		itemsA.add(createNewItem());
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(createNewItem());
//		itemsB.add(createNewItem());
//		itemsB.add(createNewItem());		
		
		String dataSetId = "12345";

		MockSmsConnection smsConnectionEndpointA = new MockSmsConnection("sms:123");
		MockSmsConnection smsConnectionEndpointB = new MockSmsConnection("sms:456");

		smsConnectionEndpointA.setEndPoint(smsConnectionEndpointB);
		smsConnectionEndpointA.activateTrace();
		smsConnectionEndpointB.setEndPoint(smsConnectionEndpointA);
		smsConnectionEndpointB.activateTrace();
		
		IChannel channelEndpointA = new SmsChannel(smsConnectionEndpointA, MockMessageEncoding.INSTANCE);
		IChannel channelEndpointB = new SmsChannel(smsConnectionEndpointB, MockMessageEncoding.INSTANCE);
		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocol();		
		
		IMessageSyncAdapter endPointA = new MockInMemoryMessageSyncAdapter(dataSetId, itemsA);
		IMessageSyncAdapter endPointB = new MockInMemoryMessageSyncAdapter(dataSetId, itemsB);
		
		ISyncSessionFactory syncSessionFactoryA = new SyncSessionFactory();
		syncSessionFactoryA.registerSource(endPointA);
		MessageSyncEngine syncEngineEndPointA = new MessageSyncEngine(syncProtocol, channelEndpointA, syncSessionFactoryA);

		ISyncSessionFactory syncSessionFactoryB = new SyncSessionFactory();
		syncSessionFactoryB.registerSource(endPointB);
		MessageSyncEngine syncEngineEndPointB = new MessageSyncEngine(syncProtocol, channelEndpointB, syncSessionFactoryB);
		Assert.assertNotNull(syncEngineEndPointB);
				
		Assert.assertEquals(0, endPointA.getAll().size());
		Assert.assertEquals(1, endPointB.getAll().size());
		
		SmsEndpoint endpointA = new SmsEndpoint("sms:123");
		SmsEndpoint endpointB = new SmsEndpoint("sms:456");
		syncEngineEndPointA.synchronize(dataSetId, endpointB);
		Assert.assertFalse(syncSessionFactoryA.get(dataSetId, "sms:456").isOpen());
		Assert.assertFalse(syncSessionFactoryB.get(dataSetId, "sms:123").isOpen());
		
		Assert.assertEquals(1, syncSessionFactoryA.get(dataSetId, endpointB.getEndpointId()).getSnapshot().size());
		Assert.assertEquals(1, syncSessionFactoryB.get(dataSetId, endpointA.getEndpointId()).getSnapshot().size());
	}
	
	
	private Item createNewItem() {
		String syncId = IdGenerator.newID();
		
		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar");
		
		IContent content = new XMLContent(syncId, "title", "desc", payload);
		
		Sync sync = new Sync(syncId, "jmt", new Date(), false);
		sync.update("jjj", new Date());
		
		return new Item(content, sync);
	}
}
