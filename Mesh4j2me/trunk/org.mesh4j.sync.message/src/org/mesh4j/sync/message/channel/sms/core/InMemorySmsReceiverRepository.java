package org.mesh4j.sync.message.channel.sms.core;

import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

import de.enough.polish.util.HashMap;

public class InMemorySmsReceiverRepository implements ISmsReceiverRepository {

	// MODEL VARIABLES
	private  HashMap data = new HashMap();
	
	// BUSINESS METHODS

	public SmsMessageBatch get(String batchId) {
		return (SmsMessageBatch)data.get(batchId);
	}

	public void removeAll(String sessionId, int sessionVersion) {
		data.clear();
	}

	public void save(SmsMessageBatch batch) {
		data.put(batch.getId(), batch);
	}

	public Vector<SmsMessageBatch> getIncompleteIncommingBatches() {
		Vector<SmsMessageBatch> result = new Vector<SmsMessageBatch>();
		for (Object obj : data.values()) {
			result.addElement((SmsMessageBatch)obj);
		}
		return result;
	}


}
