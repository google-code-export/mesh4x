package org.mesh4j.sync.adapters.feed;

import java.io.Writer;

import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.Item;


public class XMLContent extends Content {

	// MODEL VARIABLES
	private String title;
	private String description;
	private String link;
	
	// BUSINESS METHODS
	public XMLContent(String id, String title, String description, String link, String payload) {
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
	
	private String getLink() {
		return this.link;
	}
	
	public XMLContent clone(){
		return new XMLContent(this.getId(), title, description, link, this.getPayload());		
	}

	public void setTitle(String title) {
		this.title = title;
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

	public void addToFeedPayload(Writer writer, Item itemElement, ISyndicationFormat format) throws Exception{

		String defaultValue = "---";
		
		String myTitle = null;
		if(this.getTitle() == null || this.getTitle().length() == 0){
			if(defaultValue != null){
				myTitle = defaultValue;
			}
		}else{
			myTitle = this.getTitle();
		}
		
		if(myTitle != null){
			format.addFeedItemTitleElement(writer, myTitle);
		}

		String myDesc = null;
		if(this.getDescription() == null || this.getDescription().length() == 0){
			if(defaultValue != null){
				myDesc = defaultValue;
			}
		}else{
			myDesc = this.getDescription();
		}
		
		if(myDesc != null){
			format.addFeedItemDescriptionElement(writer, myDesc);
		}
		
		if(this.getLink() != null && this.getLink().length() > 0){
			format.addFeedItemLinkElement(writer, this.getLink());
		}
		
		format.addFeedItemPayloadElement(writer, this.getPayload());
	}
}
