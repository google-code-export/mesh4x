package com.mesh4j.sync.filter;

import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.model.Item;

public class ConflictsFilter implements IFilter<Item> {

	@Override
	public boolean applies(Item obj) {
		return obj.hasSyncConflicts();
	}

}
