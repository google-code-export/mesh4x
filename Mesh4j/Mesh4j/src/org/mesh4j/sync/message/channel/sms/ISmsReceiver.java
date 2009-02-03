package org.mesh4j.sync.message.channel.sms;

import java.util.Date;


public interface ISmsReceiver {
	
	void receiveSms(SmsEndpoint endpoint, String message, Date date);
}
