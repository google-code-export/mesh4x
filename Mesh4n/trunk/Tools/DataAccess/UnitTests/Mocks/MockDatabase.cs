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

namespace Microsoft.Practices.Mobile.DataAccess.Tests
{
	public class MockDatabase : Database
	{
		public static bool FinalizerCalled = false;
		public static bool DisposedCalled = false;

		public MockDatabase(string connection, DbProviderFactory factory)
			: base(connection, factory)
		{

		}
		
		~MockDatabase()
		{
			FinalizerCalled = true;
		}

		public override void Dispose()
		{
			base.Dispose();

			DisposedCalled = true;
		}

		public override bool TableExists(string tableName)
		{
			return true;
		}



	}
}
