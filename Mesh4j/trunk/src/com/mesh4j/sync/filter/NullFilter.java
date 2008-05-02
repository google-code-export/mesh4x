package com.mesh4j.sync.filter;

import com.mesh4j.sync.Filter;

public class NullFilter<T> implements Filter<T> {

	@Override
	public boolean applies(T obj) {
		return true;
	}

}
