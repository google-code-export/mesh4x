package org.mesh4j.sync.message.channel.sms;

import java.util.List;

import org.mesh4j.sync.message.encoding.IMessageEncoding;



public interface ISmsConnection {

	void setMessageReceiver(ISmsReceiver messageReceiver);

	void send(List<String> messages, SmsEndpoint endpoint);

	int getMaxMessageLenght();

	IMessageEncoding getMessageEncoding();

	void startUp();
	void shutdown();
	

}
