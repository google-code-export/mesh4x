package com.mesh4j.sync.adapters.feed;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.model.IContent;

public class XMLContent implements IContent {

	// MODEL VARIABLES
	private String id;
	private String title;
	private String description;
	private Element payload;
	
	// BUSINESS METHODS
	public XMLContent(String id, String title, String description, Element payload) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.payload = payload;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	public Element getPayload() {
		return payload;
	}

	public String getTitle() {
		return title;
	}
	
	public XMLContent clone(){
		return new XMLContent(id, title, description, payload);		
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
	            return this.getId().equals(otherXmlItem.getId()) &&
	                this.getTitle().equals(otherXmlItem.getTitle()) &&
	                (this.getDescription() == null && otherXmlItem.getDescription() == null ||
	                	this.getDescription() != null && this.getDescription().equals(otherXmlItem.getDescription())) &&
	                this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String resultingPayload = id.toString() +
			((title != null) ? title : "") +
			((description != null) ? description : "") +
			payload.asXML();
		return resultingPayload.hashCode();
    }

	public static Element normalizeContent(IContent content) {
		if(content instanceof XMLContent){
			return content.getPayload();
		}
		Element payload = DocumentHelper.createElement("payload");
		payload.add(content.getPayload().detach());
		return payload;
	}


}
