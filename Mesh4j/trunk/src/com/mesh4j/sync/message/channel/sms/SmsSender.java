package com.mesh4j.sync.message.channel.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SmsSender {

	// MODEL 
	private ISmsConnection smsConnection;
	private HashMap<String, SmsMessageBatch> ongoingBatches = new HashMap<String, SmsMessageBatch>();
	
	// BUSINESS METHODS
	public SmsSender(ISmsConnection smsConnection) {
		super();
		this.smsConnection = smsConnection;
	}

	public void send(SmsMessageBatch batch, boolean ackIsRequired) {
		for (SmsMessage smsMessage : batch.getMessages()) {
			if(ackIsRequired){
				this.ongoingBatches.put(batch.getId(), batch);
			}
			send(smsMessage, batch.getEndpoint());
		}
	}
	
	public void receiveACK(String batchId) {
		this.ongoingBatches.remove(batchId);		
	}

	public List<SmsMessageBatch> getOngoingBatches() {
		return new ArrayList<SmsMessageBatch>(this.ongoingBatches.values());
	}

	public int getOngoingBatchesCount() {
		return this.ongoingBatches.size();
	}

	public SmsMessageBatch getOngoingBatch(String batchID) {
		return this.ongoingBatches.get(batchID);
	}

	public void send(SmsMessage smsMessage, SmsEndpoint endpoint) {
		smsMessage.setLastModificationDate(new Date());
		this.smsConnection.send(endpoint, smsMessage.getText());
	}
}
