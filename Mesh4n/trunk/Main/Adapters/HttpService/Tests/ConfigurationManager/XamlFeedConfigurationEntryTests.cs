using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Mesh4n.Adapters.HttpService.Configuration;
using System.ComponentModel;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class XamlFeedConfigurationEntryTests
	{
		public XamlFeedConfigurationEntryTests()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullName()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Title = "title",
				Description = "description",
				SyncAdapter = new MockSyncAdapter()
			};

			((ISupportInitialize)entry).EndInit();
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyName()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Name= String.Empty,
				Title = "title",
				Description = "description",
				SyncAdapter = new MockSyncAdapter()
			};

			((ISupportInitialize)entry).EndInit();
		}

		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullTitle()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Name = "name",
				Description = "description",
				SyncAdapter = new MockSyncAdapter()
			};

			((ISupportInitialize)entry).EndInit();
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyTitle()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Name = "name",
				Title = String.Empty,
				Description = "description",
				SyncAdapter = new MockSyncAdapter()
			};

			((ISupportInitialize)entry).EndInit();
		}

		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullDescription()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Name = "name",
				Title = "title",
				SyncAdapter = new MockSyncAdapter()
			};

			((ISupportInitialize)entry).EndInit();
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyDescription()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Name = "name",
				Title = "title",
				Description = String.Empty,
				SyncAdapter = new MockSyncAdapter()
			};

			((ISupportInitialize)entry).EndInit();
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullAdapter()
		{
			XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry
			{
				Name = "name",
				Title = "title",
				Description = "description"
			};

			((ISupportInitialize)entry).EndInit();
		}
		
	}
}
