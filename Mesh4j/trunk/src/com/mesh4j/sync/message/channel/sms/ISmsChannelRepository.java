package com.mesh4j.sync.message.channel.sms;

import java.util.List;

import com.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsChannelRepository {

	void writeOutcomming(List<SmsMessageBatch> batches);
	List<SmsMessageBatch> readOutcomming();

	void writeIncomming(List<SmsMessageBatch> batches);
	void writeIncommingCompleted(List<SmsMessageBatch> batches);
	void writeIncommingDiscarded(List<DiscardedBatchRecord> records);

	List<SmsMessageBatch> readIncomming();
	List<SmsMessageBatch> readIncommingCompleted();
	List<DiscardedBatchRecord> readIncommingDicarded();
	
	
}
