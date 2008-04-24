using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.Practices.EnterpriseLibrary.Data;
using System.Data.Common;

namespace SimpleSharing.Data
{
	public class GenericDbFactory : DbFactory
	{
		string providerName;
		
		public GenericDbFactory()
			:base()
		{
		}

		public string ProviderName
		{
			get { return providerName; }
			set { providerName = value; }
		}

		public override Database CreateDatabase()
		{
			DbProviderFactory factory = DbProviderFactories.GetFactory(providerName);
			return new GenericDatabase(this.ConnectionString, factory);
		}


	}
}
