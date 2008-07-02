package com.mesh4j.sync.message.channel.sms.batch;

public class DiscardedBatchRecord {

	// MODEL VARIABLES
	private SmsMessageBatch messageBatch;
	private Exception reason;

	// BUSINESS METHODS
	public DiscardedBatchRecord(SmsMessageBatch messageBatch, Exception reason) {
		super();
		this.reason = reason;
		this.messageBatch = messageBatch;
	}

	public SmsMessageBatch getMessageBatch() {
		return messageBatch;
	}

	public Exception getReason() {
		return reason;
	}
}