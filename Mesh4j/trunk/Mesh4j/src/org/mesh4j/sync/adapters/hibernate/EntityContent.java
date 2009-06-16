package org.mesh4j.sync.adapters.hibernate;

import org.dom4j.Element;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;

public class EntityContent extends Content{
	
	// MODEL VARIABLESs
	private String entityName;
	private String entityIDNode;
	
	// BUSINESS METHODS
	public EntityContent(Element payload, String entityName, String entityIDNode, String entityID) {
		super(payload, entityID);
		this.entityName = entityName; 
		this.entityIDNode = entityIDNode;
	}

	public EntityContent clone(){
		return new EntityContent(this.getPayload().createCopy(), this.entityName, this.entityIDNode, this.getId());
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof EntityContent){
        		EntityContent otherContent = (EntityContent) obj;
        		return super.equals(obj) && this.getType().equals(otherContent.getType());
        	} else {
        		if(obj instanceof IContent){
        			EntityContent otherContent = EntityContent.normalizeContent((IContent)obj, this.entityName, this.entityIDNode);
        			return super.equals(otherContent);
        		} else{
        			return false;
        		}
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
					if(RDFSchema.isRDF(content.getPayload())){
						entityElement = content.getPayload();
					}
					return new EntityContent(entityElement, entityNode, entityIDNode, entityID);
				}
			}
		}
	}

}
