package com.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mesh4j.sync.message.channel.sms.ISmsConnection;
import com.mesh4j.sync.message.channel.sms.ISmsSender;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.validations.Guard;

public class SmsSender implements ISmsSender{

	private final static Object SEMAPHORE = new Object();
	
	// MODEL 
	private ISmsConnection smsConnection;
	private Map<String, SmsMessageBatch> ongoingBatches = Collections.synchronizedMap(new HashMap<String, SmsMessageBatch>());
	private Map<String, SmsMessageBatch> ongoingCompletedBatches = Collections.synchronizedMap(new HashMap<String, SmsMessageBatch>());
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

		synchronized (SEMAPHORE) {
			if(ackIsRequired){
				this.ongoingBatches.put(batch.getId(), batch);
			}else{
				this.ongoingCompletedBatches.put(batch.getId(), batch);
			}
		}
		send(batch.getMessages(), batch.getEndpoint());
	}

	@Override
	public void send(List<SmsMessage> smsMessages, SmsEndpoint endpoint) {
		Guard.argumentNotNull(smsMessages, "smsMessages");
		
		for (SmsMessage smsMessage : smsMessages) {
			Guard.argumentNotNull(smsMessage, "smsMessage");
			Guard.argumentNotNullOrEmptyString(smsMessage.getText(), "smsMessage.text");
			Guard.argumentNotNull(endpoint, "endpoint");
		}
		
		ArrayList<String> msgTexts = new ArrayList<String>();
		for (SmsMessage smsMessage : smsMessages) {
			smsMessage.setLastModificationDate(new Date());
			msgTexts.add(smsMessage.getText());
		}
		
		this.smsConnection.send(msgTexts, endpoint);
		this.persistChanges();
	}
	
	public void receiveACK(String batchId) {
		synchronized (SEMAPHORE) {
			SmsMessageBatch batch = this.ongoingBatches.get(batchId);
			if(batch != null){
				this.ongoingCompletedBatches.put(batch.getId(), batch);
				this.ongoingBatches.remove(batchId);
				this.persistChanges();
			}
		}
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
	
	public List<SmsMessageBatch> getOngoingCompletedBatches() {
		return new ArrayList<SmsMessageBatch>(this.ongoingCompletedBatches.values());
	}
	
	private synchronized void persistChanges() {
		if(this.repository != null){
			this.repository.writeOutcomming(this.getOngoingBatches());
			this.repository.writeOutcommingCompleted(this.getOngoingCompletedBatches());
		}
	}
	
	private void initialize() {
		if(this.repository != null){
			synchronized (SEMAPHORE) {
				List<SmsMessageBatch> outcomming = this.repository.readOutcomming();
				for (SmsMessageBatch smsMessageBatch : outcomming) {
					this.ongoingBatches.put(smsMessageBatch.getId(), smsMessageBatch);
				}
				
				List<SmsMessageBatch> outcommingCompleted = this.repository.readOutcommingCompleted();
				for (SmsMessageBatch smsMessageBatch : outcommingCompleted) {
					this.ongoingCompletedBatches.put(smsMessageBatch.getId(), smsMessageBatch);
				}
			}
		}		
	}

	@Override
	public void purgeBatches(String sessionId, int sessionVersion) {
		
		synchronized (SEMAPHORE) {
			
			List<SmsMessageBatch> items = this.getOngoingBatches();
			for (SmsMessageBatch smsMessageBatch : items) {
				if(smsMessageBatch.getSessionId().equals(sessionId)){
					this.ongoingBatches.remove(smsMessageBatch.getId());
				}
			}
	
			items = this.getOngoingCompletedBatches();
			for (SmsMessageBatch smsMessageBatch : items) {
				if(smsMessageBatch.getSessionId().equals(sessionId)){
					this.ongoingCompletedBatches.remove(smsMessageBatch.getId());
				}
			}
			
			this.persistChanges();
		}
	}
	
}
