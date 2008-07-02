package com.mesh4j.sync.message.channel.sms;

import java.util.List;

import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsSender {

	void send(SmsMessageBatch batch, boolean ackRequired);

	void send(SmsMessage smsMessage, SmsEndpoint endpoint);
	
	void receiveACK(String batchId);

	int getOngoingBatchesCount();

	List<SmsMessageBatch> getOngoingBatches();

	SmsMessageBatch getOngoingBatch(String batchID);

}
