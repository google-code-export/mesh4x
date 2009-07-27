package org.mesh4j.sync.model;

import org.mesh4j.sync.validations.Guard;

public abstract class Content implements IContent{

	// MODEL VARIABLESs
	private String id;
	private String payload;
	private int version;
	
	// BUSINESS METHODS
	public Content() {
		super();
	}
	
	public Content(String payload, String id) {
		Guard.argumentNotNull(payload, "payload");
		Guard.argumentNotNullOrEmptyString(id, "id");
		
		this.payload = payload;
		this.id = id;
		this.refreshVersion();
	}

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
        			&& this.getPayload().equals(otherItem.getPayload());
        			// TODO (JMT) XMLHelper.canonicalizeXML(this.getPayload()).equals(XMLHelper.canonicalizeXML(otherItem.getPayload()));
        	}
        }
        return false;
    }

    public int hashCode()
    {
		return this.id.hashCode() + this.version;
    }
	public void refreshVersion() {
		this.version = this.payload.hashCode();		
	}

	public String getId() {
		return id;
	}
	
	protected void setId(String id) {
		this.id = id;
	}
	
	public String getPayload() {
		return payload;
	}

	public int getVersion() {
		return version;
	}
	
	protected void changePayload(String newPayload) {
		this.payload = newPayload;
		this.refreshVersion();
	}
}
