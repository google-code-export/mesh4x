#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Text;
using System.Collections.Generic;
using System.Threading;

namespace Mesh4n.Tests
{
	[TestClass]
	public class BehaviorFixture : TestFixtureBase
	{
		#region Merge

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void MergeShouldThrowIfIncomingItemNull()
		{
			Behaviors.Merge(new Item(new NullXmlItem("1"), new Sync("1")), null);
		}

		[TestMethod]
		public void MergeShouldNotThrowIfOriginalItemNull()
		{
			Behaviors.Merge(null, new Item(new NullXmlItem("1"), new Sync("1")));
		}

		[TestMethod]
		public void MergeShouldAddWithoutConflict()
		{
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now, false);

			Item remoteItem = new Item(
				new XmlItem(sync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			ItemMergeResult result = Behaviors.Merge(null, remoteItem);

			Assert.AreEqual(MergeOperation.Added, result.Operation);
			Assert.IsNotNull(result.Proposed);
		}

		[TestMethod]
		public void MergeShouldUpdateWithoutConflict()
		{
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			Item originalItem = new Item(
				new XmlItem(sync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			// Simulate editing.
			sync = Behaviors.Update(originalItem.Sync, "REMOTE\\kzu", DateTime.Now, false);
			Item incomingItem = new Item(new XmlItem(sync.Id, "changed", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				sync);

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Updated, result.Operation);
			Assert.IsNotNull(result.Proposed);
			Assert.AreEqual("changed", result.Proposed.XmlItem.Title);
			Assert.AreEqual("REMOTE\\kzu", result.Proposed.Sync.LastUpdate.By);
		}

		[TestMethod]
		public void MergeShouldDeleteWithoutConflict()
		{
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			string id = sync.Id;
			Item originalItem = new Item(
				new XmlItem(sync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			// Simulate editing.
			sync = Behaviors.Update(originalItem.Sync, "REMOTE\\kzu", DateTime.Now, true);
			Item incomingItem = new Item(originalItem.XmlItem, sync);

			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Updated, result.Operation);
			Assert.IsNotNull(result.Proposed);
			Assert.AreEqual(true, result.Proposed.Sync.Deleted);
			Assert.AreEqual("REMOTE\\kzu", result.Proposed.Sync.LastUpdate.By);
		}

		[TestMethod]
		public void MergeShouldConflictOnDeleteWithConflict()
		{
			Sync localSync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			Item originalItem = new Item(
				new XmlItem(localSync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				localSync);

			Item incomingItem = originalItem.Clone();

			// Local editing.
			localSync = Behaviors.Update(originalItem.Sync, "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			originalItem = new Item(new XmlItem(localSync.Id, "changed", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				localSync);

			// Remote editing.
			Sync remoteSync = Behaviors.Update(incomingItem.Sync, "REMOTE\\kzu", DateTime.Now, false);
			remoteSync.Deleted = true;
			incomingItem = new Item(incomingItem.XmlItem, remoteSync);

			// Merge conflicting changed incoming item.
			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.IsNotNull(result.Proposed);
			Assert.AreEqual(true, result.Proposed.Sync.Deleted);
			Assert.AreEqual("REMOTE\\kzu", result.Proposed.Sync.LastUpdate.By);
		}

		[TestMethod]
		public void MergeShouldNoOpWithNoChanges()
		{
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now, false);
			Item item = new Item(
				new XmlItem(sync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			// Do a merge with the same item.
			ItemMergeResult result = Behaviors.Merge(item, item);

			Assert.AreEqual(MergeOperation.None, result.Operation);
			Assert.IsNull(result.Proposed);
		}

		[TestMethod]
		public void MergeShouldNoOpOnUpdatedLocalItemWithUnchangedIncoming()
		{
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			Item originalItem = new Item(
				new XmlItem(sync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			Item incomingItem = originalItem.Clone();

			// Simulate editing.
			sync = Behaviors.Update(originalItem.Sync, "mypc\\user", DateTime.Now, false);
			originalItem = new Item(new XmlItem(sync.Id, "changed", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				sync);

			// Merge with the older incoming item.
			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.None, result.Operation);
			Assert.IsNull(result.Proposed);
		}

		[TestMethod]
		public void MergeShouldIncomingWinWithConflict()
		{
			Sync localSync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			Item originalItem = new Item(
				new XmlItem(localSync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				localSync);

			Item incomingItem = originalItem.Clone();

			// Local editing.
			localSync = Behaviors.Update(originalItem.Sync, "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			originalItem = new Item(new XmlItem(localSync.Id, "changed", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				localSync);

			// Remote editing.
			Sync remoteSync = Behaviors.Update(incomingItem.Sync, "REMOTE\\kzu", DateTime.Now, false);
			incomingItem = new Item(new XmlItem(localSync.Id, "changed2", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				remoteSync);

			// Merge conflicting changed incoming item.
			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.IsNotNull(result.Proposed);
			// Remote item won
			Assert.AreEqual("REMOTE\\kzu", result.Proposed.Sync.LastUpdate.By);
			Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);
			Assert.AreEqual("mypc\\user", result.Proposed.Sync.Conflicts[0].Sync.LastUpdate.By);
		}

		[TestMethod]
		public void MergeShouldLocalWinWithConflict()
		{
			Sync localSync = Behaviors.Create(Guid.NewGuid().ToString(), "mypc\\user", DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			Item originalItem = new Item(
				new XmlItem(localSync.Id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				localSync);

			// Remote editing.
			Sync remoteSync = Behaviors.Update(localSync, "REMOTE\\kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			Item incomingItem = new Item(new XmlItem(localSync.Id, "changed2", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				remoteSync);

			// Local editing.
			localSync = Behaviors.Update(originalItem.Sync, "mypc\\user", DateTime.Now, false);
			originalItem = new Item(new XmlItem(localSync.Id, "changed", originalItem.XmlItem.Description,
				originalItem.XmlItem.Payload),
				localSync);

			// Merge conflicting changed incoming item.
			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.IsNotNull(result.Proposed);
			// Local item won
			Assert.AreEqual("mypc\\user", result.Proposed.Sync.LastUpdate.By);
			Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);
			Assert.AreEqual("REMOTE\\kzu", result.Proposed.Sync.Conflicts[0].Sync.LastUpdate.By);
		}

		[TestMethod]
		public void MergeShouldConflictWithDeletedLocalItem()
		{
			Sync localSync = Behaviors.Create(Guid.NewGuid().ToString(), DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(3)), false);
			string id = localSync.Id;
			Item originalItem = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				localSync);

			// Remote editing.
			Sync remoteSync = Behaviors.Update(originalItem.Sync, "REMOTE\\kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			Item incomingItem = new Item(
				new XmlItem(id, "changed2", originalItem.XmlItem.Description, originalItem.XmlItem.Payload),
				remoteSync);

			localSync = Behaviors.Delete(localSync, DeviceAuthor.Current, DateTime.Now);
			originalItem = new Item(originalItem.XmlItem, localSync);

			// Merge conflicting changed incoming item.
			ItemMergeResult result = Behaviors.Merge(originalItem, incomingItem);

			Assert.AreEqual(MergeOperation.Conflict, result.Operation);
			Assert.IsNotNull(result.Proposed);
			// Local item won
			Assert.AreEqual(DeviceAuthor.Current, result.Proposed.Sync.LastUpdate.By);
			Assert.AreEqual(1, result.Proposed.Sync.Conflicts.Count);
			Assert.AreEqual("REMOTE\\kzu", result.Proposed.Sync.Conflicts[0].Sync.LastUpdate.By);
			Assert.IsTrue(result.Proposed.Sync.Deleted);
		}

		// TODO:
		// WinnerPicking missing tests: FirstWinsWithBy and comparison with updates.
		// FirstWinsWithWhen when lastupdate.when is null

		#endregion

		#region Update

		[TestMethod]
		public void UpdateShouldNotModifyArgument()
		{
			Sync expected = Behaviors.Update(new Sync(Guid.NewGuid().ToString()), "foo", null, false);

			Sync updated = Behaviors.Update(expected, "bar", null, false);

			Assert.AreEqual("foo", expected.LastUpdate.By);
			Assert.AreNotEqual(expected, updated);
			Assert.AreEqual("bar", updated.LastUpdate.By);
		}

		[TestMethod]
		public void UpdateShouldIncrementUpdatesByOne()
		{
			Sync sync = new Sync(Guid.NewGuid().ToString());

			int original = sync.Updates;

			Sync updated = Behaviors.Update(sync, "foo", DateTime.Now, false);

			Assert.AreEqual(original + 1, updated.Updates);
		}

		[TestMethod]
		public void UpdateShouldAddTopmostHistory()
		{
			Sync sync = new Sync(Guid.NewGuid().ToString());

			int original = sync.Updates;

			sync = Behaviors.Update(sync, "foo", DateTime.Now, false);
			sync = Behaviors.Update(sync, "bar", DateTime.Now, false);

			Assert.AreEqual("bar", GetFirst<History>(sync.UpdatesHistory).By);
		}

		#endregion

		#region Create

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void CreateShouldThrowExceptionIfIdNull()
		{
			Behaviors.Create(null, "mypc\\user", DateTime.Now, true);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void CreateShouldThrowExceptionIfIdEmpty()
		{
			Behaviors.Create("", "mypc\\user", DateTime.Now, true);
		}

		[TestMethod]
		public void CreateShouldNotThrowIfNullByWithWhen()
		{
			Behaviors.Create(Guid.NewGuid().ToString(), null, DateTime.Now, true);
		}

		[TestMethod]
		public void CreateShouldNotThrowIfNullWhenWithBy()
		{
			Behaviors.Create(Guid.NewGuid().ToString(), "foo", null, true);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void CreateShouldThrowIfNullWhenAndBy()
		{
			Behaviors.Create(Guid.NewGuid().ToString(), null, null, true);
		}

		[TestMethod]
		public void CreateShouldReturnSyncWithId()
		{
			Guid id = Guid.NewGuid();
			Sync sync = Behaviors.Create(id.ToString(), "mypc\\user", DateTime.Now, true);
			Assert.AreEqual(id.ToString(), sync.Id);
		}

		[TestMethod]
		public void CreateShouldReturnSyncWithUpdatesEqualsToOne()
		{
			Guid id = Guid.NewGuid();
			Sync sync = Behaviors.Create(id.ToString(), "mypc\\user", DateTime.Now, true);
			Assert.AreEqual(1, sync.Updates);
		}

		[TestMethod]
		public void CreateShouldHaveAHistory()
		{
			Guid id = Guid.NewGuid();
			Sync sync = Behaviors.Create(id.ToString(), "mypc\\user", DateTime.Now, true);
			List<History> histories = new List<History>(sync.UpdatesHistory);
			Assert.AreEqual(1, histories.Count);
		}

		[TestMethod]
		public void CreateShouldHaveHistorySequenceSameAsUpdateCount()
		{
			Guid id = Guid.NewGuid();
			Sync sync = Behaviors.Create(id.ToString(), "mypc\\user", DateTime.Now, true);
			History history = new List<History>(sync.UpdatesHistory)[0];
			Assert.AreEqual(sync.Updates, history.Sequence);
		}

		[TestMethod]
		public void CreateShouldHaveHistoryWhenEqualsToNow()
		{
			Guid id = Guid.NewGuid();
			DateTime time = DateTime.Now;
			Sync sync = Behaviors.Create(id.ToString(), "mypc\\user", DateTime.Now, true);
			History history = new List<History>(sync.UpdatesHistory)[0];
			DatesEqualWithoutMillisecond(time, history.When.Value);
		}

		# endregion

		#region Delete

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfSyncNull()
		{
			Behaviors.Delete(null, "mypc\\user", DateTime.Now);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfByNull()
		{
			Behaviors.Delete(new Sync(Guid.NewGuid().ToString()), null, DateTime.Now);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfWhenParameterNull()
		{
			Behaviors.Delete(new Sync(Guid.NewGuid().ToString()), "mypc\\user", null);
		}

		[TestMethod]
		public void ShouldIncrementUpdatesByOneOnDeletion()
		{
			Sync sync = new Sync(new Guid().ToString());
			int updates = sync.Updates;
			sync = Behaviors.Delete(sync, "mypc\\user", DateTime.Now);
			Assert.AreEqual(updates + 1, sync.Updates);
		}

		[TestMethod]
		public void ShouldDeletionAttributeBeTrue()
		{
			Sync sync = new Sync(new Guid().ToString());
			sync = Behaviors.Delete(sync, "mypc\\user", DateTime.Now);
			Assert.AreEqual(true, sync.Deleted);
		}

		#endregion Delete

		#region ResolveConflicts

		[TestMethod]
		public void ResolveShouldNotUpdateArgument()
		{
			Item item = new Item(
				new XmlItem("foo", "bar", GetElement("<payload/>"), DateTime.Now),
				Behaviors.Create(Guid.NewGuid().ToString(), "one", DateTime.Now, false));

			Item resolved = Behaviors.ResolveConflicts(item, "two", DateTime.Now, false);

			Assert.AreNotSame(item, resolved);
		}

		[TestMethod]
		public void ResolveShouldUpdateEvenIfNoConflicts()
		{
			Item item = new Item(
				new XmlItem("foo", "bar", GetElement("<payload/>"), DateTime.Now),
				Behaviors.Create(Guid.NewGuid().ToString(), "one", DateTime.Now, false));

			Item resolved = Behaviors.ResolveConflicts(item, "two", DateTime.Now, false);

			Assert.AreNotEqual(item, resolved);
			Assert.AreEqual(2, resolved.Sync.Updates);
			Assert.AreEqual("two", resolved.Sync.LastUpdate.By);
		}

		[TestMethod]
		public void ResolveShouldAddConflictItemHistoryWithoutIncrementingUpdates()
		{
			XmlItem xml = new XmlItem("foo", "bar", GetElement("<payload/>"), DateTime.Now);
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "one",
				DateTime.Now.Subtract(TimeSpan.FromMinutes(10)), false);
			Sync conflictSync = Behaviors.Create(sync.Id, "two",
				DateTime.Now.Subtract(TimeSpan.FromHours(1)), false);
			sync.Conflicts.Add(new Item(xml.Clone(), conflictSync));

			Item conflicItem = new Item(xml, sync);
			Item resolvedItem = Behaviors.ResolveConflicts(conflicItem, "one", DateTime.Now, false);

			Assert.AreEqual(2, resolvedItem.Sync.Updates);
			Assert.AreEqual(3, Count(resolvedItem.Sync.UpdatesHistory));
		}

		[TestMethod]
		public void ResolveShouldRemoveConflicts()
		{
			XmlItem xml = new XmlItem("foo", "bar", GetElement("<payload/>"), DateTime.Now);
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "one",
				DateTime.Now.Subtract(TimeSpan.FromMinutes(10)), false);
			Sync conflictSync = Behaviors.Create(sync.Id, "two",
				DateTime.Now.Subtract(TimeSpan.FromHours(1)), false);
			sync.Conflicts.Add(new Item(xml.Clone(), conflictSync));

			Item conflicItem = new Item(xml, sync);
			Item resolvedItem = Behaviors.ResolveConflicts(conflicItem, "one", DateTime.Now, false);

			Assert.AreEqual(0, resolvedItem.Sync.Conflicts.Count);
		}

		[TestMethod]
		public void ResolveShouldNotAddConflictItemHistoryIfSubsumed()
		{
			XmlItem xml = new XmlItem("foo", "bar", GetElement("<payload/>"), DateTime.Now);
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "one",
				DateTime.Now, false);
			Sync conflictSync = sync.Clone();
			// Add subsuming update
			sync = Behaviors.Update(sync, "one", DateTime.Now.AddDays(1), false);

			conflictSync = Behaviors.Update(conflictSync, "two", DateTime.Now.AddMinutes(5), false);

			sync.Conflicts.Add(new Item(xml.Clone(), conflictSync));

			Item conflicItem = new Item(xml, sync);
			Item resolvedItem = Behaviors.ResolveConflicts(conflicItem, "one", DateTime.Now, false);

			Assert.AreEqual(3, resolvedItem.Sync.Updates);
			// there would otherwise be 3 updates to the original item + 2 on the conflict.
			Assert.AreEqual(4, Count(resolvedItem.Sync.UpdatesHistory));
		}

		#endregion

		#region SparsePurge

		[TestMethod]
		public void PurgeShouldRemoveOlderSequence()
		{
			Sync s = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false);
			s = Behaviors.Update(s, "kzu", DateTime.Now, false);

			Assert.AreEqual(2, Count(s.UpdatesHistory));
			Assert.AreEqual(2, s.Updates);

			Sync purged = Behaviors.SparsePurge(s);

			Assert.AreEqual(1, Count(purged.UpdatesHistory));
		}

		[TestMethod]
		public void PurgeShouldPreserveHistoryOrder()
		{
			Sync s = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false);
			s = Behaviors.Update(s, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(30)), false);
			s = Behaviors.Update(s, "vga", DateTime.Now, false);

			Sync purged = Behaviors.SparsePurge(s);

			Assert.AreEqual(2, Count(purged.UpdatesHistory));
			Assert.AreEqual("vga", purged.LastUpdate.By);
		}

		[TestMethod]
		public void PurgeShouldPreserveHistoryNoBy()
		{
			Sync s = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false);
			s = Behaviors.Update(s, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(30)), false);
			s = Behaviors.Update(s, null, DateTime.Now.Subtract(TimeSpan.FromMinutes(10)), false);
			DateTime lastWhen = Timestamp.Normalize(DateTime.Now.Subtract(TimeSpan.FromMinutes(5)));
			s = Behaviors.Update(s, null, lastWhen, false);

			Sync purged = Behaviors.SparsePurge(s);

			Assert.AreEqual(3, Count(purged.UpdatesHistory));
			Assert.AreEqual(null, purged.LastUpdate.By);
			Assert.AreEqual(lastWhen, purged.LastUpdate.When);
		}

		[TestMethod]
		public void PurgeShouldPreserveOtherSyncProperties()
		{
			Sync s = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), true);
			s = Behaviors.Update(s, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(30)), false);
			s = Behaviors.Update(s, "vga", DateTime.Now, false);

			// TODO: set other properties
			s.Tag = 5;
			s.NoConflicts = true;
			s.Conflicts.Add(new Item(new XmlItem("foo", "bar", GetElement("<payload/>"), DateTime.Now), new Sync("foo")));

			Sync purged = Behaviors.SparsePurge(s);

			Assert.AreEqual(2, Count(purged.UpdatesHistory));
			Assert.AreEqual("vga", purged.LastUpdate.By);
			Assert.AreEqual(5, purged.Tag);
			Assert.IsTrue(purged.NoConflicts);
			Assert.AreEqual(1, purged.Conflicts.Count);
		}

		#endregion

		private static void DatesEqualWithoutMillisecond(DateTime d1, DateTime d2)
		{
			Assert.AreEqual(d1.Year, d2.Year);
			Assert.AreEqual(d1.Month, d2.Month);
			Assert.AreEqual(d1.Date, d2.Date);
			Assert.AreEqual(d1.Hour, d2.Hour);
			Assert.AreEqual(d1.Minute, d2.Minute);
			Assert.AreEqual(d1.Second, d2.Second);
		}
	}
}