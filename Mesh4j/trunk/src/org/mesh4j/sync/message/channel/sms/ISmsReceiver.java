package org.mesh4j.sync.message.channel.sms;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public interface ISmsReceiver {

	void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver);

	int getOngoingBatchesCount();

	List<SmsMessageBatch> getOngoingBatches();
	
	void receiveSms(SmsEndpoint endpoint, String message, Date date);

	List<SmsMessageBatch> getCompletedBatches();

	List<DiscardedBatchRecord> getDiscardedBatches();

	void purgeBatches(String sessionId, int sessionVersion);

}
