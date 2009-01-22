package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.core.AbstractSmsConnection;
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


public class SmsLibAsynchronousConnection extends AbstractSmsConnection implements IInboundMessageNotification, IOutboundMessageNotification {

	// MODEL VARIABLES
	private Service service;
			
	// BUSINESS METHODS
	public SmsLibAsynchronousConnection(String gatewayId, String comPort, int baudRate, String manufacturer, String model) {
		this(gatewayId, comPort, baudRate, manufacturer, model, 140, NonMessageEncoding.INSTANCE, null);
	}
	
	public SmsLibAsynchronousConnection(String gatewayId, String comPort, int baudRate, String manufacturer, String model, 
			int maxMessageLenght, IMessageEncoding messageEncoding, ISmsConnectionInboundOutboundNotification[] smsAware) {
		super(maxMessageLenght, messageEncoding, smsAware);
		initialize(gatewayId, comPort, baudRate, manufacturer, model);
	}

	public void initialize(String gatewayId, String comPort, int baudRate, String manufacturer, String model) {
		
		this.service = null;
		
		SerialModemGateway gateway = new SerialModemGateway(gatewayId, comPort, baudRate, manufacturer, model);
		
		gateway.setOutbound(true);
		gateway.setInbound(true);
		gateway.setSimPin("0000");
		
		this.service = new Service();
		this.service.setInboundNotification(this);
		this.service.setOutboundNotification(this);
		this.service.addGateway(gateway);
	}

	@Override
	public void startUp() {
		try {
			this.service.startService();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public void shutdown() {
		try {
			this.service.stopService();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@Override
	public void send(String smsNumber, String message, boolean isBinary) {
		OutboundMessage msg = null;
		if(isBinary){
			msg = new OutboundBinaryMessage(smsNumber, message.getBytes());
		} else {
			msg = new OutboundMessage(smsNumber, message);
		}
		this.service.queueMessage(msg);
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
			boolean ok = this.processReceivedMessage(endpointId, text, date);
			if(ok){
				try{
					this.service.deleteMessage(smsMessage);
				} catch(Throwable re){
					LOGGER.info(re.getMessage());
				}
			}
		}
	}

	@Override
	public void process(String gatewayId, OutboundMessage msg) {
		if(msg instanceof OutboundBinaryMessage){
			OutboundBinaryMessage binaryMessage = (OutboundBinaryMessage) msg;
			byte[] bytes = binaryMessage.getDataBytes();
			String text = new String(bytes);
			this.notifySendMessage(msg.getRecipient(), text);	
		} else {
			this.notifySendMessage(msg.getRecipient(), msg.getText());
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
