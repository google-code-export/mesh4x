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

namespace Microsoft.Practices.Mobile.DataAccess
{
	/// <summary>
	///		An instance of this class creates SQL Server CE-specific objects that the
	///		<see cref="Database"/> class needs to work with SQL Server CE database files.
	/// </summary>
	public class SqlClientFactory : DbProviderFactory
	{
		/// <summary>
		///		Creates a new, empty <see cref="SqlCeCommand"/> instance.
		/// </summary>
		/// <returns>Returns a new instance.</returns>
		public override DbCommand CreateCommand()
		{
			return new SqlCeCommand();
		}

		/// <summary>
		///		Creates a new, empty <see cref="SqlCeConnection"/> instance.
		/// </summary>
		/// <returns>Returns a new instance.</returns>
		public override DbConnection CreateConnection()
		{
			return new SqlCeConnection();
		}

		/// <summary>
		///		Creates a new, empty <see cref="SqlCeParameter"/> instance.
		/// </summary>
		/// <returns>Returns a new instance.</returns>
		public override DbParameter CreateParameter()
		{
			return new SqlCeParameter();
		}
	}
}
