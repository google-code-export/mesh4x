package org.mesh4j.sync.adapters.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class SyncInfoSinceEntityNameRecordFilter implements RecordFilter{

	// MODEL VARIABLES
	private String type;
	private SyncInfoObjectParser parser;

	// BUSINESS METHODS

	public SyncInfoSinceEntityNameRecordFilter(SyncInfoObjectParser parser, String type) {
		Guard.argumentNotNull(parser, "parser");
		this.parser = parser;
		this.type = type;	
	}

	public boolean matches(byte[] data) {
		return this.parser.matchesByType(data, this.type);
	}
}
