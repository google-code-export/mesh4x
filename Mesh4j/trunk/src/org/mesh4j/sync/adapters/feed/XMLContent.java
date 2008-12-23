package org.mesh4j.sync.adapters.feed;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.Sync;


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

	
	@SuppressWarnings("unchecked")
	@Override
	public void addToFeedPayload(Sync sync, Element itemElement, ISyndicationFormat format){
		
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
			format.addFeedItemTitleElement(itemElement, myTitle);
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
			format.addFeedItemDescriptionElement(itemElement, myDesc);
		}
		
		if(this.getLink() != null && this.getLink().length() > 0){
			format.addFeedItemLinkElement(itemElement, this.getLink());
		}
		
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(this.getPayload().getName())){
			List<Element> payloadElements = this.getPayload().elements();
			
			if(payloadElements.size() > 1){
				format.addFeedItemPayloadElement(itemElement, this.getPayload().createCopy());
			} else if(payloadElements.size() == 1){
				format.addFeedItemPayloadElement(itemElement, payloadElements.get(0).createCopy());
			}
		} else {
			format.addFeedItemPayloadElement(itemElement, this.getPayload().createCopy());
		}
		
	}
}

//XMLContent contextAsXMLContent = (XMLContent) content;
//if(contextAsXMLContent.getTitle() != null && contextAsXMLContent.getTitle().trim().length() > 0){
//	Element titleElement = this.syndicationFormat.getFeedItemTitleElement(itemElement);
//	if(titleElement == null){
//		titleElement = this.syndicationFormat.addFeedItemTitleElement(itemElement);
//		titleElement.setText(contextAsXMLContent.getTitle());
//	} else {
//		titleElement.setText(contextAsXMLContent.getTitle());
//	}
//}else{
//	Element titleElement = this.syndicationFormat.getFeedItemTitleElement(itemElement);
//	if(titleElement != null){
//		itemElement.remove(titleElement);
//	}
//}
//
//if(contextAsXMLContent.getDescription() != null && contextAsXMLContent.getDescription().trim().length() > 0){
//	Element descriptionElement = this.syndicationFormat.getFeedItemDescriptionElement(itemElement);
//	if(descriptionElement == null){
//		descriptionElement = this.syndicationFormat.addFeedItemDescriptionElement(itemElement);
//		descriptionElement.setText(contextAsXMLContent.getDescription());
//		itemElement.add(descriptionElement);
//	} else {
//		descriptionElement.setText(contextAsXMLContent.getDescription());
//	}
//}else{
//	Element descriptionElement = this.syndicationFormat.getFeedItemDescriptionElement(itemElement);
//	if(descriptionElement != null){
//		itemElement.remove(descriptionElement);
//	}
//}
//
//if(contextAsXMLContent.getLink() != null && contextAsXMLContent.getLink().trim().length() > 0){
//	Element linkElement = this.syndicationFormat.getFeedItemLinkElement(itemElement);
//	if(linkElement == null){
//		linkElement = this.syndicationFormat.addFeedItemLinkElement(itemElement);
//		linkElement.setText(contextAsXMLContent.getLink());
//		itemElement.add(linkElement);
//	} else {
//		linkElement.setText(contextAsXMLContent.getLink());
//	}
//}else{
//	Element linkElement = this.syndicationFormat.getFeedItemLinkElement(itemElement);
//	if(linkElement != null){
//		itemElement.remove(linkElement);
//	}
//}
//}