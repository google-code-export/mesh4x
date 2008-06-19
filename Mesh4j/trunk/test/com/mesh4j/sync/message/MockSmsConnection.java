package com.mesh4j.sync.message;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsMessageReceiver;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;

public class MockSmsConnection implements ISmsConnection{

	// MODEL VARIABLES
	private SmsEndpoint smsEndpoint;
	private MockSmsConnection endpoint;
	private ISmsMessageReceiver messageReceiver;
	private int generatedMessagesStatistics = 0;
	private int generatedMessagesSizeStatistics = 0;
	private boolean activeTrace = false;
	
	// METHODS
	public MockSmsConnection(String smsNumber) {
		super();
		this.smsEndpoint = new SmsEndpoint(smsNumber);
	}
	
	public void setEndPoint(MockSmsConnection endpoint){
		this.endpoint = endpoint;
	}
	
	@Override
	public void registerSmsMessageReceiver(ISmsMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;			
	}

	@Override
	public void send(SmsEndpoint endpoint, String messageText) {
		if(this.activeTrace){
			System.out.println("SMS from: " + this.smsEndpoint.getEndpointId() + " to: " + endpoint.getEndpointId() + " msg: " + messageText);
		}
		this.generatedMessagesStatistics = this.generatedMessagesStatistics + 1;
		this.generatedMessagesSizeStatistics = this.generatedMessagesSizeStatistics + messageText.length();
		this.endpoint.receiveSms(this.smsEndpoint, messageText);
	}
	
	public void receiveSms(SmsEndpoint endpoint, String messageText){
		this.messageReceiver.receiveSms(endpoint, messageText);
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