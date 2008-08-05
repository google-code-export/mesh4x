package org.mesh4j.sync.message.channel.sms.connection;

import java.util.Date;

public interface ISmsConnectionInboundOutboundNotification {

	void notifyReceiveMessage(String endpointId, String message, Date date);

	void notifyReceiveMessageError(String endpointId, String message, Date date);

	void notifySendMessageError(String endpointId, String message);

	void notifySendMessage(String endpointId, String message);


}
