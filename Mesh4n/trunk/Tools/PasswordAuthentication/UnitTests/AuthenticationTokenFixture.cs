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
	public class AuthenticationTokenFixture
	{
		private const string containerName = "TestContainer";

		[TestMethod]
		public void AuthenticationTokenCreatedFromAnIdentityDoesNotContainUser()
		{
			string user = "user";
			string password = "password";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(user, password, provider);

				AuthenticationToken token = new AuthenticationToken(identity);

				string tokenData = token.TokenData;

				Assert.IsFalse(tokenData.IndexOf(user) >= 0);
			}
		}

		[TestMethod]
		public void CreatingTokenTwiceReturnsDifferentTokens()
		{
			string user = "user";
			string password = "password";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(user, password, provider);

				AuthenticationToken token1 = new AuthenticationToken(identity);
				string tokenData1 = token1.TokenData;

				AuthenticationToken token2 = new AuthenticationToken(identity);
				string tokenData2 = token2.TokenData;

				Assert.AreNotEqual(tokenData1, tokenData2);
			}
		}

		[TestMethod]
		public void AuthenticatingReturnsIdentityWhenSameUserPwdUsed()
		{
			string user = "user";
			string password = "password";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(user, password, provider);

				AuthenticationToken token1 = new AuthenticationToken(identity);
				string tokenData1 = token1.TokenData;


				AuthenticationToken token2 = new AuthenticationToken(tokenData1);
				PasswordIdentity identity2 = token2.Authenticate(user, password, provider);

				Assert.AreEqual(user, identity2.Name);
				Assert.IsTrue(CryptographyUtility.CompareBytes(identity.CryptoKey, identity2.CryptoKey));
				Assert.IsTrue(identity2.IsAuthenticated);
			}
		}

		[TestMethod]
		public void AuthenticatingReturnsNullIdentityWithWrongCredentials()
		{
			string user = "user";
			string password = "password";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity(user, password, provider);

				AuthenticationToken token1 = new AuthenticationToken(identity);
				string tokenData1 = token1.TokenData;


				AuthenticationToken token2 = new AuthenticationToken(tokenData1);
				PasswordIdentity identity2 = token2.Authenticate(user, "wrong", provider);

				Assert.IsNull(identity2);
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void CreateFromTokenWithWrongVersionNumberThrows()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				PasswordIdentity identity = new PasswordIdentity("user", "password", provider);

				AuthenticationToken token1 = new AuthenticationToken(identity);
				byte[] tokenData = Convert.FromBase64String(token1.TokenData);
				tokenData[0] -= 1;					// Change to invalid version number

				string tokenString = Convert.ToBase64String(tokenData);

				AuthenticationToken token2 = new AuthenticationToken(tokenString);
			}
		}
	}
}
