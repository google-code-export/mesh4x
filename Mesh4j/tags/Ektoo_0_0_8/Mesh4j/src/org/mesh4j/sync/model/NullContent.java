package org.mesh4j.sync.model;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.validations.Guard;


public class NullContent implements IContent {
	
	public static final Element PAYLOAD = DocumentHelper.createElement("payload");
	private String id;

	public NullContent(String id)
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		this.id = id;
	}
	
	@Override
	public Element getPayload() {
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
	
	@Override
	public int getVersion() {
		return this.getPayload().asXML().hashCode();
	}
}
