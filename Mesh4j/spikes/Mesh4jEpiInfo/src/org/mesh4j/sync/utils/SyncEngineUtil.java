package org.mesh4j.sync.utils;

import java.io.File;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.connection.SmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.IProgressMonitor;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import org.mesh4j.sync.message.channel.sms.connection.smslib.SmsLibMessageSyncEngineFactory;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.LoggerMessageSyncAware;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class SyncEngineUtil {

	public static List<Item> synchronize(String url, String mdbFileName, String mdbTableName, IIdentityProvider identityProvider, String baseDirectory) {
		
		try{
			ISyncAdapter httpAdapter = HttpSyncAdapterFactory.INSTANCE.createSyncAdapter(url, identityProvider);
			ISyncAdapter msAccessAdapter = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(mdbFileName, mdbTableName, baseDirectory);
	
			SyncEngine syncEngine = new SyncEngine(msAccessAdapter, httpAdapter);
			List<Item> conflicts = syncEngine.synchronize();
			return conflicts;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public static void synchronize(MessageSyncEngine syncEngine, String phoneNumber, String mdbFileName, String mdbTableName, IIdentityProvider identityProvider, String baseDirectory) throws Exception {
		
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(mdbFileName, mdbTableName);
		IMessageSyncAdapter adapter = syncEngine.getSource(sourceID);
		if(adapter == null){
			String mappingsDirectory = baseDirectory + "/" +phoneNumber +"/";
			ISyncAdapter syncAdapter = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(mdbFileName, mdbTableName, mappingsDirectory);
			adapter = new MessageSyncAdapter(sourceID, identityProvider, syncAdapter);
		}
		syncEngine.synchronize(adapter, new SmsEndpoint(phoneNumber), true);
	}

	public static void cancelSynchronize(MessageSyncEngine syncEngine,String phoneNumber, String mdbFileName, String mdbTableName) {
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(mdbFileName, mdbTableName);
		SmsEndpoint target = new SmsEndpoint(phoneNumber);
		syncEngine.cancelSync(sourceID, target);
	}

	public static MessageSyncEngine createEmulator(FileNameResolver fileNameResolver, ISmsConnectionInboundOutboundNotification smsConnectionNotification, 
			IMessageSyncAware syncAware, String smsFrom, IMessageEncoding encoding, 
			IIdentityProvider identityProvider, String baseDirectory, 
			int senderDelay, int receiverDelay, int readDelay, int channelDelay, int maxMessageLenght) throws Exception {

		String targetDirectory = baseDirectory + "/" +smsFrom +"/";
		
		SmsEndpoint target = new SmsEndpoint(smsFrom);
		
		MessageSyncEngine syncEngine = createSyncEngineEmulator(
				fileNameResolver, smsFrom, encoding, identityProvider, targetDirectory,
				senderDelay, receiverDelay, readDelay, channelDelay,
				maxMessageLenght, target, smsConnectionNotification, syncAware);
	
		return syncEngine;
	}

	public static void registerNewEndpointToEmulator(MessageSyncEngine syncEngine, String smsTo, IMessageEncoding encoding, 
			IIdentityProvider identityProvider, String baseDirectory, 
			int senderDelay, int receiverDelay, int readDelay, int channelDelay, int maxMessageLenght) {

		String targetDirectory = baseDirectory + "/" +smsTo +"/";
		
		SmsChannel foregroundChannel = (SmsChannel)syncEngine.getChannel();
		InMemorySmsConnection foregroundSmsConnection = (InMemorySmsConnection) foregroundChannel.getSmsConnection(); 
		
		SmsEndpoint backgroundTarget = new SmsEndpoint(smsTo);
		if(!foregroundSmsConnection.hasEndpointConnection(backgroundTarget)){
		
			FileNameResolver fileNameResolver = new FileNameResolver(targetDirectory+"myFiles.properties");
			MessageSyncEngine backgroundSyncEngine = createSyncEngineEmulator(fileNameResolver,
					smsTo, encoding, identityProvider, targetDirectory,
					senderDelay, receiverDelay, readDelay, channelDelay,
					maxMessageLenght, backgroundTarget, new SmsConnectionInboundOutboundNotification(), new LoggerMessageSyncAware());

			SmsChannel backgroundChannel = (SmsChannel)backgroundSyncEngine.getChannel();
			InMemorySmsConnection backgroundSmsConnection = (InMemorySmsConnection) backgroundChannel.getSmsConnection(); 
			
			foregroundSmsConnection.addEndpointConnection(backgroundSmsConnection);
			backgroundSmsConnection.addEndpointConnection(foregroundSmsConnection);
		}
	}

	private static MessageSyncEngine createSyncEngineEmulator(FileNameResolver fileNameResolver, String smsTarget,
			IMessageEncoding encoding, IIdentityProvider identityProvider,
			String baseDirectory, int senderDelay, int receiverDelay,
			int readDelay, int channelDelay, int maxMessageLenght, SmsEndpoint target,
			ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification, IMessageSyncAware syncAware) {
		
		InMemorySmsConnection smsConnection = new InMemorySmsConnection(encoding, maxMessageLenght, readDelay, target, channelDelay);
		smsConnection.setSmsConnectionOutboundNotification(smsConnectionInboundOutboundNotification);
		
		MsAccessSyncAdapterFactory syncAdapterFactory = new MsAccessSyncAdapterFactory(baseDirectory, fileNameResolver);
		
		MessageSyncAdapterFactory messageSyncAdapterFactory = new MessageSyncAdapterFactory(syncAdapterFactory, false);
		
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, baseDirectory);
		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(100, baseDirectory, identityProvider, syncAware, SmsEndpointFactory.INSTANCE, messageSyncAdapterFactory);		
		
		MessageSyncEngine syncEngineEndPoint = new MessageSyncEngine(syncProtocol, channel); 

		return syncEngineEndPoint;
	}
	

	public static void addDataSource(FileNameResolver fileNameResolver, String fileName) {
		File file = new File(fileName);
		if(file.exists()){
			fileNameResolver.putSource(file.getName(), fileName);
			fileNameResolver.store();
		}
	}

	public static MessageSyncEngine createSyncEngine(
			FileNameResolver fileNameResolver, Modem modem,
			String baseDirectory, int senderDelay, int receiverDelay,
			int readDelay, int maxMessageLenght, int channelDelay,
			IIdentityProvider identityProvider,
			IMessageEncoding messageEncoding,
			ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification,
			IMessageSyncAware messageSyncAware) {
		
		String targetDirectory = baseDirectory + "/" + modem.toString() + "/";
		MsAccessSyncAdapterFactory syncAdapterFactory = new MsAccessSyncAdapterFactory(targetDirectory, fileNameResolver);
		
		return SmsLibMessageSyncEngineFactory.createSyncEngine(
			modem, targetDirectory, senderDelay, receiverDelay, readDelay, maxMessageLenght, channelDelay,
			identityProvider, messageEncoding, smsConnectionInboundOutboundNotification, messageSyncAware, syncAdapterFactory);
	}
	
	public static Modem[] getAvailableModems(IProgressMonitor progressMonitor, Modem demoModem) {
		List<Modem> availableModems = ModemHelper.getAvailableModems(progressMonitor);
		//List<Modem> availableModems = new ArrayList<Modem>();
		if(availableModems.isEmpty()){
			availableModems.add(demoModem);
//			availableModems.add(new Modem("10", 100, "nokia", "n95", "123", null, 0, 0));
//			availableModems.add(new Modem("20", 100, "motorola", "z3", "123", null, 0, 0));
		}
		return availableModems.toArray(new Modem[0]);
	}

	public static void saveDefaults(Modem modem, String defaultPhoneNumber, String defaultDataSource, String defaultTableName, String defaultURL) {
		PropertiesProvider propertiesProvider = new PropertiesProvider();
		propertiesProvider.setDefaults(modem, defaultPhoneNumber, defaultDataSource, defaultTableName, defaultURL);
		propertiesProvider.store();
	}

}
