package com.mesh4j.sync.message.channel.sms.connection.inmemory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mesh4j.sync.message.channel.sms.SmsEndpoint;

public class SmsConnectionOutboundNotification implements ISmsConnectionOutboundNotification{

	private final static Log LOGGER = LogFactory.getLog(SmsConnectionOutboundNotification.class);
	
	@Override
	public void notifySend(SmsEndpoint endpointFrom, SmsEndpoint endpointTo, String message) {
		LOGGER.info("Send from: " + endpointFrom.getEndpointId() + " to: " + endpointTo.getEndpointId() + " message: " + message);	
	}

}
