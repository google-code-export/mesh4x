package com.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mesh4j.sync.message.channel.sms.ISmsBatchReceiver;
import com.mesh4j.sync.message.channel.sms.ISmsReceiver;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public class SmsReceiver implements ISmsReceiver {

	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private  Map<String, SmsMessageBatch> completedBatches = Collections.synchronizedMap(new HashMap<String, SmsMessageBatch>());
	private  Map<String, SmsMessageBatch> ongoingBatches = Collections.synchronizedMap(new HashMap<String, SmsMessageBatch>());
	private  Map<String, DiscardedBatchRecord> discardedBatches = Collections.synchronizedMap(new HashMap<String, DiscardedBatchRecord>());

	private ISmsBatchReceiver smsBatchReceiver;
	private ISmsReceiverRepository repository;
	
	// BUSINESS METHODS
	public SmsReceiver() {
		super();
	}

	public SmsReceiver(ISmsReceiverRepository repository) {
		super();
		this.repository = repository;
		this.initialize();
	}

	public SmsReceiver receive(String endpoint, SmsMessage message) {
		return receive(new SmsEndpoint(endpoint), message);	
	}
	
	public SmsReceiver receive(SmsEndpoint endpoint, SmsMessage message) {

		SmsMessageBatch batch = null;
		boolean notifyACK = false;
		synchronized (SEMAPHORE) {
			
			// do we have the batch?
			String receivedMessageBatchId = MessageFormatter.getBatchId(message.getText());
	
			if (ongoingBatches.containsKey(receivedMessageBatchId)) {
				batch = ongoingBatches.get(receivedMessageBatchId);
			} else {
				// if it is a discarded batch exit
				if (this.discardedBatches.containsKey(receivedMessageBatchId))
					return this;
	
				// if it is a completed batch exit
				if (this.completedBatches.containsKey(receivedMessageBatchId))
					return this;
	
				String protocolHeader = MessageFormatter.getBatchProtocolHeader(message.getText());
				String sessionId = MessageFormatter.getBatchSessionId(message.getText());
				batch = new SmsMessageBatch(
					sessionId,
					endpoint,
					protocolHeader, 
					receivedMessageBatchId,
					MessageFormatter.getBatchExpectedMessageCount(message.getText()));
				ongoingBatches.put(receivedMessageBatchId, batch);
	
				notifyACK = true;
			}
	
			int sequence = MessageFormatter.getBatchMessageSequenceNumber(message.getText());
	
			SmsMessage batchMsg = batch.getMessage(sequence);
			if (batchMsg == null) {
				batch.addMessage(sequence, message);
			} else {
				// validate the same
				if (!(message.getText().equals(batchMsg.getText()))) {
					discardBatch(receivedMessageBatchId);
				} else {
					return this;
				}
			}
	
			if (batch.isComplete()) {
				batch.reconstitutePayload();
	
				this.ongoingBatches.remove(batch.getId());
				this.completedBatches.put(batch.getId(), batch);
			}
			this.persistChanges();
		}
		if(notifyACK){
			this.notifyBatchACK(message);
		}
		if(batch != null && batch.isComplete()){
			this.notifyBatchCompleted(batch);
		}
		return this;
	}
	
	private void notifyBatchCompleted(SmsMessageBatch batch){
		if(this.smsBatchReceiver != null){
			this.smsBatchReceiver.receive(batch);
		}
	}

	private void notifyBatchACK(SmsMessage message){
		if(this.smsBatchReceiver != null){
			this.smsBatchReceiver.receiveACK(MessageFormatter.getBatchACK(message.getText()));
		}
	}
	
	public SmsReceiver discardBatch(String ongoingBatchId) {
		discardBatch(ongoingBatchId, null);
		return this;
	}

	public SmsReceiver discardBatch(String ongoingBatchId, Exception reason) {
		synchronized (SEMAPHORE) {
			
			SmsMessageBatch batch = this.ongoingBatches.get(ongoingBatchId);
			this.ongoingBatches.remove(ongoingBatchId);
	
			DiscardedBatchRecord discardedRecord = new DiscardedBatchRecord(batch, reason);
			this.discardedBatches.put(ongoingBatchId, discardedRecord);
		}
		return this;
	}


	public int getCompletedBatchesCount() {
		return this.completedBatches.size();
	}

	public SmsMessageBatch getCompletedBatch(String id) {
		return this.completedBatches.get(id);
	}

	public int getOngoingBatchesCount() {
		return this.ongoingBatches.size();
	}

	public int getDiscardedBatchesCount() {
		return this.discardedBatches.size();
	}

	public DiscardedBatchRecord getDiscardedBatch(String id) {
		return this.discardedBatches.get(id);
	}

	public SmsMessageBatch getOngoingBatch(String id) {
		return this.ongoingBatches.get(id);
	}

	public SmsMessageBatch getFirstOngoingBatch() {
		if (this.ongoingBatches.size() == 0) {
			return null;
		}
		return (SmsMessageBatch) this.ongoingBatches.values().toArray()[0];
	}

	@Override
	public void receiveSms(SmsEndpoint endpoint, String message, Date date) {
		SmsMessage smsMessage = new SmsMessage(message, date);
		this.receive(endpoint, smsMessage);		
	}

	public List<SmsMessageBatch> getOngoingBatches() {
		return new ArrayList<SmsMessageBatch>(this.ongoingBatches.values());
	}

	@Override
	public void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver) {
		this.smsBatchReceiver = smsBatchReceiver;		
	}

	@Override
	public List<SmsMessageBatch> getCompletedBatches() {
		return new ArrayList<SmsMessageBatch>(this.completedBatches.values());
	}

	@Override
	public List<DiscardedBatchRecord> getDiscardedBatches() {
		return new ArrayList<DiscardedBatchRecord>(this.discardedBatches.values());
	}

	public void initialize() {
		if(this.repository != null){
			synchronized (SEMAPHORE) {
				
				List<SmsMessageBatch> incommingCompleted = this.repository.readIncommingCompleted();
				for (SmsMessageBatch smsMessageBatch : incommingCompleted) {
					this.completedBatches.put(smsMessageBatch.getId(), smsMessageBatch);
				}
		
				List<SmsMessageBatch> incommingOngoing = this.repository.readIncomming();
				for (SmsMessageBatch smsMessageBatch : incommingOngoing) {
					this.ongoingBatches.put(smsMessageBatch.getId(), smsMessageBatch);
				}
		
				List<DiscardedBatchRecord> incoingDiscarded = this.repository.readIncommingDicarded();
				for (DiscardedBatchRecord discardedBatchRecord : incoingDiscarded) {
					this.discardedBatches.put(discardedBatchRecord.getMessageBatch().getId(), discardedBatchRecord);
				}
			}
		}
	}

	private synchronized void persistChanges() {
		if(this.repository != null){
			this.repository.write(this.getOngoingBatches(), this.getCompletedBatches(), this.getDiscardedBatches());
		}
	}

	@Override
	public void purgeBatches(String sessionId, int sessionVersion) {
		synchronized (SEMAPHORE) {
			
			List<SmsMessageBatch> items = this.getCompletedBatches();
			for (SmsMessageBatch smsMessageBatch : items) {
				if(smsMessageBatch.getSessionId().equals(sessionId)){
					this.completedBatches.remove(smsMessageBatch.getId());
				}
			}
	
			items = this.getOngoingBatches();
			for (SmsMessageBatch smsMessageBatch : items) {
				if(smsMessageBatch.getSessionId().equals(sessionId)){
					this.ongoingBatches.remove(smsMessageBatch.getId());
				}
			}
	
			List<DiscardedBatchRecord> records = this.getDiscardedBatches();
			for (DiscardedBatchRecord discardedBatchRecord : records) {
				if(discardedBatchRecord.getSessionId().equals(sessionId)){
					this.discardedBatches.remove(discardedBatchRecord.getId());
				}
			}
			
			this.persistChanges();
		}
	}
}
