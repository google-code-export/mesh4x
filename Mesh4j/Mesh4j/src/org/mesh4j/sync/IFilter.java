package org.mesh4j.sync;

public interface IFilter<T> {

	boolean applies(T obj);
}

