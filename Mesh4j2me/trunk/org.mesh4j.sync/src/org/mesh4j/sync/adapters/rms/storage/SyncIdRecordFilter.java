package org.mesh4j.sync.adapters.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class SyncIdRecordFilter implements RecordFilter {

	// MODEL VARIABLES
	private String syncId;
	private SyncInfoObjectParser parser;

	// BUSINESS METHODS

	public SyncIdRecordFilter(SyncInfoObjectParser parser, String syncId) {
		Guard.argumentNotNull(parser, "parser");
		Guard.argumentNotNullOrEmptyString(syncId, "syncId");
		this.parser = parser;
		this.syncId = syncId;
	}

	public boolean matches(byte[] data) {
		return this.parser.matchesBySyncId(data, this.syncId);
	}
}