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

#if PocketPC
using Microsoft.Practices.Mobile.Configuration.Tests.CF;
#endif

using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.TestUtilities;
using System.IO;
using System.Xml;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	public class AppConfig
	{
		private static ResourceInstance instance = new ResourceInstance();

		public static TestResourceFile GetFile()
		{
#if PocketPC
			return new TestResourceFile(instance, "App.CF.Config", "App.Config");
#else
			return new TestResourceFile(instance, "App.config", "App.Config");
#endif
		}

		public static string GetResourceString(string resourceName)
		{
			using (Stream stream = GetResource(resourceName))
			{
				StreamReader resourceReader = new StreamReader(stream);
				return resourceReader.ReadToEnd();
			}
		}

		public static Stream GetResource(string resourceName)
		{
			Type type = instance.GetType();
			string[] resource = type.Assembly.GetManifestResourceNames();

			resourceName = type.Namespace + "." + resourceName.Replace('\\', '.');
			return type.Assembly.GetManifestResourceStream(resourceName);
		}
	}
}

#if PocketPC
namespace Microsoft.Practices.Mobile.Configuration.Tests.CF
#else
namespace Microsoft.Practices.Mobile.Configuration.Tests
#endif
{
	internal class ResourceInstance
	{
	}
}

