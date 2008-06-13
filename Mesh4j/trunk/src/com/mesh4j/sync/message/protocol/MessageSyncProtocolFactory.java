package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;

import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessageSyncProtocol;

public class MessageSyncProtocolFactory {

	public static IMessageSyncProtocol createSyncProtocol(IDataSetManager dataSetManager) {

		MessageSyncProtocol syncProtocol = new MessageSyncProtocol();
		
		LastVersionStatusMessageProcessor lastVersionStatus = new LastVersionStatusMessageProcessor(syncProtocol, dataSetManager);
		OkLastVersionMessageProcessor okLastVersion = new OkLastVersionMessageProcessor(syncProtocol);
		CheckForUpdateMessageProcessor checkForUpdate = new CheckForUpdateMessageProcessor(syncProtocol, dataSetManager, okLastVersion, lastVersionStatus);

		ArrayList<IMessageProcessor> msgProcessors = new ArrayList<IMessageProcessor>();
		msgProcessors.add(checkForUpdate);
		msgProcessors.add(okLastVersion);
		msgProcessors.add(lastVersionStatus);
		
		syncProtocol.setInitialMessage(checkForUpdate);
		syncProtocol.setMessageProcessors(msgProcessors);
		return syncProtocol;
	}

}
