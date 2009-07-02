package org.mesh4j.sync.message.channel.sms;

import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public interface ISmsSender {

	void send(SmsMessageBatch batch);

	void send(SmsMessageBatch batch, Vector<SmsMessage> smsMessages, SmsEndpoint endpoint);
	
	void receiveACK(String batchId);

	SmsMessageBatch getBatch(String batchID);

	void purgeBatches(String sessionId, int sessionVersion);

	Vector<SmsMessageBatch> getPendingACKOutcommingBatches();

}
