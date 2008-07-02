package com.mesh4j.sync.message.channel.sms;

import java.util.List;

import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsChannel extends IChannel{

	void receive(SmsMessageBatch batch);

	List<SmsMessageBatch> getIncommingBatches();

	void sendAskForRetry(SmsMessageBatch incommingBatch);

	void send(SmsMessageBatch batch, boolean ackIsRequired);

	void receiveACK(String batchId);

	List<SmsMessageBatch> getOutcommingBatches();

	void resend(SmsMessageBatch outcommingBatch);

}
