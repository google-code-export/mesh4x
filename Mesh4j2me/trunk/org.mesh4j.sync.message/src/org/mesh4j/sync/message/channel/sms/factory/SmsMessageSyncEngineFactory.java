package org.mesh4j.sync.message.channel.sms.factory;

import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.ISmsRetiesNotification;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.connection.SmsConnection;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.message.core.repository.IMessageSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.rms.storage.RmsStorageSyncSessionRepository;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.security.IIdentityProvider;

public class SmsMessageSyncEngineFactory {

	public static MessageSyncEngine makeSyncEngine(SmsConnection smsConnection, IMessageSyncAdapterFactory messageSyncAdapterFactory, IMessageSyncAware syncAware, int senderRetryTimeOut, int receiverRetryTimeOut, int diffBlockSize, ISmsRetiesNotification retriesNotification, IIdentityProvider identityProvider, IIdGenerator idGenerator) {
		SmsEndpointFactory endpointFactory = new SmsEndpointFactory();
		
		//ISyncSessionRepository repository = new InMemorySyncSessionRepository(endpointFactory, messageSyncAdapterFactory);
		ISyncSessionRepository repository = new RmsStorageSyncSessionRepository(endpointFactory, messageSyncAdapterFactory, identityProvider, idGenerator);
		
		IChannel channel = SmsChannelFactory.createChannel(smsConnection, senderRetryTimeOut, receiverRetryTimeOut, retriesNotification);
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocol(diffBlockSize, repository, syncAware);		
		return new MessageSyncEngine(syncProtocol, channel);
	}
}
