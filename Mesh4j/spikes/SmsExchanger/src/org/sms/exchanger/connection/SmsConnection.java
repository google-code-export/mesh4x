package org.sms.exchanger.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sms.exchanger.message.repository.Message;
import org.smslib.GatewayException;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundBinaryMessage;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.modem.SerialModemGateway;

public class SmsConnection implements ISmsConnection, IInboundMessageNotification, IOutboundMessageNotification, IGatewayStatusNotification {

	// CONSTANTS 
	private final static Log LOGGER = LogFactory.getLog(SmsConnection.class);
	
	// MODEL VARIABLES
	private String gatewayId;
	private String comPort;
	private int baudRate;
	private String manufacturer;
	private String model;
	
	protected IMessageNotification messageNotification;
	private SerialModemGateway gateway; 
	protected Service srv;

	// BUSINESS METHODS
	public SmsConnection(String comPort, int baudRate, IMessageNotification messageNotification) {
		super();
		this.comPort = comPort;
		this.baudRate = baudRate;
		this.gatewayId = "smsGateway";
		this.manufacturer = "generic";
		this.model = "generic";
		this.messageNotification = messageNotification;
	}

	public void connect() throws Exception{
		this.gateway = new SerialModemGateway(this.gatewayId, this.comPort, this.baudRate, this.manufacturer, this.model);
		this.gateway.setInbound(true);
		this.gateway.setOutbound(true);
		this.gateway.setSimPin("0000");

		this.srv = new Service();
		this.srv.setInboundNotification(this);
		this.srv.setOutboundNotification(this);
		this.srv.setGatewayStatusNotification(this);
		this.srv.addGateway(this.gateway);
		this.srv.startService();
	}
	
	public void disconnect() throws Exception{
		this.srv.stopService();
		this.gateway.stopGateway();
	}
	
	public void sendMessage(Message message) throws Exception {
		OutboundMessage msg = new OutboundMessage(message.getNumber(), message.getText());
		msg.setStatusReport(true);
		boolean wasSent = this.srv.sendMessage(msg);
		if(wasSent){
			this.messageNotification.notifySentMessage(message);
		}
//		OutboundMessage msg = new OutboundMessage(message.getNumber(), message.getText());
//		this.srv.queueMessage(msg);
	}
	
	public void removeMessage(InboundMessage msg) throws Exception {
		this.srv.deleteMessage(msg);
	}

	public List<Message> getAllMessages() throws Exception{
		return readMessages(MessageClasses.ALL);
	}
	
	public List<Message> getReadMessages() throws Exception{
		return readMessages(MessageClasses.READ);
	}
	
	public List<Message> getUnreadMessages() throws Exception{
		return readMessages(MessageClasses.UNREAD);
	}
	
	private List<Message> readMessages(MessageClasses filter) throws TimeoutException, GatewayException, IOException, InterruptedException{
		List<InboundMessage> msgList = new ArrayList<InboundMessage>();
		this.srv.readMessages(msgList, filter);
		
		ArrayList<Message> result = new ArrayList<Message>();
		Message message;
		for (InboundMessage inboundMessage : msgList) {
			if(!(inboundMessage instanceof InboundBinaryMessage)){
				message = createMessage(inboundMessage.getOriginator(), inboundMessage.getText(), inboundMessage.getDate());
				result.add(message);
			}
		}
		return result;
	}
	
	protected Message createMessage(String originator, String text, Date date){
		return new Message(newGUID(), originator, text, date);
	}
	
	private String newGUID(){
		return UUID.randomUUID().toString();
	}
	
	@Override
	public void process(String gatewayId, MessageTypes msgType, InboundMessage msg) {
		if(MessageTypes.INBOUND.equals(msgType)){
			Message message = createMessage(msg.getOriginator(), msg.getText(), msg.getDate());
			boolean ok = this.messageNotification.notifyReceiveMessage(message);
			if(ok){
				try{
					this.removeMessage(msg);
				} catch(Exception e){
					LOGGER.error(e.getMessage(), e);
				}
			}
		}		
	}

	@Override
	public void process(String gatewayId, OutboundMessage msg) {
		if(MessageStatuses.SENT.equals(msg.getMessageStatus())){
			Message message = createMessage(msg.getRecipient(), msg.getText(), msg.getDate());
			this.messageNotification.notifySentMessage(message);
		}
	}

	@Override
	public void process(String gtwId, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
		if(GatewayStatuses.RESTART.equals(oldStatus) && GatewayStatuses.RUNNING.equals(newStatus)){
			this.messageNotification.notifyStartUpGateway();
		}
	}
}