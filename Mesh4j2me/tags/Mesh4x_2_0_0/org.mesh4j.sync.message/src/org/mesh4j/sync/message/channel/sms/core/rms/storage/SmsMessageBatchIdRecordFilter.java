package org.mesh4j.sync.message.channel.sms.core.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class SmsMessageBatchIdRecordFilter implements RecordFilter {

	// MODEL VARIABLES
	private String id;
	private SmsMessageBatchParser parser;
	
	// BUSINESS METHODS
	
	public SmsMessageBatchIdRecordFilter(SmsMessageBatchParser parser) {
		Guard.argumentNotNull(parser, "parser");
		this.parser = parser;
	}

	public boolean matches(byte[] data) {
		return this.parser.matchByID(data, this.getId());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
