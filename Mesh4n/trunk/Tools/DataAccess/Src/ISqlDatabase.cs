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
	public interface ISqlDatabase
	{
		/// <summary>
		///		This method allows you to use SQL Server CE-specific parameter types.
		/// </summary>
		/// <param name="name">Name of the parameter, with a prefix (such as '@').</param>
		/// <param name="type">The SQL-specific type for the parameter.</param>
		/// <param name="size">
		///		Length of the paramter. This is ignored for variable-length parameters, or value
		///		types such as int.
		/// </param>
		/// <param name="value">The value you want assigned to this parameter.</param>
		/// <returns>A new <see cref="SqlCeParameter"/> instance initialized for you.</returns>
		DbParameter CreateParameter(string name, SqlDbType type, int size, object value);

		/// <summary>
		///		If you're using SQL Server CE, you'll get much better performance using result
		///		sets instead of data readers. This method executes a command and returns the data
		///		in a <see cref="SqlCeResultSet"/>.
		/// </summary>
		/// <remarks>
		///		Make sure you call the <see cref="SqlCeResultSet.Dispose()"/> method when you're done
		///		using the result set. The best practice would be to put the call to this method inside
		///		a C# using statement, or the equivalent in other languages.
		/// </remarks>
		/// <param name="command">
		///		A <see cref="SqlCeCommand"/> object initialized with the command you want to execute.
		/// </param>
		/// <param name="options">Any options you want to specify for running this command.</param>
		/// <param name="parameters">Zero or more parameters to the command.</param>
		/// <returns>The result set. Make sure you dipose this result set when you're finished.</returns>
		SqlCeResultSet ExecuteResultSet(SqlCeCommand command, ResultSetOptions options, params DbParameter[] parameters);

		/// <summary>
		///		If you're using SQL Server CE, you'll get much better performance using result
		///		sets instead of data readers. This method executes a command and returns the data
		///		in a <see cref="SqlCeResultSet"/>.
		/// </summary>
		/// <remarks>
		///		Make sure you call the <see cref="SqlCeResultSet.Dispose()"/> method when you're done
		///		using the result set. The best practice would be to put the call to this method inside
		///		a C# using statement, or the equivalent in other languages.
		/// </remarks>
		/// <param name="sqlCommand">A SQL statement that you want to run, which returns a set of rows.</param>
		/// <param name="options">Any options you want to specify for running this command.</param>
		/// <param name="parameters">Zero or more parameters to the command.</param>
		/// <returns>The result set. Make sure you dispose of this result set when you're finished.</returns>
		SqlCeResultSet ExecuteResultSet(string sqlCommand, ResultSetOptions options, params DbParameter[] parameters);
	}
}
