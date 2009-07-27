package org.mesh4j.sync.message.channel.sms.connection;

import java.util.Date;

public interface ISmsConnectionInboundOutboundNotification {

	public void notifyReceiveMessage(String endpointId, String message, Date date);

	public void notifyReceiveMessageError(String endpointId, String message, Date date, String error);

	public void notifySendMessageError(String endpointId, String message, String error);

	public void notifySendMessage(String endpointId, String message);


}
