package com.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsSender;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.validations.Guard;

public class SmsSender implements ISmsSender{

	// TODO (JMT) MeshSMS: persist state in feed file
	
	// MODEL 
	private ISmsConnection smsConnection;
	private HashMap<String, SmsMessageBatch> ongoingBatches = new HashMap<String, SmsMessageBatch>();
	
	// BUSINESS METHODS
	public SmsSender(ISmsConnection smsConnection) {
		Guard.argumentNotNull(smsConnection, "smsConnection");
		this.smsConnection = smsConnection;
	}

	public void send(SmsMessageBatch batch, boolean ackIsRequired) {
		Guard.argumentNotNull(batch, "batch");

		if(ackIsRequired){
			this.ongoingBatches.put(batch.getId(), batch);
		}
		for (SmsMessage smsMessage : batch.getMessages()) {
			send(smsMessage, batch.getEndpoint());
		}
	}

	public void send(SmsMessage smsMessage, SmsEndpoint endpoint) {
		Guard.argumentNotNull(smsMessage, "smsMessage");
		Guard.argumentNotNullOrEmptyString(smsMessage.getText(), "smsMessage.text");
		Guard.argumentNotNull(endpoint, "endpoint");
		
		smsMessage.setLastModificationDate(new Date());
		this.smsConnection.send(endpoint, smsMessage.getText());
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

}
