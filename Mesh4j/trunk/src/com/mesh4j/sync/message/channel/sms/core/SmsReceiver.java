package com.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mesh4j.sync.message.channel.sms.ISmsChannel;
import com.mesh4j.sync.message.channel.sms.ISmsReceiver;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public class SmsReceiver implements ISmsReceiver {

	// TODO (JMT) MeshSMS: persist state in feed file
	
	// MODEL VARIABLES
	private  HashMap<String, SmsMessageBatch> completedBatches = new HashMap<String, SmsMessageBatch>();
	private  HashMap<String, SmsMessageBatch> ongoingBatches = new HashMap<String, SmsMessageBatch>();
	private  HashMap<String, DiscardedBatchRecord> discardedBatches = new HashMap<String, DiscardedBatchRecord>();

	private ISmsChannel smsChannel;
	
	// BUSINESS METHODS
	public SmsReceiver() {
		super();
	}
	
	public SmsReceiver(ISmsChannel smsChannel) {
		super();
		this.smsChannel = smsChannel;
	}

	public SmsReceiver receive(String endpoint, SmsMessage message) {
		return receive(new SmsEndpoint(endpoint), message);	
	}
	
	public SmsReceiver receive(SmsEndpoint endpoint, SmsMessage message) {
		// do we have the batch?
		String receivedMessageBatchId = MessageFormatter.getBatchId(message.getText());
		SmsMessageBatch batch = null;

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
			batch = new SmsMessageBatch(
				endpoint,
				protocolHeader, 
				receivedMessageBatchId,
				MessageFormatter.getBatchExpectedMessageCount(message.getText()));
			ongoingBatches.put(receivedMessageBatchId, batch);

			this.notifyBathACK(message);
		}

		int sequence = MessageFormatter.getBatchMessageSequenceNumber(message.getText());

		SmsMessage batchMsg = batch.getMessage(sequence);
		if (batchMsg == null) {
			batch.addMessage(sequence, message);
		} else {
			// validate the same
			if (!(message.getText().equals(batchMsg.getText()))) {
				discardBatch(receivedMessageBatchId);
			}
		}

		if (batch.isComplete()) {
			batch.reconstitutePayload();

			this.ongoingBatches.remove(batch.getId());
			this.completedBatches.put(batch.getId(), batch);
			
			this.notifyBathCompleted(batch);
		}
		return this;
	}
	
	private void notifyBathCompleted(SmsMessageBatch batch){
		if(this.smsChannel != null){
			this.smsChannel.receive(batch);
		}
	}

	private void notifyBathACK(SmsMessage message){
		if(this.smsChannel != null){
			this.smsChannel.receiveACK(MessageFormatter.getBatchACK(message.getText()));
		}
	}
	
	public SmsReceiver discardBatch(String ongoingBatchId) {
		discardBatch(ongoingBatchId, null);
		return this;
	}

	public SmsReceiver discardBatch(String ongoingBatchId, Exception reason) {
		SmsMessageBatch batch = this.ongoingBatches.get(ongoingBatchId);
		this.ongoingBatches.remove(ongoingBatchId);

		DiscardedBatchRecord discardedRecord = new DiscardedBatchRecord(batch,
				reason);
		this.discardedBatches.put(ongoingBatchId, discardedRecord);
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
	public void receiveSms(SmsEndpoint endpoint, String message) {
		SmsMessage smsMessage = new SmsMessage(message);
		this.receive(endpoint, smsMessage);		
	}

	public List<SmsMessageBatch> getOngoingBatches() {
		return new ArrayList<SmsMessageBatch>(this.ongoingBatches.values());
	}

	@Override
	public void setBatchReceiver(ISmsChannel smsChannel) {
		this.smsChannel = smsChannel;		
	}
}
