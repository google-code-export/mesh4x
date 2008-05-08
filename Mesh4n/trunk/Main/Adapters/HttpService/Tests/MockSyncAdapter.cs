using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n.Adapters.HttpService.Tests
{
	public class MockSyncAdapter : ISyncAdapter
	{
		public MockSyncAdapter()
		{
		}

		#region ISyncAdapter Members

		public bool SupportsMerge
		{
			get { throw new NotImplementedException(); }
		}

		public Item Get(string id)
		{
			throw new NotImplementedException();
		}

		public IEnumerable<Item> GetAll()
		{
			throw new NotImplementedException();
		}

		public IEnumerable<Item> GetAll(Predicate<Item> filter)
		{
			throw new NotImplementedException();
		}

		public IEnumerable<Item> GetAllSince(DateTime? since)
		{
			throw new NotImplementedException();
		}

		public IEnumerable<Item> GetAllSince(DateTime? since, Predicate<Item> filter)
		{
			throw new NotImplementedException();
		}

		public IEnumerable<Item> GetConflicts()
		{
			throw new NotImplementedException();
		}

		public void Add(Item item)
		{
			throw new NotImplementedException();
		}

		public void Delete(string id)
		{
			throw new NotImplementedException();
		}

		public void Update(Item item)
		{
			throw new NotImplementedException();
		}

		public Item Update(Item item, bool resolveConflicts)
		{
			throw new NotImplementedException();
		}

		public IEnumerable<Item> Merge(IEnumerable<Item> items)
		{
			throw new NotImplementedException();
		}

		public string FriendlyName
		{
			get { throw new NotImplementedException(); }
		}

		#endregion
	}
}
