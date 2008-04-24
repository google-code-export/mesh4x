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
using Microsoft.Practices.Mobile.DataAccess;
using System.Data.Common;

namespace Microsoft.Practices.Mobile.DataAccess.Tests
{
	public class MockProvider : Microsoft.Practices.Mobile.DataAccess.DbProviderFactory
	{
		public override DbParameter CreateParameter()
		{
			return new MockParameter();
		}

		public override DbCommand CreateCommand()
		{
			return new MockCommand();
		}

		public override DbConnection CreateConnection()
		{
			return new MockConnection();
		}
	}
}
