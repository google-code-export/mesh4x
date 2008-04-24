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
using Microsoft.Practices.Mobile.PasswordAuthentication;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class RijndaelConfigurationProviderFixture
	{
		public static byte[] EncryptSectionData(string section, out byte[] key)
		{
			Rijndael crypt = Rijndael.Create();
			key = crypt.Key;
			CryptographyBlock block = new CryptographyBlock(crypt, key);
			return block.Encrypt(CryptographyUtility.GetBytes(section), crypt.IV);
		}

		private static string sectionXml = @"
					<MyCustomSection>
						<MenuItems>
							<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							<add id=""2"" site=""MainMenu"" label=""&amp;Help"" register=""true"" registrationsite=""Help"" />
							<add id=""3"" site=""Help"" label=""&amp;About..."" commandname=""HelpAbout"" key=""F1"" />
						</MenuItems>
					</MyCustomSection>
				";

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void NullKeyThrows()
		{
			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(null);
		}

		[TestMethod]
		public void NullDataReturnsNull()
		{
			byte[] key = new byte[20];
			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);
			Assert.IsNull(provider.Decrypt(null));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void DataTooShortThrows()
		{
			byte[] key = new byte[20];
			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);

			byte[] data = new byte[] { 1 };
			provider.Decrypt(Convert.ToBase64String(data));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void HeaderTooShortThrows()
		{
			byte[] key = new byte[20];
			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);

			byte[] data = new byte[] { 1, 0, 5, 1, 2 };
			provider.Decrypt(Convert.ToBase64String(data));
		}

		[TestMethod]
		public void ProviderDecryptsSectionDataCorrectly()
		{
			byte[] key;

			byte[] encryptedData = EncryptSectionData(sectionXml, out key);

			string encryptedSection = Convert.ToBase64String(encryptedData);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);
			string decryptedXml = provider.Decrypt(encryptedSection);
			Assert.AreEqual(sectionXml, decryptedXml);
		}

		[TestMethod]
		[ExpectedException(typeof(CryptographicException))]
		public void ThrowsWhenDataIsTruncated()
		{
			byte[] key;

			byte[] data = EncryptSectionData(sectionXml, out key);
			byte[] encryptedData = new byte[data.Length - 1];
			Array.Copy(data, encryptedData, encryptedData.Length);
			string encryptedSection = Convert.ToBase64String(encryptedData);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);
			string decryptedXml = provider.Decrypt(encryptedSection);
		}

		[TestMethod]
		public void SavedKeyCanEncryptAndDecrypt()
		{
			string keyString = "pvUF7eNVQ2FGkf/k9e3IpeKdxfClOrqHwAm9cfiL6nU=";
			byte[] key = Convert.FromBase64String(keyString);

			Rijndael crypt = Rijndael.Create();
			CryptographyBlock block = new CryptographyBlock(crypt, key);
			string testString = "this is a test";
			byte[] original = CryptographyUtility.GetBytes(testString);
			byte[] encrypted = block.Encrypt(original, crypt.IV);
			byte[] decrypted = block.Decrypt(encrypted);

			Assert.IsTrue(CryptographyUtility.CompareBytes(original, decrypted));
		}

		[TestMethod]
		public void DataEncryptedWithKeyCanBeDecryptedByProvider()
		{
			string keyString = "pvUF7eNVQ2FGkf/k9e3IpeKdxfClOrqHwAm9cfiL6nU=";
			byte[] key = Convert.FromBase64String(keyString);

			Rijndael crypt = Rijndael.Create();
			CryptographyBlock block = new CryptographyBlock(crypt, key);
			string testString = "this is a test";
			byte[] original = CryptographyUtility.GetBytes(testString);
			byte[] encrypted = block.Encrypt(original, crypt.IV);
			string encryptedString = Convert.ToBase64String(encrypted);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);
			string result = provider.Decrypt(encryptedString);
			Assert.AreEqual(result, testString);
		}

		[TestMethod]
		public void DecryptFromSavedKeyAndData()
		{
			string data = "AQAQZ54JC/5LOcOkaRJ0sl8sCaGH66jInb1IFFwDlaWGLde+VfZGTlDSYPkuGb9rq85FNWCuQ2eou117tAwiVFp3bhOHWHn1nmcz/HeBXd5+1pk=";
			string keyString = "Z64cSa53krkEhrHzKV20Bng5YYeyfctmlWaiRnA2/6M=";

			byte[] key = Convert.FromBase64String(keyString);

			Rijndael crypt = Rijndael.Create();
			crypt.Key = key;
			CryptographyBlock block = new CryptographyBlock(crypt, key);
			byte[] stuff = block.Decrypt(Convert.FromBase64String(data));
			string other = CryptographyUtility.GetString(stuff);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);
			string result = provider.Decrypt(data);
			Assert.AreEqual("<test>this is a test</test>", result);
		}
	}
}
