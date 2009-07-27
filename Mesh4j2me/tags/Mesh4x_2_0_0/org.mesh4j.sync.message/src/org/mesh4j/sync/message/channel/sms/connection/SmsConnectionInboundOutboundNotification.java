package org.mesh4j.sync.message.channel.sms.connection;

import java.util.Date;

public class SmsConnectionInboundOutboundNotification implements ISmsConnectionInboundOutboundNotification{

	public final static SmsConnectionInboundOutboundNotification INSTANCE = new SmsConnectionInboundOutboundNotification();
	
	public void notifyReceiveMessage(String endpointId, String message,
			Date date) {
		System.out.println("Receive from: " + endpointId + " message: " + message);		
	}


	public void notifyReceiveMessageError(String endpointId, String message,
			Date date, String error) {
		System.out.println("Error - Receive from: " + endpointId + " message: " + message + " error: " + error);		
	}


	public void notifySendMessage(String endpointId, String message) {
		System.out.println("Send to: " + endpointId + " message: " + message);	
	}


	public void notifySendMessageError(String endpointId, String message, String error) {
		System.out.println("Error - Send to: " + endpointId + " message: " + message + " error: " + error);		
	}

}
