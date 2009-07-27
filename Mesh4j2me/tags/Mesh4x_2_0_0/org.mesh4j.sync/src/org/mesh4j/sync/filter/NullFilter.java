package org.mesh4j.sync.filter;

import org.mesh4j.sync.IFilter;

public class NullFilter<T> implements IFilter<T> {

	public boolean applies(T obj) {
		return true;
	}

}
