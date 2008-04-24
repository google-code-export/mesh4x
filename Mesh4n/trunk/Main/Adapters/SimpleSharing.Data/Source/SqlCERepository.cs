using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Text;
using Microsoft.Practices.Mobile.DataAccess;

namespace SimpleSharing
{
	public abstract class DbRepository
	{
        Database database;
        bool initialized = false;

        protected DbRepository(Database database)
		{
            Guard.ArgumentNotNull(database, "database");

            this.database = database;
        }

		protected Database Database
		{
			get { return database; }
		}

        /// <summary>
        /// Returns the shared connection to the database.
        /// <b>Note:</b> Never close nor dispose of the returned connection!
        /// </summary>
        /// <returns></returns>
		protected DbConnection OpenConnection()
		{
            DbConnection cn = database.GetConnection();
            // NOTE: MCSF DAB auto-opens the shared connection

			if (!initialized)
			{
				InitializeSchema(cn);
				initialized = true;
			}

			return cn;
		}

		protected abstract void InitializeSchema(DbConnection openedConnection);

		/// <summary>
		/// Checks if the given table exist. Not really needed for the MCSF DAB
		/// </summary>
		protected virtual bool TableExists(DbConnection connection, string tableName)
		{
            return database.TableExists(tableName);
		}
	}
}
