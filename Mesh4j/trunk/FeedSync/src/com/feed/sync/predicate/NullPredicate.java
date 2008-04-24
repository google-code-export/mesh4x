package com.feed.sync.predicate;

import com.feed.sync.Predicate;

public class NullPredicate<T> implements Predicate<T> {

	@Override
	public boolean evaluate(T obj) {
		return true;
	}

}
