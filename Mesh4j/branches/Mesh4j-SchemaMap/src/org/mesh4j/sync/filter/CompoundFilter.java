package org.mesh4j.sync.filter;

import java.util.ArrayList;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class CompoundFilter implements IFilter<Item> {

	// Model Variables
	private ArrayList<IFilter<Item>> filters = new ArrayList<IFilter<Item>>();
	
	// Business Methods
	
	public CompoundFilter(IFilter<Item> ... allFilters) {
		super();
		if(allFilters == null || allFilters.length == 0){
			Guard.throwsArgumentException("Arg_Empty_Filters");
		}
		addFilters(allFilters);
	}
	
	public void addFilters(IFilter<Item> ... allFilters) {
		if(allFilters == null || allFilters.length == 0){
			return;
		}
		
		for (IFilter<Item> filter : allFilters) {
			this.filters.add(filter);
		}
	}

	@Override
	public boolean applies(Item item) {
		for (IFilter<Item> filter : this.filters) {
			if(filter != null && !filter.applies(item)){
				return false;
			}
		}
		return true;
	}

}
