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
using System.Security.Cryptography;
using System.IO;

namespace Microsoft.Practices.Mobile.PasswordAuthentication.Tests
{
	[TestClass]
	public class CryptoNativeHelperFixture
	{
		private const string containerName = "TestContainer";

		[TestMethod]
		public void BuildKeyFromPasswordReturnsKey()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				CryptNativeHelper crypto = new CryptNativeHelper(provider);
				byte[] data = crypto.GetPasswordDerivedKey("test");
				Assert.IsNotNull(data);
				Assert.AreNotEqual(0, data.Length);
			}
		}

		[TestMethod]
		public void BuildKeyFromPasswordReturnsSameKeyForSamePassword()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				CryptNativeHelper crypto = new CryptNativeHelper(provider);
				byte[] data1 = crypto.GetPasswordDerivedKey("test");
				byte[] data2 = crypto.GetPasswordDerivedKey("test");

				Assert.AreEqual(data1.Length, data2.Length);
				Assert.IsTrue(CryptographyUtility.CompareBytes(data1, data2));
			}
		}

		[TestMethod]
		public void BuildKeyFromPasswordReturnsDifferentKeyForDifferent()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				CryptNativeHelper crypt = new CryptNativeHelper(provider);
				byte[] key1 = crypt.GetPasswordDerivedKey("password");
				byte[] key2 = crypt.GetPasswordDerivedKey("wrong");
				Assert.IsFalse(CryptographyUtility.CompareBytes(key1, key2));
			}
		}

		[TestMethod]
		public void GetHashFromDataAndKey()
		{
			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				CryptNativeHelper crypto = new CryptNativeHelper(provider);
				byte[] data = CryptographyUtility.GetBytes("This is a test");
				byte[] salt = CryptographyUtility.GetRandomBytes(8);
				byte[] key = CryptographyUtility.GetRandomBytes(32);

				byte[] hash1 = crypto.GetHmacHash(data, salt, key);
				Assert.IsNotNull(hash1);

				byte[] hash2 = crypto.GetHmacHash(data, salt, key);
				Assert.IsTrue(CryptographyUtility.CompareBytes(hash1, hash2));
			}
		}

		[TestMethod]
		public void CanEncryptAndDecryptUsingPasswordDerivedKey()
		{
			Rijndael crypto = RijndaelManaged.Create();
			KeySizes[] sizes = crypto.LegalKeySizes;
			const string text = "This is a test.";

			using (RsaAesCryptographyProvider provider = new RsaAesCryptographyProvider(containerName))
			{
				CryptNativeHelper native = new CryptNativeHelper(provider);
				byte[] key = native.GetPasswordDerivedKey("test");
				byte[] iv = crypto.IV;

				byte[] originalText = CryptographyUtility.GetBytes(text);
				byte[] encryptedText = CryptographyUtility.Transform(crypto.CreateEncryptor(key, iv), originalText);
				byte[] decryptedText = CryptographyUtility.Transform(crypto.CreateDecryptor(key, iv), encryptedText);

				Assert.IsTrue(CryptographyUtility.CompareBytes(originalText, decryptedText));

				string decryptedString = CryptographyUtility.GetString(decryptedText);
				Assert.AreEqual(text, decryptedString);
			}
		}
	}
}
