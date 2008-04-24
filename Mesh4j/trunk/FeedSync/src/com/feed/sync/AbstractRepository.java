package com.feed.sync;

import java.util.Date;
import java.util.List;

import com.feed.sync.behavior.Behaviors;
import com.feed.sync.model.Item;
import com.feed.sync.predicate.ConflictsPredicate;
import com.feed.sync.predicate.NullPredicate;
import com.feed.sync.utils.DateHelper;
import com.feed.sync.validations.Guard;

public abstract class AbstractRepository implements Repository{

	// CONSTANTS
	private final static NullPredicate<Item> NULL_FILTER = new NullPredicate<Item>();
	private final static ConflictsPredicate<Item> CONFLICTS_FILTER = new ConflictsPredicate<Item>();

	// BUSINESS METHODS
	public abstract boolean supportsMerge();
	public abstract Item get(String id);
	protected abstract List<Item> getAll(Date since, Predicate<Item> filter);
	public abstract void add(Item item);
	public abstract void delete(String id);
	public abstract void update(Item item);
	public abstract List<Item> merge(List<Item> items);
	public abstract String getFriendlyName();
	protected abstract String getCurrentAuthor();


	public List<Item> getAll()
	{
		return getAllSince(null, NULL_FILTER);
	}

	public List<Item> getAll(Predicate<Item> filter)
	{
		return getAllSince(null, filter);
	}

	public List<Item> getAllSince(Date since)
	{
		return getAllSince(since, NULL_FILTER);
	}

	public List<Item> getAllSince(Date since, Predicate<Item> filter)
	{
		return getAll(since == null ? since : DateHelper.normalize(since), filter);
	}

	public List<Item> getConflicts()
	{
		return getAllSince(null, CONFLICTS_FILTER);
	}

	public void update(Item item, boolean resolveConflicts)
	{
		Guard.argumentNotNull(item, "item");

		if (resolveConflicts)
		{
			item = Behaviors.INSTANCE.resolveConflicts(item, getCurrentAuthor(), new Date(), item.getSync().isDeleted());
		}
		
		update(item);
	}
}
