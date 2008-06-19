package com.mesh4j.sync.message;

public interface IMessage {

	String getProtocol();
	
	String getMessageType();
	
	String getSourceId();
	
	String getData();

	IEndpoint getEndpoint();
	
	String getEndpointId();
	
}
