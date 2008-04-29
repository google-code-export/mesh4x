package com.mesh4j.sync.filter;

import com.mesh4j.sync.Filter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.translator.MessageTranslator;

public class CompoundFilter implements Filter<Item> {

	// Model Variables
	private Filter<Item>[] filters;
	
	// Business Methods
	
	public CompoundFilter(Filter<Item> ... filters) {
		super();
		if(filters.length == 0){
			throw new IllegalArgumentException(MessageTranslator.translate("Arg_Empty_Filters"));
		}
		this.filters = filters;
	}

	@Override
	public boolean applies(Item item) {
		for (Filter<Item> filter : this.filters) {
			if(!filter.applies(item)){
				return false;
			}
		}
		return true;
	}

}
