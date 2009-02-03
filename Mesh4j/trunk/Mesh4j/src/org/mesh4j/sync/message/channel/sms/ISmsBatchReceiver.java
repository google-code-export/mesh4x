package org.mesh4j.sync.message.channel.sms;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsBatchReceiver {

	void receiveACK(String batchId);
	
	void receive(SmsMessageBatch batch);
}
