#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;

namespace Mesh4n.Tests
{
	[TestClass]
	public class ItemEventArgsFixture
	{
		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullItem()
		{
			new ItemEventArgs(null);
		}
	}
}
