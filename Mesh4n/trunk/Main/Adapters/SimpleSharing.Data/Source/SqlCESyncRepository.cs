using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.IO;
using System.Text;
using System.Xml;
using Microsoft.Practices.Mobile.DataAccess;

namespace SimpleSharing
{
    public class DbSyncRepository : DbRepository, ISyncRepository
    {
        private const string REPOSITORY_PREFIX = "SSE_";

        string repositoryId = String.Empty;

        public DbSyncRepository(Database database)
            : this(database, null)
        {
        }

        public DbSyncRepository(Database database, string repositoryId)
            : base(database)
        {
            if (!String.IsNullOrEmpty(repositoryId))
                this.repositoryId = repositoryId;
        }

        public Sync Get(string id)
        {
            OpenConnection(); // Needed to initialize the database
            using (DbDataReader reader = Database.ExecuteReader(
                FormatSQL(@"SELECT * FROM [{0}] WHERE Id = {1}", "Sync", "id"),
                Database.CreateParameter("id", DbType.String, 254, id)))
            {
                if (reader.Read())
                    return Read(reader);
                return null;
            }
        }

        public void Save(Sync sync)
        {
            Guard.ArgumentNotNull(sync.ItemTimestamp, "sync.ItemTimestamp");

            string data = Write(sync);

            DbConnection conn = OpenConnection();
            using (DbTransaction transaction = conn.BeginTransaction())
            {
                using (DbCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandText = FormatSQL(@"
UPDATE [{0}] 
   SET Sync = {2}, ItemTimestamp = {3}
 WHERE Id   = {1}", "Sync", "id", "sync", "ts");

                    int count = Database.ExecuteNonQuery(cmd,
                        Database.CreateParameter("id", DbType.String, 254, sync.Id),
                        Database.CreateParameter("sync", DbType.String, 0, data),
                        Database.CreateParameter("ts", DbType.DateTime, 0, sync.ItemTimestamp));
                    if (count == 0)
                    {
                        cmd.CommandText = FormatSQL(@"
INSERT INTO [{0}] (Id, Sync, ItemTimestamp)
VALUES ({1}, {2}, {3})", "Sync", "id", "sync", "ts");
                        // The parameters are already set on the command
                        count = Database.ExecuteNonQuery(cmd);
                    }
                }
                transaction.Commit();
            }
        }

        public DateTime? GetLastSync(string feed)
        {
            OpenConnection(); // Needed to initialize the database
            using (DbDataReader reader = Database.ExecuteReader(
                FormatSQL(@"SELECT LastSync FROM [{0}] WHERE Feed = {1}", "LastSync", "feed"),
                Database.CreateParameter("feed", DbType.String, 1000, feed)))
            {
                if (reader.Read())
                    return reader.GetDateTime(0);
                return null;
            }
        }

        public void SetLastSync(string feed, DateTime date)
        {
            DbConnection conn = OpenConnection();
            using (DbTransaction transaction = conn.BeginTransaction())
            {
                using (DbCommand cmd = conn.CreateCommand())
                {
                    cmd.CommandText = FormatSQL(@"
UPDATE [{0}] 
   SET LastSync = {1}
 WHERE Feed     = {2}", "LastSync", "date", "feed");

                    int count = Database.ExecuteNonQuery(cmd,
                        Database.CreateParameter("date", DbType.DateTime, 0, date),
                        Database.CreateParameter("feed", DbType.String, 1000, feed));
                    if (count == 0)
                    {
                        cmd.CommandText = FormatSQL(@"
INSERT INTO [{0}] (LastSync, Feed)
VALUES ({1}, {2})", "LastSync", "date", "feed");
                        // The parameters are already set on the command
                        count = Database.ExecuteNonQuery(cmd);
                    }

                    transaction.Commit();
                }
            }
        }

        public IEnumerable<Sync> GetAll()
        {
            OpenConnection(); // Needed to initialize the database
            using (DbDataReader reader = Database.ExecuteReader(FormatSQL("SELECT * FROM [{0}]", "Sync")))
            {
                while (reader.Read())
                {
                    yield return Read(reader);
                }
            }
        }

        public IEnumerable<Sync> GetConflicts()
        {
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
            using (XmlReader xr = XmlReader.Create(new StringReader(xml)))
            {
                xr.MoveToContent();

                Sync sync = new FeedReader.SyncXmlReader(xr, new RssFeedReader(xr)).ReadSync();
                sync.ItemTimestamp = (DateTime)reader["ItemTimestamp"];
                return sync;
            }
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

        protected override void InitializeSchema(DbConnection conn)
        {
            if (!Database.TableExists(FormatTableName("Sync")))
            {
                Database.ExecuteNonQuery(FormatSQL(@"
CREATE TABLE [{0}](
	[Id]            NVARCHAR(254) NOT NULL PRIMARY KEY,
	[Sync]          NTEXT NULL, 
	[ItemTimestamp] DATETIME NOT NULL
)", "Sync"));
            }

            if (!Database.TableExists(FormatTableName("LastSync")))
            {
                Database.ExecuteNonQuery(FormatSQL(@"
CREATE TABLE [{0}](
	[Feed]     NVARCHAR(1000) NOT NULL PRIMARY KEY,
	[LastSync] DATETIME NOT NULL
)", "LastSync"));
            }
        }

        protected string FormatTableName(string tableName)
        {
            StringBuilder buf = new StringBuilder(REPOSITORY_PREFIX);
            if (!String.IsNullOrEmpty(repositoryId))
            {
                buf.Append(repositoryId);
                buf.Append('_');
            }
            buf.Append(tableName);
            return buf.ToString();
        }

        protected string FormatSQL(string cmd, string tableName, params string[] parms)
        {
            string[] names = new string[1 + (parms != null ? parms.Length : 0)];
            names[0] = FormatTableName(tableName);
            if (parms != null)
            {
                int index = 1;
                foreach (string parm in parms)
                    names[index++] = Database.BuildParameterName(parm);
            }
            return String.Format(cmd, names);
        }
    }
}
