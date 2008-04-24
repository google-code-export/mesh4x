using System;
using System.Collections.Generic;
using System.Text;

namespace SimpleSharing
{
	[Obsolete("Use IRepository interface directly")]
	public interface ISyncRepository
	{
		Sync Get(string id);
		void Save(Sync sync);

		IEnumerable<Sync> GetAll();
		IEnumerable<Sync> GetConflicts();
	}
}
