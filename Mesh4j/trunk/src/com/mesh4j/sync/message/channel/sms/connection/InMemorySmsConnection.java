package com.mesh4j.sync.message.channel.sms.connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsReceiver;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.connection.smslib.IRefreshTask;
import com.mesh4j.sync.message.channel.sms.connection.smslib.RefreshSchedulerTimerTask;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;

public class InMemorySmsConnection implements ISmsConnection, IRefreshTask{

	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private List<String> messages = new ArrayList<String>();
	private ISmsReceiver messageReceiver;
	private InMemorySmsConnection endpointConnection;
	private SmsEndpoint endpoint;
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding;
	private ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification = new SmsConnectionInboundOutboundNotification();
	private int channelDelay = 300;
	
	// BUSINESS METHODS
	public InMemorySmsConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int readDelay){
		this(messageEncoding, maxMessageLenght, readDelay, 300);
	}
	
	public InMemorySmsConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int readDelay, int channelDelay){
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
		this.channelDelay = channelDelay;
		if(readDelay > 0){
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), readDelay);
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
		if(this.smsConnectionInboundOutboundNotification != null){
			for (String msg : msgs) {
				this.smsConnectionInboundOutboundNotification.notifySendMessage(endpoint.getEndpointId(), msg);
			}
		}
		this.endpointConnection.receive(msgs, this.endpoint);
	}

	@Override
	public void refresh() {
		synchronized (SEMAPHORE) {
			for (String msg : this.messages) {
				this.sleep(this.channelDelay);
				Date date = new Date();
				if(this.smsConnectionInboundOutboundNotification != null){
					this.smsConnectionInboundOutboundNotification.notifyReceiveMessage(this.endpointConnection.getEndpoint().getEndpointId(), msg, date);
				}
				this.messageReceiver.receiveSms(this.endpointConnection.getEndpoint(), msg, date);			
			}
			this.messages = new ArrayList<String>();
		}
	}

	private void sleep(int i) {
		try {
			if(i > 0){
				Thread.sleep(i);
			}
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
				this.messages.add(msg);
			}
		}		
	}

	public void setEndpointConnection(InMemorySmsConnection endpointConnection) {
		this.endpointConnection = endpointConnection; 
	}

	public void setSmsConnectionOutboundNotification(ISmsConnectionInboundOutboundNotification smsConnectionOutboundNotification) {
		this.smsConnectionInboundOutboundNotification = smsConnectionOutboundNotification;
		
	}

}
