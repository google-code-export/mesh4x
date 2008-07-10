package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.modem.SerialModemGateway;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsReceiver;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;
import com.mesh4j.sync.validations.MeshException;

public class SmsLibConnection implements ISmsConnection, IRefreshTask {

	// CONSTANTS
	private final static Log LOGGER = LogFactory.getLog(SmsLibConnection.class);
	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private String gatewayId;
	private String comPort;
	private int baudRate;
	private String manufacturer;
	private String model;
	private int maxMessageLenght = 140;
	private IMessageEncoding messageEncoding;
	private ISmsReceiver messageReceiver;
	private IOutboundMessageNotification outboundMessageNotification;
	private IInboundMessageNotification inboundMessageNotification;
		
	// BUSINESS METHODS
	public SmsLibConnection(String gatewayId, String comPort, int baudRate,
			String manufacturer, String model, int maxMessageLenght, IMessageEncoding messageEncoding, IOutboundMessageNotification outboundMessageNotification, IInboundMessageNotification inboundMessageNotification, int refrehDelay) {
		super();
		this.gatewayId = gatewayId;
		this.comPort = comPort;
		this.baudRate = baudRate;
		this.manufacturer = manufacturer;
		this.model = model;
		this.maxMessageLenght = maxMessageLenght;
		this.messageEncoding = messageEncoding;
		this.outboundMessageNotification = outboundMessageNotification;
		this.inboundMessageNotification = inboundMessageNotification;
		
		if(refrehDelay > 0){
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), refrehDelay);
		}
	}
	
	@Override
	public int getMaxMessageLenght() {
		return this.maxMessageLenght;
	}

	@Override
	public IMessageEncoding getMessageEncoding() {
		return this.messageEncoding;
	}

	@Override
	public void registerSmsReceiver(ISmsReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	@Override
	public void send(List<String> messages, SmsEndpoint endpoint) {
		for (String smsText : messages) {
			try{
				this.sendMessage(endpoint.getEndpointId(), smsText);	
			} catch(MeshException e){
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	private void sendMessage(String smsNumber, String smsText) {
		synchronized (SEMAPHORE) {
			Service srv = null;
			SerialModemGateway gateway = null;
			try{			
				// Create new gateway
				gateway = new SerialModemGateway(this.gatewayId, this.comPort, this.baudRate, this.manufacturer, this.model);
				
				// Create new Service object - the parent of all and the main interface to you.
				srv = new Service();
							
				gateway.setInbound(true);
				gateway.setOutbound(true);
				gateway.setSimPin("0000");
				
				if(this.outboundMessageNotification != null){
					gateway.setOutboundNotification(this.outboundMessageNotification);
				}
				
				srv.addGateway(gateway);
				srv.startService();
				
				// Send a message synchronously.
				OutboundMessage msg = new OutboundMessage(smsNumber, smsText);
				srv.sendMessage(msg);
				
			} catch (Exception e) {
				throw new MeshException(e);
			} finally {
				if(srv != null){
					try {
						srv.stopService();
					} catch (Exception stopException) {
						throw new MeshException(stopException);
					}
				}
				if(gateway != null){
					try {
						gateway.stopGateway();
					} catch (Exception e1) {
						throw new MeshException(e1);
					}
				}
			}
		}
	}

	public void processReceivedMessages() {
		List<InboundMessage> messages = this.readAllMessages();
		for (InboundMessage smsMessage : messages) {
			processReceivedMessage(smsMessage);
		}
	}

	private void processReceivedMessage(InboundMessage smsMessage) {
		try{
			this.messageReceiver.receiveSms(
				new SmsEndpoint(smsMessage.getOriginator()), 
				smsMessage.getText(),
				smsMessage.getDate());
		} catch(RuntimeException re){
			LOGGER.info(re.getMessage());
		}
	}
	
	public List<InboundMessage> readAllMessages() {
		synchronized (SEMAPHORE) {
			List<InboundMessage> msgList = new ArrayList<InboundMessage>();		
			Service srv = null;
			SerialModemGateway gateway = null;
			try {
	
				// Create new gateway
				gateway = new SerialModemGateway(this.gatewayId, this.comPort, this.baudRate, this.manufacturer, this.model);
				
				// Create new Service object - the parent of all and the main interface to you.
				srv = new Service();
				
				// Do we want the Gateway to be used for Inbound messages? If not, SMSLib will never read messages from this Gateway.
				gateway.setInbound(true);
				
				// Do we want the Gateway to be used for Outbound messages? If not, SMSLib will never send messages from this Gateway.
				gateway.setOutbound(true);
				gateway.setSimPin("0000");
				
				if(this.inboundMessageNotification != null){
					gateway.setInboundNotification(this.inboundMessageNotification);
				}
				
				// Add the Gateway to the Service object.
				srv.addGateway(gateway);
				
				// Similarly, you may define as many Gateway objects, representing
				// various GSM modems, add them in the Service object and control
				// all of them.
				// Start! (i.e. connect to all defined Gateways)
				srv.startService();
				
				// Read Messages. The reading is done via the Service object and
				// affects all Gateway objects defined. This can also be more
				// directed to a specific Gateway
				srv.readMessages(msgList, MessageClasses.ALL);
			
			} catch (Exception e) {
				throw new MeshException(e);
			} finally {
				if(srv != null){
					try {
						srv.stopService();
					} catch (Exception e1) {
						throw new MeshException(e1);
					}
				}
				if(gateway != null){
					try {
						gateway.stopGateway();
					} catch (Exception e1) {
						throw new MeshException(e1);
					}
				}
			}
			return msgList;
		}
	}

	@Override
	public void refresh() {
		this.processReceivedMessages();
	}
		
}
