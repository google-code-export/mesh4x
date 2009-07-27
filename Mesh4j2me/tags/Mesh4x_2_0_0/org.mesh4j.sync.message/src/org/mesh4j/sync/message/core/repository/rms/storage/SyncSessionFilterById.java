package org.mesh4j.sync.message.core.repository.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class SyncSessionFilterById implements RecordFilter{

	// MODEL VARIABLES
	private String sessionId;
	private SyncSessionObjectParser parser;
	
	// BUSINESS METHODS
	public SyncSessionFilterById(String sessionId, SyncSessionObjectParser parser) {
		Guard.argumentNotNull(parser, "parser");
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		
		this.parser = parser;
		this.sessionId = sessionId;
	}

	public boolean matches(byte[] data) {
		String sessionIdData = this.parser.getSessionId(data);
		return this.sessionId.equals(sessionIdData);
	}

}
