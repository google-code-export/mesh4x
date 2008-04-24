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

namespace Microsoft.Practices.Mobile.DataAccess
{
	/// <summary>
	///		This is the base class for subclasses that create database-specific objects
	///		used by the <see cref="Database"/> class. For example, <see cref="SqlClientFactory"/>
	///		creates SQL-specific objects.
	/// </summary>
	/// <remarks>
	///		You might expect the methods in this class to be abstract since subclasses need
	///		to return actual instances. However, returning null makes it easier to create
	///		mock instances for testing purposes where the mock database doesn't actually
	///		call any of these methods.
	/// </remarks>
	public abstract class DbProviderFactory
	{
		/// <summary>
		///		This method is called by <see cref="Database"/> when it needs a <see cref="DbCommand"/>
		///		object to execute a command.
		/// </summary>
		/// <returns>A new, database-specific instance of <see cref="DbCommand"/>.</returns>
		public virtual DbCommand CreateCommand()
		{
			return null;
		}

		/// <summary>
		///		This method is called by <see cref="Database"/> when it needs a connection
		///		for executing a command.
		/// </summary>
		/// <returns>A new, database-specific instance of <see cref="DbConnection"/></returns>
		public virtual DbConnection CreateConnection()
		{
			return null;
		}

		/// <summary>
		///		This method is called by <see cref="Database"/> when it needs to add parameters to
		///		a command object.
		/// </summary>
		/// <returns>A new, database-specific instance of <see cref="DbParameter"/></returns>
		public virtual DbParameter CreateParameter()
		{
			return null;
		}
	}
}
