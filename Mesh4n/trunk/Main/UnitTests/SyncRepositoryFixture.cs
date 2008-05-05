#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif
using System;
using System.Collections.Generic;
using System.Text;
using Mesh4n;
using System.Xml;
using System.Threading;

namespace Mesh4n.Tests
{
	/// <summary>
	/// Base class for fixtures of implementations of <see cref="ISyncRepository"/>.
	/// </summary>
	[TestClass]
	public class SyncRepositoryFixture : TestFixtureBase
	{
		protected virtual ISyncRepository CreateRepository()
		{
			return new MockSyncRepository();
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfSaveNullSync()
		{
			ISyncRepository repo = CreateRepository();

			repo.Save(null);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfGetNullId()
		{
			ISyncRepository repo = CreateRepository();

			repo.Get(null);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfGetEmptyId()
		{
			ISyncRepository repo = CreateRepository();

			repo.Get("");
		}

		[TestMethod]
		public void ShouldSaveSync()
		{
			ISyncRepository repo = CreateRepository();
			string id = Guid.NewGuid().ToString();

			repo.Save(Behaviors.Create(id, "kzu", DateTime.Now, false));

			Sync sync = repo.Get(id);

			Assert.IsNotNull(sync);
			Assert.AreEqual(id, sync.Id);
		}

		[TestMethod]
		public void ShouldGetAll()
		{
			ISyncRepository repo = CreateRepository();
			repo.Save(Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false));
			repo.Save(Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false));
			repo.Save(Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false));

			IEnumerable<Sync> syncs = repo.GetAll();

			Assert.AreEqual(3, Count(syncs));
		}

		[TestMethod]
		public void ShouldGetAllConflicts()
		{
			ISyncRepository repo = CreateRepository();
			repo.Save(Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false));
			repo.Save(Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false));
			repo.Save(Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false));

			Sync s = Behaviors.Create(Guid.NewGuid().ToString(), "kzu", DateTime.Now, false);
			Sync conflict = s.Clone();
			s = Behaviors.Update(s, "vcc", null, false);
			conflict = Behaviors.Update(conflict, "ary", null, false);

			s.Conflicts.Add(new Item(new MockXmlItem(s.Id), conflict));

			repo.Save(s);

			IEnumerable<Sync> conflicts = repo.GetConflicts();

			Assert.AreEqual(1, Count(conflicts));
		}

		[TestMethod]
		public void ShouldGetNullItemIfMissing()
		{
			ISyncRepository repo = CreateRepository();
			Sync s = repo.Get(Guid.NewGuid().ToString());

			Assert.IsNull(s);
		}		
	}
}