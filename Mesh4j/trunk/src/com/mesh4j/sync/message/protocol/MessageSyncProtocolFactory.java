package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;

import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessageSyncProtocol;

public class MessageSyncProtocolFactory {

	public static IMessageSyncProtocol createSyncProtocol(IDataSetManager dataSetManager, IItemEncoding itemEncoding) {

		MessageSyncProtocol syncProtocol = new MessageSyncProtocol();
		
		UpdateMessageProcessor update = new UpdateMessageProcessor(syncProtocol, dataSetManager, itemEncoding);		
		GetForUpdateMessageProcessor getForUpdate = new GetForUpdateMessageProcessor(update);		
		LastVersionStatusMessageProcessor lastVersionStatus = new LastVersionStatusMessageProcessor(dataSetManager, getForUpdate, update);
		OkLastVersionMessageProcessor okLastVersion = new OkLastVersionMessageProcessor(syncProtocol);
		CheckForUpdateMessageProcessor checkForUpdate = new CheckForUpdateMessageProcessor(dataSetManager, okLastVersion, lastVersionStatus);

		ArrayList<IMessageProcessor> msgProcessors = new ArrayList<IMessageProcessor>();
		msgProcessors.add(checkForUpdate);
		msgProcessors.add(okLastVersion);
		msgProcessors.add(lastVersionStatus);
		msgProcessors.add(getForUpdate);
		msgProcessors.add(update);
		
		syncProtocol.setInitialMessage(checkForUpdate);
		syncProtocol.setMessageProcessors(msgProcessors);
		return syncProtocol;
	}

}
