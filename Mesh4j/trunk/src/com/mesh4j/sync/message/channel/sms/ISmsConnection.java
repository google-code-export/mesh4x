package com.mesh4j.sync.message.channel.sms;

import java.util.List;

import com.mesh4j.sync.message.encoding.IMessageEncoding;


public interface ISmsConnection {

	void registerSmsReceiver(ISmsReceiver messageReceiver);

	void send(List<String> messages, SmsEndpoint endpoint);

	int getMaxMessageLenght();

	IMessageEncoding getMessageEncoding();

}
