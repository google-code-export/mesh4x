package org.mesh4j.sync.message.channel.sms.connection;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmsConnectionInboundOutboundNotification implements ISmsConnectionInboundOutboundNotification{

	private final static Log LOGGER = LogFactory.getLog(SmsConnectionInboundOutboundNotification.class);
	
	@Override
	public void notifyReceiveMessage(String endpointId, String message,
			Date date) {
		LOGGER.info("Receive from: " + endpointId + " message: " + message);		
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message,
			Date date) {
		LOGGER.info("Error - Receive from: " + endpointId + " message: " + message);		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		LOGGER.info("Send to: " + endpointId + " message: " + message);	
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		LOGGER.info("Error - Send to: " + endpointId + " message: " + message);		
	}

}
