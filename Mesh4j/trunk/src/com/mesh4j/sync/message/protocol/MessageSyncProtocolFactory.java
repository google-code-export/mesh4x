package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;

import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.ISyncSessionRepository;
import com.mesh4j.sync.message.core.MessageSyncProtocol;
import com.mesh4j.sync.message.core.repository.SyncSessionFactory;
import com.mesh4j.sync.message.core.repository.file.FileSyncSessionRepository;
import com.mesh4j.sync.security.IIdentityProvider;

public class MessageSyncProtocolFactory {

	public static IMessageSyncProtocol createSyncProtocol(int diffBlockSize, ISyncSessionRepository repository) {

		IItemEncoding itemEncoding = new ItemEncoding(diffBlockSize);
		
		ACKEndSyncMessageProcessor ackEndMessage = new ACKEndSyncMessageProcessor();
		EndSyncMessageProcessor endMessage = new EndSyncMessageProcessor(ackEndMessage);
		
		ACKMergeMessageProcessor ackMergeMessage = new ACKMergeMessageProcessor(itemEncoding, endMessage);
		MergeMessageProcessor mergeMessage = new MergeMessageProcessor(itemEncoding, endMessage);
		MergeWithACKMessageProcessor mergeWithACKMessage = new MergeWithACKMessageProcessor(itemEncoding, ackMergeMessage);
		GetForMergeMessageProcessor getForMergeMessage = new GetForMergeMessageProcessor(itemEncoding, mergeMessage);
		LastVersionStatusMessageProcessor lastVersionMessage = new LastVersionStatusMessageProcessor(getForMergeMessage, mergeWithACKMessage, endMessage);
		NoChangesMessageProcessor noChangesMessage = new NoChangesMessageProcessor(endMessage, mergeWithACKMessage);
		BeginSyncMessageProcessor beginMessage = new BeginSyncMessageProcessor(noChangesMessage, lastVersionMessage);
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
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol(IProtocolConstants.PROTOCOL, beginMessage, cancelMessage, repository, msgProcessors);
		ackEndMessage.setMessageSyncProtocol(syncProtocol);
		endMessage.setMessageSyncProtocol(syncProtocol);
		return syncProtocol;
	}

	public static IMessageSyncProtocol createSyncProtocolWithFileRepository(int diffBlockSize, String repositoryBaseDirectory, IIdentityProvider identityProvider) {
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(repositoryBaseDirectory, identityProvider);
		ISyncSessionRepository repo = new FileSyncSessionRepository(repositoryBaseDirectory, syncSessionFactory);
		return createSyncProtocol(diffBlockSize, repo);
	}

}
