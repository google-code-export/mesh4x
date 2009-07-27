package org.mesh4j.sync.message.channel.sms.core;

import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public interface ISmsSenderRepository {

	void receiveACK(String batchId);

	void removeAll(String sessionId, int sessionVersion);

	SmsMessageBatch get(String batchID);

	void save(SmsMessageBatch batch);

	Vector<SmsMessageBatch> getPendingACKOutcommingBatches();

}
