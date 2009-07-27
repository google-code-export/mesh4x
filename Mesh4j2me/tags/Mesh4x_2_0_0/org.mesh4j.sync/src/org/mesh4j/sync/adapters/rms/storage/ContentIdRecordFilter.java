package org.mesh4j.sync.adapters.rms.storage;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class ContentIdRecordFilter implements RecordFilter {

	// MODEL VARIABLES
	private String id;
	private ContentObjectParser parser;
	
	// BUSINESS METHODS
	
	public ContentIdRecordFilter(ContentObjectParser parser, String id) {
		Guard.argumentNotNull(parser, "parser");
		Guard.argumentNotNullOrEmptyString(id, "id");
		this.parser = parser;
		this.id = id;
	}

	public boolean matches(byte[] data) {
		return this.parser.matchesById(data, this.id);
	}
}
