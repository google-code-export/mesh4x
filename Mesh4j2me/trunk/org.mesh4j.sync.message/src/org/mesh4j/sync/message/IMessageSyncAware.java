package org.mesh4j.sync.message;

import java.util.Vector;

import org.mesh4j.sync.model.Item;

public interface IMessageSyncAware {

	void beginSync(ISyncSession syncSession);
	
	void endSync(ISyncSession syncSession, Vector<Item> conflicts);

	void beginSyncWithError(ISyncSession syncSession);

	void notifyInvalidMessageProtocol(IMessage message);

	void notifyMessageProcessed(IMessage message, Vector<IMessage> response);

	void notifyInvalidProtocolMessageOrder(IMessage message);

	void notifyProblemWithSessionCreation(IMessage message);

	void notifyCancelSync(ISyncSession syncSession);

	void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint);
	
}
