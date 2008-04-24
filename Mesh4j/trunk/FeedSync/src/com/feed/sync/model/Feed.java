package com.feed.sync.model;

import com.feed.sync.validations.Guard;

public class Feed {

	// MODEL VARIABLES
	private String title;
	private String description;
	private String link;
	private Object payload;
	private Sharing sharing = new Sharing();
	
	// BUSINESS METHODS
	
	public Feed(String title, String linkUrl, String description) 
	{
		this(title, linkUrl, description, null);
	}

	public Feed(String title, String linkUrl, String description, Object payload)
	{
		Guard.argumentNotNullOrEmptyString(title, "title");
		Guard.argumentNotNullOrEmptyString(linkUrl, "linkUrl");
	
		if (payload == null)
		{
			//TODO (?): this.payload = new XmlDocument().CreateElement("payload");
			this.payload = payload;
		}
		else
		{
			this.payload = payload;
		}
	
		this.title = title;
		this.link = linkUrl;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public Sharing getSharing() {
		return sharing;
	}

	public void setSharing(Sharing sharing) {
		this.sharing = sharing;
	}

}
