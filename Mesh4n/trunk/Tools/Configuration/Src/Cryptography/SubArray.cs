//===============================================================================
// Microsoft patterns & practices
// Mobile Client Software Factory - July 2006
//===============================================================================
// Copyright  Microsoft Corporation.  All rights reserved.
// THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY
// OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE.
//===============================================================================
// The example companies, organizations, products, domain names,
// e-mail addresses, logos, people, places, and events depicted
// herein are fictitious.  No association with any real company,
// organization, product, domain name, email address, logo, person,
// places, or events is intended or should be inferred.
//===============================================================================

using System;
using System.Collections.Generic;
using System.Text;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		Provides easier access to segments of a larger array.
	/// </summary>
	/// <typeparam name="T">The type of the array elements.</typeparam>
	internal struct SubArray<T>
	{
		private T[] array;
		private int offset;
		private int count;

		/// <summary>
		///		Creates a new instance that refers to part of a larger array.
		/// </summary>
		/// <param name="array">The full array.</param>
		/// <param name="offset">Offset into the full array for the first element of this subset.</param>
		/// <param name="count">Length of the subset.</param>
		public SubArray(T[] array, int offset, int count)
		{
			if (offset < 0 || offset + count > array.Length)
				throw new ArgumentOutOfRangeException("offset");

			this.array = array;
			this.offset = offset;
			this.count = count;
		}

		/// <summary>
		///		Creates a new subset that refers to the array starting at <paramref name="offset"/> and
		///		continuing to the end of the array.
		/// </summary>
		/// <param name="array"></param>
		/// <param name="offset"></param>
		public SubArray(T[] array, int offset)
			: this(array, offset, array.Length - offset)
		{
		}

		/// <summary>
		///		Number of elements in the sub array.
		/// </summary>
		public int Count
		{
			get { return count; }
		}

		/// <summary>
		///		Gets an element of the array.
		/// </summary>
		/// <param name="index">Index into the array. 0 refers to the first element in the subset.</param>
		/// <returns>One element from the array.</returns>
		/// <exception cref="ArgumentOutOfRangeException">If the <paramref name="index"/> is outside the sub-range.</exception>
		public T this[int index]
		{
			get
			{
				if (index < 0 || index >= count)
					throw new ArgumentOutOfRangeException("index");
				return array[offset + index];
			}
		}

		/// <summary>
		///		Returns a copy of the sub array.
		/// </summary>
		public T[] Array
		{
			get
			{
				T[] subArray = new T[count];
				System.Array.Copy(array, offset, subArray, 0, count);
				return subArray;
			}
		}

		/// <summary>
		///		Allows you to use the sub-array in places where an array of type <typeparamref name="T"/>.</summary>
		/// <param name="sa"></param>
		/// <returns>Sub array.</returns>
		public static implicit operator T[](SubArray<T> sa)
		{
			return sa.Array;
		}
	}
}
