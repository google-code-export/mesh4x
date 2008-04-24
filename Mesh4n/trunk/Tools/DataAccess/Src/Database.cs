//===============================================================================
// Microsoft patterns & practices
// Mobile Client Software Factory - July 2006
//===============================================================================
// Copyright  Microsoft Corporation.  All rights reserved.
// THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY
// OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE.
//===============================================================================
// The example companies, organizations, products, domain names,
// e-mail addresses, logos, people, places, and events depicted
// herein are fictitious.  No association with any real company,
// organization, product, domain name, email address, logo, person,
// places, or events is intended or should be inferred.
//===============================================================================

using System;
using System.Collections.Generic;
using System.Text;
using System.Data.SqlServerCe;
using System.Data.Common;
using System.Data;

namespace Microsoft.Practices.Mobile.DataAccess
{
	/// <summary>
	///		This class provides the foundation for simplified access to a database. It contains a number
	///		of helper methods that make working with the database easier.
	/// </summary>
	public abstract class Database : IDisposable
	{
		private DbProviderFactory dbProviderFactory;
		private string connectionString;
		private DbConnection databaseConnection;

		private Database() { }

		/// <summary>
		///		The base constructor that derived classes must call.
		/// </summary>
		/// <param name="connectionString">
		///		The connection string that will be used to connect to the database.
		/// </param>
		/// <param name="dbProviderFactory">
		///		An object that can create the different database objects need to execute
		///		commands.
		/// </param>
		protected Database(string connectionString, DbProviderFactory dbProviderFactory)
		{
			Guard.ArgumentNotNullOrEmptyString(connectionString, "connectionString");
			Guard.ArgumentNotNull(dbProviderFactory, "dbProviderFactory");

			this.dbProviderFactory = dbProviderFactory;
			this.connectionString = connectionString;

			CreateConnection();
		}

		/// <summary>
		///		Closes the current connection. You should call this method when you are completely
		///		done using this database instance. Methods will fail after you've disposed of this
		///		instance.
		/// </summary>
		public virtual void Dispose()
		{
			if (databaseConnection != null)
			{
				databaseConnection.Close();			// .NET documents recommend closing instead of disposing connection
				databaseConnection = null;
			}
		}

		/// <summary>
		///		Creates a new, unopened connection instance for this database.
		/// </summary>
		/// <returns>
		/// An unopened <see cref="DbConnection"/> for this database.
		/// </returns>
		/// <seealso cref="DbConnection"/>        
		private void CreateConnection()
		{
			if (databaseConnection == null)
			{
				databaseConnection = dbProviderFactory.CreateConnection();
				databaseConnection.ConnectionString = ConnectionString;
			}
		}

		/// <summary>
		///		Gets the connection string for this instance.
		/// </summary>
		public string ConnectionString
		{
			get { return connectionString; }
		}

		/// <summary>
		/// Builds a value parameter name for the current database.
		/// </summary>
		/// <param name="name">The name of the parameter.</param>
		/// <returns>A correctly formated parameter name.</returns>
		public virtual string BuildParameterName(string name)
		{
			return name;
		}

		/// <summary>
		///		Creates a new parameter and sets the name of the parameter.
		/// </summary>
		/// <param name="name">The name of the parameter.</param>
		/// <returns>
		///		A new <see cref="DbParameter"/> instance of the correct type for this database.</returns>
		/// <remarks>
		///		The database will automatically add the correct prefix, like "@" for SQL Server, to the
		///		parameter name. In other words, you can just supply the name without a prefix.
		/// </remarks>
		public virtual DbParameter CreateParameter(string name)
		{
			DbParameter parameter = dbProviderFactory.CreateParameter();
			parameter.ParameterName = BuildParameterName(name);
			return parameter;
		}

		/// <summary>
		///		Creates a new parameter and sets the name of the parameter.
		/// </summary>
		/// <param name="name">The name of the parameter.</param>
		/// <param name="value">
		///		The value you want assigned to thsi parameter. A null value will be converted to
		///		a <see cref="DBNull"/> value in the parameter.
		/// </param>
		/// <returns>
		///		A new <see cref="DbParameter"/> instance of the correct type for this database.</returns>
		/// <remarks>
		///		The database will automatically add the correct prefix, like "@" for SQL Server, to the
		///		parameter name. In other words, you can just supply the name without a prefix.
		/// </remarks>
		public virtual DbParameter CreateParameter(string name, object value)
		{
			DbParameter param = CreateParameter(name);
			param.Value = (value == null) ? DBNull.Value : value;
			return param;
		}

		/// <summary>
		///		Creates a new parameter and sets the name of the parameter.
		/// </summary>
		/// <param name="name">The name of the parameter.</param>
		/// <param name="type">The type of the parameter.</param>
		/// <param name="size">The size of this parameter.</param>
		/// <param name="value">
		///		The value you want assigned to this parameter. A null value will be converted to
		///		a <see cref="DBNull"/> value in the parameter.
		/// </param>
		/// <returns>
		///		A new <see cref="DbParameter"/> instance of the correct type for this database.</returns>
		/// <remarks>
		///		The database will automatically add the correct prefix, like "@" for SQL Server, to the
		///		parameter name. In other words, you can just supply the name without a prefix.
		/// </remarks>
		public virtual DbParameter CreateParameter(string name, DbType type, int size, object value)
		{
			DbParameter param = CreateParameter(name);
			param.DbType = type;
			param.Size = size;
			param.Value = (value == null) ? DBNull.Value : value;
			return param;
		}

		/// <summary>
		///		The specific <see cref="DbProviderFactory"/> instance provided by a subclass of
		///		this class.
		/// </summary>
		public DbProviderFactory DbProviderFactory
		{
			get { return dbProviderFactory; }
		}

		/// <summary>
		///		Executes an SQL query with an optional set of parameters.
		/// </summary>
		/// <param name="command">The command to execute.</param>
		/// <param name="parameters">Zero or more parameters for the query.</param>
		/// <returns>The number of rows affected.</returns>
		public virtual int ExecuteNonQuery(DbCommand command, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNull(command, "Command");
			int result;
			DbConnection connection = GetConnection();
			PrepareCommand(command, connection, parameters);
			result = command.ExecuteNonQuery();
			return result;
		}

		/// <summary>
		///		Executes an SQL query with an optional set of parameters.
		/// </summary>
		/// <param name="sqlCommand">The SQL statement to execute.</param>
		/// <param name="parameters">Zero or more parameters for the query.</param>
		/// <returns>The number of rows affected.</returns>
		public virtual int ExecuteNonQuery(string sqlCommand, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNull(sqlCommand, "sqlCommand");

			using (DbCommand command = dbProviderFactory.CreateCommand())
			{
				command.CommandText = sqlCommand;
				return ExecuteNonQuery(command, parameters);
			}
		}

		/// <summary>
		///		Execute a command and return a <see cref="DbDataReader"/> that contains the rows
		///		returned.
		/// </summary>
		/// <param name="command">The command to execute.</param>
		/// <param name="parameters">Zero or more parameters for the query.</param>
		/// <returns>A <see cref="DbDataReader"/> that contains the rows returned by the query.</returns>
		public virtual DbDataReader ExecuteReader(DbCommand command, params DbParameter[] parameters)
		{
			DbDataReader result;
			Guard.ArgumentNotNull(command, "command");

			DbConnection connection = GetConnection();
			PrepareCommand(command, connection, parameters);

			result = command.ExecuteReader();
			return result;
		}

		/// <summary>
		///		Execute a command and return a <see cref="DbDataReader"/> that contains the rows
		///		returned.
		/// </summary>
		/// <param name="sqlCommand">The SQL query to execute.</param>
		/// <param name="parameters">Zero or more parameters for the query.</param>
		/// <returns>A <see cref="DbDataReader"/> that contains the rows returned by the query.</returns>
		public virtual DbDataReader ExecuteReader(string sqlCommand, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNullOrEmptyString(sqlCommand, "sqlCommand");

			using (DbCommand command = dbProviderFactory.CreateCommand())
			{
				command.CommandText = sqlCommand;
				return ExecuteReader(command, parameters);
			}
		}

		/// <summary>
		/// <para>
		///		Executes the <paramref name="command"/> and returns the first column of the first
		///		row in the result set returned by the query. Extra columns or rows are ignored.
		/// </para>
		/// </summary>
		/// <param name="command">
		/// <para>
		///		The command that contains the query to execute.
		/// </para>
		/// </param>
		/// <param name="parameters">Zero or more parameters for the query.</param>
		/// <returns>
		/// <para>
		///		The first column of the first row in the result set.
		/// </para>
		/// </returns>
		/// <seealso cref="IDbCommand.ExecuteScalar"/>
		public virtual object ExecuteScalar(DbCommand command, params DbParameter[] parameters)
		{
			object result;
			Guard.ArgumentNotNull(command, "command");

			DbConnection connection = GetConnection();
			PrepareCommand(command, connection, parameters);
			result = command.ExecuteScalar();
			return result;
		}

		/// <summary>
		///		Executes the <paramref name="command"/> and returns the first column of the first
		///		row in the result set returned by the query. Extra columns or rows are ignored.
		/// </summary>
		/// <param name="sqlCommand">The SQL statement to execute.</param>
		/// <param name="parameters">Zero or more parameters for the query.</param>
		/// <returns>
		/// <para>
		///		The first column of the first row in the result set.
		/// </para>
		/// </returns>
		/// <seealso cref="IDbCommand.ExecuteScalar"/>
		public virtual object ExecuteScalar(string sqlCommand, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNull(sqlCommand, "sqlCommand");

			using (DbCommand command = dbProviderFactory.CreateCommand())
			{
				command.CommandText = sqlCommand;
				return ExecuteScalar(command, parameters);
			}
		}

		/// <summary>
		/// <para>Returns the shared connection, and opens it the first time.</para>
		/// </summary>
		/// <returns>The opened connection.</returns>
		public DbConnection GetConnection()
		{
			if (databaseConnection.State != ConnectionState.Open)
				databaseConnection.Open();

			return databaseConnection;
		}

		/// <summary>
		/// <para>
		///		Assigns a <paramref name="connection"/> to the <paramref name="command"/> and 
		///		discovers parameters if needed.
		/// </para>
		/// </summary>
		/// <param name="command">The command that contains the query to prepare.</param>
		/// <param name="connection">The connection to assign to the command.</param>
		protected static void PrepareCommand(DbCommand command, DbConnection connection)
		{
			Guard.ArgumentNotNull(command, "command");
			Guard.ArgumentNotNull(connection, "connection");

			command.Connection = connection;
		}

		/// <summary>
		///		Prepares a <see cref="DbCommand"/> object for use. This involves setting the connection
		///		and adding any parameters to the command.
		/// </summary>
		/// <param name="command">The command object you want prepared.</param>
		/// <param name="connection">The connection to use with the command.</param>
		/// <param name="parameters">Zero or more parameters to add to the command.</param>
		protected void PrepareCommand(DbCommand command, DbConnection connection, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNull(command, "command");
			Guard.ArgumentNotNull(connection, "connection");

			command.Connection = connection;

			if (parameters != null)
			{
				for (int i = 0; i < parameters.Length; i++)
					command.Parameters.Add(parameters[i]);
			}
		}

		/// <summary>
		///		Checks to see if a specific table is in the database.
		/// </summary>
		/// <param name="tableName">Name of the table to look for.</param>
		/// <returns>True if table exists and false if it doesn't.</returns>
		public abstract bool TableExists(string tableName);

		/// <summary>
		///		This is a simple helper method that will convert a DBNull value into
		///		a null value.
		/// </summary>
		/// <param name="value">The value you want to check for DBNull</param>
		/// <returns>Null if <paramref name="value"/> is DBNull.Value, or <paramref name="value"/>.</returns>
		public static object GetNullable(object value)
		{
			return (value is DBNull) ? null : value;
		}
	}
}
