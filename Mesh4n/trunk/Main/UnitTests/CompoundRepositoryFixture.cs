#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Threading;

namespace Mesh4n.Tests
{
	[TestClass]
	public class CompoundRepositoryFixture : RepositoryFixture
	{
		IContentAdapter xmlRepo;
		ISyncRepository syncRepo;

		[TestInitialize]
		public void SetUp()
		{
			xmlRepo = new MockXmlRepository();
			syncRepo = new MockSyncRepository();
		}

		protected override ISyncAdapter CreateRepository()
		{
			return new CompoundSyncAdapter(xmlRepo, syncRepo);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullXmlRepo()
		{
			new CompoundSyncAdapter(null, syncRepo);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullSyncRepo()
		{
			new CompoundSyncAdapter(xmlRepo, null);
		}

		[TestMethod]
		public void ShouldNotSupportMerge()
		{
			CompoundSyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			Assert.IsFalse(repo.SupportsMerge);
		}

		[TestMethod]
		public void ShouldExposeInnerRepositories()
		{
			CompoundSyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			Assert.AreSame(xmlRepo, repo.XmlRepository);
			Assert.AreSame(syncRepo, repo.SyncRepository);
		}

		[TestMethod]
		public void ShouldGetExistingItem()
		{
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"), DateTime.Now);
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			
			object tag = null;
			xmlRepo.Add(item, out tag);

			sync.Tag = tag;
			syncRepo.Save(sync);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			Item i = repo.Get(item.Id);

			Assert.AreEqual(item.Tag.ToString(), i.XmlItem.Tag.ToString());
			Assert.AreEqual(sync.Tag, i.Sync.Tag);
		}

		[TestMethod]
		public void ShouldGetUpdatedItemIfHashChanged()
		{
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			
			object tag = null;
			xmlRepo.Add(item, out tag);

			sync.Tag = tag;
			syncRepo.Save(sync);

			item.Title = "changed";

			Thread.Sleep(1000);

			xmlRepo.Update(item, out tag);
			item.Tag = tag;

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			Item i = repo.Get(item.Id);

			Assert.AreEqual(item.Tag, i.XmlItem.Tag);
			Assert.AreEqual(2, i.Sync.Updates);
		}

		[TestMethod]
		public void ShouldGetCreateNewSyncForNewXmlItem()
		{
			object tag = null;
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			xmlRepo.Add(item, out tag);

			item.Tag = tag;

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			Item i = repo.Get(item.Id);

			Assert.AreEqual(item.Tag, i.XmlItem.Tag);
			Assert.AreEqual(1, i.Sync.Updates);
		}

		[TestMethod]
		public void ShouldGetDeletedItem()
		{
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"), DateTime.Now);
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			sync.Tag = item.Tag;
			//Not added simulates deleted.
			//xmlRepo.Add(item);
			syncRepo.Save(sync);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			Item i = repo.Get(item.Id);

			Assert.IsTrue(i.Sync.Deleted);
			Assert.AreEqual(2, i.Sync.Updates);
		}

		[TestMethod]
		public void ShouldGetAll()
		{
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);

			object tag = null;
			xmlRepo.Add(item, out tag);

			item.Tag = tag;
			sync.Tag = tag;

			syncRepo.Save(sync);

			item = new XmlItem(Guid.NewGuid().ToString(), "bar", "baz", GetElement("<payload />"));
			sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			List<Item> items = new List<Item>(repo.GetAll());

			Assert.AreEqual(2, items.Count);
		}

		[TestMethod]
		public void ShouldGetAllWithAutoUpdatesToSync()
		{
			object tag = null;
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			xmlRepo.Add(item, out tag);
			item.Tag = tag;

			item = new XmlItem(Guid.NewGuid().ToString(), "bar", "baz", GetElement("<payload />"));
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			item.Title = "changed";

			Thread.Sleep(1000);

			xmlRepo.Update(item, out tag);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			List<Item> items = new List<Item>(repo.GetAll());

			Assert.AreEqual(2, items.Count);
			Assert.AreEqual(1, items[0].Sync.Updates);
			Assert.AreEqual(2, items[1].Sync.Updates);
		}

		[TestMethod]
		public void ShouldGetAllIncludeDeleted()
		{
			// simulates a deleted item, there's sync but no item.
			Sync sync = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			syncRepo.Save(sync);

			object tag;
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = item.Tag;
			syncRepo.Save(sync);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			List<Item> items = new List<Item>(repo.GetAll());

			Assert.AreEqual(2, items.Count);
			Assert.IsTrue(items[1].Sync.Deleted);
		}

		[TestMethod]
		public void ShouldGetAllIncludeMultipleDeleted()
		{
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			object tag;
			xmlRepo.Add(item, out tag);

			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			// simulates a deleted item, there's sync but no item.
			sync = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			syncRepo.Save(sync);
			sync = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			syncRepo.Save(sync);
			sync = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			syncRepo.Save(sync);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			List<Item> items = new List<Item>(repo.GetAll());

			Assert.AreEqual(4, items.Count);
		}

		[TestMethod]
		public void ShouldGetAddedAfterSince()
		{
			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			object tag;
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			DateTime since = DateTime.Now;

			item = new XmlItem(Guid.NewGuid().ToString(), "bar", "baz", GetElement("<payload />"));
			sync = Behaviors.Create(item.Id, "kzu", DateTime.Now, false);
			
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			List<Item> items = new List<Item>(repo.GetAllSince(since));

			Assert.AreEqual(1, items.Count);
		}

		[TestMethod]
		public void ShouldGetUpdatedAfterSince()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem item = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(item.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			object tag;
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			DateTime since = DateTime.Now;

			item = new XmlItem(Guid.NewGuid().ToString(), "bar", "baz", GetElement("<payload />"));
			sync = Behaviors.Create(item.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(4)), false);
			
			xmlRepo.Add(item, out tag);
			item.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			Assert.AreEqual(0, Count(repo.GetAllSince(since)));

			sync = Behaviors.Update(sync, "kzu", DateTime.Now, false);
			syncRepo.Save(sync);
			
			List<Item> items = new List<Item>(repo.GetAllSince(since));

			Assert.AreEqual(1, items.Count);
		}

		[TestMethod]
		public void ShouldAddItem()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			
			Item item = new Item(xml, sync);

			repo.Add(item);

			Assert.AreEqual(xml.Tag, xmlRepo.Get(xml.Id).Tag);
			Assert.AreEqual(sync.Tag, syncRepo.Get(sync.Id).Tag);
		}

		[TestMethod]
		public void ShouldAddItemSaveItemHash()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);

			Item item = new Item(xml, sync);

			repo.Add(item);

			Item saved = repo.Get(xml.Id);

			Assert.AreEqual(saved.XmlItem.Tag, saved.Sync.Tag);
		}

		[TestMethod]
		public void ShouldAddOnlySyncForDeletedItem()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			
			sync = Behaviors.Delete(sync, "kzu", DateTime.Now);

			Item item = new Item(xml, sync);

			repo.Add(item);

			Assert.IsNull(xmlRepo.Get(xml.Id));
			Assert.AreEqual(sync.Tag, syncRepo.Get(sync.Id).Tag);
		}

		[TestMethod]
		public void ShouldDeleteItem()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);

			object tag;
			xmlRepo.Add(xml, out tag);
			xml.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			repo.Delete(xml.Id);

			Assert.IsNull(xmlRepo.Get(xml.Id));
			Assert.IsTrue(syncRepo.Get(sync.Id).Deleted);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowUpdateNullItem()
		{
			new CompoundSyncAdapter(xmlRepo, syncRepo).Update(null);
		}

		[TestMethod]
		public void ShouldUpdateItem()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			sync = Behaviors.Delete(sync, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(4)));

			object tag;
			xmlRepo.Add(xml, out tag);
			xml.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			sync = Behaviors.Update(sync, "kzu", DateTime.Now, false);
			xml.Title = "changed";

			Item item = new Item(xml, sync);

			repo.Update(item);

			Assert.AreEqual(xml.Tag, xmlRepo.Get(xml.Id).Tag); 
			Assert.AreEqual(sync.Tag, syncRepo.Get(sync.Id).Tag);
		}

		[TestMethod]
		public void ShouldUpdateAndGetItem()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			
			sync = Behaviors.Delete(sync, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(4)));

			object tag;
			xmlRepo.Add(xml, out tag);
			xml.Tag = tag;
			sync.Tag = tag;

			syncRepo.Save(sync);

			sync = Behaviors.Update(sync, "kzu", DateTime.Now, false);
			xml.Title = "changed";

			Item item = new Item(xml, sync);

			repo.Update(item);

			Item item2 = repo.Get(xml.Id);

			Assert.AreEqual(xml.Tag, item2.XmlItem.Tag);
			Assert.AreEqual(sync.Tag, item2.Sync.Tag);
		}

		[TestMethod]
		public void ShouldUpdateSaveItemHash()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			
			sync = Behaviors.Delete(sync, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(4)));

			object tag;
			xmlRepo.Add(xml, out tag);
			xml.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			sync = Behaviors.Update(sync, "kzu", DateTime.Now, false);
			xml.Title = "changed";

			Item item = new Item(xml, sync);

			repo.Update(item);

			Item item2 = repo.Get(xml.Id);

			Assert.AreEqual(item2.XmlItem.Tag, item2.Sync.Tag);
		}

		[TestMethod]
		public void ShouldGetConflicts()
		{
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			XmlItem xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false);
			
			Item conflict = new Item(xml.Clone(), sync.Update("vga", DateTime.Now));
			conflict.XmlItem.Title = "bar";

			sync = sync.Update("kzu", DateTime.Now);
			sync.Conflicts.Add(conflict);

			object tag;
			xmlRepo.Add(xml, out tag);
			xml.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			xml = new XmlItem(Guid.NewGuid().ToString(), "foo", "bar", GetElement("<payload />"));
			sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);

			xmlRepo.Add(xml, out tag);
			xml.Tag = tag;
			sync.Tag = tag;
			syncRepo.Save(sync);

			List<Item> conflicts = new List<Item>(repo.GetConflicts());

			Assert.AreEqual(1, conflicts.Count);
		}

		[TestMethod]
		public void ShouldUpdateSyncLocalItemsOnUpdate()
		{
			ISyncRepository syncRepo = new MockSyncRepository();
			IContentAdapter xmlRepo = new MockXmlRepository();
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			string id = Guid.NewGuid().ToString();
			Sync sync = Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false);
			Item item = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			// Save original item.
			repo.Add(item);
			DateTime? lastUpdated = item.Sync.LastUpdate.When;

			Thread.Sleep(1000);

			object tag;
			// Local editing outside of SSE by the local app.
			xmlRepo.Update(new XmlItem(id, "changed", item.XmlItem.Description,
				item.XmlItem.Payload), out tag);

			// Export of same item should cause it to 
			// be updated with new Sync info and a no-op on sync.
			Item saved = GetFirst<Item>(repo.GetAll());

			Sync updated = saved.Sync;
			Assert.AreEqual(2, updated.Updates);
			Assert.AreNotEqual(lastUpdated, updated.LastUpdate.When);
		}

		[TestMethod]
		public void ShouldUpdateSyncDeletedOnGetConflictsIfItemIsDeleted()
		{
			ISyncRepository syncRepo = new MockSyncRepository();
			IContentAdapter xmlRepo = new MockXmlRepository().AddOneItem();
			ISyncAdapter repo = new CompoundSyncAdapter(xmlRepo, syncRepo);

			// Cause the item to be Sync'ed.
			Item item = GetFirst<Item>(repo.GetAll());

			// Introduce a conflict.
			Item clone = item.Clone();
			clone.XmlItem.Title = "Conflict";
			Sync updatedSync = Behaviors.Update(clone.Sync, "Conflict", DateTime.Now, false);
			item.Sync.Conflicts.Add(new Item(clone.XmlItem, updatedSync));
			syncRepo.Save(item.Sync);

			// Delete xml item.
			xmlRepo.Remove(item.XmlItem.Id);

			Item conflict = GetFirst<Item>(repo.GetConflicts());
			Assert.IsNotNull(conflict);
		}
	}
}
