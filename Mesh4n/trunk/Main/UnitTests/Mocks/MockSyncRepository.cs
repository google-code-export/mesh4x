using System;
using System.Collections.Generic;
using System.Text;
using System.Configuration;

namespace SimpleSharing.Tests
{

	public class MockSyncRepository : ISyncRepository
	{
		Dictionary<string, Sync> syncs = new Dictionary<string, Sync>();
		Dictionary<string, DateTime> lastSync = new Dictionary<string, DateTime>();

		public Dictionary<string, Sync> Syncs { get { return syncs; } }

		public Sync Get(string id)
		{
			Guard.ArgumentNotNullOrEmptyString(id, "id");

			if (!syncs.ContainsKey(id))
				return null;

			return syncs[id].Clone();
		}

		public void Save(Sync sync)
		{
			Guard.ArgumentNotNull(sync, "sync");

			syncs[sync.Id] = sync.Clone();
		}

		public IEnumerable<Sync> GetAll()
		{
			Sync[] values = new Sync[syncs.Count];
			syncs.Values.CopyTo(values, 0);

			return values;
		}

		public IEnumerable<Sync> GetConflicts()
		{
			foreach (Sync sync in syncs.Values)
			{
				if (sync.Conflicts.Count > 0)
					yield return sync;
			}
		}
	}

}
