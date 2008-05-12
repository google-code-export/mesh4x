using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Mesh4n.Tests;
using System.Data.SqlServerCe;
using System.IO;
using Microsoft.Practices.EnterpriseLibrary.Data.SqlCe;
using System.Threading;

namespace Mesh4n.Adapters.Data.Tests
{
	[TestClass]
	public class GenericSyncAdapterFixture : RepositoryFixture
	{
		GenericSyncAdapter adapter;
		string dbFile;

		protected override ISyncAdapter CreateRepository()
		{
			this.dbFile = "SyncDb" + Guid.NewGuid() + ".sdf";
			string connectionString = "Data Source=" + dbFile;

			if (File.Exists(dbFile))
				File.Delete(dbFile);

			new SqlCeEngine(connectionString).CreateDatabase();

			DbFactory databaseFactory = new SqlCeDbFactory();
			databaseFactory.ConnectionString = connectionString;
			
			this.adapter = new GenericSyncAdapter(databaseFactory, "Foo");

			return this.adapter;
		}

    	[TestCleanup]
		public void Cleanup()
		{
			if (adapter != null)
			{
				if (adapter.Database is SqlCeDatabase)
					((SqlCeDatabase)adapter.Database).CloseSharedConnection();

				File.Delete(this.dbFile);
			}
		}
	}
}
