package com.mesh4j.sync.message.channel.sms;

import java.util.Date;

public class SmsMessage {

	// MODEL VARIABLE
	private String text;
	private Date received;

	// BUSINESS METHODS
	public SmsMessage(String text) {
		this(text, new Date());
	}

	public SmsMessage(String text, Date received) {
		super();
		this.text = text;
		this.received = received;
	}

	public String getText() {
		return text;
	}

	public Date getReceived() {
		return received;
	}

	public SmsMessage setReceived(Date date) {
		this.received = date;
		return this;
	}

	public SmsMessage setText(String text) {
		this.text = text;
		return this;
	}
}
