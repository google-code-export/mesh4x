using System;
using System.Collections.Generic;

namespace Mesh4n
{
	public interface IContentAdapter
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
