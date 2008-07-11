package com.mesh4j.sync.message.channel.sms.connection.inmemory;

import com.mesh4j.sync.message.channel.sms.SmsEndpoint;

public interface ISmsConnectionOutboundNotification {

	void notifySend(SmsEndpoint endpointFrom, SmsEndpoint endpointTo, String message);

}
