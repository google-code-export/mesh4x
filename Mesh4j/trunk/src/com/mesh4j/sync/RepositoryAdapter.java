package com.mesh4j.sync;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.model.Item;

/**
 * Main repository interface for an SSE adapter.
 * 
 * @author jtondato
 */
public interface RepositoryAdapter {
	
	/// <summary>
	/// Whether the repository performs its own merge behavior, or 
	/// it must be provided by the <see cref="SyncEngine"/>.
	/// </summary>
	boolean supportsMerge();

	/// <summary>
	/// Tries to retrieve an item with the given <paramref name="id"/>.
	/// </summary>
	/// <param name="id">SSE identifier for the item</param>
	/// <returns>An <see cref="Item"/> if the item was found in the repository; <see langword="null"/> otherwise.</returns>
	Item get(String id);

	/// <summary>
	/// Gets all the items in the repository, including deleted ones.
	/// </summary>
	List<Item> getAll();

	/// <summary>
	/// Gets all the items in the repository, including deleted ones, and filters 
	/// the result using the given filter predicate.
	/// </summary>
	List<Item> getAll(Filter<Item> filter);

	/// <summary>
	/// Gets all the items in the repository that were added, changed or removed after the given date.
	/// </summary>
	/// <param name="since">Optional date to retrieve items since.</param>
	List<Item> getAllSince(Date since);

	/// <summary>
	/// Gets all the items in the repository that were added, changed or removed after the given date, 
	/// and filters the result using the given filter predicate.
	/// </summary>
	/// <param name="since">Optional date to retrieve items since.</param>
	List<Item> getAllSince(Date since, Filter<Item> filter);

	/// <summary>
	/// Returns the items with conflicts.
	/// </summary>
	List<Item> getConflicts();

	/// <summary>
	/// Adds an item to the repository.
	/// </summary>
	/// <param name="item">The item to add.</param>
	void add(Item item);

	/// <summary>
	/// Permanently deletes the item from the repository, if supported by the implementation.
	/// </summary>
	/// <param name="id">The item SSE identifier.</param>
	/// <remarks>
	/// In order to perform an SSE delete, use <see cref="Behaviors.Delete"/> on 
	/// the <see cref="Item.Sync"/>, and perform an <see cref="Update"/> instead of 
	/// trying to delete directly from the repository.
	/// <para>
	/// Not all repositories support permanently deleting items.
	/// </para>
	/// </remarks>
	void delete(String id);

	/// <summary>
	/// Updates the item on the repository.
	/// </summary>
	/// <param name="item">The item to update.</param>
	void update(Item item);

	/// <summary>
	/// Updates the item on the repository, optionally merging the conflicts history 
	/// depending on the value of <paramref name="resolveConflicts"/>.
	/// </summary>
	/// <param name="item">The item to update.</param>
	/// <param name="resolveConflicts"><see langword="true"/> to apply the 
	/// conflict resolution algorithm and update the item; <see langword="false"/> to 
	/// only save the item any potential conflicts it may have.</param>
	/// <returns>The updated item if conflicts were resolved.</returns>
	/// <remarks>
	/// See 3.4 on SSE spec.
	/// </remarks>
	void update(Item item, boolean resolveConflicts);

	/// <summary>
	/// Merges the list of items in the repository, and returns any conflicting 
	/// items that were saved.
	/// </summary>
	/// <param name="items">The items to merge.</param>
	/// <returns>List of conflicts resulting from the merge. Items with conflicts are 
	/// persisted to the repository, and the winner determines the item payload.</returns>
	List<Item> merge(List<Item> items);

	/// <summary>
	/// Friendly name of the repository, useful for showing 
	/// in dialogs to identify a given repository.
	/// </summary>
	String getFriendlyName();
}
