package org.mesh4j.sync.message.channel.sms.core;

import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public interface ISmsReceiverRepository {

	void removeAll(String sessionId, int sessionVersion);

	void save(SmsMessageBatch batch);

	SmsMessageBatch get(String batchId);

	Vector<SmsMessageBatch> getIncompleteIncommingBatches();


}
