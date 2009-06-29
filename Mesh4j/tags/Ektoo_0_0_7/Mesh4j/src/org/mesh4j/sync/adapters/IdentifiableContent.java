package org.mesh4j.sync.adapters;

import org.dom4j.Element;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;

public class IdentifiableContent extends Content{
	
	// MODEL VARIABLESs
	private IIdentifiableMapping identifiableMapping;
	
	// BUSINESS METHODS
	public IdentifiableContent(Element payload, IIdentifiableMapping identifiableMapping, String id) {
		super(payload, id);
		this.identifiableMapping = identifiableMapping;
	}

	public IdentifiableContent clone(){
		return new IdentifiableContent(this.getPayload().createCopy(), this.identifiableMapping, this.getId());
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof IdentifiableContent){
        		IdentifiableContent otherContent = (IdentifiableContent) obj;
        		return super.equals(obj) && this.getType().equals(otherContent.getType());
        	} else {
        		if(obj instanceof IContent){
        			IdentifiableContent otherContent = IdentifiableContent.normalizeContent((IContent)obj, this.identifiableMapping);
        			return super.equals(otherContent);
        		} else{
        			return false;
        		}
        	}
        }
        return false;
    }

    public int hashCode(){		
		return super.hashCode() + this.identifiableMapping.getType().hashCode();
    }

	public String getType() {
		return this.identifiableMapping.getType();
	}
	
	public static IdentifiableContent normalizeContent(IContent content, IIdentifiableMapping identifiableMapping){
		if(content instanceof IdentifiableContent){
			IdentifiableContent entity = (IdentifiableContent)content;
			entity.refreshVersion();
			return entity;
		}else{
			Element entityElement = identifiableMapping.getTypeElement(content.getPayload());
			if(entityElement == null){
				return null;
			}else{
				String id = identifiableMapping.getId(content.getPayload());
				if(id == null){
					return null;
				} else {
					return new IdentifiableContent(entityElement, identifiableMapping, id);
				}
			}
		}
	}
}
