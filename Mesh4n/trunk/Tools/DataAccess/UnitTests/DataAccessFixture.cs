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
using System.Data;
using System.Data.Common;
using Microsoft.Practices.Mobile.DataAccess;
using Microsoft.Practices.TestUtilities;
#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

namespace Microsoft.Practices.Mobile.DataAccess.Tests
{
	[TestClass]
	public class DataAccessFixture
	{
		[TestMethod]
		public void GetNullableValueReturnsNullInsteadOfDbNull()
		{
			DataTable table = new DataTable();
			table.Columns.Add();
			table.Rows.Add(DBNull.Value);
			table.Rows.Add("test");
			DbDataReader reader = table.CreateDataReader();
			reader.Read();
			object value = Database.GetNullable(reader[0]);
			Assert.IsNull(value);

			reader.Read();
			value = Database.GetNullable(reader[0]);
			Assert.IsNotNull(value);
			Assert.AreEqual("test", value);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void CreateDatabaseWithEmptyConnectionStringThrows()
		{
			DbProviderFactory factory = new MockProvider();
			new MockDatabase("", factory);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void CreateDatabaseWithNullConnectionStringThrows()
		{
			DbProviderFactory factory = new MockProvider();
			new MockDatabase(null, factory);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void CreateDatabaseWithNullDbProviderFactoryThrows()
		{
			new MockDatabase("data", null);
		}

		[TestMethod]
		public void CanCreateDatabaseConnectionWithConnectionString()
		{
			DbProviderFactory factory = new MockProvider();
			Database database = new MockDatabase("data", factory);
			DbConnection connection = database.GetConnection();

			Assert.IsNotNull(connection);
			Assert.AreEqual("data", connection.ConnectionString);
			Assert.AreEqual("data", database.ConnectionString);
		}


		[TestMethod]
		public void CreateParameterReturnValidParameter()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbParameter parameter = database.CreateParameter("param");

			Assert.IsNotNull(parameter);
			Assert.AreEqual("param", parameter.ParameterName);
		}

		[TestMethod]
		public void CreateParameterWithValueReturnValidParameter()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbParameter parameter = database.CreateParameter("param", 1234);

			Assert.IsNotNull(parameter);
			Assert.AreEqual("param", parameter.ParameterName);
			Assert.AreEqual(1234, parameter.Value);
		}

		[TestMethod]
		public void CreateParameterWithNullValueReturnValidParameterWithDBNullValue()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbParameter parameter = database.CreateParameter("param", null);

			Assert.IsNotNull(parameter);
			Assert.AreEqual("param", parameter.ParameterName);
			Assert.AreEqual(DBNull.Value, parameter.Value);
			Assert.AreNotEqual(null, parameter.Value);
		}

		[TestMethod]
		public void CreateParameterWithValueTypeAndSizeReturnValidParameter()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbParameter parameter = database.CreateParameter("param", DbType.String, 20, "parameter");

			Assert.IsNotNull(parameter);
			Assert.AreEqual("param", parameter.ParameterName);
			Assert.AreEqual(DbType.String, parameter.DbType);
			Assert.AreEqual(20, parameter.Size);
			Assert.AreEqual("parameter", parameter.Value);
		}


		[TestMethod]
		public void DbProviderCreateRightDbCommand()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbCommand command = database.DbProviderFactory.CreateCommand();

			Assert.IsNotNull(command);
			Assert.AreSame(typeof(MockCommand), command.GetType());
		}

		[TestMethod]
		public void DbProviderCreateRightDbParameter()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbParameter parameter = database.DbProviderFactory.CreateParameter();

			Assert.IsNotNull(parameter);
			Assert.AreSame(typeof(MockParameter), parameter.GetType());
		}

		[TestMethod]
		public void DbProviderCreateRightDbConnection()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbConnection connection = database.DbProviderFactory.CreateConnection();

			Assert.IsNotNull(connection);
			Assert.AreSame(typeof(MockConnection), connection.GetType());
		}

		[TestMethod]
		public void DatabaseCreatesOnlyOneConnectionForEachDatabase()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbConnection connection = database.GetConnection();
			DbConnection otherConnection = database.GetConnection();
			Assert.AreSame(connection, otherConnection);
		}

		[TestMethod]
		public void ExecuteNonQueryDoesNotCloseConnection()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbConnection connection = database.GetConnection();
			Assert.AreEqual(ConnectionState.Open, connection.State, "Should be open before Execute");
			database.ExecuteNonQuery("SELECT * FROM junk");
			Assert.AreEqual(ConnectionState.Open, connection.State, "Should remain open after Execute");
		}

		[TestMethod]
		public void ExecuteReaderDoesNotCloseConnection()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbConnection connection = database.GetConnection();
			Assert.AreEqual(ConnectionState.Open, connection.State, "Should be open before Execute");
			database.ExecuteReader("SELECT * FROM junk");
			Assert.AreEqual(ConnectionState.Open, connection.State, "Should remain open after Execute");
			Assert.AreNotEqual(CommandBehavior.CloseConnection, MockCommand.LastBehavior);
		}

		[TestMethod]
		public void ExecuteScalarDoesNotCloseConnection()
		{
			Database database = new MockDatabase("data", new MockProvider());
			DbConnection connection = database.GetConnection();
			Assert.AreEqual(ConnectionState.Open, connection.State, "Should be open before Execute");
			database.ExecuteScalar("SELECT * FROM junk");
			Assert.AreEqual(ConnectionState.Open, connection.State, "Should remain open after Execute");
		}

		[TestMethod]
		public void FinalizerDoesNotCallDispose()
		{
			Database database = new MockDatabase("data", new MockProvider());
			database.GetConnection();

			MockDatabase.FinalizerCalled = false;
			MockDatabase.DisposedCalled = false;

			database = null;

			GC.Collect();
			GC.WaitForPendingFinalizers();

			Assert.IsTrue(MockDatabase.FinalizerCalled);
			Assert.IsFalse(MockDatabase.DisposedCalled);
		}

		
	}
}
