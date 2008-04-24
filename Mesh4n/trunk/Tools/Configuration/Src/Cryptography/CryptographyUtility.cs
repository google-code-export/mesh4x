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
using System.Security.Cryptography;
using System.Globalization;
using System.IO;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	/// <para>Common Cryptography methods.</para>
	/// </summary>
	internal static class CryptographyUtility
	{
		/// <summary>
		/// <para>Determine if two byte arrays are equal.</para>
		/// </summary>
		/// <param name="byte1">
		/// <para>The first byte array to compare.</para>
		/// </param>
		/// <param name="byte2">
		/// <para>The byte array to compare to the first.</para>
		/// </param>
		/// <returns>
		/// <para><see langword="true"/> if the two byte arrays are equal; otherwise <see langword="false"/>.</para>
		/// </returns>
		public static bool CompareBytes(byte[] byte1, byte[] byte2)
		{
			if (byte1 == null || byte2 == null)
			{
				return false;
			}
			if (byte1.Length != byte2.Length)
			{
				return false;
			}

			bool result = true;
			for (int i = 0; i < byte1.Length; i++)
			{
				if (byte1[i] != byte2[i])
				{
					result = false;
					break;
				}
			}

			return result;
		}

		/// <summary>
		/// <para>Returns a byte array from a string representing a hexidecimal number.</para>
		/// </summary>
		/// <param name="hexidecimalNumber">
		/// <para>The string containing a valid hexidecimal number.</para>
		/// </param>
		/// <returns><para>The byte array representing the hexidecimal.</para></returns>
		public static byte[] GetBytesFromHexString(string hexidecimalNumber)
		{
			Guard.ArgumentNotNullOrEmptyString(hexidecimalNumber, "hexidecimalNumber");

			StringBuilder sb = new StringBuilder(hexidecimalNumber.ToUpper(CultureInfo.CurrentCulture));

			if (sb[0].Equals('0') && sb[1].Equals('X'))
			{
				sb.Remove(0, 2);
			}

			if (sb.Length % 2 != 0)
			{
				throw new ArgumentException(Properties.Resources.InvalidHexString);
			}

			byte[] hexBytes = new byte[sb.Length / 2];
			try
			{
				for (int i = 0; i < hexBytes.Length; i++)
				{
					int stringIndex = i * 2;
					hexBytes[i] = Convert.ToByte(sb.ToString(stringIndex, 2), 16);
				}
			}
			catch (FormatException ex)
			{
				throw new ArgumentException(Properties.Resources.InvalidHexString, ex);
			}

			return hexBytes;
		}

		/// <summary>
		/// <para>Returns a string from a byte array represented as a hexidecimal number (eg: 0F351A).</para>
		/// </summary>
		/// <param name="bytes">
		/// <para>The byte array to convert to forat as a hexidecimal number.</para>
		/// </param>
		/// <returns>
		/// <para>The formatted representation of the bytes as a hexidcimal number.</para>
		/// </returns>
		public static string GetHexStringFromBytes(byte[] bytes)
		{
			Guard.ArgumentNotNull(bytes, "bytes");
			if (bytes.Length == 0) throw new ArgumentException(Properties.Resources.ExceptionByteArrayValueMustBeGreaterThanZeroBytes, "bytes");

			StringBuilder sb = new StringBuilder(bytes.Length * 2);
			for (int i = 0; i < bytes.Length; i++)
			{
				sb.Append(bytes[i].ToString("X2", CultureInfo.InvariantCulture));
			}
			return sb.ToString();
		}

		/// <summary>
		/// <para>Combines two byte arrays into one.</para>
		/// </summary>
		/// <param name="buffer1"><para>The prefixed bytes.</para></param>
		/// <param name="buffer2"><para>The suffixed bytes.</para></param>
		/// <returns><para>The combined byte arrays.</para></returns>
		public static byte[] CombineBytes(byte[] buffer1, byte[] buffer2)
		{
			byte[] combinedBytes = new byte[buffer1.Length + buffer2.Length];
			Buffer.BlockCopy(buffer1, 0, combinedBytes, 0, buffer1.Length);
			Buffer.BlockCopy(buffer2, 0, combinedBytes, buffer1.Length, buffer2.Length);

			return combinedBytes;
		}

		/// <summary>
		/// Creates a cryptographically strong random set of bytes.
		/// </summary>
		/// <param name="size">The size of the byte array to generate.</param>
		/// <returns>The computed bytes.</returns>
		public static byte[] GetRandomBytes(int size)
		{
			byte[] randomBytes = new byte[size];
			GetRandomBytes(randomBytes);
			return randomBytes;
		}

		/// <summary>
		/// <para>Fills a byte array with a cryptographically strong random set of bytes.</para>
		/// </summary>
		/// <param name="bytes"><para>The byte array to fill.</para></param>
		public static void GetRandomBytes(byte[] bytes)
		{
			RNGCryptoServiceProvider.Create().GetBytes(bytes);
		}

		/// <summary>
		/// <para>Fills <paramref name="bytes"/> zeros.</para>
		/// </summary>
		/// <param name="bytes">
		/// <para>The byte array to fill.</para>
		/// </param>
		public static void ZeroOutBytes(byte[] bytes)
		{
			if (bytes == null)
			{
				return;
			}
			Array.Clear(bytes, 0, bytes.Length);
		}

		/// <summary>
		/// Transforms an array of bytes according to the given cryptographic transform.
		/// </summary>
		/// <param name="transform"><see cref="ICryptoTransform" /> used to transform the given <paramref name="buffer" />.</param>
		/// <param name="buffer">Buffer to transform. It is the responsibility of the caller to clear this array when finished.</param>
		/// <returns>Transformed array of bytes. It is the responsibility of the caller to clear this byte array
		/// if necessary.</returns>
		public static byte[] Transform(ICryptoTransform transform, byte[] buffer)
		{
			byte[] transformBuffer = null;

			using (MemoryStream ms = new MemoryStream())
			{
				CryptoStream cs = null;
				try
				{
					cs = new CryptoStream(ms, transform, CryptoStreamMode.Write);
					cs.Write(buffer, 0, buffer.Length);
					cs.FlushFinalBlock();
					transformBuffer = ms.ToArray();
				}
				finally
				{
					if (cs != null)
					{
						cs.Close();
					} // Dispose is called by Close
				}
			}

			return transformBuffer;
		}

		/// <summary>
		///		Provides a standard way to get a string from a set of Unicode bytes.
		/// </summary>
		/// <param name="bytes">The raw Unicode bytes.</param>
		/// <returns>The string derived from the bytes.</returns>
		public static string GetString(byte[] bytes)
		{
			return UnicodeEncoding.Unicode.GetString(bytes, 0, bytes.Length);
		}

		/// <summary>
		///		Converts a string into a Unicode byte array.
		/// </summary>
		/// <param name="text">The string to convert to a byte array.</param>
		/// <returns>An array of Unicode bytes.</returns>
		public static byte[] GetBytes(string text)
		{
			return UnicodeEncoding.Unicode.GetBytes(text);
		}

		/// <summary>
		///		This is a helper method that converts an unsigned integer into an array of bytes.
		/// </summary>
		/// <param name="value"></param>
		/// <returns></returns>
		public static byte[] GetBytes(uint value)
		{
			byte[] results = new byte[4];
			results[0] = (byte) (value & 0xFF);
			results[1] = (byte)((value & 0xFF00) >> 8);
			results[2] = (byte)((value & 0xFF0000) >> 16);
			results[3] = (byte)((value & 0xFF000000) >> 24);

			return results;
		}
	}
}
