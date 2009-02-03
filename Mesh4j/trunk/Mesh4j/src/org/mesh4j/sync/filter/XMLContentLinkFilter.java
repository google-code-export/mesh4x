package org.mesh4j.sync.filter;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class XMLContentLinkFilter implements IFilter<Item>{

	// MODEL VARIABLES
	private String link;
	
	// BUSINESS METHODS
	public XMLContentLinkFilter(String link){
		Guard.argumentNotNullOrEmptyString(link, "link");
		
		this.link = link;
	}
	
	@Override
	public boolean applies(Item item) {
		return this.link.equals(((XMLContent)item.getContent()).getLink());
	}

}
