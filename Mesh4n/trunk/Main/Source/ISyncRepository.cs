using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	public interface ISyncRepository
	{
		Sync Get(string id);
		void Save(Sync sync);

		IEnumerable<Sync> GetAll();
		IEnumerable<Sync> GetConflicts();
	}
}
