package org.mesh4j.sync.message.channel.sms;

import java.util.List;

import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsReceiverAndBatchManager extends ISmsReceiver{

	void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver);

	int getOngoingBatchesCount();

	List<SmsMessageBatch> getOngoingBatches();
	
	List<SmsMessageBatch> getCompletedBatches();

	List<DiscardedBatchRecord> getDiscardedBatches();

	void purgeBatches(String sessionId, int sessionVersion);

	List<SmsMessageBatch> getOngoingBatches(String sessionId, int version);
	List<SmsMessageBatch> getCompletedBatches(String sessionId, int version);
}
