package org.mesh4j.sync.message.channel.sms.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.core.NonMessageEncoding;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.validations.MeshException;

public abstract class AbstractSmsConnection implements ISmsConnection{

	// CONSTANTS
	protected final static Log LOGGER = LogFactory.getLog(AbstractSmsConnection.class);
	
	// MODEL VARIABLES
	private ISmsConnectionInboundOutboundNotification[] smsConnectionNotifications = new ISmsConnectionInboundOutboundNotification[]{};
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding = NonMessageEncoding.INSTANCE;
	private HashMap<IFilter<String>, ISmsReceiver> messageReceivers = new HashMap<IFilter<String>, ISmsReceiver>();
	
	// BUSINESS METHODS
	public AbstractSmsConnection(int maxMessageLenght, IMessageEncoding messageEncoding, ISmsConnectionInboundOutboundNotification[] smsAware) {
		super();

		if(smsAware != null){
			this.smsConnectionNotifications = smsAware;
		}
		
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
	}
	
	@Override
	public int getMaxMessageLenght() {
		return this.maxMessageLenght;
	}

	@Override
	public IMessageEncoding getMessageEncoding() {
		return this.messageEncoding;
	}
	
	public ISmsReceiver getMessageReceiver(){
		return this.getMessageReceiver();
	}

	@Override
	public void send(List<String> messages, SmsEndpoint endpoint) {
		for (String message : messages) {
			try{
				this.send(endpoint.getEndpointId(), message, this.messageEncoding.isBynary());
			} catch (Throwable e) {
				this.notifySendMessageError(endpoint.getEndpointId(), message);
				throw new MeshException(e);
			}
		}		
	}
	
	protected abstract void send(String endpointId, String message, boolean bynary);

	protected boolean processReceivedMessage(String endpointId, String text, Date date) {
		if(!(text == null || text.isEmpty())){
			try{
				if(!this.messageReceivers.isEmpty()){
					ISmsReceiver messageReceiver = getMessageReceiver(text);
					if(messageReceiver != null){
						notifyReceiveMessage(endpointId, text, date);
						messageReceiver.receiveSms(new SmsEndpoint(endpointId), text, date);
						return true;
					} else {
						notifyReceiveMessageWasNotProcessed(endpointId, text, date);	
						return false;
					}
				} else {
					notifyReceiveMessageWasNotProcessed(endpointId, text, date);
					return false;
				}
			} catch(Throwable re){
				LOGGER.info(re.getMessage());
				this.notifyReceiveMessageError(endpointId, text, date);
				return false;
			}
		} else {
			return false;
		}
	}

	private ISmsReceiver getMessageReceiver(String text) {
		for (IFilter<String> filter : this.messageReceivers.keySet()) {
			if(filter.applies(text)){
				return this.messageReceivers.get(filter);
			}
		}
		return null;
	}

	@Override
	public void registerMessageReceiver(IFilter<String> filter, ISmsReceiver messageReceiver) {
		this.messageReceivers.put(filter, messageReceiver);		
	}
	
	protected void notifyReceiveMessage(String endpointId, String message, Date date) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Receive msg from: " + endpointId + " message: " + message );
		}
		for (ISmsConnectionInboundOutboundNotification smsConnectionNotification : this.smsConnectionNotifications) {
			try{
				smsConnectionNotification.notifyReceiveMessage(endpointId, message, date);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
	}
	
	protected void notifyReceiveMessageWasNotProcessed(String endpointId, String message, Date date) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Receive msg was not processed: " + endpointId + " message: " + message );
		}
		for (ISmsConnectionInboundOutboundNotification smsConnectionNotification : this.smsConnectionNotifications) {
			try{
				smsConnectionNotification.notifyReceiveMessageWasNotProcessed(endpointId, message, date);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
	}
	
	protected void notifyReceiveMessageError(String endpointId, String message, Date date) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Receive msg with error from: " + endpointId + " message: " + message );
		}
		for (ISmsConnectionInboundOutboundNotification smsConnectionNotification : this.smsConnectionNotifications) {
			try{
				smsConnectionNotification.notifyReceiveMessageError(endpointId, message, date);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
	}
	
	protected void notifySendMessageError(String endpointId, String message) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Send msg with error to: " + endpointId + " message: " + message );
		}
		for (ISmsConnectionInboundOutboundNotification smsConnectionNotification : this.smsConnectionNotifications) {
			try{
				smsConnectionNotification.notifySendMessageError(endpointId, message);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
	}

	protected void notifySendMessage(String endpointId, String message) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Send msg to: " + endpointId + " message: " + message );
		}
		for (ISmsConnectionInboundOutboundNotification smsConnectionNotification : this.smsConnectionNotifications) {
			try{
				smsConnectionNotification.notifySendMessage(endpointId, message);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}		
	}
	
}
