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
using System.Data;
using System.Data.SqlServerCe;
using Microsoft.Practices.TestUtilities;
#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

namespace Microsoft.Practices.Mobile.DataAccess.Tests
{
	[TestClass]
	public class SqlDatabaseFixture
	{
		private string connectionStringPattern = @"Data Source=""{0}""";
		private string connectionString;
		private TestResourceFile dbFile;

		private TestResourceFile CreateDbFile()
		{
			dbFile = new TestResourceFile(this, "MockDatastore.sdf");
			connectionString = String.Format(connectionStringPattern, dbFile.Filename);
			return dbFile;
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ThrowsIfNullParameterNameIsPassed()
		{
			Database service = new SqlDatabase(connectionString);
			string sql = @"SELECT COUNT(*) FROM Customers WHERE ContactName = @Name";
			DbParameter param = service.CreateParameter(null, "Maria Anders");

			Assert.AreEqual(1, service.ExecuteScalar(sql, param));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteNonQueryThrowsIfNullQueryIsPassed()
		{
			Database service = new SqlDatabase(connectionString);
			Assert.AreEqual(1, service.ExecuteNonQuery((string)null, null));
		}



		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void TableExistsThrowsForNullName()
		{
			using (TestResourceFile file = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.TableExists(null);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void TableExistsThrowsForEmptyName()
		{
			using (TestResourceFile file = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.TableExists("");
				}
			}
		}

		#region SqlCe Tests

		[TestMethod]
		public void CanConnectToSqlServerCeDatabase()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database sqlDatabase = new SqlDatabase(connectionString))
				{
					DbConnection connection = sqlDatabase.GetConnection();
					Assert.IsNotNull(connection);
					Assert.IsTrue(connection is SqlCeConnection);
					Assert.IsTrue(connection.State == ConnectionState.Open);
				}
			}
		}

		[TestMethod]
		public void TestTableReportsTrueIfTableExists()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database db = new SqlDatabase(connectionString))
				{
					Assert.IsTrue(db.TableExists("TestTable"));
				}
			}
		}

		[TestMethod]
		public void TestTableRepotsFalseWhenNoSuchTable()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database db = new SqlDatabase(connectionString))
				{
					Assert.IsFalse(db.TableExists("JunkName"));
				}
			}
		}

		[TestMethod]
		public void AtAddedToParameterNameForSqlMobile()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database db = new SqlDatabase(connectionString))
				{
					DbParameter parameter = db.CreateParameter("test");
					Assert.AreEqual("@test", parameter.ParameterName);
				}
			}
		}

		[TestMethod]
		public void CanCreateSQLSpecificParameters()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					DbParameter param = database.CreateParameter("@Price", SqlDbType.Money, 19, 12.95);
					Assert.IsInstanceOfType(param, typeof(SqlCeParameter));

					SqlCeParameter sqlParam = (SqlCeParameter)param;
					Assert.AreEqual(SqlDbType.Money, sqlParam.SqlDbType);
				}
			}
		}

		#endregion

		#region ExecuteScalarFixture

		[TestMethod]
		public void ExecuteScalarWithDbCommand()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					DbCommand command = database.DbProviderFactory.CreateCommand();
					command.CommandText = @"SELECT COUNT(*) FROM TestTable";

					int count = Convert.ToInt32(database.ExecuteScalar(command));

					Assert.AreEqual(4, count);
				}
			}
		}

		[TestMethod]
		public void ExecuteScalarWithStringSql()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string command = @"select count(*) from TestTable";

					int count = Convert.ToInt32(database.ExecuteScalar(command));
					Assert.AreEqual(4, count);
				}
			}
		}

		[TestMethod]
		public void ExecuteScalarDoAnInsertionAndDelete()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string insertCommand = @"Insert into TestTable values (5, 'Cinco')";
					database.ExecuteScalar(insertCommand);

					string countCommand = @"select count(*) from TestTable";
					int count = Convert.ToInt32(database.ExecuteScalar(countCommand));

					Assert.AreEqual(5, count);

					string deleteCommand = @"delete from TestTable where TestColumn = 5";
					database.ExecuteScalar(deleteCommand);
					int deleteCount = Convert.ToInt32(database.ExecuteScalar(countCommand));

					Assert.AreEqual(4, deleteCount);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteScalarWithNullDbCommandThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteScalar((DbCommand)null);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteScalarWithNullStringDoesNotReturnNull()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteScalar((String)null);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(SqlCeException))]
		public void ExecuteScalarWithInvalidSqlStringThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteScalar("junk");
				}
			}
		}

		[TestMethod]
		public void ExecuteScalarWithParametersReturnExpectedValue()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string command = @"select * from TestTable where TestColumn > @TestColumn";
					DbParameter parameter = database.CreateParameter("TestColumn", 2);

					int value = Convert.ToInt32(database.ExecuteScalar(command, parameter));

					Assert.AreEqual(3, value);
				}
			}
		}


		#endregion

		#region ExecuteNonQueryFixture

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteNonQueryWithNullDbCommandThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteNonQuery((DbCommand)null);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteNonQueryWithNullStringThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteNonQuery((String)null);
				}
			}
		}

		[TestMethod]
		public void CanExecuteNonQueryWithDbCommand()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string insertionString = @"insert into TestTable values (5, 'Cinco')";
					DbCommand insertionCommand = database.DbProviderFactory.CreateCommand();
					insertionCommand.CommandText = insertionString;

					database.ExecuteNonQuery(insertionCommand);

					string countCommand = @"select count(*) from TestTable";
					int count = Convert.ToInt32(database.ExecuteScalar(countCommand));

					string cleanupString = "delete from TestTable where TestColumn = 5";
					DbCommand cleanupCommand = database.DbProviderFactory.CreateCommand();
					cleanupCommand.CommandText = cleanupString;

					int rowsAffected = database.ExecuteNonQuery(cleanupCommand);

					Assert.AreEqual(5, count);
					Assert.AreEqual(1, rowsAffected);
				}
			}
		}

		[TestMethod]
		public void CanExecuteNonQueryWithSqlString()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string insertionString = @"insert into TestTable values (5, 'Cinco')";
					database.ExecuteNonQuery(insertionString);

					string countCommand = @"select count(*) from TestTable";
					int count = Convert.ToInt32(database.ExecuteScalar(countCommand));

					string cleanupString = "delete from TestTable where TestColumn = 5";
					int rowsAffected = database.ExecuteNonQuery(cleanupString);

					Assert.AreEqual(5, count);
					Assert.AreEqual(1, rowsAffected);
				}
			}
		}


		#endregion

		#region ExecuteReaderFixture

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteReaderWithNullDbCommandThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteReader((DbCommand)null);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ExecuteReaderWithNullStringThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteReader((String)null);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ExecuteReaderWithEmptyStringThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					database.ExecuteReader(String.Empty);
				}
			}
		}

		[TestMethod]
		public void CanExecuteReaderWithCommandText()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string queryString = @"select * from TestTable";

					string accumulator = "";
					using (DbDataReader reader = database.ExecuteReader(queryString))
					{
						while (reader.Read())
						{
							accumulator += ((string)reader["TestColumn2"]).Trim();
						}
					}

					Assert.AreEqual("UnoDosTresCuatro", accumulator);
				}
			}
		}

		[TestMethod]
		public void CanExecuteReaderFromDbCommand()
		{
			using (dbFile = CreateDbFile())
			{
				using (Database database = new SqlDatabase(connectionString))
				{
					string queryString = @"select * from TestTable";
					DbCommand queryCommand = database.DbProviderFactory.CreateCommand();
					queryCommand.CommandText = queryString;

					string accumulator = "";
					using (DbDataReader reader = database.ExecuteReader(queryCommand))
					{
						while (reader.Read())
						{
							accumulator += ((string)reader["TestColumn2"]).Trim();
						}
					}

					Assert.AreEqual("UnoDosTresCuatro", accumulator);
					Assert.AreEqual(ConnectionState.Open, queryCommand.Connection.State);
				}
			}
		}

		[TestMethod]
		public void WhatGetsReturnedWhenWeDoAnInsertThroughDbCommandExecute()
		{
			using (dbFile = CreateDbFile())
			{
				int count = -1;
				DbDataReader reader = null;

				using (Database database = new SqlDatabase(connectionString))
				{
					try
					{
						string insertString = @"insert into TestTable values (5, 'Cinco')";

						reader = database.ExecuteReader(insertString);
						count = reader.RecordsAffected;
					}
					finally
					{
						if (reader != null)
						{
							reader.Close();
						}

						string deleteString = "Delete from TestTable where TestColumn = 5";
						database.ExecuteNonQuery(deleteString);
					}

					Assert.AreEqual(1, count);
				}
			}
		}

		#endregion

		#region ExecuteResultSetFixture


		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void RetriveResultSetFromEmptySqlStringThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					database.ExecuteResultSet("", ResultSetOptions.None);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void RetriveResultSetFromNullSqlCommandThrows()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					database.ExecuteResultSet((SqlCeCommand)null, ResultSetOptions.None);
				}
			}
		}

		[TestMethod]
		public void ExecuteResultSetFromSqlStringSucceeds()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{

					string queryString = @"select * from TestTable";
					using (SqlCeResultSet resultSet = database.ExecuteResultSet(queryString, ResultSetOptions.None))
					{
						Assert.IsNotNull(resultSet);
					}
				}
			}
		}

		[TestMethod]
		public void ExecuteResultSetFromSqlCommandSucceeds()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					SqlCeCommand queryCommand = (SqlCeCommand)database.DbProviderFactory.CreateCommand();

					string queryString = @"select * From TestTable";
					queryCommand.CommandText = queryString;

					using (SqlCeResultSet result = database.ExecuteResultSet(queryCommand, ResultSetOptions.None))
					{
						Assert.IsNotNull(result);
					}
				}
			}
		}

		[TestMethod]
		public void ExecuteResultSetCanBeScrollable()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string queryString = @"select * From TestTable";
					using (SqlCeResultSet result = database.ExecuteResultSet(queryString, ResultSetOptions.Scrollable))
					{
						Assert.IsTrue(result.Scrollable);
					}
				}
			}
		}

		[TestMethod]
		public void ExecuteResultSetReturnExpectedValues()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string queryString = @"select * From TestTable";
					using (SqlCeResultSet result = database.ExecuteResultSet(queryString, ResultSetOptions.Scrollable))
					{
						Assert.IsTrue(result.HasRows);
						Assert.AreEqual(2, result.FieldCount);
					}
				}
			}
		}

		[TestMethod]
		public void ExecuteResultSetDoesNotCloseConnection()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					DbConnection connection = database.GetConnection();
					Assert.AreEqual(ConnectionState.Open, connection.State, "Should be open before Execute");
					string queryString = @"select * From TestTable";
					using (database.ExecuteResultSet(queryString, ResultSetOptions.Scrollable))
					{
					}
					Assert.AreEqual(ConnectionState.Open, connection.State, "Should remain open after Execute");
				}
			}
		}

		#endregion

		#region ExecuteWithParameters

		[TestMethod]
		public void CanInsertNullStringParameter()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string sqlString = "insert into TestTable Values (@Param1, @Param2)";
					DbParameter[] parameters = new DbParameter[]
				{
					database.CreateParameter("@Param1", DbType.Int32, 0, 5),
					database.CreateParameter("@Param2", DbType.String, 50, null)
				};

					database.ExecuteNonQuery(sqlString, parameters);

					string sqlCount = "SELECT COUNT(*) FROM TestTable ";

					Assert.AreEqual(5, database.ExecuteScalar(sqlCount, null));
				}
			}
		}

		[TestMethod]
		public void ExecuteSqlStringCommandWithParameters()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string sql = "select * from TestTable where (TestColumn=@Param1) and TestColumn2=@Param2";

					DbParameter[] parameters = new DbParameter[]
				{
					database.CreateParameter("@Param1", DbType.Int32, 0, 1),
					database.CreateParameter("@Param2", DbType.String, 50, "Uno")
				};

					using (DbDataReader reader = database.ExecuteReader(sql, parameters))
					{
						reader.Read();
						Assert.AreEqual(1, reader["TestColumn"]);
						Assert.AreEqual("Uno", reader["TestColumn2"]);
					}
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(InvalidOperationException))]
		public void ExecuteSqlStringCommandWithNotEnoughParameterValues()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string sql = "select * from TestTable where (TestColumn=@Param1) and TestColumn2=@Param2";

					DbParameter parameter = database.CreateParameter("@Param1", DbType.Int32, 0, 1);
					database.ExecuteScalar(sql, parameter);
				}
			}
		}

		[TestMethod]
		[ExpectedException(typeof(InvalidOperationException))]
		public void ExecuteSqlStringCommandWithTooManyParameterValues()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string sql = "select * from TestTable where (TestColumn=@Param1) and TestColumn2=@Param2";

					DbParameter[] parameters = new DbParameter[]
				{
					database.CreateParameter("@Param1", DbType.Int32, 0, 1),
					database.CreateParameter("@Param2", DbType.String, 50, "Uno"),
					database.CreateParameter("@Param3", DbType.Int32, 0, 123)
				};

					database.ExecuteScalar(sql, parameters);
				}
			}
		}


		[TestMethod]
		public void ExecuteSqlStringWithoutParametersButWithValues()
		{
			using (dbFile = CreateDbFile())
			{
				using (SqlDatabase database = new SqlDatabase(connectionString))
				{
					string sql = "select * from TestTable";

					DbParameter[] parameters = new DbParameter[]
				{
					database.CreateParameter("@Param1", DbType.Int32, 0, 1),
					database.CreateParameter("@Param2", DbType.String, 50, "Uno")
				};

					using (DbDataReader reader = database.ExecuteReader(sql, parameters))
					{
						reader.Read();
						Assert.AreEqual(1, reader["TestColumn"]);
						Assert.AreEqual("Uno", reader["TestColumn2"]);
						reader.Read();
						Assert.AreEqual(2, reader["TestColumn"]);
						Assert.AreEqual("Dos", reader["TestColumn2"]);
						reader.Read();
						Assert.AreEqual(3, reader["TestColumn"]);
						Assert.AreEqual("Tres", reader["TestColumn2"]);
						reader.Read();
						Assert.AreEqual(4, reader["TestColumn"]);
						Assert.AreEqual("Cuatro", reader["TestColumn2"]);
					}
				}
			}
		}

		#endregion

	}
}
