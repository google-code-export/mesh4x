package org.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private ArrayList<IMessageProcessor> messageProcessors = new ArrayList<IMessageProcessor>();
	private IBeginSyncMessageProcessor initialMessage;
	private ICancelSyncMessageProcessor cancelMessage;
	private ISyncSessionRepository repository;
	private String protocolPrefix = "";
	private List<IMessageSyncAware> syncAwareList = new ArrayList<IMessageSyncAware>();
	
	// METHODS
	public MessageSyncProtocol(String protocolPrefix, IBeginSyncMessageProcessor initialMessage, ICancelSyncMessageProcessor cancelMessage, ISyncSessionRepository repository, ArrayList<IMessageProcessor> messageProcessors){
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

	@Override
	public boolean isValidMessageProtocol(IMessage message) {
		return message != null 
			&& message.getProtocol().equals(this.protocolPrefix);
	}
	
	@Override
	public List<IMessage> processMessage(IMessage message) {		
	
		if(this.isValidMessageProtocol(message)){
			ISyncSession syncSession = this.repository.getSession(message.getSessionId());
			if(syncSession == null){
				if(this.initialMessage.getMessageType().equals(message.getMessageType())){
					String sourceId = this.initialMessage.getSourceId(message.getData());
					boolean isFull = this.initialMessage.getFullProtocol(message.getData());
					boolean shouldSendChanges = this.initialMessage.getSendChanges(message.getData());
					boolean shouldReceiveChanges = this.initialMessage.getReceiveChanges(message.getData());
					
					syncSession = this.repository.createSession(message.getSessionId(), message.getSessionVersion(), sourceId, message.getEndpoint(), isFull, shouldSendChanges, shouldReceiveChanges);
					if(syncSession == null){
						this.notifySessionCreationError(message, sourceId);
						return NO_RESPONSE;
					}
				} else {
					this.notifyInvalidProtocolMessageOrder(message);
					return NO_RESPONSE;
				}
			} else {
				if(!this.initialMessage.getMessageType().equals(message.getMessageType()) && syncSession.isCancelled()){
					this.notifyInvalidProtocolMessageOrder(message);
					return NO_RESPONSE;
				}
			}
			
			List<IMessage> response = new ArrayList<IMessage>();
			for (IMessageProcessor processor : this.messageProcessors) {
				List<IMessage> msgResponse = processor.process(syncSession, message);
				response.addAll(msgResponse);
			}
			this.persistChanges(syncSession);
			this.notifyMessageProcessed(syncSession, message, response);
			return response;
		} else {
			this.notifyInvalidMessageProtocol(message);
			return NO_RESPONSE;
		}
	}
	
	
	@Override
	public IMessage cancelSync(String sourceId, IEndpoint endpoint) {
		ISyncSession syncSession = this.repository.getSession(sourceId, endpoint.getEndpointId());
		if(syncSession == null || !syncSession.isOpen()){
			this.notifyCancelSyncErrorSyncSessionNotOpen(sourceId, endpoint);
			Guard.throwsException("ERROR_MESSAGE_SYNC_SESSION_IS_NOT_OPEN", sourceId, endpoint.getEndpointId());
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

	private synchronized void persistChanges(ISyncSession syncSession) {
		if(syncSession.isOpen()){
			this.repository.flush(syncSession);
		} else {
			if(syncSession.isCancelled()){
				this.repository.cancel(syncSession);
			} else {
				this.repository.snapshot(syncSession);
			}
		}
	}

	@Override
	public IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {
		ISyncSession syncSession = this.repository.getSession(sourceId, endpoint.getEndpointId());
		if(syncSession != null && syncSession.isOpen()){
			this.notifyBeginSyncError(syncSession);
			return null;
		}
		if(syncSession == null){
			syncSession = this.repository.createSession(IdGenerator.INSTANCE.newID(), 0, sourceId, endpoint, fullProtocol, shouldSendChanges, shouldReceiveChanges);
			if(syncSession == null){
				return null;
			}
		}
		
		syncSession.beginSync(fullProtocol, shouldSendChanges, shouldReceiveChanges);
		
		IMessage message = this.initialMessage.createMessage(syncSession);
		this.persistChanges(syncSession);
		this.notifyBeginSync(syncSession);
		return message;
	}
	
	@Override
	public void endSync(ISyncSession syncSession, Date date) {
		syncSession.endSync(date);
		
		IMessageSyncAdapter adapter = this.repository.getSourceOrCreateIfAbsent(syncSession.getSourceId());
		List<Item> conflicts = adapter.synchronizeSnapshot(syncSession);
		
		this.notifyEndSync(syncSession, conflicts);
	}

	@Override
	public ISyncSession getSyncSession(String sourceId, IEndpoint endpoint) {
		return this.repository.getSession(sourceId, endpoint.getEndpointId());
	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		this.repository.registerSourceIfAbsent(adapter);
	}
	
	@Override
	public void registerSource(IMessageSyncAdapter adapter) {
		this.repository.registerSource(adapter);
	}
	
	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		return this.repository.getSource(sourceId);
	}
	
	public void registerSyncAware(IMessageSyncAware syncAware){
		if(syncAware != null){
			this.syncAwareList.add(syncAware);
		}
	}
	
	private void notifyEndSync(ISyncSession syncSession, List<Item> conflicts) {
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

	public void notifyInvalidMessageProtocol(IMessage message) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyInvalidMessageProtocol(message);
		}	
	}


	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyMessageProcessed(syncSession, message, response);
		}
	}


	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyInvalidProtocolMessageOrder(message);
		}
	}


	public void notifySessionCreationError(IMessage message, String sourceId) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifySessionCreationError(message, sourceId);
		}
	}
	

	public void notifyCancelSync(ISyncSession syncSession) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyCancelSync(syncSession);
		}
	}


	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		for (IMessageSyncAware syncAware : this.syncAwareList) {
			syncAware.notifyCancelSyncErrorSyncSessionNotOpen(sourceId, endpoint);
		}
	}
	
	public IBeginSyncMessageProcessor getInitialMessage(){
		return this.initialMessage;
	}

}
