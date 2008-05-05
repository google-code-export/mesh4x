using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.Practices.EnterpriseLibrary.Data;
using Microsoft.Practices.EnterpriseLibrary.Data.SqlCe;

namespace Mesh4n.Adapters.Data
{
	public class SqlCeDbFactory : DbFactory
	{
		public SqlCeDbFactory()
			: base()
		{
		}
		
		public override Database CreateDatabase()
		{
			return new SqlCeDatabase(this.ConnectionString);
		}
	}
}
