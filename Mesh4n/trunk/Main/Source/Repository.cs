using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;

namespace Mesh4n
{
	/// <summary>
	/// Base implementation of <see cref="IRepositoryAdapter"/> that provides support for 
	/// <see cref="ISupportInitialize"/> for XAML-friendly serialization and validation.
	/// </summary>
	public abstract class RepositoryAdapter : IRepositoryAdapter
	{
		/// <summary>
		/// See <see cref="IRepositoryAdapter.SupportsMerge"/>.
		/// </summary>
		public abstract bool SupportsMerge { get; }

		/// <summary>
		/// See <see cref="IRepositoryAdapter.Get"/>.
		/// </summary>
		public abstract Item Get(string id);

		private static bool NullFilter(Item item)
		{
			return true;
		}

		/// <summary>
		/// See <see cref="IRepositoryAdapter.GetAll"/>.
		/// </summary>
		public IEnumerable<Item> GetAll()
		{
			return GetAllSince(null, NullFilter);
		}

		/// <summary>
		/// See <see cref="IRepositoryAdapter.GetAll(Predicate{Item})"/>.
		/// </summary>
		public IEnumerable<Item> GetAll(Predicate<Item> filter)
		{
			return GetAllSince(null, filter);
		}

		/// <summary>
		/// See <see cref="IRepositoryAdapter.GetAllSince(DateTime?)"/>.
		/// </summary>
		public IEnumerable<Item> GetAllSince(DateTime? since)
		{
			return GetAllSince(since, NullFilter);
		}

		/// <summary>
		/// See <see cref="IRepositoryAdapter.GetAllSince(DateTime?, Predicate{Item})"/>.
		/// </summary>
		public IEnumerable<Item> GetAllSince(DateTime? since, Predicate<Item> filter)
		{
			return GetAll(since == null ? since : Timestamp.Normalize(since.Value), filter);
		}

		protected abstract IEnumerable<Item> GetAll(DateTime? since, Predicate<Item> filter);

		/// <summary>
		/// See <see cref="IRepositoryAdapter.GetConflicts"/>. Default implementation retrieves 
		/// all items and filters out those without conflicts.
		/// </summary>
		public virtual IEnumerable<Item> GetConflicts()
		{
			return GetAllSince(null, delegate(Item item)
			{
				return item.Sync.Conflicts.Count > 0;
			});
		}

		/// <summary>
		/// See <see cref="IRepositoryAdapter.Add"/>.
		/// </summary>
		public abstract void Add(Item item);

		/// <summary>
		/// See <see cref="IRepositoryAdapter.Delete"/>.
		/// </summary>
		public abstract void Delete(string id);

		/// <summary>
		/// See <see cref="IRepositoryAdapter.Update"/>.
		/// </summary>
		public abstract void Update(Item item);

		/// <summary>
		/// See <see cref="IRepositoryAdapter.Update(Item, bool)"/>. Default implementation 
		/// uses <see cref="Behaviors.ResolveConflicts"/> to generate a new update 
		/// that resolves all conflicts, with the <see cref="DeviceAuthor.Current"/> and 
		/// <see cref="DateTime.Now"/> as the by/when information.
		/// </summary>
		public virtual Item Update(Item item, bool resolveConflicts)
		{
			Guard.ArgumentNotNull(item, "item");

			if (resolveConflicts)
			{
				item = Behaviors.ResolveConflicts(item, DeviceAuthor.Current, DateTime.Now, item.Sync.Deleted);
			}
			
			Update(item);

            return item;
		}

		/// <summary>
		/// See <see cref="IRepositoryAdapter.Merge"/>.
		/// </summary>
		public abstract IEnumerable<Item> Merge(IEnumerable<Item> items);

		/// <summary>
		/// See <see cref="IRepositoryAdapter.FriendlyName"/>.
		/// </summary>
		public abstract string FriendlyName { get; }
	}
}
