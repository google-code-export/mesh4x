package org.mesh4j.sync.message.channel.sms;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsBatchReceiver {

	public void receiveACK(String batchId);
	
	public void receive(SmsMessageBatch batch);
}
