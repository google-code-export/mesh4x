package com.mesh4j.sync.message;


public interface IMessage {

	String getProtocol();
	
	String getProtocolVersion();
	
	String getMessageType();
	
	String getDataSetId();
	
	String getData();

}
