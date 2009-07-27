package org.mesh4j.sync.message.channel.sms;

import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsChannel extends IChannel, ISmsBatchReceiver{

	public void sendAskForRetry(SmsMessageBatch batch);
	
	public void resend(SmsMessageBatch batch);

	public void send(SmsMessageBatch batch);

	public void resendPendingACKOutcommingBatches(long min);

	public void sendAskForRetryIncompleteIncommingBatches(long min);

}
