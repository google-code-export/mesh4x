package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.core.AbstractSmsConnection;
import org.mesh4j.sync.message.channel.sms.schedule.IRefreshTask;
import org.mesh4j.sync.message.channel.sms.schedule.RefreshSchedulerTimerTask;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.schedule.timer.TimerScheduler;
import org.mesh4j.sync.validations.MeshException;
import org.smslib.InboundBinaryMessage;
import org.smslib.InboundMessage;
import org.smslib.OutboundBinaryMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.modem.SerialModemGateway;


public class SmsLibConnection extends AbstractSmsConnection implements IRefreshTask {

	// CONSTANTS
	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private String gatewayId;
	private String comPort;
	private int baudRate;
	private String manufacturer;
	private String model;
	private int channelDelay = 1000;
		
	// BUSINESS METHODS
	public SmsLibConnection(String gatewayId, String comPort, int baudRate,
			String manufacturer, String model, int maxMessageLenght, IMessageEncoding messageEncoding, 
			ISmsConnectionInboundOutboundNotification[] smsAware,
			int refrehDelay, int channelDelay) {
		
		super(maxMessageLenght, messageEncoding, smsAware);
		
		this.gatewayId = gatewayId;
		this.comPort = comPort;
		this.baudRate = baudRate;
		this.manufacturer = manufacturer;
		this.model = model;
	
		if(channelDelay > 0){
			this.channelDelay = channelDelay;
		}
		
		if(refrehDelay > 0){
			TimerScheduler.INSTANCE.schedule(new RefreshSchedulerTimerTask(this), refrehDelay);
		}
	}

	@Override
	protected void send(String endpointId, String message, boolean bynary) {
		this.notifySendMessage(endpointId, message);

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
				
				srv.addGateway(gateway);
				srv.startService();
				
				// Send a message synchronously.
				OutboundMessage msg = null;
				if(bynary){
					msg = new OutboundBinaryMessage(endpointId, message.getBytes());
				} else {
					msg = new OutboundMessage(endpointId, message);
				}
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
		sleepChannelDelay();
	}

	public void processReceivedMessages() {
		List<InboundMessage> messages = this.readAllMessages();
		for (InboundMessage smsMessage : messages) {
			processReceivedMessage(smsMessage);
		}
	}

	private void processReceivedMessage(InboundMessage smsMessage) {
		if(smsMessage == null){
			return;
		}
		
		if(smsMessage instanceof InboundBinaryMessage){
			InboundBinaryMessage binaryMessage = (InboundBinaryMessage) smsMessage;
			byte[] bytes = binaryMessage.getDataBytes();
			String text = new String(bytes);
			processReceivedTextMessage(smsMessage.getOriginator(), text, smsMessage.getDate(), smsMessage);
		} else {
			processReceivedTextMessage(smsMessage.getOriginator(), smsMessage.getText(), smsMessage.getDate(), smsMessage);
		}
	}

	private void processReceivedTextMessage(String endpointId, String text, Date date, InboundMessage smsMessage) {
		if(!(text == null || text.isEmpty())){
			boolean ok = this.processReceivedMessage(endpointId, text, date);
			if(ok){
				try{
					sleepChannelDelay();					
					this.removeMessage(smsMessage);
				} catch(Throwable re){
					LOGGER.info(re.getMessage());
				}
			}
		}
	}
	
	private void removeMessage(InboundMessage msg) {
		synchronized (SEMAPHORE) {
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
				srv.deleteMessage(msg);
			
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
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("SMS - Read messaged from the modem...");
		}
		this.processReceivedMessages();
	}
	
	private void sleepChannelDelay() {
		try {
			Thread.sleep(this.channelDelay);
		} catch (InterruptedException e) {
			// nothing to do
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
}
