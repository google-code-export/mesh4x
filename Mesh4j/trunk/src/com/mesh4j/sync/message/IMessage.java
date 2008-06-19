package com.mesh4j.sync.message;

public interface IMessage {

	String getProtocol();
	
	String getMessageType();
	
	String getSessionId();
	
	String getData();

	IEndpoint getEndpoint();
	
}
