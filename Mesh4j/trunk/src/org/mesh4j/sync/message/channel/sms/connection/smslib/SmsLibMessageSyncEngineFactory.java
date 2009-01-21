package org.mesh4j.sync.message.channel.sms.connection.smslib;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.protocol.IProtocolConstants;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.security.IIdentityProvider;

public class SmsLibMessageSyncEngineFactory {

	public static MessageSyncEngine createSyncEngine(
		Modem modem,
		String baseDirectory,
		int senderDelay,
		int receiverDelay,
		int maxMessageLenght,
		IIdentityProvider identityProvider,			
		IMessageEncoding messageEncoding,
		ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification, 
		IMessageSyncAware syncAware,
		ISyncAdapterFactory ... syncAdapterFactories) {
	
		IFilter<String> protocolFilter = new IFilter<String>(){
			@Override
			public boolean applies(String message) {  // Accept only protocol messages
				return message != null && message.length() > 0 && message.startsWith(IProtocolConstants.PROTOCOL);
			}
		};
		
		ISmsConnection smsConnection =  new SmsLibAsynchronousConnection("mesh4j.sync", modem.getComPort(), modem.getBaudRate(),
					modem.getManufacturer(), modem.getModel(), maxMessageLenght, messageEncoding, smsConnectionInboundOutboundNotification, protocolFilter);
		
		MessageSyncEngine syncEngine = createSyncEngine(syncAware, baseDirectory, identityProvider, smsConnection, senderDelay, receiverDelay, syncAdapterFactories);
		
		return syncEngine;
	}
	
	private static MessageSyncEngine createSyncEngine(IMessageSyncAware syncAware, String repositoryBaseDirectory, IIdentityProvider identityProvider, ISmsConnection smsConnection, int senderDelay, int receiverDelay, ISyncAdapterFactory ... syncAdapterFactories){
		MessageSyncAdapterFactory msgSyncAdapterFactory = new MessageSyncAdapterFactory(null, false, syncAdapterFactories);		
		SmsChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, repositoryBaseDirectory);
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(100, repositoryBaseDirectory, channel, identityProvider, new IMessageSyncAware[]{syncAware}, SmsEndpointFactory.INSTANCE, msgSyncAdapterFactory);
		return new MessageSyncEngine(syncProtocol, channel);		
	}
}
