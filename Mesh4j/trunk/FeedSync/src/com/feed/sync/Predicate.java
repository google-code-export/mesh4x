package com.feed.sync;

public interface Predicate<T> {

	boolean evaluate(T obj);
}

