using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	public static class Schema
	{
		/// <summary>
		/// Namespace of the feedsync elements.
		/// </summary>
		public const string Namespace = "http://feedsync.org/2007/feedsync";

		/// <summary>
		/// Default prefix used for feedsync elements.
		/// </summary>
		public const string DefaultPrefix = "sx";

		public static class ElementNames
		{
			public const string Sharing = "sharing";
			public const string Related = "related";
			public const string Sync = "sync";
			public const string History = "history";
			public const string Conflicts = "conflicts";
		}

		public static class AttributeNames
		{
			// sx:sharing
			public const string Since = "since";
			public const string Until = "until";
			public const string Version = "version";
			public const string Expires = "expires";
			// sx:related
			public const string Link = "link";
			public const string Title = "title";
			public const string Type = "type";
			// sx:sync
			public const string Id = "id";
			public const string Updates = "updates";
			public const string Deleted = "deleted";
			public const string NoConflicts = "noconflicts";
			// sx:history
			public const string Sequence = "sequence";
			public const string When = "when";
			public const string By = "by";
		}
	}
}
