package com.feed.sync.predicate;

import com.feed.sync.Predicate;
import com.feed.sync.model.Item;

public class ConflictsPredicate<T extends Item> implements Predicate<T> {

	@Override
	public boolean evaluate(T obj) {
		return obj.getSync().getConflicts().size() > 0;
	}

}
