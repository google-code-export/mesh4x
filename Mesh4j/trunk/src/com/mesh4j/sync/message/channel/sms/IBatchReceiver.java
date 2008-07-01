package com.mesh4j.sync.message.channel.sms;

public interface IBatchReceiver {

	void receive(SmsMessageBatch batch);

	void receiveACK(String batchId);

}
