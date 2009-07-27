package org.mesh4j.sync.message.core;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;

public class Message implements IMessage {

	// MODEL VARIABLES
	private String protocol;
	private String messageType;
	private String sessionId;
	private int sessionVersion = 0;
	private String data;
	private IEndpoint endpoint;
	private String origin;
	private boolean ackRequired = true;
	
	// METHODs
	public Message(String protocol, String messageType, String sessionId, int sessionVersion, String data, IEndpoint endpoint) {
		super();
		this.protocol = protocol;
		this.messageType = messageType;
		this.sessionId = sessionId;
		this.sessionVersion = sessionVersion;
		this.data = data;
		this.endpoint = endpoint;
	}

	public String getData() {
		return this.data;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public String getMessageType() {
		return this.messageType;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public IEndpoint getEndpoint() {
		return endpoint;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public boolean isAckRequired() {
		return ackRequired;
	}

	public void setAckIsRequired(boolean ackRequired) {
		this.ackRequired = ackRequired;
	}

	public int getSessionVersion() {
		return this.sessionVersion;
	}
}
