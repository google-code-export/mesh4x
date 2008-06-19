package com.mesh4j.sync.message.channel.sms;

public interface ISmsConnection {

	void registerSmsMessageReceiver(ISmsMessageReceiver messageText);

	void send(String smsNumber, String messageText);

	int getMaxMessageLenght();

}
