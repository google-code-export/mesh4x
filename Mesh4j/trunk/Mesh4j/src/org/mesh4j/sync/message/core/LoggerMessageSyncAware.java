package org.mesh4j.sync.message.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;

public class LoggerMessageSyncAware implements IMessageSyncAware {

	private final static Log Logger = LogFactory.getLog(LoggerMessageSyncAware.class);
	
	@Override
	public void beginSync(ISyncSession syncSession) {
		Logger.info("Begin sync: " + syncSession.getSessionId());
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		Logger.info("End sync: " + syncSession.getSessionId());
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		Logger.info("Error Begin sync: " + syncSession.getSessionId());		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		Logger.info("Cancel sync: " + syncSession.getSessionId());
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		Logger.info("Cancel sync error: " + syncSession.getSourceId() + " endpoint: " + syncSession.getTarget().getEndpointId());
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		Logger.info("Invalid message protocol: " + message.getSessionId());		
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		Logger.info("Invalid protocol message order: " + message.getSessionId());	
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		Logger.info("Protocol message processed: " + message.getMessageType());
		for (IMessage message2 : response) {
			Logger.info("	response: " + message2.getMessageType());	
		}
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		Logger.info("Problem with session creation: " + message.getSessionId() + " source: " + sourceId);
	}
}
