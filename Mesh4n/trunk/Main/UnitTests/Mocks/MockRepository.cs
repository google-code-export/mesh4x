using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n.Tests
{
	public class MockRepository : RepositoryAdapter
	{
		public string name;
		public Dictionary<string, Item> Items = new Dictionary<string, Item>();

		public MockRepository(params Item[] items)
		{
			foreach (Item item in items)
			{
				Items.Add(item.Sync.Id, item);
			}
		}

		public MockRepository(string name)
		{
			this.name = name;
		}

		public override string FriendlyName
		{
			get { return name; }
		}

		public override bool SupportsMerge
		{
			get { return false; }
		}

		public override void Add(Item item)
		{
			Guard.ArgumentNotNull(item, "item");

			if (Items.ContainsKey(item.Sync.Id))
				throw new ArgumentException();

			Items.Add(item.Sync.Id, item);
		}

		public override Item Get(string id)
		{
			Guard.ArgumentNotNullOrEmptyString(id, "id");

			if (Items.ContainsKey(id))
				return Items[id].Clone();
			else
				return null;
		}

		protected override IEnumerable<Item> GetAll(DateTime? since, Predicate<Item> filter)
		{
			Guard.ArgumentNotNull(filter, "filter");

			foreach (Item i in Items.Values)
			{
				if ((since == null || 
					i.Sync.LastUpdate == null || 
					i.Sync.LastUpdate.When == null || 
					i.Sync.LastUpdate.When >= since)
					&& filter(i))
					yield return i.Clone();
			}
		}

		public override void Delete(string id)
		{
			Guard.ArgumentNotNullOrEmptyString(id, "id");

			Items.Remove(id);
		}

		public override void Update(Item item)
		{
			Guard.ArgumentNotNull(item, "item");

			Item i;
			if (item.Sync.Deleted)
				i = new Item(new NullXmlItem(item.Sync.Id), item.Sync.Clone());
			else
				i = item.Clone();

			Items[item.Sync.Id] = i;
		}

		public override IEnumerable<Item> Merge(IEnumerable<Item> items)
		{
			throw new NotSupportedException();
		}
	}
}
