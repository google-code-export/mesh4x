package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsReceiver;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;

public class MockSmsRefreshConnection implements ISmsConnection, IRefreshTask{

	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private List<String> messages = new ArrayList<String>();
	private ISmsReceiver messageReceiver;
	private MockSmsRefreshConnection endpointConnection;
	private SmsEndpoint endpoint;
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding;
	
	// BUSINESS METHODS
	
	public MockSmsRefreshConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int delay){
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
		if(delay > 0){
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), delay);
		} else {
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), 500);
		}
	}
	
	@Override
	public int getMaxMessageLenght() {
		return maxMessageLenght;
	}

	@Override
	public IMessageEncoding getMessageEncoding() {
		return messageEncoding;
	}

	@Override
	public void registerSmsReceiver(ISmsReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	@Override
	public void send(List<String> msgs, SmsEndpoint endpoint) {
		for (String msg : msgs) {
			System.out.println("Send from: " + this.endpoint.getEndpointId() + " to: " + endpoint.getEndpointId() + " msg: " + msg);
		}
		this.endpointConnection.receive(msgs, endpoint);
	}

	@Override
	public void refresh() {
		synchronized (SEMAPHORE) {
			for (String msg : this.messages) {
				this.sleep(300);
				//System.out.println("Read: " + this.endpoint.getEndpointId() + " msg: " + msg);
				this.messageReceiver.receiveSms(this.endpointConnection.getEndpoint(), msg, new Date());			
			}
			this.messages = new ArrayList<String>();
		}
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// nothing to do
		}
		
	}

	public SmsEndpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(SmsEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	public void receive(List<String> msgs, SmsEndpoint endpoint) {
		synchronized (SEMAPHORE) {
			for (String msg : msgs) {
				//System.out.println("Receive: " + this.endpoint.getEndpointId() + " endpoint: " + endpoint.getEndpointId() + " msg: " + msg);
				this.messages.add(msg);		
			}
		}		
	}

	public void setEndpointConnection(MockSmsRefreshConnection endpointConnection) {
		this.endpointConnection = endpointConnection; 
	}

}
