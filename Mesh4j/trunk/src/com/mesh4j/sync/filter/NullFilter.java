package com.mesh4j.sync.filter;

import com.mesh4j.sync.IFilter;

public class NullFilter<T> implements IFilter<T> {

	@Override
	public boolean applies(T obj) {
		return true;
	}

}
