using System;
using System.Collections.Generic;
using System.Text;

namespace SimpleSharing
{
	public enum MergeOperation
	{
		Added,
		Updated, 
		Conflict,
		Removed,
		None
	}
}
