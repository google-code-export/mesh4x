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
	public class CryptographyUtilityFixture
	{
		[TestMethod]
		public void CompareTwoNullByteArrays()
		{
			byte[] b = new byte[] { };
			Assert.IsFalse(CryptographyUtility.CompareBytes(null, null));
			Assert.IsFalse(CryptographyUtility.CompareBytes(b, null));
			Assert.IsFalse(CryptographyUtility.CompareBytes(null, b));
		}

		[TestMethod]
		public void GetBytesFromValidHexString()
		{
			string hex = "00FF";

			byte[] hexByte = CryptographyUtility.GetBytesFromHexString(hex);

			byte expected = 0;
			Assert.AreEqual(expected, hexByte[0]);
			expected = 255;
			Assert.AreEqual(expected, hexByte[1]);
		}

		[TestMethod]
		public void GetBytesFromValidPrefixedHexString()
		{
			string hex = "0x00FF";

			byte[] hexByte = CryptographyUtility.GetBytesFromHexString(hex);

			byte expected = 0;
			Assert.AreEqual(expected, hexByte[0]);
			expected = 255;
			Assert.AreEqual(expected, hexByte[1]);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void GetBytesFromInvalidLengthHexStringThrowsThrows()
		{
			string hex = "0FF";

			CryptographyUtility.GetBytesFromHexString(hex);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void GetBytesFromInvalidCharactersHexStringThrows()
		{
			string hex = "Invalid!";

			CryptographyUtility.GetBytesFromHexString(hex);
		}

		[TestMethod]
		public void GetHexStringFromBytes()
		{
			byte[] bytes = new byte[] { 0, 255 };
			string hex = CryptographyUtility.GetHexStringFromBytes(bytes);
			Assert.AreEqual("00FF", hex);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void GetHexStringFromNullBytesThrows()
		{
			CryptographyUtility.GetHexStringFromBytes(null);
		}

		[TestMethod]
		public void ZeroOutNullByteArray()
		{
			CryptographyUtility.ZeroOutBytes(null);

			byte[] b = new byte[] { 1, 2, 3, 4 };
			CryptographyUtility.ZeroOutBytes(b);
			Assert.AreEqual(4, b.Length);
			byte expected = 0;
			Assert.AreEqual(expected, b[2]);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void GetHexStringFromZeroBytesThrows()
		{
			CryptographyUtility.GetHexStringFromBytes(new byte[0]);
		}

		[TestMethod]
		public void CombineBytes()
		{
			byte[] test1 = new byte[] { 0, 1, 2, 3 };
			byte[] test2 = new byte[] { 0, 1, 2, 3 };

			byte[] combinedBytes = CryptographyUtility.CombineBytes(test1, test2);
			Assert.AreEqual(test1.Length + test2.Length, combinedBytes.Length);
		}

		[TestMethod]
		public void GenerateRandomBytes()
		{
			int rndSize = 16;
			byte[] rnd1 = CryptographyUtility.GetRandomBytes(rndSize);
			byte[] rnd2 = CryptographyUtility.GetRandomBytes(rndSize);

			Assert.IsFalse(CryptographyUtility.CompareBytes(rnd1, rnd2));
		}

		[TestMethod]
		public void GetBytesFromInt()
		{
			byte[] results;

			results = CryptographyUtility.GetBytes((uint)1);
			Assert.IsTrue(CryptographyUtility.CompareBytes(results, new byte[] { 1, 0, 0, 0 }));

			results = CryptographyUtility.GetBytes((uint)255);
			Assert.IsTrue(CryptographyUtility.CompareBytes(results, new byte[] { 255, 0, 0, 0 }));

			results = CryptographyUtility.GetBytes((uint)256);
			Assert.IsTrue(CryptographyUtility.CompareBytes(results, new byte[] { 0, 1, 0, 0 }));

			results = CryptographyUtility.GetBytes((uint)0xFEDC);
			Assert.IsTrue(CryptographyUtility.CompareBytes(results, new byte[] { 0xDC, 0xFE, 0, 0 }));

			results = CryptographyUtility.GetBytes((uint)0xFEDCBA98);
			Assert.IsTrue(CryptographyUtility.CompareBytes(results, new byte[] { 0x98, 0xBA, 0xDC, 0xFE }));
		}
	}
}
