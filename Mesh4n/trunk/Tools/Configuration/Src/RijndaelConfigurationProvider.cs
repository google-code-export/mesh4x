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
using System.Xml;
using System.Security.Cryptography;
using System.IO;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		This class supports configuration sections that have been encrypted with another program using
	///		the Rijndael algorithm.
	/// </summary>
	public class RijndaelConfigurationProvider : ProtectedConfigurationProvider
	{
		private byte[] key;

		/// <summary>
		///		This class can be used by the <see cref="ConfigurationManager"/> class to decrypt encrypted sections
		///		of the XML file. You'll need to create an instance of this class and assign it to the
		///		<see cref="ConfigurationManager.ProtectedConfigurationProvider"/> property before loading any
		///		sections.
		/// </summary>
		/// <param name="key">The key to use when decrypting data.</param>
		/// <exception cref="ArgumentNullException">Thown if the <paramref name="key"/> parameter is null.</exception>
		public RijndaelConfigurationProvider(byte[] key)
		{
			Guard.ArgumentNotNull(key, "key");
			this.key = key;
		}

		/// <summary>
		///		This method decrypts section data, which is saved in the XML file as a base-64 encrypted string.
		/// </summary>
		/// <param name="encryptedSection">The encrypted data, base-64 encoded.</param>
		/// <returns></returns>
		/// <exception cref="CryptographicException">This exception will be thrown if there is a problem with the encrypted data.</exception>
		/// <exception cref="ArgumentOutOfRange">Thrown if the data supplied is shorter than the required header.</exception>
		public override string Decrypt(string encryptedSection)
		{
			if (encryptedSection == null)
				return null;

			byte[] data = Convert.FromBase64String(encryptedSection);

			Rijndael crypt = Rijndael.Create();
			CryptographyBlock block = new CryptographyBlock(crypt, key);
			return CryptographyUtility.GetString(block.Decrypt(data));
		}
	}
}
