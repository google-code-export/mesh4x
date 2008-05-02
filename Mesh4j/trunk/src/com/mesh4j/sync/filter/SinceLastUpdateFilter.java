package com.mesh4j.sync.filter;

import java.util.Date;

import com.mesh4j.sync.Filter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.validations.Guard;

public class SinceLastUpdateFilter implements Filter<Item>{

	// Model variables
	private Date since;

	// Business methods
	public SinceLastUpdateFilter(Date since) {
		super();
		
		Guard.argumentNotNull(since, "since");
		this.since = since;
	}
	
	@Override
	public boolean applies(Item item) {
		if(item.getSync().getLastUpdate() == null || item.getSync().getLastUpdate().getWhen() == null){
			return true;
		} else {
			Date lastUpdate = item.getSync().getLastUpdate().getWhen();
			return this.since.compareTo(lastUpdate) <= 0;
		}
	}

}
