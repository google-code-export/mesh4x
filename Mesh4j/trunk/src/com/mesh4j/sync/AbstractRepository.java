package com.mesh4j.sync;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.filter.ConflictsFilter;
import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.merge.MergeBehavior;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.Security;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;

public abstract class AbstractRepository implements Repository{

	// CONSTANTS
	private final static NullFilter<Item> NULL_FILTER = new NullFilter<Item>();
	private final static ConflictsFilter CONFLICTS_FILTER = new ConflictsFilter();

	// BUSINESS METHODS
	public abstract boolean supportsMerge();
	public abstract Item get(String id);
	protected abstract List<Item> getAll(Date since, Filter<Item> filter);
	public abstract void add(Item item);
	public abstract void delete(String id);
	public abstract void update(Item item);
	public abstract List<Item> merge(List<Item> items);
	public abstract String getFriendlyName();

	public List<Item> getAll()
	{
		return getAllSince(null, NULL_FILTER);
	}

	public List<Item> getAll(Filter<Item> filter)
	{
		return getAllSince(null, filter);
	}

	public List<Item> getAllSince(Date since)
	{
		return getAllSince(since, NULL_FILTER);
	}

	public List<Item> getAllSince(Date since, Filter<Item> filter)
	{
		Guard.argumentNotNull(filter, "filter");
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
			item = MergeBehavior.resolveConflicts(item, Security.getAuthenticatedUser(), new Date(), item.getSync().isDeleted());
		}
		
		update(item);
	}
}
