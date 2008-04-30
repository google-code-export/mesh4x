package com.mesh4j.sync.filter;

import com.mesh4j.sync.Filter;
import com.mesh4j.sync.model.Item;

public class ConflictsFilter implements Filter<Item> {

	@Override
	public boolean applies(Item obj) {
		return obj.hasSyncConflicts();
	}

}
