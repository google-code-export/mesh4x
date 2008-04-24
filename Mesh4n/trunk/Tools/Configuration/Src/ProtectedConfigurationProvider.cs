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
	///		This is the base class for providers that can decrypt sections of the configuration XML.
	///		Right now the only subclass is <see cref="RijndaelConfigurationProvider"/>.
	/// </summary>
	public abstract class ProtectedConfigurationProvider
	{
		/// <summary>
		///		This method is called with the encrypted string and must return the decrypted string.
		/// </summary>
		/// <param name="encryptedSection">The encrypted string.</param>
		/// <returns>The decrypted string.</returns>
		public abstract string Decrypt(string encryptedSection);
	}
}
