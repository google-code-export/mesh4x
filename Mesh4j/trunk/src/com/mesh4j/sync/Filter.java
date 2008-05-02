package com.mesh4j.sync;

public interface Filter<T> {

	boolean applies(T obj);
}

