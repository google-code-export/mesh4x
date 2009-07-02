package org.mesh4j.sync.message.channel.sms.connection;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.schedule.IRefreshTask;
import org.mesh4j.sync.message.channel.sms.schedule.RefreshSchedulerTimerTask;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.schedule.timer.TimerScheduler;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.Map;


public class InMemorySmsConnection implements ISmsConnection, IRefreshTask{

	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private Map messages = new HashMap();
	private ISmsReceiver messageReceiver;
	private Map endpointConnections = new HashMap();
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
	

	public int getMaxMessageLenght() {
		return maxMessageLenght;
	}


	public IMessageEncoding getMessageEncoding() {
		return messageEncoding;
	}


	public void setMessageReceiver(ISmsReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}


	public void send(Vector<String> msgs, SmsEndpoint endpoint) {
		if(this.smsConnectionInboundOutboundNotification != null){
			for (String msg : msgs) {
				this.smsConnectionInboundOutboundNotification.notifySendMessage(endpoint.getEndpointId(), msg);
			}
		}
		
		InMemorySmsConnection endpointConnection = (InMemorySmsConnection)this.endpointConnections.get(endpoint.getEndpointId());
		endpointConnection.receive(msgs, this.endpoint);
	}


	public void refresh() {
		synchronized (SEMAPHORE) {
			
			String endpointId = null;
			for (int i = 0; i < this.messages.keys().length; i++) {
				endpointId = (String)this.messages.keys()[i];

				Vector<String> localMsgs = (Vector<String>)this.messages.get(endpointId);
				if(localMsgs != null){
					for (String msg : localMsgs) {
						this.sleep(this.channelDelay);
						Date date = new Date();
						if(this.smsConnectionInboundOutboundNotification != null){
							this.smsConnectionInboundOutboundNotification.notifyReceiveMessage(endpointId, msg, date);
						}
						this.messageReceiver.receiveSms(new SmsEndpoint(endpointId), msg, date);			
					}
					this.messages.put(endpointId, new Vector<String>());
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

	public void receive(Vector<String> msgs, SmsEndpoint endpoint) {
		synchronized (SEMAPHORE) {
			Vector<String> localMsgs = (Vector<String>)this.messages.get(endpoint.getEndpointId());
			if(localMsgs == null){
				localMsgs = new Vector<String>();
				this.messages.put(endpoint.getEndpointId(), localMsgs);
			}
			
			for (String msg : msgs) {
				localMsgs.addElement(msg);	
			}			
		}		
	}

	public void addEndpointConnection(InMemorySmsConnection endpointConnection) {
		this.endpointConnections.put(endpointConnection.getEndpoint().getEndpointId(), endpointConnection); 
	}

	public void setSmsConnectionOutboundNotification(ISmsConnectionInboundOutboundNotification smsConnectionOutboundNotification) {
		this.smsConnectionInboundOutboundNotification = smsConnectionOutboundNotification;
		
	}

}
