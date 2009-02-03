package org.mesh4j.sync;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.filter.ConflictsFilter;
import org.mesh4j.sync.filter.NullFilter;
import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;


public abstract class AbstractSyncAdapter implements ISyncAdapter{

	// CONSTANTS
	private final static NullFilter<Item> NULL_FILTER = new NullFilter<Item>();
	private final static ConflictsFilter CONFLICTS_FILTER = new ConflictsFilter();

	// BUSINESS METHODS
	public abstract Item get(String id);
	protected abstract List<Item> getAll(Date since, IFilter<Item> filter);
	public abstract void add(Item item);
	public abstract void delete(String id);
	public abstract void update(Item item);
	public abstract String getFriendlyName();
	public abstract String getAuthenticatedUser();

	public List<Item> getAll()
	{
		return getAllSince(null, NULL_FILTER);
	}

	public List<Item> getAll(IFilter<Item> filter)
	{
		return getAllSince(null, filter);
	}

	public List<Item> getAllSince(Date since)
	{
		return getAllSince(since, NULL_FILTER);
	}

	public List<Item> getAllSince(Date since, IFilter<Item> filter)
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
			item = MergeBehavior.resolveConflicts(item, this.getAuthenticatedUser(), new Date(), item.getSync().isDeleted());
		}
		
		update(item);
	}
}
