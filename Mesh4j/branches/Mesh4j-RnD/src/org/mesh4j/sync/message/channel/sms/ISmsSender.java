package org.mesh4j.sync.message.channel.sms;

import java.util.List;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsSender {

	void send(SmsMessageBatch batch, boolean ackRequired);

	void send(List<SmsMessage> smsMessages, SmsEndpoint endpoint);

	void send(SmsMessage smsMessage, SmsEndpoint endpoint);
	
	void receiveACK(String batchId);

	int getOngoingBatchesCount();

	List<SmsMessageBatch> getOngoingBatches();

	SmsMessageBatch getOngoingBatch(String batchID);

	void purgeBatches(String sessionId, int sessionVersion);

	void startUp();
	
	void shutdown();

	List<SmsMessageBatch> getOngoingBatches(String sessionId, int version);
	List<SmsMessageBatch> getCompletedBatches(String sessionId, int version);

}
