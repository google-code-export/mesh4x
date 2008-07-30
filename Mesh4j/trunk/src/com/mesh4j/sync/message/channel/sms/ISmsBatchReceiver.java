package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsBatchReceiver {

	void receiveACK(String batchId);
	
	void receive(SmsMessageBatch batch);
}
