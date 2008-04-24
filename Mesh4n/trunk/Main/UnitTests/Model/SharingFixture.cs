#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;

namespace SimpleSharing.Tests
{
	[TestClass]
	public class SharingFixture : TestFixtureBase
	{
		[TestMethod]
		public void ShouldGetSetPublicProperties()
		{
			TestProperties(new Sharing());
		}
	}
}
