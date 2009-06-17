package org.mesh4j.sync.model;

import org.dom4j.Element;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;


public abstract class Content implements IContent{

	// MODEL VARIABLESs
	private String id;
	private Element payload;
	private int version;
	
	// BUSINESS METHODS
	public Content(Element payload, String id) {
		Guard.argumentNotNull(payload, "payload");
		Guard.argumentNotNullOrEmptyString(id, "id");
		
		this.payload = payload;
		this.id = id;
		this.refreshVersion();
	}

	public abstract Content clone();
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof IContent){
        		IContent otherItem = (IContent) obj;
        		return
        			this.getId().equals(otherItem.getId())
        			&& this.getVersion() == otherItem.getVersion()
        			&& XMLHelper.canonicalizeXML(this.getPayload()).equals(XMLHelper.canonicalizeXML(otherItem.getPayload()));
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String payloadXML = XMLHelper.canonicalizeXML(payload);
		return this.id.hashCode() + this.version + payloadXML.hashCode();
    }
	public void refreshVersion() {
		String xml = XMLHelper.canonicalizeXML(this.getPayload());
		
		System.out.println("------------------");
		System.out.println("entity: "+ this.getPayload().getQName());
		System.out.println("payload: "+ this.getPayload().asXML());
		System.out.println("xml: "+ xml);
		System.out.println("hash: "+ xml.hashCode());
		System.out.println("asXML hash: "+ this.getPayload().asXML().hashCode());
		System.out.println("------------------");
		
		this.version = xml.hashCode();		
	}

	public String getId() {
		return id;
	}

	public Element getPayload() {
		return payload;
	}

	public int getVersion() {
		return version;
	}
}
