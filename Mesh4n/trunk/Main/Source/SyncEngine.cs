using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	/// <summary>
	/// Main class that performs synchronization between two repositories.
	/// </summary>
	public class SyncEngine
	{
		public event EventHandler<ItemEventArgs> ItemReceived;
		public event EventHandler<ItemEventArgs> ItemSent;

		IRepositoryAdapter source;
		IRepositoryAdapter target;

		/// <summary>
		/// Initializes the engine with the two repositories to synchronize.
		/// </summary>
		public SyncEngine(IRepositoryAdapter source, IRepositoryAdapter target)
		{
			Guard.ArgumentNotNull(source, "left");
			Guard.ArgumentNotNull(target, "right");

			this.source = source;
			this.target = target;
		}

		/// <summary>
		/// Performs a full sync between the two repositories, automatically 
		/// incorporating changes in both.
		/// </summary>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize()
		{
			return SynchronizeImpl(null, new MergeFilter(), new ItemFilter());
		}

		/// <summary>
		/// Performs a full sync between the two repositories, optionally calling the 
		/// given <paramref name="mergeFilter"/> callback.
		/// </summary>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize(MergeFilter mergeFilter)
		{
			return SynchronizeImpl(null, mergeFilter, new ItemFilter());
		}

		/// <summary>
		/// Performs a full sync between the two repositories, automatically 
		/// incorporating changes in both.
		/// </summary>
		/// <param name="itemFilter">Represents a method that filter items according to a specified criteria. </param>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize(ItemFilter itemFilter)
		{
			return SynchronizeImpl(null, new MergeFilter(), itemFilter);
		}

		/// <summary>
		/// Performs a partial sync between the two repositories since the specified date, automatically 
		/// incorporating changes in both.
		/// </summary>
		/// <param name="since">Synchronize changes that happened after this date.</param>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize(DateTime? since)
		{
			return SynchronizeImpl(since, new MergeFilter(), new ItemFilter());
		}

		/// <summary>
		/// Performs a partial sync between the two repositories since the specified date, automatically 
		/// incorporating changes in both.
		/// </summary>
		/// <param name="since">Synchronize changes that happened after this date.</param>
		/// <param name="itemFilter">Represents a method that filter items according to a specified criteria.</param>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize(DateTime? since, ItemFilter itemFilter)
		{
			return SynchronizeImpl(since, new MergeFilter(), itemFilter);
		}

		/// <summary>
		/// Performs a partial sync between the two repositories since the specified date, optionally calling the 
		/// given <paramref name="mergeFilter"/> callback.
		/// </summary>
		/// <param name="since">Synchronize changes that happened after this date.</param>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize(DateTime? since, MergeFilter mergeFilter)
		{
			return SynchronizeImpl(since, mergeFilter, new ItemFilter());
		}

		/// <summary>
		/// Performs a partial sync between the two repositories since the specified date, optionally calling the 
		/// given <paramref name="mergeFilter"/> callback.
		/// </summary>
		/// <param name="since">Synchronize changes that happened after this date.</param>
		/// <param name="itemFilter">Represents a method that filter items according to a specified criteria.</param>
		/// <remarks>
		/// Items on the source repository are sent first, and then the 
		/// changes from the target repository are incorporated into the source.
		/// </remarks>
		/// <returns>The list of items that had conflicts.</returns>
		public IList<Item> Synchronize(DateTime? since, MergeFilter mergeFilter, ItemFilter itemFilter)
		{
			return SynchronizeImpl(since, mergeFilter, itemFilter);
		}

		private IList<Item> SynchronizeImpl(DateTime? since, MergeFilter mergeFilter, ItemFilter itemFilter)
		{
			Guard.ArgumentNotNull(mergeFilter, "mergeFilter");
			Guard.ArgumentNotNull(itemFilter, "itemFilter");

			IEnumerable<Item> outgoingItems = EnumerateItemsProgress(
				(since == null) ? source.GetAll(itemFilter.Left) : source.GetAllSince(since, itemFilter.Left),
				RaiseItemSent);

			if (!target.SupportsMerge)
			{
				IEnumerable<ItemMergeResult> outgoingToMerge = MergeItems(outgoingItems, target);
				if ((mergeFilter.Behaviors & MergeFilterBehaviors.Right) == MergeFilterBehaviors.Right)
				{
					outgoingToMerge = mergeFilter.Handler(target, outgoingToMerge);
				}
				Import(outgoingToMerge, target);
			}
			else
			{
				target.Merge(outgoingItems);
			}

			IEnumerable<Item> incomingItems = EnumerateItemsProgress(
				(since == null) ? target.GetAll(itemFilter.Right) : target.GetAllSince(since, itemFilter.Right),
				RaiseItemReceived);

			if (!source.SupportsMerge)
			{
				IEnumerable<ItemMergeResult> incomingToMerge = MergeItems(incomingItems, source);
				if ((mergeFilter.Behaviors & MergeFilterBehaviors.Left) == MergeFilterBehaviors.Left)
				{
					incomingToMerge = mergeFilter.Handler(source, incomingToMerge);
				}
				
				return Import(incomingToMerge, source);
			}
			else
			{
				// If repository supports its own SSE merge behavior, don't apply it locally.
				return new List<Item>(source.Merge(incomingItems));
			}
		}

		private IEnumerable<ItemMergeResult> MergeItems(IEnumerable<Item> items, IRepositoryAdapter repository)
		{
			foreach (Item incoming in items)
			{
				Item original = repository.Get(incoming.Sync.Id);
				ItemMergeResult result = new MergeBehavior().Merge(original, incoming);

				if (result.Operation != MergeOperation.None)
					yield return result;
			}
		}

		private IList<Item> Import(IEnumerable<ItemMergeResult> items, IRepositoryAdapter repository)
		{
			// Straight import of data in merged results. 
			// Conflicting items are saved and also 
			// are returned for conflict resolution by the user or 
			// a custom component. MergeBehavior determines 
			// the winner element that is saved.
			// Conflicts are returned in a list because we need 
			// the full iteration over the merged items to be 
			// processed. If we returned an IEnumerable, we would 
			// depend on the client iterating it in order to 
			// actually import items, which is undesirable.
			List<Item> conflicts = new List<Item>();

			foreach (ItemMergeResult result in items)
			{
				if (result.Operation != MergeOperation.None &&
					result.Proposed != null &&
					result.Proposed.Sync.Conflicts.Count > 0)
				{
					conflicts.Add(result.Proposed);
				}

				switch (result.Operation)
				{
					case MergeOperation.Added:
						// Clean history before adding
						repository.Add(
							new Item(result.Proposed.XmlItem, 
							Behaviors.SparsePurge(result.Proposed.Sync)));
						break;
					case MergeOperation.Updated:
					case MergeOperation.Conflict:
						// Clean history before updating
						repository.Update(
							new Item(result.Proposed.XmlItem,
							Behaviors.SparsePurge(result.Proposed.Sync)));
						break;
					case MergeOperation.None:
						break;
					default:
						throw new InvalidOperationException();
				}
			}

			return conflicts;
		}

		private IEnumerable<Item> EnumerateItemsProgress(IEnumerable<Item> items, RaiseHandler raiser)
		{
			foreach (Item item in items)
			{
				raiser(item);
				yield return item;
			}
		}

		private void RaiseItemReceived(Item item)
		{
			if (ItemReceived != null)
				ItemReceived(this, new ItemEventArgs(item));
		}

		private void RaiseItemSent(Item item)
		{
			if (ItemSent != null)
				ItemSent(this, new ItemEventArgs(item));
		}

		delegate void RaiseHandler(Item item);
	}
}
