#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using SimpleSharing;
using System.Data.SqlServerCe;
using Microsoft.Practices.EnterpriseLibrary.Data.SqlCe;

namespace CustomerLibrary.Tests
{
	[TestClass]
	public class CustomerRepositoryFixture
	{
		const string ConnectionString = "Data Source=CustomerDb.sdf"; 

		[TestInitialize]
		public void Initialize()
		{
			if (File.Exists("CustomerDb.sdf"))
				File.Delete("CustomerDb.sdf");

			SqlCeEngine engine = new SqlCeEngine(ConnectionString);
			engine.CreateDatabase();

			CustomerDataAccess dac = new CustomerDataAccess(new SqlCeDatabase(ConnectionString));
			dac.Add(new Customer("Daniel", "Cazzulino", new DateTime(1974, 4, 9)));
			dac.Add(new Customer("Victor", "Garcia Aprea", new DateTime(1975, 2, 21)));
		}

		[TestMethod]
		public void MapperCanAssignGuid()
		{
			CustomerIdMapper mapper = new CustomerIdMapper(new SqlCeDatabase(ConnectionString));

			string id1 = mapper.Map(1);
			string id2 = mapper.Map(1);

			Assert.AreEqual(id1, id2);
		}

		[TestMethod]
		public void NonExistingGuidReturnsNegativeId()
		{
			CustomerIdMapper mapper = new CustomerIdMapper(new SqlCeDatabase(ConnectionString));

			int id = mapper.Map(Guid.NewGuid().ToString());

			Assert.AreEqual(-1, id);
		}

		[TestMethod]
		public void CanGetAll()
		{
			CustomerRepository repo = new CustomerRepository(new SqlCeDatabase(ConnectionString));

			Assert.AreEqual(2, new List<IXmlItem>(repo.GetAll()).Count);
		}

		[TestMethod]
		public void CanGetCustomerItem()
		{
			CustomerRepository repo = new CustomerRepository(new SqlCeDatabase(ConnectionString));
			CustomerIdMapper mapper = new CustomerIdMapper(new SqlCeDatabase(ConnectionString));

			IXmlItem item = repo.Get(mapper.Map(1));

			Assert.IsNotNull(item);
			Assert.AreEqual("Daniel, Cazzulino", item.Title);
		}

		[TestMethod]
		public void CanAddCustomerFromItem()
		{
			CustomerIdMapper mapper = new CustomerIdMapper(new SqlCeDatabase(ConnectionString));
			CustomerConverter converter = new CustomerConverter(mapper);
			Customer c = new Customer("Ed", "Jez", new DateTime(1974, 5, 13));

			IXmlItem item = converter.Convert(c);
			CustomerRepository repo = new CustomerRepository(new SqlCeDatabase(ConnectionString));

			repo.Add(item);

			IXmlItem savedItem = repo.Get(item.Id);

			Assert.IsNotNull(savedItem);
			Assert.AreEqual(item.Id, savedItem.Id);
		}

		[TestMethod]
		public void CanUpdateCustomerFromItem()
		{
			CustomerIdMapper mapper = new CustomerIdMapper(new SqlCeDatabase(ConnectionString));
			CustomerConverter converter = new CustomerConverter(mapper);
			CustomerRepository repo = new CustomerRepository(new SqlCeDatabase(ConnectionString));

			IXmlItem item = repo.Get(mapper.Map(1));
			Customer c = converter.Convert(item);
			c.FirstName = "kzu";

			item = converter.Convert(c);
			repo.Update(item);

			Customer c2 = converter.Convert(repo.Get(item.Id));

			Assert.AreEqual("kzu", c2.FirstName);
		}

		[TestMethod]
		public void CanDeleteCustomer()
		{
			CustomerIdMapper mapper = new CustomerIdMapper(new SqlCeDatabase(ConnectionString));
			CustomerRepository repo = new CustomerRepository(new SqlCeDatabase(ConnectionString));

			repo.Remove(mapper.Map(1));

			Assert.AreEqual(1, new List<IXmlItem>(repo.GetAll()).Count);
		}
	}
}
