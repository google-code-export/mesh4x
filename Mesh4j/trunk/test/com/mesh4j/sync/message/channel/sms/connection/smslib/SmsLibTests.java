package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Message.MessageTypes;
import org.smslib.modem.SerialModemGateway;

import com.mesh4j.sync.adapters.dom.DOMAdapter;
import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.adapters.kml.DOMLoaderFactory;
import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.MessageSyncEngine;
import com.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.core.SmsChannel;
import com.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import com.mesh4j.sync.message.channel.sms.core.repository.file.FileSmsChannelRepository;
import com.mesh4j.sync.message.core.ISyncSessionRepository;
import com.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import com.mesh4j.sync.message.core.MessageSyncAdapter;
import com.mesh4j.sync.message.core.repository.SyncSessionFactory;
import com.mesh4j.sync.message.core.repository.file.FileSyncSessionRepository;
import com.mesh4j.sync.message.encoding.CompressBase91MessageEncoding;
import com.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class SmsLibTests {

	//@Test	
	public void shouldReadMessages() throws Exception{
		SerialModemGateway gateway = new SerialModemGateway("modem.com18", "COM18", 115200, "Nokia", "6070");		
		ReadMessageCommand command = new ReadMessageCommand();
		command.execute(gateway);
		
		SerialModemGateway gatewayB = new SerialModemGateway("modem.com23", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV");		
		ReadMessageCommand commandB = new ReadMessageCommand();
		commandB.execute(gatewayB);
	}
	
	//@Test	
	public void shouldSendMessage() throws Exception{
		SerialModemGateway gateway = new SerialModemGateway("modem.com18", "COM18", 115200, "Nokia", "6070");		
		//SerialModemGateway gateway = new SerialModemGateway("modem.com23", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV");		

		SendMessageCommand command = new SendMessageCommand();
		command.execute(gateway, "<phone number here>", "hi...");
	}
	
	//@Test
	public void shouldMeshWithSMSLib() throws InterruptedException{
		
		int delay = 500; //1 * 60 * 1000; // min * seg * miliseconds
		String sourceId = IdGenerator.newID().substring(0, 5);
		List<Item> items = createItems(1);						
				
		IMessageSyncAdapter adapterA = new InMemoryMessageSyncAdapter(sourceId, items);
		MessageSyncEngine syncEngineEndPointA = createSyncSmsEndpoint(adapterA, "sonyEricsson", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", delay, delay, delay);
		IEndpoint targetA = new SmsEndpoint("<phone number here>");

		IMessageSyncAdapter adapterB = new InMemoryMessageSyncAdapter(sourceId, new ArrayList<Item>());
		MessageSyncEngine syncEngineEndPointB = createSyncSmsEndpoint(adapterB, "nokia", "COM28", 115200, "Nokia", "6070", delay, delay, delay);
		IEndpoint targetB = new SmsEndpoint("<phone number here>");
		
		//syncEngineEndPointA.synchronize(sourceId, targetB, true);

		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionA.isOpen() && syncSessionB.isOpen()){			
			Thread.sleep(500);
		}
		
		Assert.assertFalse(syncSessionA.isOpen());
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
		
		int delay = 1 * 60 * 1000; // min * seg * miliseconds
		String sourceId = IdGenerator.newID().substring(0, 5);
		List<Item> items = createItems(1);						
		
		String fileNameA = this.getClass().getResource("kmlWithSyncInfo.kml").getFile();
		DOMAdapter kmlAdapterA = new DOMAdapter(DOMLoaderFactory.createDOMLoader(fileNameA, NullIdentityProvider.INSTANCE));
		IMessageSyncAdapter adapterA = new MessageSyncAdapter(sourceId, NullIdentityProvider.INSTANCE, kmlAdapterA);
		MessageSyncEngine syncEngineEndPointA = createSyncSmsEndpoint(adapterA, "modem.com23", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", delay, delay, delay);
		IEndpoint targetA = new SmsEndpoint("<phone number here>");

		String fileNameB = this.getClass().getResource("kmlDummyForSync.kml").getFile();
		DOMAdapter kmlAdapterB = new DOMAdapter(DOMLoaderFactory.createDOMLoader(fileNameB, NullIdentityProvider.INSTANCE));
		IMessageSyncAdapter adapterB = new MessageSyncAdapter(sourceId, NullIdentityProvider.INSTANCE, kmlAdapterB);
		MessageSyncEngine syncEngineEndPointB = createSyncSmsEndpoint(adapterB, "modem.com28", "COM28", 115200, "Nokia", "6070", delay, delay, delay);
		IEndpoint targetB = new SmsEndpoint("<phone number here>");
		
		syncEngineEndPointA.synchronize(sourceId, targetB, true);

		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionA.isOpen() && syncSessionB.isOpen()){			
			Thread.sleep(500);
		}
		
		Assert.assertFalse(syncSessionA.isOpen());
		Assert.assertEquals(items.size(), syncSessionA.getSnapshot().size());
		Assert.assertEquals(items.size(), syncSessionB.getSnapshot().size());
		
		for (Item item : items) {
			Item itemA = syncSessionA.get(item.getSyncId());
			Item itemB = syncSessionA.get(item.getSyncId());
			Assert.assertNotNull(itemA);
			Assert.assertNotNull(itemB);
			Assert.assertTrue(itemA.equals(itemB));
		}
		
		adapterA.synchronizeSnapshot(syncSessionA);
		adapterB.synchronizeSnapshot(syncSessionB);
	}
	
	private MessageSyncEngine createSyncSmsEndpoint(IMessageSyncAdapter adapter, String gatewayId, String comPort, int baudRate, String manufacturer, String model, int readSmsTaskDelay, int sendTaskDelay, int receiveACKTaskDelay){
		SmsLibConnection smsConnection = new SmsLibConnection(gatewayId, comPort, baudRate, manufacturer, model, 140, CompressBase91MessageEncoding.INSTANCE, new OutboundNotification(), new InboundNotification(), readSmsTaskDelay);
		
		FileSmsChannelRepository channelRepo = new FileSmsChannelRepository(TestHelper.baseDirectoryForTest()+gatewayId+"\\");
		SmsChannelWrapper channel = new SmsChannelWrapper((SmsChannel) SmsChannelFactory.createChannel(smsConnection, sendTaskDelay, receiveACKTaskDelay, channelRepo, channelRepo));
						
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
		SmsLibConnection smsConnection = new SmsLibConnection("modem.com23", "COM23", 115200, "Sony Ericsson", "FAD-3022013-BV", 140, CompressBase91MessageEncoding.INSTANCE, new OutboundNotification(), new InboundNotification(), 0);
		smsConnection.registerSmsReceiver(messageReceiver);
		smsConnection.processReceivedMessages();
		
		SmsReceiver messageReceiverB = new SmsReceiver();		
		SmsLibConnection smsConnectionB = new SmsLibConnection("modem.com18", "COM18", 115200, "Nokia", "6070", 140, CompressBase91MessageEncoding.INSTANCE, new OutboundNotification(), new InboundNotification(), 0);
		smsConnectionB.registerSmsReceiver(messageReceiverB);
		smsConnectionB.processReceivedMessages();

	}

//	private void emulateResponses(SmsChannelWrapper channel, SyncSessionFactory syncSessionFactory, String dataSetId, IEndpoint endpoint) {
//		ISyncSession syncSession = syncSessionFactory.get(dataSetId, endpoint.getEndpointId());
//		Message messageNC = new Message(IProtocolConstants.PROTOCOL, "2", syncSession.getSessionId(), "", endpoint);
//		messageNC.setOrigin(channel.getLastSentBathID());
//		
//		SmsMessageBatch batchNC = channel.createBatch(messageNC);
//		System.out.println(batchNC.getMessage(0).getText());
//		
//		Message messageEnd = new Message(
//				IProtocolConstants.PROTOCOL,
//				"9",
//				syncSession.getSessionId(),
//				DateHelper.formatDateTime(TestHelper.now()),
//				syncSession.getTarget());
//		messageEnd.setAckIsRequired(false);
//		
//		SmsMessageBatch batchEnd = channel.createBatch(messageEnd);
//		System.out.println(batchEnd.getMessage(0).getText());
//	}

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
	
	private class InboundNotification implements IInboundMessageNotification{
		@Override
		public void process(String gtwId, MessageTypes msgType, InboundMessage msg) {
			System.out.println("Read:" + gtwId+ " - " + msgType.name() + " - " + msg.getText());			
		}
	}
	
	private class OutboundNotification implements IOutboundMessageNotification{
		@Override
		public void process(String gtwId, OutboundMessage msg) {
			System.out.println("Send:" + gtwId+ " - " + msg.getText());			
		}
	}

}

