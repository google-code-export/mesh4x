package org.mesh4j.sync.message;

import java.util.List;

import org.mesh4j.sync.model.Item;


public interface IMessageSyncAware {

	void beginSync(ISyncSession syncSession);
	
	void endSync(ISyncSession syncSession, List<Item> conflicts);

	void beginSyncWithError(ISyncSession syncSession);

	void notifyInvalidMessageProtocol(IMessage message);

	void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response);

	void notifyInvalidProtocolMessageOrder(IMessage message);

	void notifySessionCreationError(IMessage message, String sourceId);

	void notifyCancelSync(ISyncSession syncSession);

	void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession);
}
