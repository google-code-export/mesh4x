package org.mesh4j.sync.message;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.encoding.IMessageEncoding;


public class MockSmsConnection implements ISmsConnection{

	// MODEL VARIABLES
	private SmsEndpoint smsEndpoint;
	private MockSmsConnection endpoint;
	private ISmsReceiver messageReceiver;
	private int generatedMessagesStatistics = 0;
	private int generatedMessagesSizeStatistics = 0;
	private boolean activeTrace = false;
	private IMessageEncoding messageEncoding;
	private int sleepDelay = 0;
	private int maxMessageLenght = 140;
	
	// METHODS
	public MockSmsConnection(String smsNumber, IMessageEncoding messageEncoding) {
		super();
		this.smsEndpoint = new SmsEndpoint(smsNumber);
		this.messageEncoding = messageEncoding;
	}
	
	public void setEndPoint(MockSmsConnection endpoint){
		this.endpoint = endpoint;
	}
	
	@Override
	public void setMessageReceiver(ISmsReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;			
	}

	@Override
	public void send(List<String> messageTexts, SmsEndpoint smsEndpoint) {
		for (String messageText : messageTexts) {
			if(this.activeTrace){
				System.out.println("SMS from: " + this.smsEndpoint.getEndpointId() + " to: " + smsEndpoint.getEndpointId() + " msg: " + messageText);
			}
			this.generatedMessagesStatistics = this.generatedMessagesStatistics + 1;
			this.generatedMessagesSizeStatistics = this.generatedMessagesSizeStatistics + messageText.length();
			this.sleep();
			if(this.endpoint != null){
				this.endpoint.receiveSms(this.smsEndpoint, messageText);
			}
			this.sleep();
		}
	}

	private void sleep() {
		if(sleepDelay > 0){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Nothing to do
			}
		}
	}
	
	public void receiveSms(SmsEndpoint endpoint, String messageText){
		this.messageReceiver.receiveSms(endpoint, messageText, new Date());
	}

	@Override
	public int getMaxMessageLenght() {
		return maxMessageLenght;
	}
	
	public void setMaxMessageLenght(int max) {
		this.maxMessageLenght = max;
	}
		
	public int getGeneratedMessagesStatistics() {
		return generatedMessagesStatistics;
	}

	public int getGeneratedMessagesSizeStatistics() {
		return generatedMessagesSizeStatistics;
	}

	public void resetStatistics() {
		this.generatedMessagesStatistics = 0;
		this.generatedMessagesSizeStatistics = 0;		
	}
	
	public void activateTrace(){
		this.activeTrace = true;
	}

	@Override
	public IMessageEncoding getMessageEncoding() {
		return messageEncoding;
	}
	
	public void setSleepDelay(int sleepDelay) {
		this.sleepDelay = sleepDelay;
	}

}