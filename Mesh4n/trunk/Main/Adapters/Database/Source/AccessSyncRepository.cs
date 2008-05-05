using System;
using System.Collections.Generic;
using System.Text;
using System.Data.Common;
using System.Data;
using System.IO;
using System.Xml;
using Microsoft.Practices.EnterpriseLibrary.Data;

namespace Mesh4n.Adapters.Data
{
	public class AccessSyncRepository : DbSyncRepository
	{
		public AccessSyncRepository(DbFactory factory)
			: base(factory)
		{
		}

		public AccessSyncRepository(DbFactory factory, string repositoryId)
			: base(factory, repositoryId)
		{
		}

		protected override void InitializeSchema(DbConnection cn)
		{
			if (!TableExists(cn, FormatTableName("Sync")))
			{
				DbCommand cmd = cn.CreateCommand();
				cmd.CommandType = CommandType.Text;
				cmd.Connection = cn;
				cmd.CommandText = FormatSql(@"
						CREATE TABLE [{0}](
							[Id] TEXT (254) NOT NULL PRIMARY KEY,
							[Sync] NTEXT NULL,
                            [LastUpdate] DATETIME NULL, 
							[ItemHash] TEXT NULL
						)", "Sync");
				cmd.ExecuteNonQuery();
			}

			if (!TableExists(cn, FormatTableName("LastSync")))
			{
				DbCommand cmd = cn.CreateCommand();
				cmd.CommandType = CommandType.Text;
				cmd.Connection = cn;
				cmd.CommandText = FormatSql(@"
						CREATE TABLE [{0}](
							[Feed] TEXT NOT NULL PRIMARY KEY,
							[LastSync] DATETIME NOT NULL
						)", "LastSync");
				cmd.ExecuteNonQuery();
			}
		}
	}
}
