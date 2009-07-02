package org.mesh4j.sync.message.core.repository.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class SyncSessionFilterBySourceAndEndpoint implements RecordFilter {

	// MODEL VARIABLES
	private String sourceId;
	private String endpointId;
	private SyncSessionObjectParser parser;
	
	// BUSINESS METHODS
	public  SyncSessionFilterBySourceAndEndpoint(String sourceId, String endpointId, SyncSessionObjectParser parser) {
		Guard.argumentNotNull(parser, "parser");
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNullOrEmptyString(endpointId, "endpointId");
		
		this.parser = parser;
		this.sourceId = sourceId;
		this.endpointId = endpointId;
	}

	public boolean matches(byte[] data) {
		return this.parser.matchesBySourceIdAndEndpointId(this.sourceId, this.endpointId, data);
	}

}
