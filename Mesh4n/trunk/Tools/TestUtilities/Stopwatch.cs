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
using System.Runtime.InteropServices;

namespace Microsoft.Practices.TestUtilities
{
	/// <summary>
	/// Provides a set of methods and properties that you can use to accurately measure elapsed time.
	/// </summary>
	/// <remarks>
	/// Replicates the .NET equivalent as much as possible.
	/// See also: http://msdn.microsoft.com/library/default.asp?url=/library/en-us/dnnetcomp/html/netcfperf.asp.
	/// </remarks>
	public class Stopwatch
	{
		[DllImport("coredll.dll")]
		extern static int QueryPerformanceCounter(ref long perfCounter);

		[DllImport("coredll.dll")]
		extern static int QueryPerformanceFrequency(ref long frequency);

		static long frequency;
		long start;
		long elapsed;
		bool isRunning;

		static Stopwatch()
		{
		  if (QueryPerformanceFrequency(ref frequency) == 0) 
		  {
			throw new ApplicationException("Can't query frequency");
		  }
			// Convert to ms.
			frequency /= 1000;
		}

		/// <summary>
		/// Gets the current number of ticks in the timer mechanism.
		/// </summary>
		/// <returns>
		/// A long integer representing the tick counter value of the underlying timer mechanism.
		/// </returns>
		public static long GetTimestamp()
		{
			long value = 0;
			if (QueryPerformanceCounter(ref value) == 0)
			{
				throw new ApplicationException("Can't query performance counter");
			}

			return value;
		}

		/// <summary>
		/// Stops time interval measurement and resets the elapsed time to zero.
		/// </summary>
		public void Reset()
		{
			isRunning = false;
			start = 0;
			elapsed = 0;
		}

		/// <summary>
		/// Starts, or resumes, measuring elapsed time for an interval.
		/// </summary>
		public void Start()
		{
			if (!isRunning)
			{
				start = GetTimestamp();
			}

			isRunning = true;
		}

		/// <summary>
		/// Initializes a new <see cref="Stopwatch"></see> instance, sets the elapsed time property to zero, 
		/// and starts measuring elapsed time.
		/// </summary>
		/// <returns>
		/// A <see cref="Stopwatch"></see> that has just begun measuring elapsed time.
		/// </returns>
		public static Stopwatch StartNew()
		{
			Stopwatch watch = new Stopwatch();
			watch.Start();
			return watch;
		}

		/// <summary>
		/// Stops measuring elapsed time for an interval.
		/// </summary>
		public void Stop()
		{
			if (isRunning)
			{
				elapsed += GetTimestamp() - start;
				isRunning = false;
			}
		}

		/// <summary>
		/// Gets the total elapsed time measured by the current instance.
		/// </summary>
		/// <returns>
		/// A read-only <see cref="System.TimeSpan"></see> representing the total elapsed time 
		/// measured by the current instance.
		/// </returns>
		public TimeSpan Elapsed 
		{
			get { return TimeSpan.FromMilliseconds(GetElapsed() / frequency); }
		}

		/// <summary>
		/// Gets the total elapsed time measured by the current instance, in milliseconds.
		/// </summary>
		/// <returns>
		/// A read-only long integer representing the total number of milliseconds measured by the current instance.
		/// </returns>
		public long ElapsedMilliseconds
		{ 
			get { return GetElapsed() / frequency; }
		}

		/// <summary>
		/// Gets the total elapsed time measured by the current instance, in timer ticks.
		/// </summary>
		/// <returns>
		/// A read-only long integer representing the total number of timer ticks 
		/// measured by the current instance.
		/// </returns>
		public long ElapsedTicks 
		{
			get { return GetElapsed(); }
		}
		
		/// <summary>
		/// Gets a value indicating whether the <see cref="Stopwatch"></see> timer is running.
		/// </summary>
		/// <returns>
		/// <returns><see langword="true"/> if the <see cref="Stopwatch"></see> instance is currently running and 
		/// measuring elapsed time for an interval; <see langword="false"/> otherwise.</returns>
		/// </returns>
		public bool IsRunning 
		{
			get { return isRunning; }
		}

		private long GetElapsed()
		{
			if (isRunning)
			{
				return elapsed + GetTimestamp() - start; 
			}
			else
			{
				return elapsed;
			}
		}
	}
}
