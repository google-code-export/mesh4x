using System;
using System.Collections.Generic;
using System.Text;
using System.Data.Common;
using System.Data;
using System.IO;
using System.Xml;
using System.Diagnostics;
#if !PocketPC
using Microsoft.Practices.EnterpriseLibrary.Data;
#else
using Microsoft.Practices.Mobile.DataAccess;
using System.Globalization;
#endif

namespace Mesh4n.Adapters.Data
{
	public partial class DbSyncRepository : DbRepository, ISyncRepository
	{
		private const string RepositoryPrefix = "Usys_Mesh4n_";
		string repositoryId;

		public DbSyncRepository(DbFactory factory) 
			: this(factory, null)
		{
		}

		public DbSyncRepository(DbFactory factory, string repositoryId) 
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

		public Sync Get(string id)
		{
			Tracer.TraceData(this, TraceEventType.Verbose, "Getting sync with ID {0}", id);

			using (DbDataReader reader = ExecuteReader(
				FormatSql(@"SELECT * FROM [{0}] WHERE Id = {1}", "Sync", "pid"),
				CreateParameter("pid", DbType.String, 254, id)))
			{
				if (reader.Read())
				{
					Tracer.TraceData(this, TraceEventType.Verbose, "Item with ID {0} found", id);

					return Read(reader);
				}

				Tracer.TraceData(this, TraceEventType.Verbose, "Item with ID {0} not found", id);

				return null;
			}
		}

		public void Save(Sync sync)
		{
			string data = Write(sync);

			Tracer.TraceData(this, TraceEventType.Verbose, "Saving sync with ID {0}", sync.Id);

			ExecuteDb(delegate(DbConnection conn)
			{
				using (DbTransaction transaction = conn.BeginTransaction())
				{
					object itemHash = DBNull.Value;
					if (sync.Tag != null)
						itemHash = sync.Tag.ToString();

					int count;
					using (DbCommand cmd = conn.CreateCommand())
					{
						if (sync.LastUpdate != null && sync.LastUpdate.When.HasValue)
						{
							cmd.CommandText = FormatSql(
								"UPDATE [{0}] " +
								"SET Sync = {1}, ItemHash = {2}, LastUpdate = {3} " +
								"WHERE Id = {4}", "Sync", "sync", "itemHash", "lastUpdate", "id");

							count = ExecuteNonQuery(cmd,
								CreateParameter("sync", DbType.String, 0, data),
								CreateParameter("itemHash", DbType.String, 254, itemHash),
								CreateParameter("lastUpdate", DbType.String, 50, Timestamp.Normalize(sync.LastUpdate.When.Value).ToString()),
								CreateParameter("id", DbType.String, 254, sync.Id));
						}
						else
						{
							cmd.CommandText = FormatSql(
								"UPDATE [{0}] " +
								"SET Sync = {1}, [ItemHash] = {2} " +
								"WHERE Id = {3}", "Sync", "sync", "itemHash", "id");

							count = ExecuteNonQuery(cmd,
								CreateParameter("sync", DbType.String, 0, data),
								CreateParameter("itemHash", DbType.String, 254, itemHash),
								CreateParameter("id", DbType.String, 254, sync.Id));
						}

						Tracer.TraceData(this, TraceEventType.Verbose, "Sync with ID {0} updated - Record count {1}", sync.Id, count);
					}

					if (count == 0)
					{
						using (DbCommand cmd = conn.CreateCommand())
						{
							cmd.CommandText = FormatSql(
								"INSERT INTO [{0}] (Id, Sync, [ItemHash]) " +
								"VALUES ({1}, {2}, {3})", "Sync", "id", "sync", "itemHash");

							ExecuteNonQuery(cmd,
								CreateParameter("id", DbType.String, 254, sync.Id),
								CreateParameter("sync", DbType.String, 0, data),
								CreateParameter("itemHash", DbType.String, 254, itemHash));
						}

						Tracer.TraceData(this, TraceEventType.Verbose, "Sync with ID {0} inserted", sync.Id);
					}
					transaction.Commit();
				}
			});
		}

		public IEnumerable<Sync> GetAll()
		{
			Tracer.TraceData(this, TraceEventType.Verbose, "Getting all sync");

			using (DbDataReader reader = ExecuteReader(FormatSql("SELECT * FROM [{0}]", "Sync")))
			{
				while (reader.Read())
				{
					yield return Read(reader);
				}
			}
		}

		public IEnumerable<Sync> GetConflicts()
		{
			Tracer.TraceData(this, TraceEventType.Verbose, "Getting all conflicts");

			// TODO: sub-optimal.
			foreach (Sync sync in GetAll())
			{
				if (sync.Conflicts.Count > 0)
					yield return sync;
			}
		}

		private Sync Read(DbDataReader reader)
		{
			string xml = (string)reader["Sync"];
			XmlReader xr = XmlReader.Create(new StringReader(xml));
			xr.MoveToContent();

			Sync sync = new FeedReader.SyncXmlReader(xr, new RssFeedReader(xr)).ReadSync();
			sync.Tag = reader["ItemHash"] as string;

			Tracer.TraceData(this, TraceEventType.Verbose, "Sync read, ID {0}, tag {1}", sync.Id, sync.Tag);

			return sync;
		}

		protected virtual void InitializeSchema(DbConnection cn)
		{
			if (!TableExists(cn, FormatTableName(repositoryId, "Sync")))
			{
				ExecuteNonQuery(FormatSql(@"
						CREATE TABLE [{0}](
							[Id] NVARCHAR(254) NOT NULL PRIMARY KEY,
							[Sync] NTEXT NULL, 
                            [LastUpdate] DATETIME NULL,
							[ItemHash] NVARCHAR(254) NULL
						)", "Sync"));
			}

			if (!TableExists(cn, FormatTableName(repositoryId, "LastSync")))
			{
				ExecuteNonQuery(FormatSql(@"
						CREATE TABLE [{0}](
							[Feed] NVARCHAR(1000) NOT NULL PRIMARY KEY,
							[LastSync] DATETIME NOT NULL
						)", "LastSync"));
			}
		}

		protected string FormatTableName(string tableName)
		{
			return FormatTableName(repositoryId, tableName);
		}

		private static string FormatTableName(string repositoryId, string tableName)
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

		protected string FormatSql(string cmd, string tableName, params string[] parms)
		{
			string[] names = new string[1 + (parms != null ? parms.Length : 0)];
			names[0] = FormatTableName(tableName);
			if (parms != null)
			{
				int index = 1;
				
#if !PocketPC
				if (this.Database is GenericDatabase)
				{
					for (index = 1; index < parms.Length + 1; index++ )
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

		private string Write(Sync sync)
		{
			StringWriter sw = new StringWriter();
			using (XmlWriter xw = XmlWriter.Create(sw))
			{
				new RssFeedWriter(xw).WriteSync(sync);
			}
			return sw.ToString();
		}
	}
}
