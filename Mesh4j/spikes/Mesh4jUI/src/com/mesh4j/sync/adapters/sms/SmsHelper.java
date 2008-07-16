package com.mesh4j.sync.adapters.sms;

import java.io.File;
import java.util.ArrayList;

import com.mesh4j.sync.adapters.dom.DOMAdapter;
import com.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.MessageSyncEngine;
import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import com.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import com.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import com.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import com.mesh4j.sync.message.channel.sms.connection.smslib.SmsLibConnection;
import com.mesh4j.sync.message.core.MessageSyncAdapter;
import com.mesh4j.sync.message.core.NonMessageEncoding;
import com.mesh4j.sync.message.encoding.CompressBase91MessageEncoding;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import com.mesh4j.sync.properties.PropertiesProvider;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.ui.translator.Mesh4jSmsUITranslator;

public class SmsHelper {

	public static void emulateSync(ISmsConnectionInboundOutboundNotification smsConnectionNotification, String smsFrom, String smsTo, boolean useCompression, String kmlFileName) throws InterruptedException{
		PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
		String baseDirectory = prop.getBaseDirectory();
		int senderDelay = prop.getInt("default.sms.demo.sender.delay");
		int receiverDelay = prop.getInt("default.sms.demo.receiver.delay");
		int readDelay = prop.getInt("default.sms.demo.read.delay");
		int channelDelay = prop.getInt("default.sms.demo.channel.delay");
		int maxMessageLenght = prop.getInt("default.sms.demo.max.message.lenght");
		IIdentityProvider identityProvider = prop.getIdentityProvider();

		IMessageEncoding encoding = NonMessageEncoding.INSTANCE;
		if(useCompression){
			encoding = CompressBase91MessageEncoding.INSTANCE;
		}
		emulateSync(smsConnectionNotification, smsFrom, smsTo, encoding, kmlFileName, identityProvider, baseDirectory, senderDelay, receiverDelay, readDelay, channelDelay, maxMessageLenght);
	}
	
	private static void emulateSync(ISmsConnectionInboundOutboundNotification smsConnectionNotification, String smsFrom, String smsTo, IMessageEncoding encoding, String kmlFileName, IIdentityProvider identityProvider, String repositoryBaseDirectory, int senderDelay, int receiverDelay, int readDelay, int channelDelay, int maxMessageLenght) throws InterruptedException{

		File file = new File(kmlFileName);
		String sourceId = file.getName();

		// ENDPOINT A
		DOMAdapter kmlAdapterA = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(kmlFileName, identityProvider));
		IMessageSyncAdapter adapterA = new MessageSyncAdapter(sourceId, identityProvider, kmlAdapterA);

		InMemorySmsConnection smsConnectionA = new InMemorySmsConnection(encoding, maxMessageLenght, readDelay, channelDelay);
		smsConnectionA.setSmsConnectionOutboundNotification(smsConnectionNotification);
		SmsEndpoint targetA = new SmsEndpoint(smsFrom);
		
		MessageSyncEngine syncEngineEndPointA = createSyncEngine(repositoryBaseDirectory+"\\"+smsFrom+"\\", identityProvider, smsConnectionA, senderDelay, receiverDelay);

		// ENDPOINT B
		InMemorySmsConnection smsConnectionB = new InMemorySmsConnection(encoding, maxMessageLenght, readDelay, channelDelay);
		SmsEndpoint targetB = new SmsEndpoint(smsTo);
		
		MessageSyncEngine syncEngineEndPointB = createSyncEngine(repositoryBaseDirectory+"\\"+smsTo+"\\", identityProvider, smsConnectionB, senderDelay, receiverDelay);
		
		// CHANNEL EMULATION
		smsConnectionA.setEndpointConnection(smsConnectionB);
		smsConnectionA.setEndpoint(targetA);
		smsConnectionB.setEndpointConnection(smsConnectionA);
		smsConnectionB.setEndpoint(targetB);
		
		// SYNC
		syncEngineEndPointA.synchronize(adapterA, targetB, true);

		// WAIT until end sync
		Thread.sleep(1000);
		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionA.isOpen() || syncSessionB.isOpen()){			
			Thread.sleep(500);
		}
	}
	
	private static MessageSyncEngine createSyncEngine(String repositoryBaseDirectory, IIdentityProvider identityProvider, ISmsConnection smsConnection, int senderDelay, int receiverDelay){
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, repositoryBaseDirectory);
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(100, repositoryBaseDirectory, identityProvider);		
		return new MessageSyncEngine(syncProtocol, channel);		
	}

	public static String[] getAvailableModems(Modem modem) {
		ArrayList<String> result = new ArrayList<String>();		
		if(modem != null){
			result.add(modem.toString());
		}
//		else{
//			List<Modem> availableModems = ModemHelper.getAvailableModems();
//			for (Modem availableModem : availableModems) {
//				result.add(availableModem.toString());
//			}
//		}
		
		if(result.isEmpty()){
			result.add(Mesh4jSmsUITranslator.getLabelDemo());
		}
		return result.toArray(new String[0]);
	}

	public static Modem getDefaultModem() {
		PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
		String portName = prop.getString("default.sms.port");
		int baudRate = prop.getInt("default.sms.baud.rate");		
		
		if(portName.length() > 0 && baudRate > 0){
			return ModemHelper.getModem(portName, baudRate);
		} else {
			return null;
		}
	}

	public static MessageSyncEngine createSyncEngine(ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification, Modem modem) {
		if(modem != null){
			PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
			String baseDirectory = prop.getBaseDirectory();
			int senderDelay = prop.getInt("default.sms.sender.delay");
			int receiverDelay = prop.getInt("default.sms.receiver.delay");
			int readDelay = prop.getInt("default.sms.read.delay");
			int maxMessageLenght = prop.getInt("default.sms.max.message.lenght");
			int channelDelay = prop.getInt("default.sms.channel.delay");
			IIdentityProvider identityProvider = prop.getIdentityProvider();
			
			//IMessageEncoding messageEncoding = CompressBase91MessageEncoding.INSTANCE;
			IMessageEncoding messageEncoding = NonMessageEncoding.INSTANCE;
				
			SmsLibConnection smsConnection = new SmsLibConnection("mesh4j.sync", modem.getComPort(), modem.getBaudRate(),
				modem.getManufacturer(), modem.getModel(), maxMessageLenght, messageEncoding, readDelay, channelDelay, smsConnectionInboundOutboundNotification, null, null);
			
			return createSyncEngine(baseDirectory+"\\"+modem.toString()+"\\", identityProvider, smsConnection, senderDelay, receiverDelay);
		} else {
			return null;
		}
	}

	public static void synchronizeKml(MessageSyncEngine syncEngine, String kmlFileName, String smsNumber) {		
		PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
		IIdentityProvider identityProvider = prop.getIdentityProvider();
		
		File file = new File(kmlFileName);
		String sourceId = file.getName();		
		DOMAdapter kmlAdapter = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(kmlFileName, identityProvider));
		IMessageSyncAdapter adapter = new MessageSyncAdapter(sourceId, identityProvider, kmlAdapter);

		syncEngine.synchronize(adapter, new SmsEndpoint(smsNumber), true);
	}
}