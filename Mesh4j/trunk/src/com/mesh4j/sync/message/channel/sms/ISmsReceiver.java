package com.mesh4j.sync.message.channel.sms;

import java.util.List;

import com.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsReceiver {

	void setBatchReceiver(ISmsChannel batchReceiver);

	int getOngoingBatchesCount();

	List<SmsMessageBatch> getOngoingBatches();
	
	void receiveSms(SmsEndpoint endpoint, String message);

	List<SmsMessageBatch> getCompletedBatches();

	List<DiscardedBatchRecord> getDiscardedBatches();

}
