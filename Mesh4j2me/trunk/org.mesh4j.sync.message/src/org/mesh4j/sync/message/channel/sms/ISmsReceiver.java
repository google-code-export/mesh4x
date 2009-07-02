package org.mesh4j.sync.message.channel.sms;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsReceiver {

	void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver);

	void receiveSms(SmsEndpoint endpoint, String message, Date date);

	void purgeBatches(String sessionId, int sessionVersion);

	Vector<SmsMessageBatch> getIncompleteIncommingBatches();

}
