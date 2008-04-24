package com.feed.sync.model;

import com.feed.sync.validations.Guard;

public class Related implements Cloneable {
	
	// MODEL VARIABLES
	private String link;
	private String title;
	private RelatedType type;

	// BUSINESS METHODS
	public Related(String linkUrl, RelatedType type){
		this(linkUrl, type, null);
	}

	public Related(String linkUrl, RelatedType type, String title)
	{
		Guard.argumentNotNullOrEmptyString(linkUrl, "linkUrl");

		this.link = linkUrl;
		this.type = type;
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public String getTitle() {
		return title;
	}

	public RelatedType getType() {
		return type;
	}

}
