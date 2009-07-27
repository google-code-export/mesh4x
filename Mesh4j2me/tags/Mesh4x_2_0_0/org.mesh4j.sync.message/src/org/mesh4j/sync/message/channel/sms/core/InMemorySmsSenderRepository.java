package org.mesh4j.sync.message.channel.sms.core;

import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

import de.enough.polish.util.HashMap;

public class InMemorySmsSenderRepository implements ISmsSenderRepository {

	private HashMap data = new HashMap();
	private Vector<String> acks = new Vector<String>();
	
	public void add(SmsMessageBatch batch, boolean ackIsRequired) {
		data.put(batch.getId(), batch);
		if(ackIsRequired){
			this.acks.addElement(batch.getId());
		}
	}

	public SmsMessageBatch get(String batchID) {
		return (SmsMessageBatch)data.get(batchID);
	}

	public void receiveACK(String batchId) {
		this.acks.removeElement(batchId);
	}

	public void removeAll(String sessionId, int sessionVersion) {
		data.clear();
		acks.removeAllElements();
	}

	public void save(SmsMessageBatch batch, boolean ackIsRequired) {
		save(batch);
		
		if(ackIsRequired){
			this.acks.addElement(batch.getId());
		} else {
			this.acks.removeElement(batch.getId());
		}
	}

	public void save(SmsMessageBatch batch) {
		data.put(batch.getId(), batch);		
	}

	public Vector<SmsMessageBatch> getPendingACKOutcommingBatches() {
		Vector<SmsMessageBatch> result = new Vector<SmsMessageBatch>();
		for (Object obj : data.values()) {
			result.addElement((SmsMessageBatch)obj);
		}
		return result;
	}
}
