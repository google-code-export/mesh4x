package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;

import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.message.core.MessageSyncProtocol;
import org.mesh4j.sync.message.core.repository.IEndpointFactory;
import org.mesh4j.sync.message.core.repository.IMessageSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.SyncSessionFactory;
import org.mesh4j.sync.message.core.repository.file.FileSyncSessionRepository;
import org.mesh4j.sync.security.IIdentityProvider;


public class MessageSyncProtocolFactory {

	public static IMessageSyncProtocol createSyncProtocol(int diffBlockSize, ISyncSessionRepository repository, IChannel channel, IMessageSyncAware... syncAwareList) {

		IItemEncoding itemEncoding = new ItemEncoding(diffBlockSize);
		
		ACKEndSyncMessageProcessor ackEndMessage = new ACKEndSyncMessageProcessor();
		EndSyncMessageProcessor endMessage = new EndSyncMessageProcessor(ackEndMessage);
		
		ACKMergeMessageProcessor ackMergeMessage = new ACKMergeMessageProcessor(itemEncoding, endMessage);
		MergeMessageProcessor mergeMessage = new MergeMessageProcessor(itemEncoding, endMessage);
		MergeWithACKMessageProcessor mergeWithACKMessage = new MergeWithACKMessageProcessor(itemEncoding, ackMergeMessage);
		GetForMergeMessageProcessor getForMergeMessage = new GetForMergeMessageProcessor(itemEncoding, mergeMessage);
		LastVersionStatusMessageProcessor lastVersionMessage = new LastVersionStatusMessageProcessor(getForMergeMessage, mergeWithACKMessage, endMessage);
		NoChangesMessageProcessor noChangesMessage = new NoChangesMessageProcessor(endMessage, mergeWithACKMessage);

		EqualStatusMessageProcessor equalStatusMessage = new EqualStatusMessageProcessor(endMessage);
		BeginSyncMessageProcessor beginMessage = new BeginSyncMessageProcessor(noChangesMessage, lastVersionMessage, equalStatusMessage);
		CancelSyncMessageProcessor cancelMessage = new CancelSyncMessageProcessor();
		
		ArrayList<IMessageProcessor> msgProcessors = new ArrayList<IMessageProcessor>();
		msgProcessors.add(beginMessage);
		msgProcessors.add(endMessage);
		msgProcessors.add(ackEndMessage);
		msgProcessors.add(ackMergeMessage);
		msgProcessors.add(mergeMessage);
		msgProcessors.add(getForMergeMessage);
		msgProcessors.add(lastVersionMessage);
		msgProcessors.add(mergeWithACKMessage);
		msgProcessors.add(noChangesMessage);
		msgProcessors.add(cancelMessage);
		msgProcessors.add(equalStatusMessage);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol(IProtocolConstants.PROTOCOL, beginMessage, cancelMessage, repository, channel, msgProcessors);
		
		beginMessage.setMessageSyncProtocol(syncProtocol);
		ackEndMessage.setMessageSyncProtocol(syncProtocol);
		endMessage.setMessageSyncProtocol(syncProtocol);
		cancelMessage.setMessageSyncProtocol(syncProtocol);
		equalStatusMessage.setMessageSyncProtocol(syncProtocol);
		noChangesMessage.setMessageSyncProtocol(syncProtocol);
		lastVersionMessage.setMessageSyncProtocol(syncProtocol);
		
		for (IMessageSyncAware syncAware : syncAwareList) {
			if(syncAware != null){
				syncProtocol.registerSyncAware(syncAware);
			}
		}
		return syncProtocol;
	}

	public static IMessageSyncProtocol createSyncProtocolWithFileRepository(int diffBlockSize, String repositoryBaseDirectory, IChannel channel, IIdentityProvider identityProvider, IMessageSyncAware[] syncAware, IEndpointFactory endpointFactory, IMessageSyncAdapterFactory syncAdapterFactory) {
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(endpointFactory, syncAdapterFactory, repositoryBaseDirectory, identityProvider);
		ISyncSessionRepository repo = new FileSyncSessionRepository(repositoryBaseDirectory, syncSessionFactory);
		return createSyncProtocol(diffBlockSize, repo, channel, syncAware);
	}

}
