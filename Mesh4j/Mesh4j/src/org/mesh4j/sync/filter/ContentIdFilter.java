package org.mesh4j.sync.filter;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class ContentIdFilter implements IFilter<Item>{

	// MODEL VARIABLES
	private String id;
	
	// BUSINESS METHODS
	public ContentIdFilter(String id){
		Guard.argumentNotNullOrEmptyString(id, "id");
		
		this.id = id;
	}
	
	@Override
	public boolean applies(Item item) {
		return this.id.equals(item.getContent().getId());
	}

}
