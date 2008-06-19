package com.mesh4j.sync.message.channel.sms;


public interface ISmsConnection {

	void registerSmsMessageReceiver(ISmsMessageReceiver messageText);

	void send(SmsEndpoint endpoint, String messageText);

	int getMaxMessageLenght();

}
