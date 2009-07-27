package org.mesh4j.sync.message.channel.sms.connection;

import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SmsConnection implements ISmsConnection, MessageListener {

	// MODEL VARIABLES
	private ISmsConnectionInboundOutboundNotification smsConnectionNotification = null;
	private int maxMessageLenght = 100;
	private IMessageEncoding messageEncoding;
	protected ISmsReceiver messageReceiver = null;
	private MessageConnection messageConnection;
	private String connectionString;
			
	// BUSINESS METHODS
	public SmsConnection(String connectionString, int maxMessageLenght, IMessageEncoding messageEncoding, ISmsConnectionInboundOutboundNotification smsConnectionNotification) {
		Guard.argumentNotNullOrEmptyString(connectionString, "connectionString");
		Guard.argumentNotNull(messageEncoding, "messageEncoding");
		Guard.argumentNotNull(smsConnectionNotification, "smsConnectionNotification");

		this.smsConnectionNotification = smsConnectionNotification;
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
		this.connectionString = connectionString;
		
	}

	public void send(String smsNumber, String message, boolean isBinary) {
		try{
			if(isBinary){
				BinaryMessage msg = (BinaryMessage) this.messageConnection.newMessage(MessageConnection.BINARY_MESSAGE);
				msg.setPayloadData(message.getBytes());
				msg.setAddress(smsNumber);
				this.messageConnection.send(msg);
			} else{
				TextMessage msg = (TextMessage)this.messageConnection.newMessage(MessageConnection.TEXT_MESSAGE);
				msg.setPayloadText(message);
				msg.setAddress(smsNumber);
				this.messageConnection.send(msg);
			}
			
			this.notifySendMessage(smsNumber, message);	
		} catch (Throwable e) {
			e.printStackTrace();
			this.notifySendMessageError(smsNumber, message, e.getMessage());
			throw new MeshException(e.getMessage());
		}
	}
	

	public void send(Vector<String> messages, SmsEndpoint endpoint) {
		for (String message : messages) {
			this.send(endpoint.getEndpointId(), message, this.messageEncoding.isBynary());
		}		
	}

	public void notifyIncomingMessage(MessageConnection msgCon) {
		try{
			Message msg = msgCon.receive();
			notifyIncomingMessage(msg);			
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	
	private void notifyIncomingMessage(Message msg) {
		if(msg == null){
			return;
		}
		
		String endpointId = msg.getAddress();
		Date date = msg.getTimestamp();
		String payload = null;
		
		if(msg instanceof BinaryMessage){
			BinaryMessage binaryMessage = (BinaryMessage) msg;
			byte[] bytes = binaryMessage.getPayloadData();
			payload = new String(bytes);
		} else {
			TextMessage textMessage = (TextMessage) msg;
			payload = textMessage.getPayloadText();
		}
		
		if(!(payload == null || payload.length() == 0)){
			this.notifyReceiveMessage(endpointId, payload, date);
			this.processReceivedMessage(endpointId, date, payload);
		}
	}

	private void processReceivedMessage(final String endpointId, final Date date, final String payload) {
		if(this.messageReceiver != null){
			Runnable run = new Runnable(){
				public void run() {
					try{
						messageReceiver.receiveSms(new SmsEndpoint(endpointId), payload, date);
					} catch(Throwable th){
						th.printStackTrace();
						notifyReceiveMessageError(
								endpointId, 
								payload,
								date,
								th.getMessage());
					}
				}				
			};
			Thread receiveSmsThread = new Thread(run, "receive_sms_"+ new Date().getTime());
			receiveSmsThread.start();
		}
	}
	
	private void notifyReceiveMessage(String endpointId, String message, Date date) {
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifyReceiveMessage(endpointId, message, date);
		}
	}

	private void notifyReceiveMessageError(String endpointId, String message, Date date, String error) {
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifyReceiveMessageError(endpointId, message, date, error);
		}
	}
	
	private void notifySendMessageError(String endpointId, String message, String error) {
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifySendMessageError(endpointId, message, error);
		}
	}

	private void notifySendMessage(String endpointId, String message) {
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifySendMessage(endpointId, message);
		}
	}


	public int getMaxMessageLenght() {
		return this.maxMessageLenght;
	}

	public IMessageEncoding getMessageEncoding() {
		return messageEncoding;
	}

	public void setMessageReceiver(ISmsReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}
	
	public void closeConnection() 
	{
	    try {
	    	this.messageConnection.setMessageListener(null);
	    	this.messageConnection.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new MeshException(e.getMessage());
	    }
	}

	public void startConnection() {
		try{
			this.messageConnection = (MessageConnection)Connector.open(this.connectionString);
			this.messageConnection.setMessageListener(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}	
	}
}