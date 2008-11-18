package org.mesh4j.sync.adapters.feed;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;


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
		this.basicAddToFeedPayload(payload, null);
		this.refreshVersion();
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

	public static Element normalizeContent(IContent content) {
		Element payload = null;
		if(content instanceof XMLContent){
			payload = content.getPayload().createCopy();
		} else {
			payload = DocumentHelper.createElement("payload");
		}
		content.addToFeedPayload(payload);
		return payload;
	}
	
	@Override
	public void addToFeedPayload(Element rootPayload){
		basicAddToFeedPayload(rootPayload, "---");
	}
	
	private void basicAddToFeedPayload(Element rootPayload, String defaultValue){
		String myTitle = null;
		if(this.getTitle() == null || this.getTitle().length() == 0){
			if(defaultValue != null){
				myTitle = defaultValue;
			}
		}else{
			myTitle = this.getTitle();
		}
		
		
		if(myTitle != null){
			Element titleElement = rootPayload.element(ISyndicationFormat.SX_ELEMENT_ITEM_TITLE);
			if(titleElement == null){
				titleElement = rootPayload.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_TITLE);
			}
			titleElement.setText(myTitle);
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
			Element descriptionElement = rootPayload.element(ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION);
			if(descriptionElement == null){
				descriptionElement = rootPayload.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION);
			}
			descriptionElement.setText(myDesc);
		}
		
		if(this.getLink() != null && this.getLink().length() > 0){
			Element linkElement = rootPayload.element(ISyndicationFormat.SX_ELEMENT_ITEM_LINK);
			if(linkElement == null){
				linkElement = rootPayload.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_LINK);
			}
			linkElement.setText(this.getLink());
		}
		
	}


}
