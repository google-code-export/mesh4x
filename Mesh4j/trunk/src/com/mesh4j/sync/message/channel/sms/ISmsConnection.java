package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.encoding.IMessageEncoding;


public interface ISmsConnection {

	void registerSmsReceiver(ISmsReceiver messageReceiver);

	void send(SmsEndpoint endpoint, String messageText);

	int getMaxMessageLenght();

	IMessageEncoding getMessageEncoding();

}
