package com.mesh4j.sync.message.channel.sms.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<String, List<String>> messages = Collections.synchronizedMap(new HashMap<String, List<String>>());
	private ISmsReceiver messageReceiver;
	private Map<String, InMemorySmsConnection> endpointConnections = Collections.synchronizedMap(new HashMap<String, InMemorySmsConnection>());
	private SmsEndpoint endpoint;
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding;
	private ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification = new SmsConnectionInboundOutboundNotification();
	private int channelDelay = 300;
	
	// BUSINESS METHODS
	public InMemorySmsConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int readDelay, SmsEndpoint endpoint){
		this(messageEncoding, maxMessageLenght, readDelay, endpoint, 300);
	}
	
	public InMemorySmsConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int readDelay, SmsEndpoint endpoint, int channelDelay){
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
		this.channelDelay = channelDelay;
		this.endpoint = endpoint;
		
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
	public void setMessageReceiver(ISmsReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	@Override
	public void send(List<String> msgs, SmsEndpoint endpoint) {
		if(this.smsConnectionInboundOutboundNotification != null){
			for (String msg : msgs) {
				this.smsConnectionInboundOutboundNotification.notifySendMessage(endpoint.getEndpointId(), msg);
			}
		}
		
		InMemorySmsConnection endpointConnection = this.endpointConnections.get(endpoint.getEndpointId());
		endpointConnection.receive(msgs, this.endpoint);
	}

	@Override
	public void refresh() {
		synchronized (SEMAPHORE) {
			for (String endpointId : this.messages.keySet()) {
				List<String> localMsgs = this.messages.get(endpointId);
				if(localMsgs != null){
					for (String msg : localMsgs) {
						this.sleep(this.channelDelay);
						Date date = new Date();
						if(this.smsConnectionInboundOutboundNotification != null){
							this.smsConnectionInboundOutboundNotification.notifyReceiveMessage(endpointId, msg, date);
						}
						this.messageReceiver.receiveSms(new SmsEndpoint(endpointId), msg, date);			
					}
					this.messages.put(endpointId, new ArrayList<String>());
				}
			}
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

	public void receive(List<String> msgs, SmsEndpoint endpoint) {
		synchronized (SEMAPHORE) {
			List<String> localMsgs = this.messages.get(endpoint.getEndpointId());
			if(localMsgs == null){
				localMsgs = new ArrayList<String>();
				this.messages.put(endpoint.getEndpointId(), localMsgs);
			}
			localMsgs.addAll(msgs);
		}		
	}

	public void addEndpointConnection(InMemorySmsConnection endpointConnection) {
		this.endpointConnections.put(endpointConnection.getEndpoint().getEndpointId(), endpointConnection); 
	}

	public void setSmsConnectionOutboundNotification(ISmsConnectionInboundOutboundNotification smsConnectionOutboundNotification) {
		this.smsConnectionInboundOutboundNotification = smsConnectionOutboundNotification;
		
	}

}
