package com.feed.sync.model;

import com.feed.sync.validations.Guard;

public class NullModelItem implements IModelItem {
	
	private String id;

	public NullModelItem(String id)
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		this.id = id;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Object getPayload() {
		return null;
	}

	@Override
	public String getTitle() {
		return null;
	}
	
	public IModelItem clone(){
		return new NullModelItem(this.id);
	}
	
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj != null)
		{
			if(!(obj instanceof NullModelItem)) {
				return false;
			} else {
				NullModelItem nullModel = (NullModelItem) obj;
				return this.id == nullModel.getId();
					// TODO (?): && this.hash == obj.hash;
			}
		}
		return false;
	}

	public int hashCode()
	{
//        if (hash != null)
//            return id.GetHashCode() ^ hash.GetHashCode();
//        else
            return id.hashCode();

	}
}
