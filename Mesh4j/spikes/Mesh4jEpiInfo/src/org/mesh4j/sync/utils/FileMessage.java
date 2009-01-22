package org.mesh4j.sync.utils;

import java.util.Date;

public class FileMessage {

	// MODEL VARIABLE
	private String id;
	private String number;
	private String text;
	private Date date;
	
	// BUSINESS METHODS
	
	public FileMessage(String id, String number, String text, Date date) {
		super();
		this.id = id;
		this.number = number;
		this.text = text;
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public String getID() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public Date getDate() {
		return date;
	}
}
