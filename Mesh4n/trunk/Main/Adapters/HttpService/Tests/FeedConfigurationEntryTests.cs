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
			new FeedConfigurationEntry(null, "title", "desc", typeof(MockSyncAdapter));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyName()
		{
			new FeedConfigurationEntry("", "title", "desc", typeof(MockSyncAdapter));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullTitle()
		{
			new FeedConfigurationEntry("name", null, "desc", typeof(MockSyncAdapter));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyTitle()
		{
			new FeedConfigurationEntry("name", "", "desc", typeof(MockSyncAdapter));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullDescription()
		{
			new FeedConfigurationEntry("name", "title", null, typeof(MockSyncAdapter));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyDescription()
		{
			new FeedConfigurationEntry("name", "title", "", typeof(MockSyncAdapter));
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfNotISyncAdapterType()
		{
			new FeedConfigurationEntry("name", "title", "description", typeof(object));
		}
	}
}
