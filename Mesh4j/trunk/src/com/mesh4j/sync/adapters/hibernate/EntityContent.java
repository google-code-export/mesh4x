package com.mesh4j.sync.adapters.hibernate;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.IIdentifiableContent;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.IContent;


public class EntityContent implements IIdentifiableContent{
	
	// MODEL VARIABLESs
	private String entityName;
	private String entityId;
	private Element payload;
	private int entityVersion;
	
	// BUSINESS METHODS
	public EntityContent(Element payload, String entityName, String entityID) {
		super();
		this.payload = payload;
		this.entityName = entityName;
		this.entityId = entityID;
		this.entityVersion = payload.asXML().hashCode();
	}

	public EntityContent clone(){
		return new EntityContent(payload, entityName, entityId);
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof EntityContent){
        		EntityContent otherXmlItem = (EntityContent) obj;
        		return
        			this.getType().equals(otherXmlItem.getType())
        			&& this.getId().equals(otherXmlItem.getId())
        			&& this.getVersion() == otherXmlItem.getVersion()
        			&& this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	} else if(obj instanceof IContent){
        		IContent otherXmlItem = (IContent) obj;
        		return this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String resultingPayload = payload.asXML();
		return this.entityName.hashCode() + this.entityId.hashCode() + this.entityVersion + resultingPayload.hashCode();
    }
	public void refreshEntityVersion() {
		this.entityVersion = this.getPayload().asXML().hashCode();		
	}

	public String getType() {
		return entityName;
	}

	public String getId() {
		return entityId;
	}

	public Element getPayload() {
		return payload;
	}

	public int getVersion() {
		return entityVersion;
	}
	
	public static EntityContent normalizeContent(IContent content, String entityNode, String entityIDNode){
		if(content instanceof EntityContent){
			EntityContent entity = (EntityContent)content;
			entity.refreshEntityVersion();
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

	public void addToFeedPayload(Element rootPayload){
			
		Element titleElement = DocumentHelper.createElement(ISyndicationFormat.SX_ATTRIBUTE_ITEM_TITLE);
		titleElement.setText(this.entityName);
		rootPayload.add(titleElement);
		
		Element descriptionElement = DocumentHelper.createElement(ISyndicationFormat.SX_ATTRIBUTE_ITEM_DESCRIPTION);
		descriptionElement.setText("Entity id: " + this.entityId + " version: " + this.entityVersion);
		rootPayload.add(descriptionElement);
		
		rootPayload.add(this.getPayload().createCopy());
	}
}
