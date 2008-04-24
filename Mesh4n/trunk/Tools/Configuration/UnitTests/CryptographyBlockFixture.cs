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
using Microsoft.Practices.Mobile.PasswordAuthentication;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class CrnptographyBlockFixture
	{
		[TestMethod]
		public void EncryptedDataHasCorrectFormat()
		{
			byte[] data = new byte[32];
			for (int i = 0; i < data.Length; i++)
				data[i] = (byte)(i + 50);

			SymmetricAlgorithm symmetric = Rijndael.Create();
			byte[] key = symmetric.Key;
			byte[] iv = symmetric.IV;

			CryptographyBlock block = new CryptographyBlock(symmetric, key);
			byte[] encrypted = block.Encrypt(data, iv);

			Assert.IsNotNull(encrypted);

			SubArray<byte> version = new SubArray<byte>(encrypted, 0, 2);
			Assert.AreEqual<byte>(1, version[0]);
			Assert.AreEqual<byte>(0, version[1]);

			SubArray<byte> ivLength = new SubArray<byte>(encrypted, 2, 1);
			Assert.AreEqual<int>(iv.Length, ivLength[0]);

			SubArray<byte> ivArray = new SubArray<byte>(encrypted, 3, ivLength[0]);
			Assert.IsTrue(CryptographyUtility.CompareBytes(iv, ivArray.Array));

			SubArray<byte> dataArray = new SubArray<byte>(encrypted, 3 + ivLength[0]);
			Assert.IsTrue(dataArray.Count > data.Length);
		}

		[TestMethod]
		public void EncryptedDataCanBeDecrypted()
		{
			byte[] data = new byte[32];
			for (int i = 0; i < data.Length; i++)
				data[i] = (byte)(i + 50);

			SymmetricAlgorithm symmetric = Rijndael.Create();
			byte[] key = symmetric.Key;
			byte[] iv = symmetric.IV;

			CryptographyBlock block = new CryptographyBlock(symmetric, key);
			byte[] encrypted = block.Encrypt(data, iv);

			block = new CryptographyBlock(symmetric, key);
			byte[] decrypted = block.Decrypt(encrypted);
			Assert.IsNotNull(decrypted);

			Assert.IsTrue(CryptographyUtility.CompareBytes(decrypted, data));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ThrowsIfAlgorithmIsNull()
		{
			byte[] key = new byte[10];
			CryptographyBlock block = new CryptographyBlock(null, key);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ThrowsIfVersionNumberIsWrong()
		{
			byte[] dataBlock = new byte[100];
			dataBlock[0] = 0;

			SymmetricAlgorithm symmetric = Rijndael.Create();
			CryptographyBlock block = new CryptographyBlock(symmetric, symmetric.Key);
			block.Decrypt(dataBlock);
		}
	}
}
