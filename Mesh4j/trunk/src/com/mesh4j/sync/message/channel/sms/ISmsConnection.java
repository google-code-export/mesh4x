package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.IMessageReceiver;

public interface ISmsConnection {

	void registerMessageReceiver(IMessageReceiver messageReceiver);

	void send(String text);

	int getMaxMessageLenght();

}
