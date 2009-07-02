package org.mesh4j.sync.message.channel.sms.core.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.validations.Guard;

public class SmsMessageBatchIncompleteRecordFilter implements RecordFilter {

	// MODEL VARIABLES
	private SmsMessageBatchParser parser;
	
	// BUSINESS METHODS
	
	public SmsMessageBatchIncompleteRecordFilter(SmsMessageBatchParser parser) {
		Guard.argumentNotNull(parser, "parser");
		this.parser = parser;
	}

	public boolean matches(byte[] data) {
		SmsMessageBatch batch = (SmsMessageBatch)this.parser.bytesToObject(data);
		return !batch.isDiscarded() && !batch.isComplete();
	}
}
