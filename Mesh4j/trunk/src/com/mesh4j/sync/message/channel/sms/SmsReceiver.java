package com.mesh4j.sync.message.channel.sms;

import java.util.Date;
import java.util.HashMap;

import com.mesh4j.sync.message.IMessageReceiver;

public class SmsReceiver implements IMessageReceiver {

	// MODEL VARIABLES
	private  HashMap<String, SmsMessageBatch> completedBatches = new HashMap<String, SmsMessageBatch>();
	private  HashMap<String, SmsMessageBatch> ongoingBatches = new HashMap<String, SmsMessageBatch>();
	private  HashMap<String, DiscardedBatchRecord> discardedBatches = new HashMap<String, DiscardedBatchRecord>();

	private IMessageReceiver messageReceiver;
	
	// BUSINESS METHODS

	public SmsReceiver() {
		super();
	}
	
	public SmsReceiver(IMessageReceiver messageReceiver) {
		super();
		this.messageReceiver = messageReceiver;
	}

	public SmsReceiver receive(SmsMessage message) {
		// do we have the batch?
		String receivedMessageBatchId = getBatchId(message.getText());
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

			batch = new SmsMessageBatch(receivedMessageBatchId,
					getExpectedMessageCountForBatch(message.getText()));
			ongoingBatches.put(receivedMessageBatchId, batch);

		}

		int sequence = getMessageSequenceNumber(message.getText());

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
		if(this.messageReceiver != null){
			this.messageReceiver.receiveMessage(batch.getPayload());
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

	@SuppressWarnings("unused")
	private int getMessageSequenceNumber(String messageText) {
		return new Integer(messageText.substring(8, 11));
	}

	private String getBatchId(String messageText) {
		return messageText.substring(0, 5);
	}

	private int getExpectedMessageCountForBatch(String messageText) {
		return new Integer(messageText.substring(5, 8));
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
	public void receiveMessage(String message) {
		SmsMessage smsMessage = new SmsMessage(message, new Date());
		this.receive(smsMessage);		
	}

}
