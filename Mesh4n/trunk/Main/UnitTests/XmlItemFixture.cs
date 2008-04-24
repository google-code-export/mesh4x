using System;
#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

namespace SimpleSharing.Tests
{
	[TestClass]
	public class XmlItemFixture : TestFixtureBase
	{
		[TestMethod]
		public void ShouldGetHashcodeWithNullDescription()
		{
			XmlItem i1 = new XmlItem(Guid.NewGuid().ToString(), "title", null, GetElement("<payload/>"));
			XmlItem i2 = new XmlItem(i1.Id, "title", null, GetElement("<payload/>"));

			Assert.AreEqual(i1, i2);
			Assert.AreEqual(i1.GetHashCode(), i2.GetHashCode());
		}

		[TestMethod]
		public void ShoudAllowNullTitle()
		{
			XmlItem item = new XmlItem(null, "description", GetElement("<payload/>"), DateTime.Now);

			Assert.IsNull(item.Title);
		}

		[TestMethod]
		public void ShoudAllowNullDescription()
		{
			XmlItem item = new XmlItem("title", null, GetElement("<payload/>"), DateTime.Now);

			Assert.IsNull(item.Description);
		}

		[TestMethod]
		public void ShouldEqualWithSameValues()
		{
			XmlItem i1 = new XmlItem(Guid.NewGuid().ToString(), "title", "description", GetElement("<payload/>"), DateTime.Now);
			XmlItem i2 = new XmlItem(i1.Id, "title", "description", GetElement("<payload/>"), i1.Tag);

			Assert.AreEqual(i1, i2);
		}

		[TestMethod]
		public void ShouldNotEqualWithDifferentPayload()
		{
			XmlItem i1 = new XmlItem(Guid.NewGuid().ToString(), "title", "description", GetElement("<payload/>"), DateTime.Now);
			XmlItem i2 = new XmlItem(i1.Id, "title", "description", GetElement("<payload id='foo'/>"), DateTime.Now);

			Assert.AreNotEqual(i1, i2);
		}

		[TestMethod]
		public void ShouldNotEqualWithDifferentTitle()
		{
			XmlItem i1 = new XmlItem(Guid.NewGuid().ToString(), "title1", "description", GetElement("<payload/>"), DateTime.Now);
			XmlItem i2 = new XmlItem(i1.Id, "title2", "description", GetElement("<payload/>"), DateTime.Now);

			Assert.AreNotEqual(i1, i2);
		}

		[TestMethod]
		public void ShouldNotEqualWithDifferentDescription()
		{
			XmlItem i1 = new XmlItem(Guid.NewGuid().ToString(), "title", "description1", GetElement("<payload/>"), DateTime.Now);
			XmlItem i2 = new XmlItem(i1.Id, "title", "description2", GetElement("<payload/>"), DateTime.Now);

			Assert.AreNotEqual(i1, i2);
		}

		[TestMethod]
		public void ShouldAddEmptyPayloadIfTitleAndDescriptionAreNull()
		{
			XmlItem item = new XmlItem(null, null, null);

			Assert.IsNotNull(item.Payload);
			Assert.AreEqual(0, item.Payload.ChildNodes.Count);
		}

		[TestMethod]
		public void ShouldNotThrowExceptionIfTitleAndDescriptionAreNull()
		{
			XmlItem item = new XmlItem(null, null, null, DateTime.Now);

			item.GetHashCode();
		}
	}
}
