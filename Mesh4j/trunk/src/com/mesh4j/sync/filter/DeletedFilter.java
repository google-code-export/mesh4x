package com.mesh4j.sync.filter;

import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.model.Item;

public class DeletedFilter<T extends Item> implements IFilter<T> {

	@Override
	public boolean applies(T obj) {
		return obj.getSync().isDeleted();
	}

}
