package com.mesh4j.sync.message;


public interface IMessage {

	String getProtocol();
	
	String getMessageType();
	
	String getDataSetId();
	
	String getData();

}
