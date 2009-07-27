package org.mesh4j.sync.filter;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;

public class ConflictsFilter implements IFilter<Item> {

	public boolean applies(Item obj) {
		return obj.hasSyncConflicts();
	}

}
