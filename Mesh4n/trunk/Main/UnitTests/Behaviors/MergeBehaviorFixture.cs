#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Text;
using System.Collections.Generic;
using System.Xml.XPath;
using System.IO;
using System.Threading;

namespace SimpleSharing.Tests
{
	[TestClass]
	public class MergeBehaviorFixture : TestFixtureBase
	{
		[TestMethod]
		public void ShouldWinLatestUpdateWithoutConflicts()
		{
			Sync sa = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			Sync sb = sa.Clone();

			sb = Behaviors.Update(sb, "vga", DateTime.Now.AddSeconds(5), false);

			Item originalItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sa);
			originalItem.XmlItem.Id = sb.Id;

			Item incomingItem = new Item(new XmlItem("b", "b", GetElement("<payload/>"), DateTime.Now), sb);
			incomingItem.XmlItem.Id = sb.Id;

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Updated, result.Operation);
			Assert.AreEqual("b", result.Proposed.XmlItem.Title);
			Assert.AreEqual("vga", result.Proposed.Sync.LastUpdate.By);
		}

		[TestMethod]
		public void ShouldNoOpForEqualItem()
		{
			Sync sa = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			Sync sb = sa.Clone();

			Item originalItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sa);
			originalItem.XmlItem.Id = sb.Id;

			Item incomingItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sb);
			incomingItem.XmlItem.Id = sb.Id;

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.None, result.Operation);
		}

		[TestMethod]
		public void ShouldAddWithoutConflicts()
		{
			Sync sa = new Sync(Guid.NewGuid().ToString());
			Behaviors.Update(sa, "kzu", DateTime.Now, false);

			Item incomingItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sa);
			ItemMergeResult result = Behaviors.Merge(null, incomingItem);

			Assert.AreEqual(MergeOperation.Added, result.Operation);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfSyncNoHistory()
		{
			Sync sa = new Sync(Guid.NewGuid().ToString());

			Sync sb = sa.Clone();

			Item originalItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sa);
			originalItem.XmlItem.Id = sb.Id;
			originalItem.Sync.Tag = originalItem.XmlItem.Tag;

			Item incomingItem = new Item(new XmlItem("b", "b", GetElement("<payload/>"), DateTime.Now), sb);
			incomingItem.XmlItem.Id = sb.Id;

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);
		}

		[TestMethod]
		public void ShouldWinLatestUpdateWithConflicts()
		{
			Sync sa = Behaviors.Create(Guid.NewGuid().ToString(),
				"kzu", DateTime.Now.Subtract(TimeSpan.FromSeconds(10)), false);

			Sync sb = sa.Clone();
			sb = Behaviors.Update(sb, "vga", DateTime.Now.AddSeconds(50), false);
			sa = Behaviors.Update(sa, "kzu", DateTime.Now.AddSeconds(100), false);

			Item originalItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sa);
			originalItem.XmlItem.Id = sb.Id;
			originalItem.Sync.Tag = originalItem.XmlItem.Tag;
			Item incomingItem = new Item(new XmlItem("b", "b", GetElement("<payload/>"), DateTime.Now), sb);
			incomingItem.XmlItem.Id = sb.Id;

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.AreEqual("a", result.Proposed.XmlItem.Title);
			Assert.AreEqual("kzu", result.Proposed.Sync.LastUpdate.By);
			Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);
		}

		[TestMethod]
		public void ShouldWinLatestUpdateWithConflictsPreserved()
		{
			Sync sa = new Sync(Guid.NewGuid().ToString());
			sa = Behaviors.Update(sa, "kzu", DateTime.Now, false);

			Sync sb = sa.Clone();

			sb = Behaviors.Update(sb, "vga", DateTime.Now.AddSeconds(50), false);
			sa = Behaviors.Update(sa, "kzu", DateTime.Now.AddSeconds(100), false);

			Item originalItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), (object)DateTime.Now), sa);
			originalItem.XmlItem.Id = sb.Id;

			Item incomingItem = new Item(new XmlItem("b", "b", GetElement("<payload/>"), (object)DateTime.Now), sb);
			incomingItem.XmlItem.Id = sb.Id;

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);
			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.AreEqual("a", result.Proposed.XmlItem.Title);
			Assert.AreEqual("kzu", result.Proposed.Sync.LastUpdate.By);
			Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);

			// Merge the winner with conflict with the local no-conflict one.
			// Should be an update.
			result = Behaviors.Merge(originalItem, result.Proposed);

			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.AreEqual("a", result.Proposed.XmlItem.Title);
			Assert.AreEqual("kzu", result.Proposed.Sync.LastUpdate.By);
			Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);
		}

		[TestMethod]
		public void ShouldMergeNoneIfEqualItem()
		{
			DateTime now = DateTime.Now;
			Sync sa = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", now, false);
			Sync sb = Behaviors.Update(sa, "kzu", now, false);

			Item originalItem = new Item(new XmlItem("a", "a", GetElement("<payload/>"), DateTime.Now), sa);
			originalItem.XmlItem.Id = sb.Id;

			Item incomingItem = new Item(new XmlItem("b", "b", GetElement("<payload/>"), DateTime.Now), sb);
			incomingItem.XmlItem.Id = sb.Id;

			ItemMergeResult result = Behaviors.Merge(originalItem, originalItem);

			//Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			//Assert.AreEqual("a", result.Proposed.XmlItem.Title);
			//Assert.AreEqual("kzu", result.Proposed.Sync.LastUpdate.By);
			//Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);

			//// Merge the winner with conflict with the local no-conflict one.
			//// Should be an update.
			//result = Behaviors.Merge(xmlRepo, syncRepo, result.Proposed);

			//Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			//Assert.AreEqual("a", result.Proposed.XmlItem.Title);
			//Assert.AreEqual("kzu", result.Proposed.Sync.LastUpdate.By);
			//Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);
		}
	}
}