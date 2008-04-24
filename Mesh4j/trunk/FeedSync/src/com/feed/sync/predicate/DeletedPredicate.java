package com.feed.sync.predicate;

import com.feed.sync.Predicate;
import com.feed.sync.model.Item;

public class DeletedPredicate<T extends Item> implements Predicate<T> {

	@Override
	public boolean evaluate(T obj) {
		return obj.getSync().isDeleted();
	}

}
