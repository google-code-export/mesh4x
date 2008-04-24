package com.feed.sync.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.feed.sync.utils.DateHelper;

public class Sharing  implements Cloneable{

	// MODEL VARIABLES
	private String since;
	private String until;
	private Date expires;
	private ArrayList<Related> related = new ArrayList<Related>();
	
	// BUSINESS METHODS
	public String getSince() {
		return since;
	}
	public String getUntil() {
		return until;
	}
	public Date getExpires() {
		return expires;
	}
	public List<Related> getRelated() {		
		return related;
	}
	
	public void setSince(String since) {
		this.since = since;
	}
	public void setUntil(String until) {
		this.until = until;
	}
	public void setExpires(Date expires) {
		this.expires = DateHelper.normalize(expires);
	}
	public void setRelated(ArrayList<Related> related) {
		this.related = related;
	}

	
}
