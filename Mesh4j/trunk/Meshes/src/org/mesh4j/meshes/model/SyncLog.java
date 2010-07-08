package org.mesh4j.meshes.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class SyncLog {
	
	private Date date;
	private boolean succeeded;
	private String message;
	
	public SyncLog() {
	}
	
	public SyncLog(boolean succeeded, String message) {
		date = new Date();
		this.succeeded = succeeded;
		this.message = message;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
