package com.mesh4j.sync.message;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsMessageReceiver;

public class MockSmsConnection implements ISmsConnection{

	// MODEL VARIABLES
	private String name = "Endpoint default";
	private MockSmsConnection endpoint;
	private ISmsMessageReceiver messageReceiver;
	private int generatedMessagesStatistics = 0;
	private int generatedMessagesSizeStatistics = 0;
	private boolean activeTrace = false;
	
	// METHODS
	public MockSmsConnection(String name) {
		super();
		this.name = name;
	}
	
	public void setEndPoint(MockSmsConnection endpoint){
		this.endpoint = endpoint;
	}
	
	@Override
	public void registerSmsMessageReceiver(ISmsMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;			
	}

	@Override
	public void send(String message) {
		if(this.activeTrace){
			System.out.println(this.name + " send: " + message);
		}
		this.generatedMessagesStatistics = this.generatedMessagesStatistics + 1;
		this.generatedMessagesSizeStatistics = this.generatedMessagesSizeStatistics + message.length();
		this.endpoint.receiveSms(message);
	}
	
	public void receiveSms(String message){
		this.messageReceiver.receiveSms(message);
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

	public void resetStatistics() {
		this.generatedMessagesStatistics = 0;
		this.generatedMessagesSizeStatistics = 0;		
	}
	
	public void activateTrace(){
		this.activeTrace = true;
	}
}