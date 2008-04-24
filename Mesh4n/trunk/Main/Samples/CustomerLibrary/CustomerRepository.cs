using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;
using System.Xml.XPath;
using System.Xml.Serialization;
using System.Data.Common;
using System.Data;
using CustomerLibrary;
using SimpleSharing;
using Microsoft.Practices.EnterpriseLibrary.Data;

namespace CustomerLibrary
{
	public class CustomerRepository : IXmlRepository
	{
		CustomerDataAccess dac;
		CustomerConverter converter;
		CustomerIdMapper mapper;

		public CustomerRepository(Database database)
		{
			dac = new CustomerDataAccess(database);
			mapper = new CustomerIdMapper(database);
			converter = new CustomerConverter(mapper);
		}

		public void Add(IXmlItem item)
		{
			Customer customer = converter.Convert(item);
			int customerId = dac.Add(customer);

			mapper.Map(item.Id, customerId);
		}

		public bool Contains(string id)
		{
			return dac.Exists(mapper.Map(id));
		}

		public void Update(IXmlItem item)
		{
			Customer customer = converter.Convert(item);
			if (!dac.Update(customer))
				throw new InvalidOperationException("Could not update customer");
		}

		public bool Remove(string id)
		{
			return dac.Delete(mapper.Map(id));
		}

		public IXmlItem Get(string id)
		{
			Customer c = dac.GetById(mapper.Map(id));
			if (c == null) return null;

			return converter.Convert(c);
		}

		public IEnumerable<IXmlItem> GetAll()
		{
			foreach (Customer c in dac.GetAll())
			{
				yield return converter.Convert(c);
			}
		}

		public IEnumerable<IXmlItem> GetAllSince(DateTime date)
		{
			foreach (Customer c in dac.GetAll())
			{
				if (c.Timestamp >= date)
				{
					yield return converter.Convert(c);
				}
			}
		}
	}
}
