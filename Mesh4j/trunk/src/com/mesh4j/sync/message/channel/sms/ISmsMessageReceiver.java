package com.mesh4j.sync.message.channel.sms;

public interface ISmsMessageReceiver {

	void receiveSms(SmsEndpoint endpoint, String smsMessageText);
}
