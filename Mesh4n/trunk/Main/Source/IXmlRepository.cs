using System;
using System.Collections.Generic;

namespace SimpleSharing
{
	[Obsolete("Use IRepository interface directly")]
	public interface IXmlRepository
	{
		void Add(IXmlItem item, out object tag);
		bool Contains(string id);
		IXmlItem Get(string id);
		bool Remove(string id);

		void Update(IXmlItem item, out object tag);
		IEnumerable<IXmlItem> GetAll();
		IEnumerable<IXmlItem> GetAllSince(DateTime since);
	}
}
