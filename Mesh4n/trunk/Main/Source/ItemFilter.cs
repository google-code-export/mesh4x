using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	/// <summary>
	/// Items filter
	/// </summary>
	public class ItemFilter
	{
		private Predicate<Item> leftFilter;
		private Predicate<Item> rightFilter;

		public ItemFilter()
		{
			this.leftFilter = NullItemFilter;
			this.rightFilter = NullItemFilter;
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="commonFilter">Filter for adapters on the left and right</param>
		public ItemFilter(Predicate<Item> commonFilter)
		{
			this.leftFilter = commonFilter;
			this.rightFilter = commonFilter;
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="left">Filter for adapter on the left</param>
		/// <param name="right">Filter for adapter on the right</param>
		public ItemFilter(Predicate<Item> left, Predicate<Item> right)
		{
			this.leftFilter = left;
			this.rightFilter = right;
		}

		/// <summary>
		/// Filter for adapter on the left
		/// </summary>
		public Predicate<Item> Left
		{
			get { return leftFilter; }
			set { leftFilter = value; }
		}

		/// <summary>
		/// Filter for adapter on the right
		/// </summary>
		public Predicate<Item> Right
		{
			get { return rightFilter; }
			set { rightFilter = value; }
		}

		private bool NullItemFilter(Item item)
		{
			return true;
		}
	}
}
