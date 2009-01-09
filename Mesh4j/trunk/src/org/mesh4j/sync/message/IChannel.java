package org.mesh4j.sync.message;

public interface IChannel {

	void registerMessageReceiver(IMessageReceiver messageReceiver);

	void send(IMessage message);
	
	void startUp();
	
	void shutdown();

}
