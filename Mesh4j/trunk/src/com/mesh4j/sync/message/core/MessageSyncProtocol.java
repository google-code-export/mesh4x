package com.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.ISyncSessionFactory;

public class MessageSyncProtocol implements IMessageSyncProtocol {

	// MODEL
	private ArrayList<IMessageProcessor> messageProcessors = new ArrayList<IMessageProcessor>();
	private IBeginSyncMessageProcessor initialMessage;
	private ICancelSyncMessageProcessor cancelMessage;
	private String protocolPrefix = "";
	
	// METHODS
	public MessageSyncProtocol(String protocolPrefix, IBeginSyncMessageProcessor initialMessage, ICancelSyncMessageProcessor cancelMessage, ArrayList<IMessageProcessor> messageProcessors){
		super();
		this.protocolPrefix = protocolPrefix;
		this.initialMessage = initialMessage;
		this.cancelMessage = cancelMessage;
		this.messageProcessors = messageProcessors;
	}

	@Override
	public boolean isValidMessageProtocol(IMessage message) {
		return message != null 
			&& message.getProtocol().equals(this.protocolPrefix);
	}
	
	@Override
	public List<IMessage> processMessage(ISyncSession syncSession, IMessage message) {
		List<IMessage> response = new ArrayList<IMessage>();
		if(this.isValidMessageProtocol(message)){
			for (IMessageProcessor processor : this.messageProcessors) {
				List<IMessage> msgResponse = processor.process(syncSession, message);
				response.addAll(msgResponse);
			}
		}
		return response;
	}
	
	@Override
	public IMessage beginSync(ISyncSession syncSession) {
		return this.initialMessage.createMessage(syncSession);
	}
	
	@Override
	public IMessage cancelSync(ISyncSession syncSession) {
		return this.cancelMessage.createMessage(syncSession);
	}

	@Override
	public ISyncSession createSession(ISyncSessionFactory syncSessionFactory, IMessage message) {
		return this.initialMessage.createSession(syncSessionFactory, message);
	}

	@Override
	public ISyncSession createSession(ISyncSessionFactory syncSessionFactory,
			String sourceId, IEndpoint target, boolean fullProtocol) {
		return this.initialMessage.createSession(syncSessionFactory, sourceId, target, fullProtocol);
	}
}
