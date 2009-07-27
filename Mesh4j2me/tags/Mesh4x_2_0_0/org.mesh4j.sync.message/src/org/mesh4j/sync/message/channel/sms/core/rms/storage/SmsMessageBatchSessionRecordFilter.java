package org.mesh4j.sync.message.channel.sms.core.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class SmsMessageBatchSessionRecordFilter implements RecordFilter {

	// MODEL VARIABLES
	private SmsMessageBatchParser parser;
	private String sessionId;
	
	// BUSINESS METHODS
	public SmsMessageBatchSessionRecordFilter(SmsMessageBatchParser parser, String sessionId) {
		Guard.argumentNotNull(parser, "parser");
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		
		this.parser = parser;
		this.sessionId = sessionId;
	}

	public boolean matches(byte[] data) {
		return this.parser.matchBySessionID(data, this.sessionId);
	}


}
