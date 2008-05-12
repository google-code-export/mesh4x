using System;
using System.Collections.Generic;
using System.Text;
using Mesh4n.Adapters.HttpService.Configuration;
using NUnit.Framework;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestFixture]
	public class FeedConfigurationEntryTests
	{
		public FeedConfigurationEntryTests()
		{
		}

		[Test]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullName()
		{
			new FeedConfigurationEntry(null, "title", "desc", new MockSyncAdapter());
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyName()
		{
			new FeedConfigurationEntry("", "title", "desc", new MockSyncAdapter());
		}

		[Test]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullTitle()
		{
			new FeedConfigurationEntry("name", null, "desc", new MockSyncAdapter());
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyTitle()
		{
			new FeedConfigurationEntry("name", "", "desc", new MockSyncAdapter());
		}

		[Test]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullDescription()
		{
			new FeedConfigurationEntry("name", "title", null, new MockSyncAdapter());
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyDescription()
		{
			new FeedConfigurationEntry("name", "title", "", new MockSyncAdapter());
		}

		[Test]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullAdapter()
		{
			new FeedConfigurationEntry("name", "title", "desc", null);
		}
	}
}
