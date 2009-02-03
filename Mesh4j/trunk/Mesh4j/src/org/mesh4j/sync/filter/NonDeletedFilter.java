package org.mesh4j.sync.filter;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;

public class NonDeletedFilter implements IFilter<Item> {

	public final static NonDeletedFilter INSTANCE = new NonDeletedFilter();
	
	@Override
	public boolean applies(Item obj) {
		return !obj.getSync().isDeleted();
	}

}
