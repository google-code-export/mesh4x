package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.core.NonMessageEncoding;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.validations.MeshException;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundBinaryMessage;
import org.smslib.InboundMessage;
import org.smslib.OutboundBinaryMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageTypes;
import org.smslib.modem.SerialModemGateway;


public class SmsLibAsynchronousConnection implements ISmsConnection, IInboundMessageNotification, IOutboundMessageNotification {

	// CONSTANTS
	private final static Log LOGGER = LogFactory.getLog(SmsLibAsynchronousConnection.class);
	
	// MODEL VARIABLES
	private ISmsConnectionInboundOutboundNotification smsConnectionNotification = null;
	private Service service;
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding = NonMessageEncoding.INSTANCE;
	private ISmsReceiver messageReceiver = null;
			
	// BUSINESS METHODS
	public SmsLibAsynchronousConnection(String gatewayId, String comPort, int baudRate, String manufacturer, String model) {
		this(gatewayId, comPort, baudRate, manufacturer, model, 140, NonMessageEncoding.INSTANCE, null);
	}
	
	public SmsLibAsynchronousConnection(String gatewayId, String comPort, int baudRate, String manufacturer, String model, 
			int maxMessageLenght, IMessageEncoding messageEncoding, ISmsConnectionInboundOutboundNotification smsConnectionNotification) {
		super();

		this.smsConnectionNotification = smsConnectionNotification;
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
		
		SerialModemGateway gateway = new SerialModemGateway(gatewayId, comPort, baudRate, manufacturer, model);
				
		gateway.setOutbound(true);
		gateway.setInbound(true);
		gateway.setSimPin("0000");
		
		this.service = new Service();
		this.service.setInboundNotification(this);
		this.service.setOutboundNotification(this);
		this.service.addGateway(gateway);
	}

	public void startService() {
		try {
			this.service.startService();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public void stopService() {
		try {
			this.service.stopService();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public void send(String smsNumber, String message, boolean isBinary) {
		try{
			OutboundMessage msg = null;
			if(isBinary){
				msg = new OutboundBinaryMessage(smsNumber, message.getBytes());
			} else {
				msg = new OutboundMessage(smsNumber, message);
			}
			this.service.queueMessage(msg);
		} catch (Exception e) {
			this.notifySendMessageError(smsNumber, message);
			throw new MeshException(e);
		}
	}

	
	private void notifyReceiveMessage(String endpointId, String message, Date date) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Receive msg from: " + endpointId + " message: " + message );
		}
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifyReceiveMessage(endpointId, message, date);
		}
		if(this.messageReceiver != null){
			this.messageReceiver.receiveSms(new SmsEndpoint(endpointId), message, date);
		}
	}

	private void notifyReceiveMessageError(String endpointId, String message, Date date) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Receive msg with error from: " + endpointId + " message: " + message );
		}
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifyReceiveMessageError(endpointId, message, date);
		}
	}
	
	private void notifySendMessageError(String endpointId, String message) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Send msg with error to: " + endpointId + " message: " + message );
		}
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifySendMessageError(endpointId, message);
		}
	}

	private void notifySendMessage(String endpointId, String message) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Send msg to: " + endpointId + " message: " + message );
		}
		if(this.smsConnectionNotification != null){
			this.smsConnectionNotification.notifySendMessage(endpointId, message);
		}
	}

	@Override
	public void process(String gatewayId, MessageTypes msgType, InboundMessage msg) {
		if(msg == null){
			return;
		}
		
		if(msg instanceof InboundBinaryMessage){
			InboundBinaryMessage binaryMessage = (InboundBinaryMessage) msg;
			byte[] bytes = binaryMessage.getDataBytes();
			String text = new String(bytes);
			processMessage(msg.getOriginator(), text, msg.getDate(), msg);
		} else {
			processMessage(msg.getOriginator(), msg.getText(), msg.getDate(), msg);
		}		
	}
	
	private void processMessage(String endpointId, String text, Date date, InboundMessage smsMessage) {
		if(!(text == null || text.isEmpty())){
			try{
				this.notifyReceiveMessage(
						endpointId, 
						text,
						date);
					
				this.service.deleteMessage(smsMessage);
			} catch(RuntimeException re){
				LOGGER.info(re.getMessage());
				this.notifyReceiveMessageError(
						endpointId, 
						text,
						date);
			} catch(Exception e){
				LOGGER.info(e.getMessage());
				this.notifyReceiveMessageError(
						endpointId, 
						text,
						date);
			}
		}
	}

	@Override
	public void process(String gatewayId, OutboundMessage msg) {
		if(msg instanceof OutboundBinaryMessage){
			OutboundBinaryMessage binaryMessage = (OutboundBinaryMessage) msg;
			byte[] bytes = binaryMessage.getDataBytes();
			String text = new String(bytes);
			this.notifySendMessage(msg.getFrom(), text);	
		} else {
			this.notifySendMessage(msg.getFrom(), msg.getText());
		}	
	}

	@Override
	public int getMaxMessageLenght() {
		return this.maxMessageLenght;
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
	public void send(List<String> messages, SmsEndpoint endpoint) {
		for (String message : messages) {
			this.send(endpoint.getEndpointId(), message, this.messageEncoding.isBynary());
		}		
	}

	public void readAll() {
		List<InboundMessage> msgList = new ArrayList<InboundMessage>();
		try {
			this.service.readMessages(msgList, MessageClasses.ALL);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		for (InboundMessage msg : msgList) {
			process(msg.getGatewayId(), msg.getType(), msg);
		}
	}
}
