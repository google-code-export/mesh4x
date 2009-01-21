package org.mesh4j.sync.message.channel.sms.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.schedule.IRefreshTask;
import org.mesh4j.sync.message.channel.sms.schedule.RefreshSchedulerTimerTask;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.schedule.timer.TimerScheduler;


public class InMemorySmsConnection implements ISmsConnection, IRefreshTask{

	private final static Log LOGGER = LogFactory.getLog(InMemorySmsConnection.class);
	
	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private Map<String, List<String>> messages = Collections.synchronizedMap(new HashMap<String, List<String>>());
	private ISmsReceiver messageReceiver;
	private Map<String, InMemorySmsConnection> endpointConnections = Collections.synchronizedMap(new HashMap<String, InMemorySmsConnection>());
	private SmsEndpoint endpoint;
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding;
	private ArrayList<ISmsConnectionInboundOutboundNotification> smsConnectionInboundOutboundNotifications = new ArrayList<ISmsConnectionInboundOutboundNotification>();
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
		this.notifySendMessage(msgs, endpoint);
		InMemorySmsConnection endpointConnection = this.endpointConnections.get(endpoint.getEndpointId());
		endpointConnection.receive(msgs, this.endpoint);
	}

	private void notifySendMessage(List<String> msgs, SmsEndpoint endpoint) {
		if(this.smsConnectionInboundOutboundNotifications != null){
			for (String msg : msgs) {
				for (ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification : this.smsConnectionInboundOutboundNotifications) {
					smsConnectionInboundOutboundNotification.notifySendMessage(endpoint.getEndpointId(), msg);
				}
			}
		}
	}
	
	private void notifyReceiveMessageError(String endpointId, String msg, Date date) {
		if(this.smsConnectionInboundOutboundNotifications != null){
			for (ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification : this.smsConnectionInboundOutboundNotifications) {
					smsConnectionInboundOutboundNotification.notifyReceiveMessageError(endpointId, msg, date);
			}
		}
	}
	
	private void notifyReceivedMessage(String endpointId, String msg, Date date) {
		if(this.smsConnectionInboundOutboundNotifications != null){
			for (ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification : this.smsConnectionInboundOutboundNotifications) {
				smsConnectionInboundOutboundNotification.notifyReceiveMessage(endpointId, msg, date);
			}
		}
	}

	@Override
	public void refresh() {
		synchronized (SEMAPHORE) {
			for (String endpointId : this.messages.keySet()) {
				List<String> localMsgs = this.messages.get(endpointId);
				if(localMsgs != null){
					int size = Math.min(10, localMsgs.size()-1);
					for (int i = 0; i <= size; i++) {
						String msg = localMsgs.get(i);
						localMsgs.remove(i);
						
						this.sleep(this.channelDelay);
						Date date = new Date();
						notifyReceivedMessage(endpointId, msg, date);
						try{
							this.messageReceiver.receiveSms(new SmsEndpoint(endpointId), msg, date);
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
							notifyReceiveMessageError(endpointId, msg, date);	
						}
					}
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

	public void receive(String msg, SmsEndpoint endpoint) {
		synchronized (SEMAPHORE) {
			List<String> localMsgs = this.messages.get(endpoint.getEndpointId());
			if(localMsgs == null){
				localMsgs = new ArrayList<String>();
				this.messages.put(endpoint.getEndpointId(), localMsgs);
			}
			localMsgs.add(msg);
		}		
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

	@Override
	public void startUp() {
		// nothing to do		
	}
	
	@Override
	public void shutdown() {
		// nothing to do		
	}

	public boolean hasEndpointConnection(SmsEndpoint target) {
		return this.endpointConnections.get(target.getEndpointId()) != null;
	}

	public InMemorySmsConnection getEndpoint(String endpointId) {
		return this.endpointConnections.get(endpointId);
	}

	public ISmsReceiver getMessageReceiver(){
		return this.messageReceiver;
	}

	public void addSmsConnectionOutboundNotification(ISmsConnectionInboundOutboundNotification[] smsConnectionInboundOutboundNotifications) {
		for (ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification : smsConnectionInboundOutboundNotifications) {
			this.smsConnectionInboundOutboundNotifications.add(smsConnectionInboundOutboundNotification);
		}		
	}
}
