package org.mesh4j.sync.message.channel.sms;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsRetiesNotification {

	void notifySendAskForRetryIncompleteReceivedBatch(SmsMessageBatch batch);
	void notifySendAskForRetryError(SmsMessageBatch batch, String error);
	
	void notifyReSendIncompleteReceivedBatch(SmsMessageBatch batch);
	
	void notifyReSendBatch(SmsMessageBatch batch);
	void notifyReSendBatchError(SmsMessageBatch batch, String error);
	void notifyNoPendingACKOutcommingBatches();
	void notifyNoIncompleteIncommingBatches();
	
}
