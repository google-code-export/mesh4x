using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SyndicationModel
{
	/// <summary>
	/// Supports cloning, which creates a new instance of a class with 
	/// the same value as an existing instance.
	/// </summary>
	public interface ICloneable<T>
	{
		/// <summary>
		/// Creates a new object that is a copy of the current instance.
		/// </summary>
		/// <returns>A new object that is a copy of this instance.</returns>
		T Clone();
	}
}
