using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;

namespace Mesh4n
{
	/// <summary>
	/// Copied from full .NET as it doesn't exist on CF.
	/// </summary>
	public enum TraceEventType
	{
		Critical = 1,
		Error = 2,
		Information = 8,
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		Resume = 2048,
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		Start = 256,
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		Stop = 512,
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		Suspend = 1024,
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		Transfer = 4096,
		Verbose = 16,
		Warning = 4
	}
}
