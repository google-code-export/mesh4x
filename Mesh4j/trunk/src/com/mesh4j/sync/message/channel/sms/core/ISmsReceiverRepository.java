package com.mesh4j.sync.message.channel.sms.core;

import java.util.List;

import com.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsReceiverRepository {

	void writeIncomming(List<SmsMessageBatch> batches);
	void writeIncommingCompleted(List<SmsMessageBatch> batches);
	void writeIncommingDiscarded(List<DiscardedBatchRecord> records);

	void write(List<SmsMessageBatch> ongoingBatches,
			List<SmsMessageBatch> completedBatches,
			List<DiscardedBatchRecord> discardedBatches);

	List<SmsMessageBatch> readIncomming();
	List<SmsMessageBatch> readIncommingCompleted();
	List<DiscardedBatchRecord> readIncommingDicarded();

}
