package com.mesh4j.sync.adapters.sms;

import java.io.File;

import com.mesh4j.sync.adapters.dom.DOMAdapter;
import com.mesh4j.sync.adapters.kml.DOMLoaderFactory;
import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.MessageSyncEngine;
import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.connection.inmemory.ISmsConnectionOutboundNotification;
import com.mesh4j.sync.message.channel.sms.connection.inmemory.InMemorySmsConnection;
import com.mesh4j.sync.message.channel.sms.core.repository.file.FileSmsChannelRepository;
import com.mesh4j.sync.message.core.ISyncSessionRepository;
import com.mesh4j.sync.message.core.MessageSyncAdapter;
import com.mesh4j.sync.message.core.NonMessageEncoding;
import com.mesh4j.sync.message.core.repository.SyncSessionFactory;
import com.mesh4j.sync.message.core.repository.file.FileSyncSessionRepository;
import com.mesh4j.sync.message.encoding.CompressBase91MessageEncoding;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import com.mesh4j.sync.properties.PropertiesProvider;
import com.mesh4j.sync.security.IIdentityProvider;

public class SmsHelper {
	
	public static void synchronizeItems(ISmsConnectionOutboundNotification smsConnectionOutboundNotification, String smsFrom, String smsTo, boolean useCompression, String kmlFileName, IIdentityProvider identityProvider, String repositoryBaseDirectory, int senderDelay, int receiverDelay, int readDelay, int channelDelay, int maxMessageLenght) throws InterruptedException{
		
		IMessageEncoding encoding = NonMessageEncoding.INSTANCE;
		if(useCompression){
			encoding = CompressBase91MessageEncoding.INSTANCE;
		}

		File file = new File(kmlFileName);
		String sourceId = file.getName();

		DOMAdapter kmlAdapterA = new DOMAdapter(DOMLoaderFactory.createDOMLoader(kmlFileName, identityProvider));
		IMessageSyncAdapter adapterA = new MessageSyncAdapter(sourceId, identityProvider, kmlAdapterA);

		InMemorySmsConnection smsConnectionA = new InMemorySmsConnection(encoding, maxMessageLenght, readDelay, channelDelay);
		smsConnectionA.setSmsConnectionOutboundNotification(smsConnectionOutboundNotification);
		SmsEndpoint targetA = new SmsEndpoint(smsFrom);
		
		File fileDirFrom = new File(repositoryBaseDirectory+"\\"+smsFrom+"\\");
		if(!fileDirFrom.exists()){
			fileDirFrom.mkdirs();
		}
		
		MessageSyncEngine syncEngineEndPointA = createSyncSmsEndpoint(repositoryBaseDirectory+"\\"+smsFrom+"\\", adapterA, smsConnectionA, senderDelay, receiverDelay);

		File fileDirTo = new File(repositoryBaseDirectory+"\\"+smsTo+"\\");
		if(!fileDirTo.exists()){
			fileDirTo.mkdirs();
		}
		
		String kmlFileNameB = repositoryBaseDirectory+"\\"+smsTo+"\\"+file.getName();
		DOMAdapter kmlAdapterB = new DOMAdapter(DOMLoaderFactory.createDOMLoader(kmlFileNameB, identityProvider));
		IMessageSyncAdapter adapterB = new MessageSyncAdapter(sourceId, identityProvider, kmlAdapterB);

		InMemorySmsConnection smsConnectionB = new InMemorySmsConnection(encoding, maxMessageLenght, readDelay, channelDelay);
		smsConnectionB.setSmsConnectionOutboundNotification(smsConnectionOutboundNotification);
		SmsEndpoint targetB = new SmsEndpoint(smsTo);
		MessageSyncEngine syncEngineEndPointB = createSyncSmsEndpoint(repositoryBaseDirectory+"\\"+smsTo+"\\", adapterB, smsConnectionB, senderDelay, receiverDelay);
		
		smsConnectionA.setEndpointConnection(smsConnectionB);
		smsConnectionA.setEndpoint(targetA);
		smsConnectionB.setEndpointConnection(smsConnectionA);
		smsConnectionB.setEndpoint(targetB);
		
		syncEngineEndPointA.synchronize(sourceId, targetB, true);

		Thread.sleep(1000);
		ISyncSession syncSessionA = syncEngineEndPointA.getSyncSession(sourceId, targetB);
		ISyncSession syncSessionB = syncEngineEndPointB.getSyncSession(sourceId, targetA);
		while(syncSessionA.isOpen() || syncSessionB.isOpen()){			
			Thread.sleep(500);
		}
		
		adapterA.synchronizeSnapshot(syncSessionA);
		adapterB.synchronizeSnapshot(syncSessionB);
	}
	
	private static MessageSyncEngine createSyncSmsEndpoint(String repositoryBaseDirectory, IMessageSyncAdapter adapter, ISmsConnection smsConnection, int senderDelay, int receiverDelay){
		FileSmsChannelRepository channelRepo = null;
		if(repositoryBaseDirectory != null){
			channelRepo = new FileSmsChannelRepository(repositoryBaseDirectory);
		}
		IChannel channel = SmsChannelFactory.createChannel(smsConnection, senderDelay, receiverDelay, channelRepo, channelRepo);
						
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory();
		syncSessionFactory.registerSource(adapter);
	
		ISyncSessionRepository repo = null;
		if(repositoryBaseDirectory != null){
			repo = new FileSyncSessionRepository(repositoryBaseDirectory, syncSessionFactory);
		}		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocol(100, repo);		
		MessageSyncEngine syncEngineEndPoint = new MessageSyncEngine(syncProtocol, channel);
		
		return syncEngineEndPoint;
	}

	public static String[] getAvailablePhones() {
		PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
		return new String [] {prop.getString("default.sms.phone.number.demo"), prop.getString("default.sms.phone.number.demo1"), prop.getString("default.sms.phone.number.demo2")};
	}
}
