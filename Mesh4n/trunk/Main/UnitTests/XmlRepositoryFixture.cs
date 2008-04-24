#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif
using System;
using System.Collections.Generic;
using System.Text;
using SimpleSharing;
using System.Xml;
using System.Threading;

namespace SimpleSharing.Tests
{
	/// <summary>
	/// Base class for fixtures of implementations of <see cref="IXmlRepository"/>.
	/// </summary>
	[TestClass]
	public class XmlRepositoryFixture : TestFixtureBase
	{
		protected virtual IXmlRepository CreateRepository()
		{
			return new MockXmlRepository();
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfAddNullItem()
		{
			object tag = null;
			CreateRepository().Add(null, out tag);
		}

		[TestMethod]
		public void ShouldContainAfterAdd()
		{
			IXmlItem item = new MockXmlItem();
			IXmlRepository repo = CreateRepository();

			object tag = null;
			repo.Add(item, out tag);

			Assert.IsTrue(repo.Contains(item.Id));
		}

		[TestMethod]
		public void ShouldGetAddedItem()
		{
			IXmlItem item = new MockXmlItem();
			IXmlRepository repo = CreateRepository();
			object tag = null;
			repo.Add(item, out tag);

			IXmlItem item2 = repo.Get(item.Id);

			Assert.IsNotNull(item2);
		}

		[TestMethod]
		public void ShouldGetNullIfNonExistingId()
		{
			IXmlItem item = CreateRepository().Get("1");

			Assert.IsNull(item);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfGetNullId()
		{
			CreateRepository().Get(null);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfGetEmptyId()
		{
			CreateRepository().Get(string.Empty);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfContainsNullId()
		{
			CreateRepository().Contains(null);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfContainsEmptyId()
		{
			CreateRepository().Contains(string.Empty);
		}

		[TestMethod]
		public void ShouldEnumerateAllItems()
		{
			IXmlRepository repo = CreateRepository();
			object tag = null;
			repo.Add(new MockXmlItem(), out tag);
			repo.Add(new MockXmlItem(), out tag);
			repo.Add(new MockXmlItem(), out tag);

			IEnumerable<IXmlItem> items = repo.GetAll();

			Assert.AreEqual(3, Count(items));
		}

		[TestMethod]
		public void ShouldGetAllSinceDate()
		{
			object tag = null;

			IXmlRepository repo = CreateRepository();
			repo.Add(new MockXmlItem(), out tag);
			Thread.Sleep(1000);

			DateTime now = Timestamp.Normalize(DateTime.Now);

			Thread.Sleep(1000);

			repo.Add(new MockXmlItem(), out tag);
			repo.Add(new MockXmlItem(), out tag);

			IEnumerable<IXmlItem> items = repo.GetAllSince(now);

			Assert.AreEqual(2, Count(items));
		}

		[TestMethod]
		public void ShouldRemoveFalseIfNonExitingId()
		{
			IXmlRepository repo = CreateRepository();

			bool removed = repo.Remove("1");

			Assert.IsFalse(removed);
		}

		[TestMethod]
		public void ShouldRemoveTrueForExistingId()
		{
			object tag = null;

			IXmlRepository repo = CreateRepository();
			IXmlItem item = new MockXmlItem();
			repo.Add(item, out tag);

			bool removed = repo.Remove(item.Id);

			Assert.IsTrue(removed);
		}

		[TestMethod]
		public void ShouldNotReturnSameItemInstanceButEqual()
		{
			object tag = null;
			IXmlRepository repo = CreateRepository();
			IXmlItem item = new MockXmlItem();
			repo.Add(item, out tag);

			IXmlItem item2 = repo.Get(item.Id);

			Assert.AreNotSame(item, item2);
		}

		[TestMethod]
		public void ShouldUpdateItem()
		{
			object tag = null;

			IXmlRepository repo = CreateRepository();
			IXmlItem item = new MockXmlItem();
			repo.Add(item, out tag);

			item.Payload.InnerXml = "<foo>updated</foo>";

			repo.Update(item, out tag);

			IXmlItem item2 = repo.Get(item.Id);

			Assert.AreEqual("<payload><foo>updated</foo></payload>", item2.Payload.OuterXml);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldUpdateThrowIfNullItem()
		{
			object tag = null;
			CreateRepository().Update(null, out tag);
		}
	}
}
