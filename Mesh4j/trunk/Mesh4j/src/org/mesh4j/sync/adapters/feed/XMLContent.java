package org.mesh4j.sync.adapters.feed;

import org.dom4j.Element;
import org.mesh4j.sync.model.Content;


public class XMLContent extends Content {

	// MODEL VARIABLES
	private String title;
	private String description;
	private String link;
	
	// BUSINESS METHODS
	public XMLContent(String id, String title, String description, Element payload) {
		this(id, title, description, "", payload);
	}
	
	public XMLContent(String id, String title, String description, String link, Element payload) {
		super(payload, id);
		this.title = title;
		this.description = description;
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getLink() {
		return link;
	}
	
	public XMLContent clone(){
		return new XMLContent(this.getId(), title, description, link, this.getPayload().createCopy());		
	}


    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof XMLContent){
	        	XMLContent otherXmlItem = (XMLContent) obj;
	            return super.equals(obj) &&
	                (this.getTitle() == null && otherXmlItem.getTitle() == null ||
		                	this.getTitle() != null && this.getTitle().equals(otherXmlItem.getTitle())) &&              
	                (this.getDescription() == null && otherXmlItem.getDescription() == null ||
	                	this.getDescription() != null && this.getDescription().equals(otherXmlItem.getDescription())) &&
                	(this.getLink() == null && otherXmlItem.getLink() == null ||
	                	this.getLink() != null && this.getLink().equals(otherXmlItem.getLink()));
        	} else{
        		return super.equals(obj);
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String result = ((title != null) ? title : "") + ((description != null) ? description : "") + ((link != null) ? link : "");
		return super.hashCode() + result.hashCode();
    }

	
	public void setDescription(String description) {
		this.description = description;
	}
}

