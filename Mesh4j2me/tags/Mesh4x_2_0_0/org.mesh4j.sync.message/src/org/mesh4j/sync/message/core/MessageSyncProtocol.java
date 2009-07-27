package org.mesh4j.sync.message.core;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class MessageSyncProtocol implements IMessageSyncProtocol {

	// MODEL
	private Vector<IMessageProcessor> messageProcessors = new Vector<IMessageProcessor>();
	private IBeginSyncMessageProcessor initialMessage;
	private ICancelSyncMessageProcessor cancelMessage;
	private ISyncSessionRepository repository;
	private String protocolPrefix = "";
	private Vector<IMessageSyncAware> syncAwareList = new Vector<IMessageSyncAware>();
	
	// METHODS
	public MessageSyncProtocol(String protocolPrefix, IBeginSyncMessageProcessor initialMessage, ICancelSyncMessageProcessor cancelMessage, ISyncSessionRepository repository, Vector<IMessageProcessor> messageProcessors){
		Guard.argumentNotNullOrEmptyString(protocolPrefix, "protocolPrefix");
		Guard.argumentNotNull(initialMessage, "initialMessage");
		Guard.argumentNotNull(cancelMessage, "cancelMessage");
		Guard.argumentNotNull(repository, "repository");
		Guard.argumentNotNull(messageProcessors, "messageProcessors");

		this.protocolPrefix = protocolPrefix;
		this.initialMessage = initialMessage;
		this.cancelMessage = cancelMessage;
		this.repository = repository;
		this.messageProcessors = messageProcessors;
	}


	public boolean isValidMessageProtocol(IMessage message) {
		return message != null 
			&& message.getProtocol().equals(this.protocolPrefix);
	}
	

	public Vector<IMessage> processMessage(IMessage message) {		
		if(this.isValidMessageProtocol(message)){
			ISyncSession syncSession = this.repository.getSession(message.getSessionId());
			if(syncSession == null){
				if(this.initialMessage.getMessageType().equals(message.getMessageType())){
					String sourceId = this.initialMessage.getSourceId(message.getData());
					syncSession = this.repository.createSession(message.getSessionId(), message.getSessionVersion(), sourceId, message.getEndpoint(), false);
					if(syncSession == null){
						this.notifyProblemWithSessionCreation(message);
						return NO_RESPONSE;
					}
				} else {
					this.notifyInvalidProtocolMessageOrder(message);
					return NO_RESPONSE;
				}
			} else {
				if(syncSession.isCancelled()){
					this.notifyInvalidProtocolMessageOrder(message);
					return NO_RESPONSE;
				}
			}
			
			Vector<IMessage> response = new Vector<IMessage>();
			for (IMessageProcessor processor : this.messageProcessors) {
				Vector<IMessage> msgResponse = processor.process(syncSession, message);
				for (IMessage messageResponse : msgResponse) {
					response.addElement(messageResponse);		
				}
			}
			this.persistChanges(syncSession);
			this.notifyMessageProcessed(message, response);
			return response;
		} else {
			this.notifyInvalidMessageProtocol(message);
			return NO_RESPONSE;
		}
	}
	
	
	public IMessage cancelSync(String sourceId, IEndpoint endpoint) {
		ISyncSession syncSession = this.repository.getSession(sourceId, endpoint.getEndpointId());
		if(syncSession == null || !syncSession.isOpen()){
			this.notifyCancelSyncErrorSyncSessionNotOpen(sourceId, endpoint);
			Guard.throwsException("ERROR_MESSAGE_SYNC_SESSION_IS_NOT_OPEN", new String[] {sourceId, endpoint.getEndpointId()});
		}
		
		IMessage message = this.cancelMessage.createMessage(syncSession);
		
		this.cancelSync(syncSession);
		return message;
	}

	public void cancelSync(ISyncSession syncSession) {
		syncSession.cancelSync();
		this.persistChanges(syncSession);
		this.notifyCancelSync(syncSession);
	}

	public IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol) {
		ISyncSession syncSession = this.repository.getSession(sourceId, endpoint.getEndpointId());
		if(syncSession != null && syncSession.isOpen()){
			this.notifyBeginSyncError(syncSession);
			return null;
		}
		if(syncSession == null){
			syncSession = this.repository.createSession(IdGenerator.INSTANCE.newID(), 0, sourceId, endpoint, fullProtocol);
			if(syncSession == null){
				return null;
			}
		}
		
		syncSession.beginSync();
		
		IMessage message = this.initialMessage.createMessage(syncSession);
		this.persistChanges(syncSession);
		this.notifyBeginSync(syncSession);
		return message;
	}

	// Call for BeginSyncMessageProcessor
	public void beginSync(ISyncSession syncSession, Date sinceDate, int sessionVersion) {
		syncSession.beginSync(sinceDate, sessionVersion);
		this.persistChanges(syncSession);
		this.notifyBeginSync(syncSession);
	}

	public void endSync(ISyncSession syncSession, Date date) {
		syncSession.endSync(date);
		this.persistChanges(syncSession);
		
		IMessageSyncAdapter adapter = this.repository.getSource(syncSession.getSourceId());
		Vector<Item> conflicts = adapter.synchronizeSnapshot(syncSession);
		
		this.notifyEndSync(syncSession, conflicts);
	}


	public ISyncSession getSyncSession(String sourceId, IEndpoint endpoint) {
		return this.repository.getSession(sourceId, endpoint.getEndpointId());
	}


	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		this.repository.registerSourceIfAbsent(adapter);
	}
	
	public void registerSyncAware(IMessageSyncAware syncAware){
		if(syncAware != null){
			this.syncAwareList.addElement(syncAware);
		}
	}
	
	private void notifyEndSync(ISyncSession syncSession, Vector<Item> conflicts) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.endSync(syncSession, conflicts);
		}
	}
	
	public void notifyBeginSync(ISyncSession syncSession) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.beginSync(syncSession);
		}
	}
	
	public void notifyBeginSyncError(ISyncSession syncSession) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.beginSyncWithError(syncSession);
		}
	}

	private void notifyInvalidMessageProtocol(IMessage message) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyInvalidMessageProtocol(message);
		}	
	}


	private void notifyMessageProcessed(IMessage message,
			Vector<IMessage> response) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyMessageProcessed(message, response);
		}
	}


	private void notifyInvalidProtocolMessageOrder(IMessage message) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyInvalidProtocolMessageOrder(message);
		}
	}


	private void notifyProblemWithSessionCreation(IMessage message) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyProblemWithSessionCreation(message);
		}
	}
	

	private void notifyCancelSync(ISyncSession syncSession) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyCancelSync(syncSession);
		}
	}


	private void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyCancelSyncErrorSyncSessionNotOpen(sourceId, endpoint);
		}
	}

	public void cleanAllSessions() {
		this.repository.deleteAll();
	}
	
	public IBeginSyncMessageProcessor getBeginMessageProcessor(){
		return this.initialMessage;
	}

	private synchronized void persistChanges(ISyncSession syncSession) {
		this.repository.save(syncSession);
	}
}
     