package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	private List<Item> snapshotA = new ArrayList<Item>();
	private List<Item> snapshotB = new ArrayList<Item>();
	
	@Test
	public void shouldSyncNewB(){		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(createNewItem());
		
		sync(itemsA, itemsB);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	@Test
	public void shouldSyncNewA(){		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(createNewItem());
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		
		sync(itemsA, itemsB);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	private void sync(List<Item> itemsA, List<Item> itemsB){
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
				
	
		SmsEndpoint endpointB = new SmsEndpoint("sms:456");
		
		syncEngineEndPointA.synchronize(dataSetId, endpointB);
		
		Assert.assertFalse(syncSessionFactoryA.get(dataSetId, "sms:456").isOpen());
		Assert.assertFalse(syncSessionFactoryB.get(dataSetId, "sms:123").isOpen());
		
		System.out.println("A: " 
				+ smsConnectionEndpointA.getGeneratedMessagesSizeStatistics() 
				+ " messages: " + smsConnectionEndpointA.getGeneratedMessagesStatistics());
		
		System.out.println("B: " 
				+ smsConnectionEndpointB.getGeneratedMessagesSizeStatistics() 
				+ " messages: " + smsConnectionEndpointB.getGeneratedMessagesStatistics());
		
		snapshotA = syncSessionFactoryA.get(dataSetId, "sms:456").getSnapshot();
		snapshotB = syncSessionFactoryB.get(dataSetId, "sms:123").getSnapshot();
	}
	
	
	private Item createNewItem() {
		String syncId = IdGenerator.newID();
		
		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar");
		
		IContent content = new XMLContent(syncId, "", "", payload);
		
		Sync sync = new Sync(syncId, "jmt", new Date(), false);
		sync.update("jjj", new Date());
		
		return new Item(content, sync);
	}
}
