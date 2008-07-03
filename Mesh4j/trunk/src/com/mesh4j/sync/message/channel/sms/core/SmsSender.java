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

	// MODEL 
	private ISmsConnection smsConnection;
	private HashMap<String, SmsMessageBatch> ongoingBatches = new HashMap<String, SmsMessageBatch>();
	private ISmsSenderRepository repository;
	
	// BUSINESS METHODS
	public SmsSender(ISmsConnection smsConnection, ISmsSenderRepository repository) {
		Guard.argumentNotNull(smsConnection, "smsConnection");
		this.smsConnection = smsConnection;
		this.repository = repository;
		
		this.initialize();
	}
	
	public SmsSender(ISmsConnection smsConnection) {
		this(smsConnection, null);
	}

	public void send(SmsMessageBatch batch, boolean ackIsRequired) {
		Guard.argumentNotNull(batch, "batch");

		if(ackIsRequired){
			this.ongoingBatches.put(batch.getId(), batch);
		}
		for (SmsMessage smsMessage : batch.getMessages()) {
			send(smsMessage, batch.getEndpoint());
		}
		this.persistChanges();
	}

	@Override
	public void send(List<SmsMessage> smsMessages, SmsEndpoint endpoint) {
		Guard.argumentNotNull(smsMessages, "smsMessages");
		
		for (SmsMessage smsMessage : smsMessages) {
			send(smsMessage, endpoint);
		}
		this.persistChanges();
	}
	
	private void send(SmsMessage smsMessage, SmsEndpoint endpoint) {
		Guard.argumentNotNull(smsMessage, "smsMessage");
		Guard.argumentNotNullOrEmptyString(smsMessage.getText(), "smsMessage.text");
		Guard.argumentNotNull(endpoint, "endpoint");
		
		smsMessage.setLastModificationDate(new Date());
		this.smsConnection.send(endpoint, smsMessage.getText());
	}
	
	public void receiveACK(String batchId) {
		this.ongoingBatches.remove(batchId);
		this.persistChanges();
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
	
	private synchronized void persistChanges() {
		if(this.repository != null){
			this.repository.writeOutcomming(this.getOngoingBatches());
		}
	}
	
	private synchronized void initialize() {
		if(this.repository != null){
			List<SmsMessageBatch> outcomming = this.repository.readOutcomming();
			for (SmsMessageBatch smsMessageBatch : outcomming) {
				this.ongoingBatches.put(smsMessageBatch.getId(), smsMessageBatch);
			}
		}		
	}

}
