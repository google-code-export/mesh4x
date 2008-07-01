package com.mesh4j.sync.message.core;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessage;

public class Message implements IMessage {

	// MODEL VARIABLES
	private String protocol;
	private String messageType;
	private String sessionId;
	private String data;
	private IEndpoint endpoint;
	private String origin;
	private boolean ackRequired = true;
	
	// METHODs
	public Message(String protocol, String messageType, String sessionId, String data, IEndpoint endpoint) {
		super();
		this.protocol = protocol;
		this.messageType = messageType;
		this.sessionId = sessionId;
		this.data = data;
		this.endpoint = endpoint;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public String getMessageType() {
		return this.messageType;
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public IEndpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public String getOrigin() {
		return origin;
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public boolean isAckRequired() {
		return ackRequired;
	}

	public void setAckIsRequired(boolean ackRequired) {
		this.ackRequired = ackRequired;
	}
}
