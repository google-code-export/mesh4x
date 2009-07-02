package org.mesh4j.sync.message.protocol;

import java.util.Vector;

import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.message.core.MessageSyncProtocol;


public class MessageSyncProtocolFactory {

	public static IMessageSyncProtocol createSyncProtocol(int diffBlockSize, ISyncSessionRepository repository, IMessageSyncAware syncAware) {

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
		
		Vector<IMessageProcessor> msgProcessors = new Vector<IMessageProcessor>();
		msgProcessors.addElement(beginMessage);
		msgProcessors.addElement(endMessage);
		msgProcessors.addElement(ackEndMessage);
		msgProcessors.addElement(ackMergeMessage);
		msgProcessors.addElement(mergeMessage);
		msgProcessors.addElement(getForMergeMessage);
		msgProcessors.addElement(lastVersionMessage);
		msgProcessors.addElement(mergeWithACKMessage);
		msgProcessors.addElement(noChangesMessage);
		msgProcessors.addElement(cancelMessage);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol(IProtocolConstants.PROTOCOL, beginMessage, cancelMessage, repository, msgProcessors);
		
		beginMessage.setMessageSyncProtocol(syncProtocol);
		ackEndMessage.setMessageSyncProtocol(syncProtocol);
		endMessage.setMessageSyncProtocol(syncProtocol);
		cancelMessage.setMessageSyncProtocol(syncProtocol);
		
		if(syncAware != null){
			syncProtocol.registerSyncAware(syncAware);
		}
		return syncProtocol;
	}

}
