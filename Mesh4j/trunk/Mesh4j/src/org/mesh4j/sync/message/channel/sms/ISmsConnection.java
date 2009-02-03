package org.mesh4j.sync.message.channel.sms;

import java.util.List;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.message.encoding.IMessageEncoding;



public interface ISmsConnection {

	void send(List<String> messages, SmsEndpoint endpoint);

	int getMaxMessageLenght();

	IMessageEncoding getMessageEncoding();

	void startUp();
	void shutdown();

	void registerMessageReceiver(IFilter<String> filter, ISmsReceiver messageReceiver);
	

}
