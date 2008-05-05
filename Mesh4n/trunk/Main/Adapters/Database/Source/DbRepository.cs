using System;
using System.Collections.Generic;
using System.Text;
using System.Data.Common;
using System.Data;
using System.Reflection;

#if !PocketPC
using Microsoft.Practices.EnterpriseLibrary.Data;
using System.Diagnostics;
#else
using Microsoft.Practices.Mobile.DataAccess;
#endif

namespace Mesh4n.Adapters.Data
{
	public abstract partial class DbRepository
	{
		protected delegate void ExecuteDbHandler(DbConnection connection);

		Database database;
		DbFactory dbFactory;

		protected DbRepository(DbFactory factory)
		{
			if (factory == null)
				throw new ArgumentNullException("factory");

			this.dbFactory = factory;
			this.database = factory.CreateDatabase();
		}

		public Database Database
		{
			get
			{
				return database;
			}
		}


		public DbFactory DatabaseFactory
		{
			get { return dbFactory; }
			set
			{
				dbFactory = value;
				if(dbFactory != null)
					database = dbFactory.CreateDatabase();
			}
		}

		protected DbDataReader ExecuteReader(string sqlCommand, params DbParameter[] parameters)
		{
			Tracer.TraceData(this, TraceEventType.Verbose, "ExecuteReader: {0}", sqlCommand);

#if PocketPC
			return Database.ExecuteReader(sqlCommand, parameters);
#else
			DbCommand cmd = Database.DbProviderFactory.CreateCommand();
			cmd.CommandText = sqlCommand;
			cmd.Parameters.AddRange(parameters);
			DbConnection conn = Database.CreateConnection();
			cmd.Connection = conn;
			conn.Open();
			return cmd.ExecuteReader(CommandBehavior.CloseConnection);
#endif
		}

		protected int ExecuteNonQuery(string sqlCommand, params DbParameter[] parameters)
		{
			Tracer.TraceData(this, TraceEventType.Verbose, "ExecuteNonQuery: {0}", sqlCommand);

#if PocketPC
			int count = Database.ExecuteNonQuery(sqlCommand, parameters);
			Tracer.TraceData(this, TraceEventType.Verbose, "Affected rows: {0}", count);

			return count;
#else
			DbCommand cmd = Database.DbProviderFactory.CreateCommand();
			cmd.CommandText = sqlCommand;
			cmd.CommandType = CommandType.Text;
			cmd.Parameters.AddRange(parameters);
			using (DbConnection conn = Database.CreateConnection())
			{
				cmd.Connection = conn;
				conn.Open();
				int count = cmd.ExecuteNonQuery();
				conn.Close();

				Tracer.TraceData(this, TraceEventType.Verbose, "Affected rows: {0}", count);

				return count;
			}
#endif
		}

		protected int ExecuteNonQuery(DbCommand command, params DbParameter[] parameters)
		{
			Tracer.TraceData(this, TraceEventType.Verbose, "ExecuteNonQuery: {0}", command.CommandText);

#if PocketPC
			int count = Database.ExecuteNonQuery(command, parameters);
			Tracer.TraceData(this, TraceEventType.Verbose, "Affected rows: {0}", count);
			
			return count;
#else
			command.Parameters.AddRange(parameters);
			using (DbConnection conn = Database.CreateConnection())
			{
				command.Connection = conn;
				conn.Open();
				int count = command.ExecuteNonQuery();
				conn.Close();

				Tracer.TraceData(this, TraceEventType.Verbose, "Affected rows: {0}", count);

				return count;
			}

#endif
		}

		protected virtual DbParameter CreateParameter(string name, DbType type, int size, object value)
		{
#if PocketPC
			return Database.CreateParameter(name, type, size, value);
#else
			DbParameter param = Database.DbProviderFactory.CreateParameter();
			param.ParameterName = Database.BuildParameterName(name);
			param.DbType = type;
			param.Value = value;

			return param;
#endif
		}

		protected void ExecuteDb(ExecuteDbHandler handler)
		{
#if PocketPC
			DbConnection conn = Database.GetConnection();
			handler(conn);
#else
			using (DbConnection conn = Database.CreateConnection())
			{
				conn.Open();
				handler(conn);
				conn.Close();
			}
#endif
		}

		/// <summary>
		/// Checks if the given table exist.
		/// </summary>
		protected virtual bool TableExists(DbConnection connection, string tableName)
		{
#if PocketPC
			return database.TableExists(tableName);
#else
			// First try ADO.NET schema mechanism.
			try
			{
				DataTable userTables = connection.GetSchema("Tables");
				foreach (DataRow row in userTables.Rows)
				{
					if (tableName == row["TABLE_NAME"].ToString())
						return true;
				}

				return false;
			}
			catch (NotSupportedException)
			{
				// If it didn't work, try the ANSI SQL92 INFORMATION_SCHEMA 
				/// view on the database to query for the given table.
				using (DbCommand cmd = connection.CreateCommand())
				{
					cmd.CommandType = CommandType.Text;
					cmd.CommandText = String.Format(@"
					SELECT COUNT(*) 
					FROM	[INFORMATION_SCHEMA].[TABLES] 
					WHERE	[TABLE_NAME] = '{0}'",
							tableName);
					cmd.Connection = connection;

					int count = Convert.ToInt32(cmd.ExecuteScalar());

					return count != 0;
				}
			}
#endif
		}

	}
}
