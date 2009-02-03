package org.mesh4j.sync.message.channel.sms.batch;

import org.mesh4j.sync.validations.Guard;

public class DiscardedBatchRecord {

	// MODEL VARIABLES
	private SmsMessageBatch messageBatch;
	private Exception reason;

	// BUSINESS METHODS
	public DiscardedBatchRecord(SmsMessageBatch messageBatch, Exception reason) {
		Guard.argumentNotNull(messageBatch, "messageBatch");

		this.reason = reason;
		this.messageBatch = messageBatch;
	}

	public SmsMessageBatch getMessageBatch() {
		return messageBatch;
	}

	public Exception getReason() {
		return reason;
	}

	public String getSessionId(){ 
		return this.getMessageBatch().getSessionId();
	}

	public String getId() {
		return this.getMessageBatch().getId();
	}
}