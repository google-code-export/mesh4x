package org.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.core.IMessageProcessor;

public interface IMessageSyncProtocol {

	public static final List<IMessage> NO_RESPONSE = new ArrayList<IMessage>();
		
	boolean isValidMessageProtocol(IMessage message);

	IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges);
	
	List<IMessage> processMessage(IMessage message);

	ISyncSession getSyncSession(String sourceId, IEndpoint target);

	void registerSource(IMessageSyncAdapter adapter);
	
	void registerSourceIfAbsent(IMessageSyncAdapter adapter);

	void endSync(ISyncSession syncSession, Date date);

	void notifyBeginSync(ISyncSession syncSession);

	void registerSyncAware(IMessageSyncAware syncAware);
	
	IMessage cancelSync(String sourceId, IEndpoint target);

	void cancelSync(ISyncSession syncSession);

	IMessageSyncAdapter getSource(String sourceId);

	List<ISyncSession> getAllSyncSessions();

	void removeSourceId(String sourceId);

	IMessageProcessor getMessageProcessor(String messageType);

}
