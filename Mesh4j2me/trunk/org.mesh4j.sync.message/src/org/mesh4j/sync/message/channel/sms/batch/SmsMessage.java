package org.mesh4j.sync.message.channel.sms.batch;

import java.util.Date;

public class SmsMessage {

	// MODEL VARIABLE
	private String text;
	private Date lastModificationDate;

	// BUSINESS METHODS
	public SmsMessage(String text) {
		this(text, new Date());
	}

	public SmsMessage(String text, Date date) {
		super();
		this.text = text;
		this.lastModificationDate = date;
	}

	public String getText() {
		return text;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}
	
	public void setLastModificationDate(Date date) {
		this.lastModificationDate = date;
	}

	public SmsMessage setText(String text) {
		this.text = text;
		return this;
	}
}
