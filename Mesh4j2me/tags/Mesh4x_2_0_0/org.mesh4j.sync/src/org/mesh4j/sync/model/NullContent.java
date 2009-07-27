package org.mesh4j.sync.model;

import java.io.Writer;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.validations.Guard;


public class NullContent implements IContent {

	public static final String PAYLOAD = "<payload/>";
	
	private String id;

	public NullContent(String id)
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		this.id = id;
	}
	
	public String getPayload() {
		return PAYLOAD;
	}
	
	public IContent clone(){
		return this;
	}
	
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj != null)
		{
			if(!(obj instanceof NullContent)) {
				return false;
			} else {
				NullContent nullModel = (NullContent) obj;
				return this.id.equals(nullModel.getId());
			}
		}
		return false;
	}

	public int hashCode()
	{
       return id.hashCode();
	}

	public String getId() {
		return id;
	}
	
	public int getVersion() {
		return this.id.hashCode();
	}

	public void addToFeedPayload(Writer writer, Item item, ISyndicationFormat format) throws Exception{
		if(item.getSync().isDeleted()){
			format.addFeedItemTitleElement(writer, "--DELETED--");
		} else {
			format.addFeedItemTitleElement(writer, "--UNKNOWN--");
		}
		format.addFeedItemDescriptionElement(writer, "Id: " + this.getId() + " version: " + this.getVersion());	
	}
}
