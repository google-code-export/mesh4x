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
	public class SyncEngineFixture : TestFixtureBase
	{
		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullLeftRepo()
		{
			new SyncEngine(null, new MockRepository());
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfNullRightRepo()
		{
			new SyncEngine(new MockRepository(), null);
		}

		[TestMethod]
		public void ShouldAddNewItems()
		{
			MockRepository left = new MockRepository(
				CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu")));
			MockRepository right = new MockRepository(
				CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga")));

			SyncEngine engine = new SyncEngine(left, right);

			IList<Item> conflicts = engine.Synchronize();

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(2, left.Items.Count);
			Assert.AreEqual(2, right.Items.Count);
		}

		[TestMethod]
		public void ShouldFilterItems()
		{
			MockRepository left = new MockRepository(
				CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu")));
			MockRepository right = new MockRepository(
				CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga")));

			SyncEngine engine = new SyncEngine(left, right);

			ItemFilter filter = new ItemFilter(delegate(Item item)
			{
				if (item.XmlItem.Title == "fizz" || item.XmlItem.Title == "buzz")
					return false;

				return true;
			});

			IList<Item> conflicts = engine.Synchronize(filter);

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(1, left.Items.Count);
			Assert.AreEqual(1, right.Items.Count);
		}

		[TestMethod]
		public void ShouldFilterItemsOnLeft()
		{
			MockRepository left = new MockRepository(
				CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu")));
			MockRepository right = new MockRepository(
				CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga")));

			SyncEngine engine = new SyncEngine(left, right);

			ItemFilter filter = new ItemFilter(delegate(Item item)
			{
				return false;
			}, delegate(Item item)
			{
				return true;
			});

			IList<Item> conflicts = engine.Synchronize(filter);

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(2, left.Items.Count);
			Assert.AreEqual(1, right.Items.Count); //Left does not return any item
		}

		[TestMethod]
		public void ShouldFilterItemsOnRight()
		{
			MockRepository left = new MockRepository(
				CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu")));
			MockRepository right = new MockRepository(
				CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga")));

			SyncEngine engine = new SyncEngine(left, right);

			ItemFilter filter = new ItemFilter(delegate(Item item)
			{
				return true;
			}, delegate(Item item)
			{
				return false;
			});

			IList<Item> conflicts = engine.Synchronize(filter);

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(1, left.Items.Count);
			Assert.AreEqual(2, right.Items.Count); 
		}


		[TestMethod]
		public void ShouldMergeChangesBothWays()
		{
			Item a = CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu"));
			Item b = CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga"));

			MockRepository left = new MockRepository(
				new Item(a.XmlItem, a.Sync.Update("kzu", DateTime.Now)), 
				b);

			MockRepository right = new MockRepository(
				a,
				new Item(b.XmlItem, b.Sync.Update("vga", DateTime.Now)));

			SyncEngine engine = new SyncEngine(left, right);

			IList<Item> conflicts = engine.Synchronize();

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(2, right.Items[a.Sync.Id].Sync.Updates);
			Assert.AreEqual(2, left.Items[b.Sync.Id].Sync.Updates);
		}

		[TestMethod]
		public void ShouldMarkItemDeleted()
		{
			Item a = CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu"));
			Item b = CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga"));

			MockRepository left = new MockRepository(a, b);
			MockRepository right = new MockRepository(
				a, 
				new Item(b.XmlItem, b.Sync.Update("vga", DateTime.Now, true)));

			SyncEngine engine = new SyncEngine(left, right);

			IList<Item> conflicts = engine.Synchronize();

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(1, Count(left.GetAll(delegate(Item i) { return !i.Sync.Deleted; })));
		}

		[TestMethod]
		public void ShouldSynchronizeSince()
		{
			Item a = CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1))));
			Item b = CreateItem("buzz", Guid.NewGuid().ToString(), new History("vga", DateTime.Now.Subtract(TimeSpan.FromDays(1))));

			MockRepository left = new MockRepository(a);
			MockRepository right = new MockRepository(b);

			SyncEngine engine = new SyncEngine(left, right);

			IList<Item> conflicts = engine.Synchronize(DateTime.Now);

			Assert.AreEqual(0, conflicts.Count);
			Assert.AreEqual(1, left.Items.Count);
			Assert.AreEqual(1, right.Items.Count);
		}

		[TestMethod]
		public void ShouldGenerateConflict()
		{
			Item a = CreateItem("fizz", Guid.NewGuid().ToString(), new History("kzu"));
			Thread.Sleep(1000);

			MockRepository left = new MockRepository(
				new Item(a.XmlItem, a.Sync.Update("kzu", DateTime.Now)));
			Thread.Sleep(1000);

			MockRepository right = new MockRepository(
				new Item(a.XmlItem, a.Sync.Update("vga", DateTime.Now)));

			SyncEngine engine = new SyncEngine(left, right);

			IList<Item> conflicts = engine.Synchronize();

			Assert.AreEqual(1, conflicts.Count);
			Assert.AreEqual(1, left.Items[a.Sync.Id].Sync.Conflicts.Count);
			Assert.AreEqual(1, right.Items[a.Sync.Id].Sync.Conflicts.Count);
		}

		[TestMethod]
		public void ShouldImportUpdateWithConflictLeft()
		{
			MockRepository left = new MockRepository();
			MockRepository right = new MockRepository();
			SyncEngine engine = new SyncEngine(left, right);

			string id = Guid.NewGuid().ToString();
			Sync sync = Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			Item item = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			left.Add(item);
			right.Add(item);

			Item incomingItem = item.Clone();

			// Local editing.
			item = new Item(new XmlItem(id, "changed", item.XmlItem.Description,
				item.XmlItem.Payload),
				Behaviors.Update(item.Sync, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(1)), false));

			left.Update(item);

			// Conflicting remote editing.
			incomingItem = new Item(new XmlItem(id, "remote", item.XmlItem.Description,
				item.XmlItem.Payload),
				Behaviors.Update(incomingItem.Sync, "REMOTE\\kzu", DateTime.Now, false));

			right.Update(incomingItem);
			
			IList<Item> conflicts = engine.Synchronize();

			Assert.AreEqual(1, conflicts.Count);
			Assert.AreEqual(1, Count(left.GetAll()));
			Assert.AreEqual("remote", left.Get(id).XmlItem.Title);
			Assert.AreEqual("REMOTE\\kzu", left.Get(id).Sync.LastUpdate.By);

			Assert.AreEqual(1, Count(left.GetConflicts()));
			Assert.AreEqual(1, Count(right.GetConflicts()));
		}

		[TestMethod]
		public void ShouldCallMergeIfRepositorySupportsIt()
		{
			MockMergeRepository left = new MockMergeRepository();
			MockMergeRepository right = new MockMergeRepository();
			SyncEngine engine = new SyncEngine(left, right);

			engine.Synchronize();

			Assert.IsTrue(left.MergeCalled);
			Assert.IsTrue(right.MergeCalled);
		}

		[TestMethod]
		public void ShouldCallImportPreviewHandler()
		{
			bool left = false;
			bool right = false;
			bool none = false;
			int both = 0;
			MergeFilterHandler leftHandler = delegate(IRepository targetRepository, IEnumerable<ItemMergeResult> mergedItems)
			{
				Assert.AreEqual("left", targetRepository.FriendlyName);
				left = true;
				return mergedItems;
			};
			MergeFilterHandler rightHandler = delegate(IRepository targetRepository, IEnumerable<ItemMergeResult> mergedItems)
			{
				Assert.AreEqual("right", targetRepository.FriendlyName);
				right = true;
				return mergedItems;
			};
			MergeFilterHandler bothHandler = delegate(IRepository targetRepository, IEnumerable<ItemMergeResult> mergedItems)
			{
				both++;
				return mergedItems;
			};
			MergeFilterHandler noneHandler = delegate(IRepository targetRepository, IEnumerable<ItemMergeResult> mergedItems)
			{
				none = true;
				return mergedItems;
			};

			SyncEngine engine = new SyncEngine(new MockRepository("left"), new MockRepository("right"));

			engine.Synchronize(new MergeFilter(leftHandler, MergeFilterBehaviors.Left));
			Assert.IsTrue(left);

			engine.Synchronize(new MergeFilter(rightHandler, MergeFilterBehaviors.Right));
			Assert.IsTrue(right);

			engine.Synchronize(new MergeFilter(bothHandler, MergeFilterBehaviors.Both));
			Assert.AreEqual(2, both);

			engine.Synchronize(new MergeFilter(noneHandler, MergeFilterBehaviors.None));
			Assert.IsFalse(none);
		}

		[TestMethod]
		public void ShouldReportImportProgress()
		{
			MockRepository left = new MockRepository();
			MockRepository right = new MockRepository();
			SyncEngine engine = new SyncEngine(left, right);

			string id = Guid.NewGuid().ToString();
			Sync sync = Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			Item item = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			left.Add(item);

			id = Guid.NewGuid().ToString();
			sync = Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			item = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			right.Add(item);

			int received = 0, sent = 0;

			engine.ItemReceived += delegate { received++; };
			engine.ItemSent += delegate { sent++; };

			engine.Synchronize();

			Assert.AreEqual(2, left.Items.Count);
			Assert.AreEqual(2, right.Items.Count);

			// Receives the item that was sent first plus the existing remote one.
			Assert.AreEqual(2, received);
			Assert.AreEqual(1, sent);
		}

		[TestMethod]
		public void ShouldNotSendReceivedItemIfModifiedBeforeSince()
		{
			MockRepository left = new MockRepository();
			MockRepository right = new MockRepository();
			SyncEngine engine = new SyncEngine(left, right);

			string id = Guid.NewGuid().ToString();
			Sync sync = Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromMinutes(2)), false);
			Item item = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			left.Add(item);

			id = Guid.NewGuid().ToString();
			sync = Behaviors.Create(id, DeviceAuthor.Current, DateTime.Now.Subtract(TimeSpan.FromDays(2)), false);
			item = new Item(
				new XmlItem(id, "foo", "bar",
					GetElement("<foo id='bar'/>")),
				sync);

			right.Add(item);

			int received = 0, sent = 0;

			engine.ItemReceived += delegate { received++; };
			engine.ItemSent += delegate { sent++; };

			engine.Synchronize(DateTime.Now.Subtract(TimeSpan.FromMinutes(5)));

			// No new item would have been received from target as it was modified in the past.
			Assert.AreEqual(1, left.Items.Count);
			// Local item was sent.
			Assert.AreEqual(2, right.Items.Count);
			// We would have received the same item we sent, as we're first 
			// sending and then receiving.
			Assert.AreEqual(1, received);
			Assert.AreEqual(1, sent);
		}

		//[TestMethod]
		//public void ShouldExportByDaysWithItemTimestampIfNoSyncLastUpdateWhen()
		//{
		//   MockXmlRepository xmlrepo = new MockXmlRepository();
		//   MockSyncRepository syncrepo = new MockSyncRepository();

		//   IXmlItem xi = new XmlItem("title", "description", new XmlDocument().CreateElement("payload"));
		//   xi.Id = Guid.NewGuid().ToString();
		//   Sync sync = Behaviors.Create(xi.Id, "kzu", DateTime.Now, false);
		//   Item item = new Item(xi, sync);

		//   xmlrepo.Add(xi);
		//   syncrepo.Save(sync);

		//   SyncEngine engine = new SyncEngine(xmlrepo, syncrepo);

		//   IEnumerable<Item> items = engine.Export(1);

		//   Assert.AreEqual(1, Count(items));
		//}

		//[ExpectedException(typeof(InvalidOperationException))]
		//[TestMethod]
		//public void ShouldThrowIfRepositoryDoesntUpdateTimestamp()
		//{
		//    ISyncRepository syncRepo = new MockSyncRepository();
		//    IXmlRepository xmlRepo = new MockXmlRepository().AddOneItem();
		//    SyncEngine engine = new SyncEngine(xmlRepo, syncRepo);

		//    IEnumerable<Item> items = engine.Export();

		//    ISyncRepository syncRepo2 = new MockSyncRepository();
		//    IXmlRepository xmlRepo2 = new NotUpdatingRepository();
		//    SyncEngine engine2 = new SyncEngine(xmlRepo2, syncRepo2);

		//    engine2.Import("mock", items);
		//}

		private Item CreateItem(string title, string id, History history, params History[] otherHistory)
		{
			XmlItem xml = new XmlItem(title, null, GetElement("<payload/>"), DateTime.Now);
			Sync sync = Behaviors.Create(id, history.By, history.When, false);
			foreach (History h in otherHistory)
			{
				sync = sync.Update(h.By, h.When);
			}

			return new Item(xml, sync);
		}

		class MockMergeRepository : IRepository
		{
			public bool MergeCalled;

			#region IRepository Members

			public string FriendlyName
			{
				get { return "MockMerge"; }
			}

			public bool SupportsMerge
			{
				get { return true; }
			}

			public Item Get(string id)
			{
				return null;
			}

			public IEnumerable<Item> GetAll()
			{
				return new Item[0];
			}

			public IEnumerable<Item> GetAllSince(DateTime? since)
			{
				return new Item[0];
			}

			public IEnumerable<Item> GetConflicts()
			{
				return new Item[0];
			}

			public void Delete(string id)
			{
			}

			public void Update(Item item)
			{
			}

			public Item Update(Item item, bool resolveConflicts)
			{
                return item;
			}

			public IEnumerable<Item> Merge(IEnumerable<Item> items)
			{
				MergeCalled = true;
				return new List<Item>();
			}

			public void Add(Item item)
			{
			}

			#endregion

			#region IRepository Members

			public IEnumerable<Item> GetAll(Predicate<Item> filter)
			{
				return new Item[0];
			}

			public IEnumerable<Item> GetAllSince(DateTime? since, Predicate<Item> filter)
			{
				return new Item[0];
			}

			#endregion
        }
	}
}
