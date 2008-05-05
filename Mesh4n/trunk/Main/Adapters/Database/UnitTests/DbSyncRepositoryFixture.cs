#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
using Microsoft.Practices.Mobile.DataAccess;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Practices.EnterpriseLibrary.Data.SqlCe;
using Microsoft.Practices.EnterpriseLibrary.Data;
#endif

using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.XPath;
using System.Data.SqlServerCe;
using System.IO;
using System.Data.Common;

using System.Data;
using System.ComponentModel;
using Mesh4n.Tests;

namespace Mesh4n.Adapters.Data.Tests
{
	[TestClass]
	public class DbSyncRepositoryFixture : SyncRepositoryFixture
	{
		private delegate void ExecuteDbHandler(DbConnection connection);
		private delegate void CleanupHandler(DbSyncRepository repository);

		protected DbFactory databaseFactory;
		protected string dbFile;

		[TestInitialize]
		public virtual void Initialize()
		{
			dbFile = "SyncDb" + Guid.NewGuid() + ".sdf";
			string connectionString = "Data Source=" + dbFile;

			if (File.Exists(dbFile))
				File.Delete(dbFile);

			new SqlCeEngine(connectionString).CreateDatabase();
#if PocketPC


			this.databaseFactory = new SqlDbFactory();
			this.databaseFactory.ConnectionString = connectionString;
#else
			this.databaseFactory = new SqlCeDbFactory();
			this.databaseFactory.ConnectionString = connectionString;
#endif
		}

		protected virtual void Cleanup(DbSyncRepository repository)
		{
			if (repository != null)
			{
#if PocketPC
			repository.Database.GetConnection().Close();
#else
				if (repository.Database is SqlCeDatabase)
					((SqlCeDatabase)repository.Database).CloseSharedConnection();
#endif
			}
		}

		protected virtual DbSyncRepository CreateRepository(DbFactory databaseFactory, string repositoryId)
		{
			DbSyncRepository repository = new DbSyncRepository(databaseFactory, repositoryId);
			return repository;
		}

		[TestMethod]
		public void ShouldAllowNullRepositoryId()
		{
			DbSyncRepository repository = null;
			try
			{
				repository = CreateRepository(databaseFactory, null);
			}
			finally
			{
				Cleanup(repository);
			}
		}

		[TestMethod]
		public void ShouldAllowEmptyRepositoryId()
		{
			DbSyncRepository repository = null;
			try
			{
				CreateRepository(databaseFactory, "");
			}
			finally
			{
				Cleanup(repository);
			}
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullDatabaseFactory()
		{
			CreateRepository(null, "Foo");
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullItemTimestamp()
		{
			DbSyncRepository repo = null;
			try
			{
				repo = CreateRepository(databaseFactory, "Foo");
				Sync s = new Sync(Guid.NewGuid().ToString());

				repo.Save(s);
			}
			finally
			{
				Cleanup(repo);
			}
		}

		[TestMethod]
		public void ShouldAddSingleSyncItem()
		{
			DbSyncRepository repo = null;
			try
			{
				repo = CreateRepository(databaseFactory, "Foo");
				Sync s = new Sync(Guid.NewGuid().ToString());
				s.Tag = "hash";

				repo.Save(s);

				int count = CountSyncRecords(s.Id);

				Assert.AreEqual(1, count);
			}
			finally
			{
				Cleanup(repo);
			}


		}

		[TestMethod]
		public void ShouldGetAllSyncItems()
		{
			DbSyncRepository repo = null;
			try
			{
				repo = CreateRepository(databaseFactory, "Foo");
				Sync s = new Sync(Guid.NewGuid().ToString());
				s.Tag = "hash";
				repo.Save(s);

				s = new Sync(Guid.NewGuid().ToString());
				s.Tag = "hash1";
				repo.Save(s);

				int count = 0;
				foreach (Sync sync in repo.GetAll())
				{
					count++;
				}

				Assert.AreEqual(2, count);
			}
			finally
			{
				Cleanup(repo);
			}


		}

		[TestMethod]
		public void ShouldModifySingleSyncItem()
		{
			DbSyncRepository repo = null;
			try
			{
				repo = CreateRepository(databaseFactory, "Foo");
				Sync s = new Sync(Guid.NewGuid().ToString());
				s.Tag = "hash";

				repo.Save(s);

				Sync s1 = s.Update("me", DateTime.Now, false);
				s1.Tag = "New Hash";
				repo.Save(s1);

				Sync s2 = repo.Get(s.Id);

				Assert.AreEqual("New Hash", s2.Tag);
			}
			finally
			{
				Cleanup(repo);
			}
		}

		private int CountSyncRecords(string syncId)
		{
			Database database = this.databaseFactory.CreateDatabase();

			DbConnection connection = null;
			try
			{
#if PocketPC
                connection = database.GetConnection();
#else
				connection = database.CreateConnection();
#endif
				connection.Open();
				DbCommand command = connection.CreateCommand();
				command.CommandText = "SELECT COUNT(*) FROM Usys_Mesh4n_Foo_Sync WHERE ID = id";

				DbParameter parameter = command.CreateParameter();
				parameter.ParameterName = database.BuildParameterName("id");
				parameter.DbType = DbType.String;
				parameter.Size = 254;
				parameter.Value = syncId;

				return (int)command.ExecuteScalar();
			}
			finally
			{
#if !PocketPC
				connection.Close();
#endif
			}
		}
	}
}
