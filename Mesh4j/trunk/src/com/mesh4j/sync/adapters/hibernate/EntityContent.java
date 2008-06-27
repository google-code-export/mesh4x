package com.mesh4j.sync.adapters.hibernate;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.IContent;

public class EntityContent extends Content{
	
	// MODEL VARIABLESs
	private String entityName;
	
	// BUSINESS METHODS
	public EntityContent(Element payload, String entityName, String entityID) {
		super(payload, entityID);
		this.entityName = entityName;
	}

	public EntityContent clone(){
		return new EntityContent(this.getPayload().createCopy(), entityName, this.getId());
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof EntityContent){
        		EntityContent otherXmlItem = (EntityContent) obj;
        		return
        			super.equals(obj) && this.getType().equals(otherXmlItem.getType());
        	} else {
        		return super.equals(obj);
        	}
        }
        return false;
    }

    public int hashCode(){		
		return super.hashCode() + this.entityName.hashCode();
    }

	public String getType() {
		return entityName;
	}
	
	public static EntityContent normalizeContent(IContent content, String entityNode, String entityIDNode){
		if(content instanceof EntityContent){
			EntityContent entity = (EntityContent)content;
			entity.refreshVersion();
			return entity;
		}else{
			Element entityElement = null;
			if(entityNode.equals(content.getPayload().getName())){
				entityElement = content.getPayload();
			}else{
				entityElement = content.getPayload().element(entityNode);
			}
			if(entityElement == null){
				return null;
			}else{
				Element idElement = entityElement.element(entityIDNode);
				if(idElement == null){
					return null;
				} else {
					String entityID = idElement.getText();
					return new EntityContent(entityElement, entityNode, entityID);
				}
			}
		}
	}

	@Override
	public void addToFeedPayload(Element rootPayload){
			
		Element titleElement = DocumentHelper.createElement(ISyndicationFormat.SX_ELEMENT_ITEM_TITLE);
		titleElement.setText(this.entityName);
		rootPayload.add(titleElement);
		
		Element descriptionElement = DocumentHelper.createElement(ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION);
		descriptionElement.setText("Entity id: " + this.getId() + " version: " + this.getVersion());
		rootPayload.add(descriptionElement);
		
		rootPayload.add(this.getPayload().createCopy());
	}
}
