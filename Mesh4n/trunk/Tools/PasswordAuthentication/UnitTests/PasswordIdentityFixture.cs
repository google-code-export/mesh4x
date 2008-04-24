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
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Text;

namespace Microsoft.Practices.Mobile.PasswordAuthentication.Tests
{
	[TestClass]
	public class PinIdentityFixture
	{
		private const string containerName = "TestContainer";

		[TestMethod]
		public void IdentityCreatesKey()
		{
			string user = "user";
			string password = "pwd";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(user, password, provider);

				Assert.IsFalse(identity.IsAuthenticated);
				Assert.AreEqual("PIN", identity.AuthenticationType);
				Assert.AreEqual(user, identity.Name);
				Assert.IsNotNull(identity.CryptoKey);
				Assert.AreEqual(32, identity.CryptoKey.Length);
			}
		}

		[TestMethod]
		public void DifferentKeysForDifferentPasswords()
		{
			string user = "user";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity1 = new PasswordIdentity(user, "pwd", provider);

				PasswordIdentity identity2 = new PasswordIdentity(user, "wrong", provider);

				Assert.IsFalse(CryptographyUtility.CompareBytes(identity1.CryptoKey, identity2.CryptoKey));
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void NullUserThrows()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(null, "password", provider);
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void NullPasswordThrows()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity("user", null, provider);
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void EmptyUserThrows()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(String.Empty, "password", provider);
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void EmptyPasswordThrows()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity("user", String.Empty, provider);
			}
		}
	}
}
