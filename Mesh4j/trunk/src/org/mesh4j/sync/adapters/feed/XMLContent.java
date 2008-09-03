package org.mesh4j.sync.adapters.feed;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;


public class XMLContent extends Content {

	// MODEL VARIABLES
	private String title;
	private String description;
	
	// BUSINESS METHODS
	public XMLContent(String id, String title, String description, Element payload) {
		super(payload, id);
		this.title = title;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}
	
	public XMLContent clone(){
		return new XMLContent(this.getId(), title, description, this.getPayload().createCopy());		
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
	                	this.getDescription() != null && this.getDescription().equals(otherXmlItem.getDescription()));
        	} else{
        		return super.equals(obj);
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String result = ((title != null) ? title : "") + ((description != null) ? description : "");
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
		
		Element titleElement = DocumentHelper.createElement(ISyndicationFormat.SX_ELEMENT_ITEM_TITLE);
		if(this.getTitle() == null || this.getTitle().length() == 0){
			titleElement.setText("---");
		}else{
			titleElement.setText(this.getTitle());
		}
		rootPayload.add(titleElement);
		
		Element descriptionElement = DocumentHelper.createElement(ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION);
		if(this.getDescription() == null || this.getDescription().length() == 0){
			descriptionElement.setText("---");
		}else{
			descriptionElement.setText(this.getDescription());
		}
		
		rootPayload.add(descriptionElement);
	}

}
