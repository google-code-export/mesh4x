package com.mesh4j.sync.filter;

import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.validations.Guard;

public class CompoundFilter implements IFilter<Item> {

	// Model Variables
	private IFilter<Item>[] filters;
	
	// Business Methods
	
	public CompoundFilter(IFilter<Item> ... filters) {
		super();
		if(filters.length == 0){
			Guard.throwsArgumentException("Arg_Empty_Filters");
		}
		this.filters = filters;
	}

	@Override
	public boolean applies(Item item) {
		for (IFilter<Item> filter : this.filters) {
			if(!filter.applies(item)){
				return false;
			}
		}
		return true;
	}

}
