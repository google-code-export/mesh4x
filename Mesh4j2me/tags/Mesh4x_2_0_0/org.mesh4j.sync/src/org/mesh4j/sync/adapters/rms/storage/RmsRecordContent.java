package org.mesh4j.sync.adapters.rms.storage;

import java.io.Writer;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class RmsRecordContent implements IContent {

	// MODEL VARIABLES
	private int recordId;
	private String syncId;
	private String payload;
	private int version;
	private String entityName;
	
	// BUSINESS METHODS
	public RmsRecordContent(String payload, String syncId, int recordId, String entityName) {
		Guard.argumentNotNull(payload, "payload");
		Guard.argumentNotNull(entityName, "entityName");

		this.recordId = recordId;
		this.syncId = syncId;
		this.payload = payload;
		this.entityName = entityName;
		
		this.refreshVersion();
	}

	public RmsRecordContent clone(){
		return new RmsRecordContent(new String(this.payload), this.syncId, this.recordId, this.entityName);
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof RmsRecordContent){
        		RmsRecordContent otherRmsRecordContent = (RmsRecordContent) obj;
        		return
        			this.entityName.equals(otherRmsRecordContent.entityName)
        			&& this.getVersion() == otherRmsRecordContent.getVersion()
        			&& this.getPayload().equals(otherRmsRecordContent.getPayload())
        			&& ((this.getId() == null && otherRmsRecordContent.getId() == null) ||
        					(this.getId() != null && otherRmsRecordContent.getId() != null && this.getId().equals(otherRmsRecordContent.getId())));
        			// TODO (JMT) XMLHelper.canonicalizeXML(this.getPayload()).equals(XMLHelper.canonicalizeXML(otherItem.getPayload()));
        	}else if(obj instanceof IContent){
        		IContent otherItem = (IContent) obj;
        		return
        			this.getVersion() == otherItem.getVersion()
        			&& this.getPayload().equals(otherItem.getPayload())
        			&& ((this.getId() == null && otherItem.getId() == null) ||
        					(this.getId() != null && otherItem.getId() != null && this.getId().equals(otherItem.getId())));
        			// TODO (JMT) XMLHelper.canonicalizeXML(this.getPayload()).equals(XMLHelper.canonicalizeXML(otherItem.getPayload()));
        	}
        }
        return false;
    }

    public int hashCode() {
		return (this.syncId == null ? 0 : this.syncId.hashCode()) + this.recordId + this.version + this.entityName.hashCode();
    }
    
	public void refreshVersion() {
		this.version = this.payload.hashCode();		
	}
	
	public void setSyncId(String syncId) {
		this.syncId = syncId;		
	}

	public int getRecordId() {
		return this.recordId;
	}

	public void setPayload(String newPayload) {
		this.payload = newPayload;
		this.refreshVersion();
	}

	public String getId() {
		return this.syncId;
	}
	
	public String getPayload() {
		return payload;
	}

	public int getVersion() {
		return version;
	}

	public void addToFeedPayload(Writer writer, Item item, ISyndicationFormat syndicationFormat) throws Exception {
		syndicationFormat.addFeedItemTitleElement(writer, this.entityName);
		syndicationFormat.addFeedItemDescriptionElement(writer, "Entity id: " + this.syncId + " version: " + this.getVersion());
		syndicationFormat.addFeedItemPayloadElement(writer, this.getPayload());
	}
}
