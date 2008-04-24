using System;
using System.Collections.Generic;
using System.Text;

#if !PocketPC
using Microsoft.Practices.EnterpriseLibrary.Data;
using Microsoft.Practices.EnterpriseLibrary.Data.Sql;
#else
using Microsoft.Practices.Mobile.DataAccess;
#endif

namespace SimpleSharing.Data
{
	public class SqlDbFactory : DbFactory
	{
		public SqlDbFactory()
			: base()
		{
		}

		public override Database CreateDatabase()
		{
			return new SqlDatabase(this.ConnectionString);
		}
	}
}
