package org.mesh4j.sync.message.channel.sms.connection.smslib;

import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.repository.ISourceIdMapper;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.protocol.IItemEncoding;
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
		IItemEncoding itemEncoding,
		IMessageEncoding messageEncoding,
		ISourceIdMapper sourceIdMapper,
		ISmsConnectionInboundOutboundNotification[] smsAware, 
		IMessageSyncAware[] syncAware,
		ISyncAdapterFactory ... syncAdapterFactories) {
	
		ISmsConnection smsConnection =  new SmsLibAsynchronousConnection("mesh4j.sync", modem.getComPort(), modem.getBaudRate(),
					modem.getManufacturer(), modem.getModel(), maxMessageLenght, messageEncoding, smsAware);
		
		MessageSyncEngine syncEngine = createSyncEngine(sourceIdMapper, syncAware, baseDirectory, identityProvider, itemEncoding, smsConnection, senderDelay, receiverDelay, syncAdapterFactories);
		
		return syncEngine;
	}
	
	private static MessageSyncEngine createSyncEngine(ISourceIdMapper sourceIdMapper, IMessageSyncAware[] syncAware, String repositoryBaseDirectory, IIdentityProvider identityProvider, IItemEncoding itemEncoding, ISmsConnection smsConnection, int senderDelay, int receiverDelay, ISyncAdapterFactory ... syncAdapterFactories){
		MessageSyncAdapterFactory msgSyncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, false, syncAdapterFactories);		
		SmsChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, repositoryBaseDirectory, MessageSyncProtocolFactory.getProtocolMessageFilter());
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(itemEncoding, repositoryBaseDirectory, channel, identityProvider, syncAware, SmsEndpointFactory.INSTANCE, msgSyncAdapterFactory);
		return new MessageSyncEngine(syncProtocol, channel);		
	}
}
