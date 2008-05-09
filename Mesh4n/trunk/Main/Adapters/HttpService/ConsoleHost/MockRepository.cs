using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Mesh4n;

namespace ConsoleHost
{
	public class MockSyncAdapter : SyncAdapter
	{
		public override bool SupportsMerge
		{
			get { return false; }
		}

		public override Item Get(string id)
		{
			return null;
		}

		protected override IEnumerable<Item> GetAll(DateTime? since, Predicate<Item> filter)
		{
			string id = Guid.NewGuid().ToString();
			Item item = new Item(new XmlItem(id, "title", "description", null), Behaviors.Create(id, "pci", DateTime.Now, false));

			return new Item[] { item };
		}

		public override void Add(Item item)
		{
			
		}

		public override void Delete(string id)
		{
			
		}

		public override void Update(Item item)
		{
			
		}

		public override IEnumerable<Item> Merge(IEnumerable<Item> items)
		{
			return null;
		}

		public override string FriendlyName
		{
			get { return "Test"; }
		}
	}
}
