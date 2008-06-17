package com.mesh4j.sync.message;

public class Message implements IMessage {

	// MODEL VARIABLES
	private String protocol;
	private String version;
	private String messageType;
	private String dataSetId;
	private String data;
	
	// METHODs
	public Message(String protocol, String version, String messageType, String dataSetId, String data) {
		super();
		this.protocol = protocol;
		this.version = version;
		this.messageType = messageType;
		this.dataSetId = dataSetId;
		this.data = data;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public String getDataSetId() {
		return this.dataSetId;
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
	public String getProtocolVersion() {
		return this.version;
	}

}
