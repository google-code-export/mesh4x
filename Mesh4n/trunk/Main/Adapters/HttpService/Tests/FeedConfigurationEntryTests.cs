using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Mesh4n.Adapters.HttpService.Configuration;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class FeedConfigurationEntryTests
	{
		public FeedConfigurationEntryTests()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullName()
		{
			new FeedConfigurationEntry(null, "title", "desc", new MockSyncAdapter());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyName()
		{
			new FeedConfigurationEntry("", "title", "desc", new MockSyncAdapter());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullTitle()
		{
			new FeedConfigurationEntry("name", null, "desc", new MockSyncAdapter());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyTitle()
		{
			new FeedConfigurationEntry("name", "", "desc", new MockSyncAdapter());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullDescription()
		{
			new FeedConfigurationEntry("name", "title", null, new MockSyncAdapter());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyDescription()
		{
			new FeedConfigurationEntry("name", "title", "", new MockSyncAdapter());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullRepository()
		{
			new FeedConfigurationEntry("name", "title", "desc", null);
		}
	}
}
