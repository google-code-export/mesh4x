using System;
using System.Collections.Generic;
using System.Text;

namespace SimpleSharing
{
	/// <summary>
	/// Contains a callback function to filter items according the value of behaviors 
	/// after performing the merge.
	/// </summary>
	public class MergeFilter
	{
		MergeFilterHandler handler;
		MergeFilterBehaviors behaviors;
		
		/// <summary>
		/// Default constructor
		/// </summary>
		public MergeFilter()
		{
			this.handler = NullFilterHandler;
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="handler">Callback</param>
		public MergeFilter(MergeFilterHandler handler)
			: this(handler, MergeFilterBehaviors.None)
		{
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="handler">Callback</param>
		/// <param name="behaviors">Filter direction</param>
		public MergeFilter(MergeFilterHandler handler, MergeFilterBehaviors behaviors)
		{
			this.handler = handler;
			this.behaviors = behaviors;
		}

		/// <summary>
		/// Callback to a filter method
		/// </summary>
		public MergeFilterHandler Handler
		{
			get { return handler; }
			set { handler = value; }
		}

		/// <summary>
		/// Filter direction
		/// </summary>
		public MergeFilterBehaviors Behaviors
		{
			get { return behaviors; }
			set { behaviors = value; }
		}

		private IEnumerable<ItemMergeResult> NullFilterHandler(IRepository targetRepository,
			IEnumerable<ItemMergeResult> mergedItems)
		{
			return mergedItems;
		}

	}
}
