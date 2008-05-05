using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;

namespace Mesh4n
{
	public static class Tracer
	{
#if !PocketPC
		static Dictionary<string, TraceSource> sources = new Dictionary<string, TraceSource>();
#endif

		[Conditional("Desktop")]
		public static void TraceData(object source, TraceEventType eventType, string message)
		{
			Guard.ArgumentNotNull(source, "source");

#if !PocketPC
			// TODO: implement logging for CF
			string sourceName = source.GetType().Namespace;

			TraceSource ts;
			if (!sources.TryGetValue(sourceName, out ts))
			{
				ts = new TraceSource(sourceName);
				sources.Add(sourceName, ts);
			}

			ts.TraceData(eventType, 0, message);
#endif
		}

		[Conditional("Desktop")]
		public static void TraceData(object source, TraceEventType eventType, string format, params object[] args)
		{
			Guard.ArgumentNotNull(source, "source");

			TraceData(source, eventType, String.Format(source.GetType().Name + ": " + format, args));
		}
	}
}
