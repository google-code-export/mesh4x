package org.mesh4j.sync.message.channel.sms.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.AbstractSmsConnection;
import org.mesh4j.sync.message.channel.sms.schedule.IRefreshTask;
import org.mesh4j.sync.message.channel.sms.schedule.RefreshSchedulerTimerTask;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.schedule.timer.TimerScheduler;


public class InMemorySmsConnection extends AbstractSmsConnection implements IRefreshTask{

	private final Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private Map<String, List<String>> messages = Collections.synchronizedMap(new HashMap<String, List<String>>());
	private Map<String, InMemorySmsConnection> endpointConnections = Collections.synchronizedMap(new HashMap<String, InMemorySmsConnection>());
	private SmsEndpoint endpoint;
	private int channelDelay = 300;
	
	// BUSINESS METHODS
	public InMemorySmsConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int readDelay, SmsEndpoint endpoint){
		this(messageEncoding, maxMessageLenght, readDelay, endpoint, 300, null);
	}
	
	public InMemorySmsConnection(IMessageEncoding messageEncoding, int maxMessageLenght, int readDelay, SmsEndpoint endpoint, int channelDelay, ISmsConnectionInboundOutboundNotification[] smsAware){
		super(maxMessageLenght, messageEncoding, smsAware);

		this.channelDelay = channelDelay;
		this.endpoint = endpoint;
		
		if(readDelay > 0){
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), readDelay);
		} else {
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), 500);
		}
	}
	
	@Override
	protected void send(String endpointId, String message, boolean bynary) {
		this.notifySendMessage(endpointId, message);
		InMemorySmsConnection endpointConnection = this.endpointConnections.get(endpointId);
		endpointConnection.receive(message, this.endpoint);
	}

	@Override
	public void refresh() {
		synchronized (SEMAPHORE) {
			for (String endpointId : this.messages.keySet()) {
				List<String> localMsgs = this.messages.get(endpointId);
				if(localMsgs != null){
					this.messages.put(endpointId, new ArrayList<String>());
					
					for (String msg : localMsgs) {
						this.processReceivedMessage(endpointId, msg, new Date());
						this.sleep(this.channelDelay);
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

	@Override
	public void startUp() {
		// nothing to do		
	}
	
	@Override
	public void shutdown() {
		// nothing to do		
	}
	
	public void addEndpointConnection(InMemorySmsConnection endpointConnection) {
		this.endpointConnections.put(endpointConnection.getEndpoint().getEndpointId(), endpointConnection); 
	}

	public boolean hasEndpointConnection(SmsEndpoint target) {
		return this.endpointConnections.get(target.getEndpointId()) != null;
	}

	public InMemorySmsConnection getEndpoint(String endpointId) {
		return this.endpointConnections.get(endpointId);
	}

}
