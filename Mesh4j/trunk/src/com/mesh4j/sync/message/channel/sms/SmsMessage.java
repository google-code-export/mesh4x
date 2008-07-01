package com.mesh4j.sync.message.channel.sms;

import java.util.Date;

public class SmsMessage {

	// MODEL VARIABLE
	private String text;
	private Date creationDate;
	private Date lastModificationDate;

	// BUSINESS METHODS
	public SmsMessage(String text) {
		this(text, new Date());
	}

	public SmsMessage(String text, Date creationDate) {
		super();
		this.text = text;
		this.creationDate = creationDate;
		this.lastModificationDate = creationDate;
	}

	public String getText() {
		return text;
	}

	public Date getCreationDate() {
		return creationDate;
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
