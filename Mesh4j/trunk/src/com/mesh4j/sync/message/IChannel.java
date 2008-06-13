package com.mesh4j.sync.message;

public interface IChannel {

	void registerMessageReceiver(IMessageReceiver messageReceiver);

	void send(String message);

}
