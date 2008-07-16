package com.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class MessageSyncProtocol implements IMessageSyncProtocol {

	// MODEL
	private ArrayList<IMessageProcessor> messageProcessors = new ArrayList<IMessageProcessor>();
	private IBeginSyncMessageProcessor initialMessage;
	private ICancelSyncMessageProcessor cancelMessage;
	private ISyncSessionRepository repository;
	private String protocolPrefix = "";
	
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
		ISyncSession syncSession = this.repository.getSession(message.getSessionId());
		if(syncSession == null){
			if(this.initialMessage.getMessageType().equals(message.getMessageType())){
				String sourceId = this.initialMessage.getSourceId(message.getData());
				syncSession = this.repository.createSession(message.getSessionId(), sourceId, message.getEndpoint(), false);
				if(syncSession == null){
					return NO_RESPONSE;
				}
			} else {
				return NO_RESPONSE;
			}
		}
		
		List<IMessage> response = new ArrayList<IMessage>();
		if(this.isValidMessageProtocol(message)){
			for (IMessageProcessor processor : this.messageProcessors) {
				List<IMessage> msgResponse = processor.process(syncSession, message);
				response.addAll(msgResponse);
			}
			this.persistChanges(syncSession);
		}
		return response;
	}
	
	
	@Override
	public IMessage cancelSync(String sourceId, IEndpoint endpoint) {
		ISyncSession syncSession = this.repository.getSession(sourceId, endpoint.getEndpointId());
		if(syncSession == null || !syncSession.isOpen()){
			Guard.throwsException("ERROR_MESSAGE_SYNC_SESSION_IS_NOT_OPEN", sourceId, endpoint.getEndpointId());
		}
		
		IMessage message = this.cancelMessage.createMessage(syncSession);
		this.repository.cancel(syncSession);
		return message;
	}


	private synchronized void persistChanges(ISyncSession syncSession) {
		if(syncSession.isOpen()){
			this.repository.flush(syncSession);
		} else {
			this.repository.snapshot(syncSession);
		}
	}

	@Override
	public IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol) {
		ISyncSession syncSession = this.repository.getSession(sourceId, endpoint.getEndpointId());
		if(syncSession != null && syncSession.isOpen()){
			Guard.throwsException("ERROR_MESSAGE_SYNC_SESSION_IS_OPEN", sourceId, endpoint.getEndpointId());
		}
		if(syncSession == null){
			syncSession = this.repository.createSession(IdGenerator.newID(), sourceId, endpoint, fullProtocol);
			if(syncSession == null){
				return null;
			}
		}
		
		IMessage message = this.initialMessage.createMessage(syncSession);
		this.persistChanges(syncSession);
		return message;
	}
	
	@Override
	public void endSync(ISyncSession syncSession, Date date) {
		syncSession.endSync(date);
		IMessageSyncAdapter adapter = this.repository.getSource(syncSession.getSourceId());
		adapter.synchronizeSnapshot(syncSession);
	}

	@Override
	public ISyncSession getSyncSession(String sourceId, IEndpoint endpoint) {
		return this.repository.getSession(sourceId, endpoint.getEndpointId());
	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		this.repository.registerSourceIfAbsent(adapter);
	}
}
