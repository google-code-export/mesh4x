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
	public class SubArrayFixture
	{
		[TestMethod]
		public void GetsJustSubArrayWithOffsetAndCount()
		{
			int size = 20;
			byte[] test = new byte[size];
			for (byte i = 0; i < size; i++)
				test[i] = i;

			SubArray<byte> small = new SubArray<byte>(test, 10, 5);
			Assert.AreEqual(5, small.Count);
			Assert.AreEqual(5, small.Array.Length);
			Assert.IsTrue(CryptographyUtility.CompareBytes(new byte[] { 10, 11, 12, 13, 14 }, small.Array));
		}

		[TestMethod]
		public void GetsRestOfTheArrayWithJustOffset()
		{
			int size = 20;
			byte[] test = new byte[size];
			for (byte i = 0; i < size; i++)
				test[i] = i;

			SubArray<byte> small = new SubArray<byte>(test, 15);
			Assert.AreEqual(5, small.Count);
			Assert.AreEqual(5, small.Array.Length);
			Assert.IsTrue(CryptographyUtility.CompareBytes(new byte[] { 15, 16, 17, 18, 19 }, small.Array));
		}

		[TestMethod]
		public void IndexerWorksWithinSubrange()
		{
			int size = 20;
			byte[] test = new byte[size];
			for (byte i = 0; i < size; i++)
				test[i] = i;

			SubArray<byte> small = new SubArray<byte>(test, 10, 5);
			Assert.AreEqual((byte) 12, small[2]);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void NegativeIndexThrows()
		{
			byte[] test = new byte[20];
			SubArray<byte> small = new SubArray<byte>(test, 10, 5);
			byte result = small[-1];
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void IndexPastEndOfSubrangeIndexThrows()
		{
			byte[] test = new byte[20];
			SubArray<byte> small = new SubArray<byte>(test, 10, 5);
			byte result = small[5];
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void SubArrayPastEndOfArrayThrows()
		{
			byte[] test = new byte[20];
			SubArray<byte> small = new SubArray<byte>(test, 19, 10);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentOutOfRangeException))]
		public void NegativeStartIndexThrows()
		{
			byte[] test = new byte[20];
			SubArray<byte> small = new SubArray<byte>(test, -1, 10);
		}

		[TestMethod]
		public void SubArrayCanBeUsedDirectlyAsArray()
		{
			int size = 20;
			byte[] test = new byte[size];
			for (byte i = 0; i < size; i++)
				test[i] = i;

			SubArray<byte> small = new SubArray<byte>(test, 10, 5);
			byte[] array = small;
			Assert.AreEqual(5, array.Length);
			Assert.IsTrue(CryptographyUtility.CompareBytes(new byte[] { 10, 11, 12, 13, 14 }, array));
		}
	}
}
