package com.mesh4j.sync.adapters.hibernate;

import org.dom4j.Element;

import com.mesh4j.sync.model.Content;


public class EntityContent implements Content{
	
	// MODEL VARIABLES
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
        			this.getEntityName().equals(otherXmlItem.getEntityName())
        			&& this.getEntityId().equals(otherXmlItem.getEntityId())
        			&& this.getEntityVersion() == otherXmlItem.getEntityVersion()
        			&& this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	} else if(obj instanceof Content){
        		Content otherXmlItem = (Content) obj;
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

	public String getEntityName() {
		return entityName;
	}

	public String getEntityId() {
		return entityId;
	}

	public Element getPayload() {
		return payload;
	}

	public int getEntityVersion() {
		return entityVersion;
	}
}
