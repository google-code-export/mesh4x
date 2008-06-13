package com.mesh4j.sync.message;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;

public class MockSmsConnection implements ISmsConnection{

	// MODEL VARIABLES
	private MockSmsConnection endpoint;
	private IMessageReceiver messageReceiver;
	private int generatedMessagesStatistics = 0;
	private int generatedMessagesSizeStatistics = 0;
	
	// METHODS
	public MockSmsConnection() {
	}
	
	public void setEndPoint(MockSmsConnection endpoint){
		this.endpoint = endpoint;
	}
	
	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;			
	}

	@Override
	public void send(String message) {
		this.generatedMessagesStatistics = this.generatedMessagesStatistics + 1;
		this.generatedMessagesSizeStatistics = this.generatedMessagesSizeStatistics + message.length();
		this.endpoint.receive(message);
	}
	
	public void receive(String message){
		this.messageReceiver.receiveMessage(message);
	}

	@Override
	public int getMaxMessageLenght() {
		return 140;
	}

	protected int getGeneratedMessagesStatistics() {
		return generatedMessagesStatistics;
	}

	protected int getGeneratedMessagesSizeStatistics() {
		return generatedMessagesSizeStatistics;
	}
}