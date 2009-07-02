package org.mesh4j.sync.adapters.rms.storage;

import java.util.Date;

import javax.microedition.rms.RecordFilter;

import org.mesh4j.sync.validations.Guard;

public class ContentSinceDateRecordFilter implements RecordFilter {

	// MODEL VARIABLES
	private Date sinceDate;
	private ContentObjectParser parser;
	
	// BUSINESS METHODS
	
	public ContentSinceDateRecordFilter(ContentObjectParser parser, Date sinceDate) {
		Guard.argumentNotNull(parser, "parser");
		this.parser = parser;
		this.sinceDate = sinceDate;
	}

	public boolean matches(byte[] data) {
		return this.parser.matchesByLastUpdateTime(data, this.sinceDate);
	}
}