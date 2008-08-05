package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.MockMessageEncoding;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import org.mesh4j.sync.message.channel.sms.core.repository.file.FileSmsChannelRepository;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.message.core.repository.SyncSessionFactory;
import org.mesh4j.sync.message.core.repository.file.FileSyncSessionRepository;
import org.mesh4j.sync.message.encoding.CompressBase91MessageEncoding;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.IdGenerator;
import org.smslib.modem.SerialModemGateway;


public class SmsLibTests {

	//@Test	
	public void shouldReadMessages() throws Exception{
		SerialModemGateway gatewayJ = new SerialModemGateway("modem.com18", "COM18", 115200, "Nokia", "6070");		
		SendMessageCommand commandJ = new SendMessageCommand();
		commandJ.execute(gatewayJ, "01136544867", "hi...");
		
		SerialModemGateway gateway = new SerialModemGateway("modem.com18", "COM18", 115200, "Nokia", "6070");		
		ReadMessageCommand command = new ReadMessageCommand();
		command.execute(gateway);
	}
	
	//@Test	
	public void shouldSendMessage() throws Exception{
		SerialModemGateway gateway = new SerialModemGateway("modem.com18", "COM18", 115200, "Nokia", "6070");		
		//SerialModemGateway gateway = new SerialModemGateway("modem.com23", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV");		

		SendMessageCommand command = new SendMessageCommand();
		command.execute(gateway, "<phone number here>", "hi...");
	}
	
	//@Test
	public void shouldMeshWithSMSLibPhoneA() throws InterruptedException{
		
		String sourceId = "12345";
		List<Item> items = createItems(1);						
				
		IMessageSyncAdapter adapterA = new InMemoryMessageSyncAdapter(sourceId, items);
		SmsLibConnection smsConnectionA = new SmsLibConnection("nokia", "COM18", 115200, "Nokia", "6070", 140, CompressBase91MessageEncoding.INSTANCE, 1000, 60000, null);
		MessageSyncEngine syncEngineEndPointA = createSyncSmsEndpoint("nokia", adapterA, smsConnectionA, 0);

		SmsEndpoint targetB = new SmsEndpoint("01136544867");
		syncEngineEndPointA.synchronize(adapterA, targetB, true);

		Thread.sleep(5000);
		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		while(syncSessionA.isOpen()){			
			Thread.sleep(5000);
		}
		
		Assert.assertFalse(syncSessionA.isOpen());
		Assert.assertEquals(items.size(), syncSessionA.getSnapshot().size());
		
	}
	
	//@Test
	public void shouldMeshWithSMSLibPhoneB() throws InterruptedException{
		
		String sourceId = "12345";
				
		IMessageSyncAdapter adapterB = new InMemoryMessageSyncAdapter(sourceId, new ArrayList<Item>());
		SmsLibConnection smsConnectionB = new SmsLibConnection("sonyEricsson", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", 140, CompressBase91MessageEncoding.INSTANCE, 1000, 60000, null);
		SmsEndpoint targetA = new SmsEndpoint("01136540460");
		MessageSyncEngine syncEngineEndPointB = createSyncSmsEndpoint("sonyEricsson", adapterB, smsConnectionB, 0);
		
		Thread.sleep(120000);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionB.isOpen()){			
			Thread.sleep(60000);
		}
	}
	
	//@Test
	public void shouldMeshWithSMSLib() throws InterruptedException{
		
		String sourceId = IdGenerator.newID().substring(0, 5);
		List<Item> items = createItems(1);						
				
		IMessageSyncAdapter adapterA = new InMemoryMessageSyncAdapter(sourceId, items);
		SmsLibConnection smsConnectionA = new SmsLibConnection("sonyEricsson", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", 140, CompressBase91MessageEncoding.INSTANCE, 1000, 0, null);
		SmsEndpoint targetA = new SmsEndpoint("01136544867");
		//MockSmsRefreshConnection smsConnectionA = new MockSmsRefreshConnection(MockMessageEncoding.INSTANCE, 160, 100); 
		//SmsEndpoint targetA = new SmsEndpoint("A");
		MessageSyncEngine syncEngineEndPointA = createSyncSmsEndpoint("sonyEricsson", adapterA, smsConnectionA, 0);

		IMessageSyncAdapter adapterB = new InMemoryMessageSyncAdapter(sourceId, new ArrayList<Item>());
		SmsLibConnection smsConnectionB = new SmsLibConnection("nokia", "COM28", 115200, "Nokia", "6070", 140, CompressBase91MessageEncoding.INSTANCE, 1000, 5000, null);
		SmsEndpoint targetB = new SmsEndpoint("01136540460");
		//MockSmsRefreshConnection smsConnectionB = new MockSmsRefreshConnection(MockMessageEncoding.INSTANCE, 160, 100);
		//SmsEndpoint targetB = new SmsEndpoint("B");
		MessageSyncEngine syncEngineEndPointB = createSyncSmsEndpoint("nokia", adapterB, smsConnectionB, 0);
		
//		smsConnectionA.setEndpointConnection(smsConnectionB);
//		smsConnectionA.setEndpoint(targetA);
//		smsConnectionB.setEndpointConnection(smsConnectionA);
//		smsConnectionB.setEndpoint(targetB);
//		
		syncEngineEndPointA.synchronize(adapterA, targetB, true);

		Thread.sleep(1000);
		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionA.isOpen() || syncSessionB.isOpen()){			
			Thread.sleep(500);
		}
		
		Assert.assertFalse(syncSessionA.isOpen());
		Assert.assertFalse(syncSessionB.isOpen());
		Assert.assertEquals(items.size(), syncSessionA.getSnapshot().size());
		Assert.assertEquals(items.size(), syncSessionB.getSnapshot().size());
		
		for (Item item : items) {
			Item itemA = syncSessionA.get(item.getSyncId());
			Item itemB = syncSessionA.get(item.getSyncId());
			Assert.assertNotNull(itemA);
			Assert.assertNotNull(itemB);
			Assert.assertTrue(itemA.equals(itemB));
		}
	}
	
	//@Test
	public void shouldMeshKMLWithSMSLib() throws InterruptedException{
		
		String sourceId = IdGenerator.newID().substring(0, 5);
		
		String fileNameA = this.getClass().getResource("kmlWithSyncInfo.kml").getFile();
		DOMAdapter kmlAdapterA = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(fileNameA, NullIdentityProvider.INSTANCE));
		IMessageSyncAdapter adapterA = new MessageSyncAdapter(sourceId, NullIdentityProvider.INSTANCE, kmlAdapterA);
		//SmsLibConnection smsConnectionA = new SmsLibConnection("sonyEricsson", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", 140, CompressBase91MessageEncoding.INSTANCE, new OutboundNotification(), new InboundNotification(), (3 * 60 * 1000));
		//SmsEndpoint targetA = new SmsEndpoint("01136544867");
		SmsEndpoint targetA = new SmsEndpoint("A");
		InMemorySmsConnection smsConnectionA = new InMemorySmsConnection(MockMessageEncoding.INSTANCE, 160, 100, targetA); 
		MessageSyncEngine syncEngineEndPointA = createSyncSmsEndpoint("sonyEricsson", adapterA, smsConnectionA, 0);

		String fileNameB = this.getClass().getResource("kmlDummyForSync.kml").getFile();
		DOMAdapter kmlAdapterB = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(fileNameB, NullIdentityProvider.INSTANCE));
		IMessageSyncAdapter adapterB = new MessageSyncAdapter(sourceId, NullIdentityProvider.INSTANCE, kmlAdapterB);
		//SmsLibConnection smsConnectionB = new SmsLibConnection("nokia", "COM28", 115200, "Nokia", "6070", 140, CompressBase91MessageEncoding.INSTANCE, new OutboundNotification(), new InboundNotification(), (3 * 60 * 1000));
		//SmsEndpoint targetB = new SmsEndpoint("01136540460");

		SmsEndpoint targetB = new SmsEndpoint("B");
		InMemorySmsConnection smsConnectionB = new InMemorySmsConnection(MockMessageEncoding.INSTANCE, 160, 100, targetB);
		MessageSyncEngine syncEngineEndPointB = createSyncSmsEndpoint("nokia", adapterB, smsConnectionB, 0);

		smsConnectionA.addEndpointConnection(smsConnectionB);
		smsConnectionB.addEndpointConnection(smsConnectionA);
		
		syncEngineEndPointA.synchronize(adapterA, targetB, true);

		Thread.sleep(1000);
		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionA.isOpen() || syncSessionB.isOpen()){			
			Thread.sleep(500);
		}
		
		Assert.assertFalse(syncSessionA.isOpen());
		Assert.assertFalse(syncSessionB.isOpen());
		Assert.assertEquals(syncSessionB.getSnapshot().size(), syncSessionA.getSnapshot().size());
		
//		adapterA.synchronizeSnapshot(syncSessionA);
//		adapterB.synchronizeSnapshot(syncSessionB);

		kmlAdapterB.beginSync();
		Assert.assertEquals(kmlAdapterA.getAll().size(), kmlAdapterB.getAll().size());
	}
	
	private MessageSyncEngine createSyncSmsEndpoint(String gatewayId, IMessageSyncAdapter adapter, ISmsConnection smsConnection, int delay){
		FileSmsChannelRepository channelRepo = new FileSmsChannelRepository(TestHelper.baseDirectoryForTest()+gatewayId+"\\");
		SmsChannelWrapper channel = new SmsChannelWrapper((SmsChannel) SmsChannelFactory.createChannel(smsConnection, delay, delay, channelRepo, channelRepo));
						
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory();
		syncSessionFactory.registerSource(adapter);
	
		ISyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest()+gatewayId+"\\", syncSessionFactory);
		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocol(100, repo);		
		MessageSyncEngine syncEngineEndPoint = new MessageSyncEngine(syncProtocol, channel);
		
		return syncEngineEndPoint;
	}

	//@Test
	public void shouldReadMeshMessages() throws InterruptedException{
		SmsReceiver messageReceiver = new SmsReceiver();		
		SmsLibConnection smsConnection = new SmsLibConnection("modem.com23", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", 140, CompressBase91MessageEncoding.INSTANCE,1000,  0, null);
		smsConnection.setMessageReceiver(messageReceiver);
		smsConnection.processReceivedMessages();
		
		SmsReceiver messageReceiverB = new SmsReceiver();		
		SmsLibConnection smsConnectionB = new SmsLibConnection("modem.com18", "COM18", 115200, "Nokia", "6070", 140, CompressBase91MessageEncoding.INSTANCE,1000,  0, null);
		smsConnectionB.setMessageReceiver(messageReceiverB);
		smsConnectionB.processReceivedMessages();

	}

	private List<Item> createItems(int max) {
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < max; i++) {
			items.add(createItem());
		}
		return items;
	}

	private Item createItem() {
		String syncID = IdGenerator.newID();
		Element payload = DocumentHelper.createElement("payload");
		payload.addElement("foo").addElement("bar").setText("test sms lib:" + syncID);
		IContent content = new XMLContent(syncID, "title: "+ syncID, "desc: "+ syncID, payload);
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		return item;
	}
	
}