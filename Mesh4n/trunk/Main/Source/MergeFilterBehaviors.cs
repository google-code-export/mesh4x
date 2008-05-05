using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	/// <summary>
	/// Flags that specify when the <see cref="FilterHandler"/> delegate should 
	/// be called while synchronizing.
	/// </summary>
	[Flags]
	public enum MergeFilterBehaviors
	{
		/// <summary>
		/// Do not apply filter behavior to any repository.
		/// </summary>
		None = 0,
		/// <summary>
		/// Call filter when merging items into the left repository.
		/// </summary>
		Left = 1,
		/// <summary>
		/// Call filter when merging items into the right repository.
		/// </summary>
		Right = 2,
		/// <summary>
		/// Call filter when merging items into both repositories.
		/// </summary>
		Both = Right | Left,
	}
}
