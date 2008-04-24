package com.feed.sync.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.feed.sync.ItemMergeResult;
import com.feed.sync.model.History;
import com.feed.sync.model.Item;
import com.feed.sync.model.Sync;
import com.feed.sync.validations.Guard;

public class Behaviors {
	
	public static final Behaviors INSTANCE = new Behaviors();

	public Sync create(String id, String by, Date when, boolean deleteItem) 
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		if (by == null && when == null){
			throw new IllegalArgumentException("MustProvideWhenOrBy"); // TODO resource bundle
		}
			

		return basicUpdate(new Sync(id), by, when, deleteItem);
	}

	public Sync delete(Sync sync, String by, Date when)
	{
		Guard.argumentNotNull(sync, "sync");
		Guard.argumentNotNullOrEmptyString(by, "by");
		Guard.argumentNotNull(when, "when");
		
		//Deleted attribute set to true because it is a deletion (3.2.4 from spec)
		return update(sync, by, when, true);
	}

	public ItemMergeResult merge(Item originalItem, Item incomingItem)
	{
		return MergeBehavior.INSTANCE.merge(originalItem, incomingItem);
	}

	// 3.2
	// 3.2
	public Sync update(Sync sync, String by, Date when, boolean deleteItem)
	{
		return basicUpdate(sync.clone(), by, when, deleteItem);
	}
	
	public Sync basicUpdate(Sync sync, String by, Date when, boolean deleteItem)
	{
		Sync updated = sync;

		// 3.2.1
		updated.increaseUpdates();

		// 3.2.2 & 3.2.2.a.i
		History history = new History(by, when, updated.getUpdates()); 

		// 3.2.3
		updated.addHistory(history);

		// 3.2.4
		updated.setDeleted(deleteItem);


		return updated;
	}

	// 3.4
	public Item resolveConflicts(Item resolvedItem, String by, Date when, boolean deleteItem)
	{
		//3.4	Conflict Resolution Behavior
		//Merging Conflict Items 
		//1.	Set R as a reference the resolved item
		//2.	Set Ry as a reference to the sx:sync sub-element for R
		//3.	For each item sub-element C of the sx:conflicts element that has been resolved:
		//	a.	Set Sc as a reference to the sx:sync sub-element for C
		//	b.	Remove C from the sx:conflicts element.
		//	b.	For each sx:history sub-element Hc of Sc:
		//		i.	For each sx:history sub-element Hr of Sr:
		//			aa.	Compare Hc with Hr to see if Hc can be subsumed2 by Hr – if so then process the next item sub-element
		//		ii.	Add Hr as a sub-element of Sr, immediately after the topmost sx:history sub-element of Sr.
		//3. If the sx:conflicts element contains no sub-elements, the sx:conflicts element SHOULD be removed.

		Item R = resolvedItem.clone();
		Sync Sr = R.getSync();
		
		ArrayList<Item> conflictItems = new ArrayList<Item>();
		conflictItems.addAll(Sr.getConflicts());
		
		for (Item C : conflictItems)
		{
			Sync Sc = C.getSync();
			Sr.removeConflict(C);
			
			ArrayList<History> allConflictUpdatesHistories = new ArrayList<History>(Sc.getUpdatesHistory());
			Collections.reverse(allConflictUpdatesHistories);
			
			for (History Hc : allConflictUpdatesHistories)
			{
				boolean isSubsumed = false;
				for (History Hr : Sr.getUpdatesHistory())
				{
					if (Hc.IsSubsumedBy(Hr))
					{
						isSubsumed = true;
						break;
					}
				}
				if (isSubsumed)
				{
					break;
				}
				else
				{
					Sr.addConflictHistory(Hc);
				}
			}
		}

		Sync updatedSync = update(Sr, by, when, deleteItem);

		return new Item(R.getModelItem(), updatedSync);
	}
}
