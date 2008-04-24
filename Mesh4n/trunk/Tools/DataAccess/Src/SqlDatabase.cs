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
using System.Data.Common;
using System.Data.SqlServerCe;
using System.Data;

namespace Microsoft.Practices.Mobile.DataAccess
{
	/// <summary>
	///		This is a specific instance of <see cref="Database"/> that provides access to SQL Server Mobile
	///		databases.
	/// </summary>
	public class SqlDatabase : Database, ISqlDatabase
	{
		/// <summary>
		///		This is the only public constructor for this class.
		/// </summary>
		/// <param name="connectionString">The connection string to connect to your database.</param>
		public SqlDatabase(string connectionString)
			: base(connectionString, new SqlClientFactory())
		{
		}

		/// <summary>
		///		Builds a value parameter name for the current database by ensuring there is an '@' at the
		///		start of the name.
		/// </summary>
		/// <param name="name">The name of the parameter.</param>
		/// <returns>A correctly formated parameter name, which starts with an '@'.</returns>
		public override string BuildParameterName(string name)
		{
			Guard.ArgumentNotNullOrEmptyString(name, "name");

			if (name[0] != '@')
				return "@" + name;
			else
				return name;
		}

		/// <summary>
		///		Allows creating a parameter with a SQL Mobile-specific type.
		/// </summary>
		/// <param name="name">Name of the parameter</param>
		/// <param name="type">The SQL Mobile type of the parameter</param>
		/// <param name="size">Size of the paramater</param>
		/// <param name="value">The value to set for this parameter</param>
		/// <returns></returns>
		public DbParameter CreateParameter(string name, SqlDbType type, int size, object value)
		{
			SqlCeParameter param = (SqlCeParameter)CreateParameter(name);
			param.SqlDbType = type;
			param.Size = size;
			param.Value = (value == null) ? DBNull.Value : value;
			return param;
		}

		/// <summary>
		///		Executes and SQL command that returns a <see cref="SqlCeResultSet"/> that can be
		///		scrollable and/or updateable. Make sure you dispose of the result set when you're done.
		/// </summary>
		/// <param name="command">
		///		The command object that contains the command that will be executed.
		/// </param>
		/// <param name="options">
		///		Options that control what capabilities the result set will have, such as updatable or scrollable.
		/// </param>
		/// <param name="parameters">
		///		An optional set of DbParameter objects that provide additional parameters required by the command.
		/// </param>
		/// <returns></returns>
		public SqlCeResultSet ExecuteResultSet(SqlCeCommand command, ResultSetOptions options, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNull(command, "command");

			SqlCeConnection connection = (SqlCeConnection) GetConnection();
			PrepareCommand(command, connection, parameters);

			return command.ExecuteResultSet(options);
		}

		/// <summary>
		///		Executes and SQL command that returns a <see cref="SqlCeResultSet"/> that can be
		///		scrollable and/or updateable. Make sure you dispose of the result set when you're done.
		/// </summary>
		/// <param name="sqlCommand">
		///		The SQL query that will be executed.
		/// </param>
		/// <param name="options">
		///		Options that control what capabilities the result set will have, such as updatable or scrollable.
		/// </param>
		/// <param name="parameters">
		///		An optional set of DbParameter objects that provide additional parameters required by the SQL command.
		/// </param>
		/// <returns></returns>
		public SqlCeResultSet ExecuteResultSet(string sqlCommand, ResultSetOptions options, params DbParameter[] parameters)
		{
			Guard.ArgumentNotNullOrEmptyString(sqlCommand, "sqlCommand");

			using (SqlCeCommand command = (SqlCeCommand)DbProviderFactory.CreateCommand())
			{
				command.CommandText = sqlCommand;
				return ExecuteResultSet(command, options, parameters);
			}
		}

		/// <summary>
		///		Checks to see if a table exists in the open database.
		/// </summary>
		/// <param name="tableName">Name of the table.</param>
		/// <returns>true if the table exists, otherwise false.</returns>
		public override bool TableExists(string tableName)
		{
			Guard.ArgumentNotNullOrEmptyString(tableName, "tableName");

			string sql = @"
					SELECT	COUNT(*) 
					FROM	[INFORMATION_SCHEMA].[TABLES] 
					WHERE	[TABLE_NAME] = @TableName";

			DbParameter param = CreateParameter("@TableName", DbType.String, 512, tableName);
			int count = (int)ExecuteScalar(sql, param);
			return (count != 0);
		}
	}
}
