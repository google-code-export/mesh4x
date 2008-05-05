using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Runtime.InteropServices;

namespace Mesh4n
{
	public class DeviceAuthor
	{
		public static string Current
		{
			get
			{
#if !PocketPC
				string current = Thread.CurrentPrincipal.Identity.Name;
				if (String.IsNullOrEmpty(current))
					current = Environment.MachineName;

				return current;
#else
				// TODO: create IAuditProvider to get/set who/when values.
				return GetDeviceID(AppDomain.CurrentDomain.FriendlyName).ToString();
#endif
			}
		}

#if PocketPC
		[DllImport("coredll.dll")]
		private extern static int GetDeviceUniqueID([In, Out] byte[] appdata,
																  int cbApplictionData,
																  int dwDeviceIDVersion,
																  [In, Out] byte[] deviceIDOuput,
																  out uint pcbDeviceIDOutput);

		private static Guid GetDeviceID(string AppString)
		{
			// Call the GetDeviceUniqueID
			byte[] AppData = new byte[AppString.Length];
			for (int count = 0; count < AppString.Length; count++)
				AppData[count] = (byte)AppString[count];

			int appDataSize = AppData.Length;
			byte[] DeviceOutput = new byte[20];
			uint SizeOut = 20;

			GetDeviceUniqueID(AppData, appDataSize, 1, DeviceOutput, out SizeOut);

			// Grab first 16bytes to make up a guid.
			byte[] guidBytes = new byte[16];
			Array.Copy(DeviceOutput, guidBytes, 16);

			return new Guid(guidBytes);
		}
#endif
	}
}
