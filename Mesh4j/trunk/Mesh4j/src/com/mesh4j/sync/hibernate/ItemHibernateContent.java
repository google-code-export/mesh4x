package com.mesh4j.sync.hibernate;

import org.dom4j.Element;

import com.mesh4j.sync.model.Content;


public class ItemHibernateContent implements Content{

	// MODEL VARIABLES
	private Element payload;
	
	// BUSINESS METHODS
	
	public ItemHibernateContent(Element payload) {
		this.payload = payload;
	}
	
	public Element getPayload() {
		return payload;
	}
	
	
	public ItemHibernateContent clone(){
		return new ItemHibernateContent(payload);
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof Content){
        		Content otherXmlItem = (Content) obj;
	            return this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String resultingPayload = payload.asXML();
		return resultingPayload.hashCode();
    }

	public static Element normalizeContent(String entityName, Content content) {
		if(content instanceof ItemHibernateContent){
			return content.getPayload();
		}else{
			if(entityName.equals(content.getPayload().getName())){
				return content.getPayload();
			}else{
				Element entityElement = content.getPayload().element(entityName);
				return entityElement;
			}
		}
	}

}
