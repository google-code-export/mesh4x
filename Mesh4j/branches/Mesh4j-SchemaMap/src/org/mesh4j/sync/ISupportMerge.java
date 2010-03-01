package org.mesh4j.sync;

import java.util.List;

import org.mesh4j.sync.model.Item;


/// <summary>
/// Whether the repository performs its own merge behavior, or 
/// it must be provided by the <see cref="SyncEngine"/>.
/// </summary>

public interface ISupportMerge {

	/// <summary>
	/// Merges the list of items in the repository, and returns any conflicting 
	/// items that were saved.
	/// </summary>
	/// <param name="items">The items to merge.</param>
	/// <returns>List of conflicts resulting from the merge. Items with conflicts are 
	/// persisted to the repository, and the winner determines the item payload.</returns>
	List<Item> merge(List<Item> items);
	
	
}
