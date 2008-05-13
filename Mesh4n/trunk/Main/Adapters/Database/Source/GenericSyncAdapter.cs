using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.EnterpriseLibrary.Data;
using System.Data.Common;
using System.Data;
using System.Xml;
using System.IO;
using Microsoft.Practices.EnterpriseLibrary.Data.SqlCe;
using System.Data.SqlTypes;
using System.ComponentModel;

namespace Mesh4n.Adapters.Data
{
	public class GenericSyncAdapter : DbRepository, ISyncAdapter, ISupportInitialize
	{
		private const string RepositoryPrefix = "Mesh4n_";
		
		string repositoryId;

		public GenericSyncAdapter() : base()
		{
		}

		public GenericSyncAdapter(DbFactory factory) 
			: this(factory, null)
		{
		}

		public GenericSyncAdapter(DbFactory factory, string repositoryId) 
			: base(factory)
		{
			this.repositoryId = repositoryId;

			ExecuteDb(delegate(DbConnection cn)
			{
				InitializeSchema(cn);
			});
		}

		public string RepositoryId
		{
			get { return repositoryId; }
			set { repositoryId = value; }
		}

		public bool SupportsMerge
		{
			get { return false; }
		}

		public Item Get(string id)
		{
			Guard.ArgumentNotNullOrEmptyString(id, "id");

			using (DbDataReader reader = ExecuteReader(
				FormatSql(@"SELECT * FROM [{0}] WHERE Id = {1}", "Sync", "pid"),
				CreateParameter("pid", DbType.String, 254, id)))
			{
				if (reader.Read())
				{
					Item item = Read((string)reader["Item"]);
					if (item.Sync.Deleted)
						return new Item(new NullXmlItem(item.Sync.Id), item.Sync);
					else
						return item;
				}

				return null;
			}
		}

		public IEnumerable<Item> GetAll(DateTime? since, Predicate<Item> filter)
		{
			Guard.ArgumentNotNull(filter, "filter");

			DbDataReader reader = null;
			try
			{
				try
				{
					if (since.HasValue)
					{
						since = Timestamp.Normalize(since.Value);

						reader = ExecuteReader(
							FormatSql(@"SELECT * FROM [{0}] WHERE LastUpdate >= {1} OR LastUpdate IS NULL", "Sync", "sd"),
							CreateParameter("sd", DbType.DateTime, 0, since));
					}
					else
					{
						reader = ExecuteReader(
							FormatSql(@"SELECT * FROM [{0}]", "Sync"));
					}
				}
				catch (Exception ex)
				{
					string s = ex.Message;
				}

				while (reader.Read())
				{
					Item item = Read((string)reader["Item"]);
					if (filter(item))
					{
						if (item.Sync.Deleted)
							yield return new Item(new NullXmlItem(item.Sync.Id), item.Sync);
						else
							yield return item;
					}
				}
			}
			finally
			{
				if (reader != null)
					reader.Close();
			}
		}

		public IEnumerable<Item> GetAll()
		{
			return GetAllSince(null, NullFilter);
		}

		public IEnumerable<Item> GetAll(Predicate<Item> filter)
		{
			return GetAllSince(null, filter);
		}

		public IEnumerable<Item> GetAllSince(DateTime? since)
		{
			return GetAllSince(since, NullFilter);
		}

		public IEnumerable<Item> GetAllSince(DateTime? since, Predicate<Item> filter)
		{
			return GetAll(since == null ? since : Timestamp.Normalize(since.Value), filter);
		}

		public IEnumerable<Item> GetConflicts()
		{
			using (DbDataReader reader = ExecuteReader(
				FormatSql(@"SELECT * FROM [{0}] WHERE Conflicts = 1", "Sync")))
			{
				while (reader.Read())
				{
					yield return Read((string)reader["Item"]);
				}

				yield break;
			}
		}

		public void Add(Item item)
		{
			Guard.ArgumentNotNull(item, "item");

			if(Contains(item.Sync.Id))
				throw new ArgumentException("An item with the same id already exists in the database");
			
			string xml = Write(item);
			bool conflicts = item.Sync.Conflicts.Count > 0;
			
			object lastUpdate = DBNull.Value;
			if (item.Sync.LastUpdate.When.HasValue)
				lastUpdate = item.Sync.LastUpdate.When.Value;

			ExecuteDb(delegate(DbConnection conn)
			{
				using (DbCommand cmd = conn.CreateCommand())
				{
					cmd.CommandText = FormatSql(
							"INSERT INTO [{0}] (Id, Item, LastUpdate, Conflicts) VALUES " +
							"({1}, {2}, {3}, {4}) ", "Sync", "id", "item", "lastUpdate", "conflicts");

					ExecuteNonQuery(cmd,
						CreateParameter("id", DbType.String, 254, item.Sync.Id),
						CreateParameter("item", DbType.String, 0, xml),
						CreateParameter("lastUpdate", DbType.DateTime, 0, lastUpdate),
						CreateParameter("conflicts", DbType.Boolean, 0, conflicts));
				}
			});

			
		}

		public void Delete(string id)
		{
			Guard.ArgumentNotNullOrEmptyString(id, "id");

			ExecuteDb(delegate(DbConnection conn)
			{
				using (DbCommand cmd = conn.CreateCommand())
				{
					cmd.CommandText = FormatSql(
								"DELETE [{0}] " +
								"WHERE Id = {1}", "Sync", "id");

					ExecuteNonQuery(cmd,
						CreateParameter("id", DbType.String, 254, id));
				}
			});
		}

		public void Update(Item item)
		{
			Guard.ArgumentNotNull(item, "item");

			string xml = Write(item);
			bool conflicts = item.Sync.Conflicts.Count > 0;

			object lastUpdate = DBNull.Value;
			if (item.Sync.LastUpdate.When.HasValue)
				lastUpdate = item.Sync.LastUpdate.When.Value;

			ExecuteDb(delegate(DbConnection conn)
			{
				using (DbCommand cmd = conn.CreateCommand())
				{
					cmd.CommandText = FormatSql(
								"UPDATE [{0}] " +
								"SET Item = {1}, LastUpdate = {2}, Conflicts = {3} " +
								"WHERE Id = {4}", "Sync", "item", "lastUpdate", "conflicts", "id");

					ExecuteNonQuery(cmd,
						CreateParameter("item", DbType.String, 0, xml),
						CreateParameter("lastUpdate", DbType.DateTime, 0, lastUpdate),
						CreateParameter("conflicts", DbType.Boolean, 0, conflicts),
						CreateParameter("id", DbType.String, 254, item.Sync.Id));
				}
			});
		}

		public Item Update(Item item, bool resolveConflicts)
		{
			if (resolveConflicts)
			{
				item = Behaviors.ResolveConflicts(item, DeviceAuthor.Current, DateTime.Now, item.Sync.Deleted);
			}

			Update(item);

			return item;
		}

		public IEnumerable<Item> Merge(IEnumerable<Item> items)
		{
			throw new NotImplementedException();
		}

		public string FriendlyName
		{
			get { return "Generic Database";  }
		}

		private static bool NullFilter(Item item)
		{
			return true;
		}

		protected void InitializeSchema(DbConnection cn)
		{
			if (!TableExists(cn, FormatTableName(repositoryId, "Sync")))
			{
				ExecuteNonQuery(FormatSql(@"
						CREATE TABLE [{0}](
							[Id] NVARCHAR(254) NOT NULL PRIMARY KEY,
							[Item] NTEXT NOT NULL, 
                            [LastUpdate] DATETIME NULL,
							[Conflicts] BIT NOT NULL
						)", "Sync"));
			}
		}

		protected string FormatSql(string cmd, string tableName, params string[] parms)
		{
			string[] names = new string[1 + (parms != null ? parms.Length : 0)];
			names[0] = FormatTableName(repositoryId, tableName);
			if (parms != null)
			{
				int index = 1;

#if !PocketPC
				if (this.Database is GenericDatabase)
				{
					for (index = 1; index < parms.Length + 1; index++)
						names[index] = "?";
				}
				else
				{
					foreach (string parm in parms)
						names[index++] = this.Database.BuildParameterName(parm);
				}
#else
				foreach (string parm in parms)
						names[index++] = this.Database.BuildParameterName(parm);
#endif

			}
			return String.Format(cmd, names);
		}

		private string FormatTableName(string repositoryId, string tableName)
		{
			if (!String.IsNullOrEmpty(repositoryId))
			{
				return RepositoryPrefix + repositoryId + "_" + tableName;
			}
			else
			{
				return RepositoryPrefix + tableName;
			}
		}

		private bool Contains(string id)
		{
			using (DbDataReader reader = ExecuteReader(
				FormatSql(@"SELECT COUNT(*) FROM [{0}] WHERE Id = {1}", "Sync", "idi"),
				CreateParameter("idi", DbType.String, 254, id)))
			{
				if (reader.Read())
				{
					return ((int)reader[0] > 0);
				}
			}

			return false;
		}


		private Item Read(string itemXml)
		{
			using (XmlReader xmlReader = XmlReader.Create(new StringReader(itemXml)))
			{
				xmlReader.MoveToContent();

				IEnumerable<Item> items;

				FeedReader feedReader = FeedReader.Create(xmlReader);
				feedReader.Read(out items);

				IEnumerator<Item> enumerator = items.GetEnumerator();
				if (enumerator.MoveNext())
					return enumerator.Current;

			}

			return null;
		}

		private string Write(Item item)
		{
			StringWriter sw = new StringWriter();
			using (XmlWriter xw = XmlWriter.Create(sw))
			{
				new RssFeedWriter(xw).Write(item);
			}
			return sw.ToString();
		}

		public void BeginInit()
		{
		}

		public void EndInit()
		{
			ExecuteDb(delegate(DbConnection cn)
			{
				InitializeSchema(cn);
			});
		}
	}
}
