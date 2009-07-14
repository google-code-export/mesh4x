package org.mesh4j.sync.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.validations.Guard;


/// <summary>
///3.3	Merge Behavior
///When a subscribing endpoint incorporates items from a publishing endpoint’s feed, these items must be merged with the existing local items. The act of merging items from an incoming feed detects new items, item updates and item conflicts and produces a merged result feed.  The merging of two items with the same id attribute value will result in a ‘winning’ item that MAY have conflict items.  In order to merge items, implementations MUST follow this algorithm for the two items:
///1.	If no local item exists with the same id attribute value as the incoming item, add the incoming item to the merge result feed; we are done processing the incoming item.
///2.	Create a collection L and populate it with the local item and the local item’s conflicts (if any exist) by using the following steps:
///a.	For each item sub-element of the sx:conflicts element for the local item:
///i.	Add the item sub-element to L
///b.	If the local item has a sx:conflicts sub-element, remove it
///c.	Add the local item to L
///3.	Create a collection I and populate it with the incoming item and the incoming item’s conflicts (if any exist) by using the following steps:
///a.	For each item sub-element of the sx:conflicts element for the incoming item:
///i.	Add the item sub-element to I
///b.	If the incoming item has a sx:conflicts sub-element, remove it
///c.	Add the incoming item to I
///4.	Create a collection M that will be used to contain items that will appear in the merged result feed
///5.	Create a reference W for the current ‘winning’ item and set it to an unassigned value
///6.	Using L as the outer collection and I as the inner collection, perform the following step
///7.	For each item X in outer collection:
///a.	For each item Y in inner collection:
///i.	Determine if X is subsumed1 by Y – if so then remove X from the outer collection; process the next item in the outer collection
///b.	Add X to M
///c.	If W has not been assigned a value, set W to X; process the next item in the outer collection 
///d.	Determine if X should be declared as the new ‘winning’ item3 – if so set W to X.
///8.	Using I as the outer collection and L as the inner collection, perform step 7 again
///9.	Add W to the merge result feed
///10.	If the noconflicts attribute is set to true, then we are done processing
///11.	If M contains more than one item:
///a.	Create a sx:conflicts element and add it as a sub-element of the sx:sync element for W
///b.	For each item Z in M:
///i.	If Z equals W (i.e. they are the same item), then process the next item in M
///ii.	Add Z as a sub-element of the sx:conflicts element created in step 11a.
/// </summary>
public class MergeBehavior {
	
	/// <summary>
	/// Merges the two items applying the SSE algorithm.
	/// </summary>
	public static MergeResult merge(Item originalItem, Item incomingItem) 
	{
		Guard.argumentNotNull(incomingItem, "incomingItem");

		Item incoming = incomingItem.clone();

		if (originalItem == null)
		{
			return new MergeResult(null, incoming, incoming, MergeOperation.Added);
		}

		Item original = originalItem.clone();

		// History on both elements must have at least one entry
		if (original.getLastUpdate() == null ||
			incoming.getLastUpdate() == null)
		{
			Guard.throwsException("SyncHistoryRequired");
		}

		return mergeItems(original, incoming);
	}
	
	private static MergeResult mergeItems(Item localItem, Item incomingItem)
	{
		MergeProcessParameters mergeProcessParameters = new MergeProcessParameters();
		
		//3.3.2    L
		mergeProcessParameters.setOuterCollection(new ArrayList<Item>());
		mergeProcessParameters.getOuterCollection().addAll(localItem.getSync().getConflicts());
		localItem.getSync().getConflicts().clear();
		mergeProcessParameters.getOuterCollection().add(localItem);

		//3.3.3   I
		mergeProcessParameters.setInnerCollection(new ArrayList<Item>());
		mergeProcessParameters.getInnerCollection().addAll(incomingItem.getSync().getConflicts());
		incomingItem.getSync().getConflicts().clear();
		mergeProcessParameters.getInnerCollection().add(incomingItem);

		//3.3.4  M
		mergeProcessParameters.setMergedCollection(new ArrayList<Item>());

		//3.3.5
		mergeProcessParameters.setWinner(null);
		
		//3.3.6 and 3.3.7
		performStep7(mergeProcessParameters);
		
		//3.3.8
		mergeProcessParameters.interchangeInnerWithOuter();
		performStep7(mergeProcessParameters);

		if (mergeProcessParameters.getWinner() == null)
		{
			//There is no need to update the local item
			return new MergeResult(localItem, incomingItem, null, MergeOperation.None);
		}

		//3.3.10
		if (!mergeProcessParameters.getWinner().getSync().isNoConflicts())
		{
			//3.3.11
			for(Item z : mergeProcessParameters.getMergedCollection())
			{
				if (!z.equals(mergeProcessParameters.getWinner()) && !mergeProcessParameters.getWinner().getSync().getConflicts().contains(z))
				{
					mergeProcessParameters.getWinner().getSync().getConflicts().add(z);
				}
			}
		}

		if (mergeProcessParameters.getWinner().getSync().getConflicts().size() > 0)
		{
			return new MergeResult(localItem, incomingItem, mergeProcessParameters.getWinner(), MergeOperation.Conflict);
		}
		else
		{
			if (mergeProcessParameters.getWinner().getSync().equals(localItem.getSync()))
			{
				return new MergeResult(localItem, incomingItem, null, MergeOperation.None);
			} else {
				return new MergeResult(localItem, incomingItem, mergeProcessParameters.getWinner(), MergeOperation.Updated);
			}
		}
	}
	
	/// <summary>
	/// 3.3.7 implementation
	/// </summary>
	/// <param name="outerCollection"></param>
	/// <param name="innerCollection"></param>
	/// <param name="M"></param>
	/// <param name="W"></param>
	private static void performStep7(MergeProcessParameters mergeProcessParameters)
	{
		ArrayList<Item> resOuter = new ArrayList<Item>(mergeProcessParameters.getOuterCollection());
		ArrayList<Item> resInner = new ArrayList<Item>(mergeProcessParameters.getInnerCollection());

		Item W = mergeProcessParameters.getWinner();
		
		// Collections must be modified from this method.
		for (Item x : mergeProcessParameters.getOuterCollection())
		{
			boolean isSubsumed = false;
			for (Item y : mergeProcessParameters.getInnerCollection())
			{
				if (x.getSync().isSubsumedBy(y.getSync()))
				{
					isSubsumed = true;
					resOuter.remove(x);
					break;
				}
			}

			if (!isSubsumed)
			{
				mergeProcessParameters.getMergedCollection().add(x);
				if (W == null)
				{
					W = x;
				}
				else
				{
					W = winnerPicking(W, x);
				}
			}
		}

		mergeProcessParameters.setOuterCollection(resOuter);
		mergeProcessParameters.setInnerCollection(resInner);
		mergeProcessParameters.setWinner(W);
	}
	
	/// <summary>
	///Winner Picking
	///The ‘winning’ item between an item X and an item Y is the item with most recent update , where X and Y are items with the same id attribute value.  In order to determine the ‘winning’ item, implementations MUST perform the following comparisons, in order, for X and Y:
	///1.	If X has a greater updates attribute value for the sx:sync sub-element than Y’s, then X is the ‘winning’ item
	///2.	If X has the same updates attribute value for the sx:sync sub-element as Y:
	///a.	If X has a when attribute for the topmost sx:history sub-element and Y does not, then X is the ‘winning’ item
	///b.	If X has a when attribute value for the topmost sx:history sub-element and that is chronologically later than Y’s, then X is the ‘winning’ item
	///c.	If X has the same when attribute for the topmost sx:history sub-element as Y:
	///i.	If X has a by attribute for the topmost sx:history sub-element and Y does not, then X is the ‘winning’ item 
	///ii.	If X has a by attribute value for the topmost sx:history sub-element that is collates greater (see Section 2.4 for collation rules) than Y’s, then X is the ‘winning’ item
	///3.	Y is the ‘winning’ item
	/// </summary>
	/// <param name="item"></param>
	/// <param name="anotherItem"></param>
	/// <returns></returns>
	private static Item winnerPicking(Item item, Item anotherItem)
	{
		Item winner = null;

		if (item.getSync().getUpdates() == anotherItem.getSync().getUpdates())
		{
			winner = firstWinsWithWhen(item, anotherItem);
			if (winner == null)
			{
				winner = firstWinsWithWhen(anotherItem, item);
			}

			if (winner == null){
				winner = firstWinsWithBy(item, anotherItem);
				if(winner == null){
					winner = firstWinsWithBy(anotherItem, item);
				}
			}
		}
		else
		{
			winner = item.getSync().getUpdates() > anotherItem.getSync().getUpdates() ? item : anotherItem;
		}

		return winner;
	}
	
	private static Item firstWinsWithWhen(Item first, Item second)
	{
		if (first.getLastUpdate().getWhen() == null) return null;

		boolean firstWins = second.getLastUpdate().getWhen() == null ||
				(first.getLastUpdate().getWhen().after(second.getLastUpdate().getWhen()));

		if (firstWins) {
			return first;
		} else {
			return null;
		}
	}
	
	private static Item firstWinsWithBy(Item first, Item second)
	{
		if (first.getLastUpdate().getBy() == null) return null;

		boolean firstWins = second.getLastUpdate().getBy() == null ||
				(second.getLastUpdate().getBy() != null &&
				!first.getLastUpdate().getBy().equals(second.getLastUpdate().getBy()) &&
				first.getLastUpdate().getBy().length() > second.getLastUpdate().getBy().length()
				);

		if (firstWins) {
			return first;
		} else {
			return null;
		}
	}
	
	// 3.4
	public static Item resolveConflicts(Item resolvedItem, String by, Date when, boolean deleteItem)
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
					/// <summary>
					/// Adds the conflict history immediately after the topmost history.
					/// </summary>
					/// <remarks>Used for conflict resolution only.</remarks>
					History topmost = Sr.getUpdatesHistory().pop();
					Sr.getUpdatesHistory().push(Hc);
					Sr.getUpdatesHistory().push(topmost);
				}
			}
		}

		
		Sync updatedSync = Sr.clone();
		updatedSync.update(by, when, deleteItem);

		return new Item(R.getContent(), updatedSync);
	}
	
	/// <summary>
	/// Purges the <paramref name="Sync.UpdatesHistory"/> by retaining the sx:history sub-elements 
	/// of sx:sync having the highest ‘sequence’ for a given ‘by’, and removing all other 
	/// sx:history sub-elements of sx:sync that have lower ‘sequence’ values for the same given ‘by’.
	/// </summary>
	/// <param name="sync"></param>
	/// <returns></returns>
	public static boolean sparsePurge(Sync sync)
	{
//		List<History> purgedHistory = new List<History>();
//
//		Dictionary<string, History> latest = new Dictionary<string, History>();
//
//		foreach (History history in sync.UpdatesHistory)
//		{
//			// By may be null or empty if not specified.
//			// feedsync allows either By or When to be specified.
//			if (String.IsNullOrEmpty(history.By))
//			{
//				// Can't purge without a By
//				purgedHistory.Add(history);
//			}
//			else
//			{
//				History last;
//				if (latest.TryGetValue(history.By, out last))
//				{
//					if (history.Sequence > last.Sequence)
//					{
//						// Replace the item we added before.
//						purgedHistory.Remove(last);
//						latest.Add(history.By, history);
//					}
//				}
//				else
//				{
//					latest.Add(history.By, history);
//					purgedHistory.Add(history);
//				}
//			}
//		}
//
//		purgedHistory.Reverse();
//		Sync purged = new Sync(sync.Id, sync.Updates);
//		purged.Conflicts.AddRange(sync.Conflicts);
//		purged.Deleted = sync.Deleted;
//		purged.Tag = sync.Tag;
//		purged.NoConflicts = sync.NoConflicts;
//		purged.Updates = sync.Updates;
//
//		foreach (History history in purgedHistory)
//		{
//			purged.AddHistory(history);
//		}
//
//		return purged;
//	}

		// TODO (JMT) SparcePurge
		if(sync.getUpdates() > 1){
			History lastUpdate = sync.getLastUpdate();
			sync.getUpdatesHistory().clear();
			sync.getUpdatesHistory().push(lastUpdate);
			sync.setUpdatesWithLastUpdateSequence();
			return true;
		} else {
			return false;
		}
	}
}
