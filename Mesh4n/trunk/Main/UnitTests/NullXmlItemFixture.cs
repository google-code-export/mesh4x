#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n.Tests
{
	[TestClass]
	public class NullXmlItemFixture
	{
		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullId()
		{
			new NullXmlItem(null);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfEmptyId()
		{
			new NullXmlItem("");
		}

		[TestMethod]
		public void ShouldEqualHash()
		{
			IXmlItem item1 = new NullXmlItem("1");
			IXmlItem item2 = new NullXmlItem("1");

			Assert.AreEqual(item1.GetHashCode(), item2.GetHashCode());
		}

		[TestMethod]
		public void ShouldNotEqualDifferentId()
		{
			IXmlItem item1 = new NullXmlItem("1");
			IXmlItem item2 = new NullXmlItem("2");

			Assert.AreNotEqual(item1, item2);
			Assert.AreNotEqual(item1.GetHashCode(), item2.GetHashCode());
		}

		[TestMethod]
		public void ShouldNotEqualDifferentHash()
		{
			IXmlItem item1 = new NullXmlItem("1");
			IXmlItem item2 = new NullXmlItem("12");
			
			Assert.AreNotEqual(item1.GetHashCode(), item2.GetHashCode());
		}

		[TestMethod]
		public void ShouldNotEqualNull()
		{
			IXmlItem item1 = new NullXmlItem("1");

			Assert.AreNotEqual(item1, null);
		}
	}
}
