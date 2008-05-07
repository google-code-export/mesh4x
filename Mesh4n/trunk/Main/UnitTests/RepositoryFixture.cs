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
	public class RepositoryFixture : TestFixtureBase
	{
		protected virtual ISyncAdapter CreateRepository()
		{
			return new MockRepository();
		}

		protected virtual XmlItem CreateItem(string id)
		{
			return new XmlItem(id, "foo", "bar", GetElement("<payload />"));
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowGetNullId()
		{
			CreateRepository().Get(null);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowGetEmptyId()
		{
			CreateRepository().Get("");
		}

		[TestMethod]
		public void ShouldGetNullIfNotExists()
		{
			ISyncAdapter repo = CreateRepository();

			Item i = repo.Get(Guid.NewGuid().ToString());

			Assert.IsNull(i);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowAddNullItem()
		{
			CreateRepository().Add(null);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowDeleteNullId()
		{
			CreateRepository().Delete(null);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowDeleteEmptyId()
		{
			CreateRepository().Delete("");
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowUpdateNullItem()
		{
			CreateRepository().Update(null);
		}

		[TestMethod]
		public void ShouldAddAndGetItem()
		{
			ISyncAdapter repo = CreateRepository();
			XmlItem xml = CreateItem(Guid.NewGuid().ToString());
			Sync sync = Behaviors.Create(xml.Id, "kzu", DateTime.Now, false);
			sync.Tag = xml.Tag;
			Item item = new Item(xml, sync);

			repo.Add(item);

			Item saved = repo.Get(xml.Id);

			Assert.IsNotNull(saved);
			Assert.AreEqual(item.XmlItem.Title, saved.XmlItem.Title);
			Assert.AreEqual(item.XmlItem.Description, saved.XmlItem.Description);
			Assert.AreEqual(item.XmlItem.Id, saved.XmlItem.Id);
			Assert.IsTrue(item.Sync.Equals(saved.Sync));
		}

		[TestMethod]
		public void ShouldGetAllItems()
		{
			ISyncAdapter repo = CreateRepository();

			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			id = Guid.NewGuid().ToString();
			item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			Assert.AreEqual(2, Count(repo.GetAll()));
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowAddDuplicateItemId()
		{
			ISyncAdapter repo = CreateRepository();

			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);
		}

		[TestMethod]
		public void ShouldGetAllSinceDate()
		{
			ISyncAdapter repo = CreateRepository();

			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromDays(1)), false));
			repo.Add(item);

			id = Guid.NewGuid().ToString();
			item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			Assert.AreEqual(1, Count(repo.GetAllSince(DateTime.Now.Subtract(TimeSpan.FromMinutes(10)))));
		}

		[TestMethod]
		public void ShouldGetAllIfNullSince()
		{
			ISyncAdapter repo = CreateRepository();

			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromDays(1)), false));
			repo.Add(item);

			id = Guid.NewGuid().ToString();
			item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			Assert.AreEqual(2, Count(repo.GetAllSince(null)));
		}

		[TestMethod]
		public void ShouldGetAllIfNullWhen()
		{
			ISyncAdapter repo = CreateRepository();

			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, null, false));
			repo.Add(item);

			id = Guid.NewGuid().ToString();
			item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			Assert.AreEqual(2, Count(repo.GetAllSince(DateTime.Now.Subtract(TimeSpan.FromMinutes(10)))));
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowGetAllNullFilter()
		{
			ISyncAdapter repo = CreateRepository();

			Count(repo.GetAll(null));
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowGetAllSinceWithNullFilter()
		{
			Count(CreateRepository().GetAllSince(DateTime.Now, null));
		}

		[TestMethod]
		public void ShouldGetAllPassFilter()
		{
			ISyncAdapter repo = CreateRepository();

			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			Predicate<Item> filter = delegate(Item i) { return i.Sync.Id == id; };

			string id2 = Guid.NewGuid().ToString();
			item = new Item(CreateItem(id2),
				Behaviors.Create(id2, DeviceAuthor.Current, DateTime.Now, false));
			repo.Add(item);

			Assert.AreEqual(1, Count(repo.GetAll(filter)));
			Item saved = GetFirst<Item>(repo.GetAll(filter));
			Assert.AreEqual(id, saved.Sync.Id);
		}

		[TestMethod]
		public void ShouldGetAllSinceRemoveMilliseconds()
		{
			DateTime created = new DateTime(2007, 9, 18, 12, 56, 23);
			DateTime since = new DateTime(2007, 9, 18, 12, 56, 23, 500);

			XmlItem item = CreateItem(Guid.NewGuid().ToString());
			Sync sync = Behaviors.Create(item.Id, "kzu", created, false);
			sync.Tag = item.Tag;

			ISyncAdapter repo = CreateRepository();
			repo.Add(new Item(item, sync));

			Assert.AreEqual(1, Count(repo.GetAllSince(since)));
		}

		[TestMethod]
		public void ShouldGetAllCallGetAllSinceWithNullSince()
		{
			SimpleRepository repo = new SimpleRepository();

			Count(repo.GetAll());

			Assert.AreEqual(null, repo.Since);
		}

		[TestMethod]
		public void ShouldGetAllWithFilterPassToImplementation()
		{
			SimpleRepository repo = new SimpleRepository();

			Count(repo.GetAll(MyFilter));

			Assert.AreEqual(new Predicate<Item>(MyFilter), repo.Filter);
		}

		[TestMethod]
		public void ShouldResolveConflicts()
		{
			MockRepository repo = new MockRepository();

			string id = Guid.NewGuid().ToString();
			Sync sync = Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false);

			Item item = new Item(new NullXmlItem(id),
				Behaviors.Update(sync, "kzu", DateTime.Now.Subtract(TimeSpan.FromHours(2)), false));
			sync.Conflicts.Add(
				new Item(
					CreateItem(Guid.NewGuid().ToString()),
					Behaviors.Update(sync, "kzu", DateTime.Now.Subtract(TimeSpan.FromHours(4)), false)));

			repo.Add(item);

			Item updated = repo.Update(item, true);

			item = repo.Get(id);

			Assert.AreEqual(0, item.Sync.Conflicts.Count);
            Assert.AreEqual(item, updated);
		}

		[TestMethod]
		public void ShouldSaveUpdatedItemOnResolveConflicts()
		{
			ISyncAdapter repo = CreateRepository();
			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false));
			repo.Add(item);

			// Introduce a conflict.
			IXmlItem xml = item.XmlItem.Clone();
			xml.Title = "Conflict";
			Sync updatedSync = Behaviors.Update(item.Sync, "Conflict", DateTime.Now, false);
			item.Sync.Conflicts.Add(new Item(xml, updatedSync));

			item.XmlItem.Title = "Resolved";

			repo.Update(item, true);

			IXmlItem storedXml = repo.Get(item.XmlItem.Id).XmlItem;

			Assert.AreEqual("Resolved", storedXml.Title);
		}

		[TestMethod]
		public void ShouldSaveUpdatedItemOnResolveConflicts2()
		{
			ISyncAdapter repo = CreateRepository();
			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false));
			repo.Add(item);

			// Introduce a conflict.
			IXmlItem xml = item.XmlItem.Clone();
			xml.Title = "Conflict";
			Sync updatedSync = Behaviors.Update(item.Sync, "Conflict", DateTime.Now, false);
			item.Sync.Conflicts.Add(new Item(xml, updatedSync));

			item.XmlItem.Title = "Resolved";

			repo.Update(item, true);

			IXmlItem storedXml = repo.Get(item.XmlItem.Id).XmlItem;

			Assert.AreEqual("Resolved", storedXml.Title, "An update with resolve conflicts was issued, but the stored item Title was not the updated one from the resolved conflict.");
		}

		[TestMethod]
		public void ShouldResolveConflictsPreserveDeletedState()
		{
			ISyncAdapter repo = CreateRepository();
			string id = Guid.NewGuid().ToString();
			Item item = new Item(CreateItem(id),
				Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(5)), false));
			repo.Add(item);

			// Introduce a conflict.
			IXmlItem xml = item.XmlItem.Clone();
			xml.Title = "Conflict";
			Sync updatedSync = Behaviors.Update(item.Sync, "Conflict", DateTime.Now, false);
			item.Sync.Conflicts.Add(new Item(xml, updatedSync));

			item.XmlItem.Title = "Resolved";

			Sync deletedSync = Behaviors.Delete(item.Sync, "Deleted", DateTime.Now);

			repo.Update(new Item(item.XmlItem, deletedSync), true);

			Item saved = repo.Get(item.XmlItem.Id);

			Assert.IsTrue(saved.XmlItem == null || saved.XmlItem is NullXmlItem,
				"An update to delete an item was issued. The repository should either return null for the Item.XmlItem or an instance of a NullXmlItem");
			Assert.IsTrue(saved.Sync.Deleted, "An update to delete an item was issued but its Sync.Deleted was not true.");
		}

		private bool MyFilter(Item item)
		{
			return true;
		}

		class SimpleRepository : SyncAdapter
		{
			public DateTime? Since;
			public Predicate<Item> Filter;

			public override bool SupportsMerge
			{
				get { throw new NotImplementedException("The method or operation is not implemented."); }
			}

			public override Item Get(string id)
			{
				throw new NotImplementedException("The method or operation is not implemented.");
			}

			protected override IEnumerable<Item> GetAll(DateTime? since, Predicate<Item> filter)
			{
				Since = since;
				Filter = filter;

				return new Item[0];
			}

			public override void Add(Item item)
			{
				throw new NotImplementedException("The method or operation is not implemented.");
			}

			public override void Delete(string id)
			{
				throw new NotImplementedException("The method or operation is not implemented.");
			}

			public override void Update(Item item)
			{
				throw new NotImplementedException("The method or operation is not implemented.");
			}

			public override IEnumerable<Item> Merge(IEnumerable<Item> items)
			{
				throw new NotImplementedException("The method or operation is not implemented.");
			}

			public override string FriendlyName
			{
				get { throw new NotImplementedException("The method or operation is not implemented."); }
			}
		}
	}
}
