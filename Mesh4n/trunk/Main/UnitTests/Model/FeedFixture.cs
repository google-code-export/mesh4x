#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;

namespace Mesh4n.Tests
{
	[TestClass]
	public class FeedFixture
	{
		[TestMethod]
		public void ShouldMatchConstructorWithProperties()
		{
			Feed f = new Feed("title", "link", "description");
			Assert.AreEqual("title", f.Title);
			Assert.AreEqual("link", f.Link);
			Assert.AreEqual("description", f.Description);
		}
	}
}
