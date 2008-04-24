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
using Microsoft.Practices.Mobile.PasswordAuthentication;

namespace Microsoft.Practices.Mobile.PasswordAuthentication
{
	/// <summary>
	///		Provides an "identity" object that can derive a key from a username and password. This key is a
	///		one-way key. In other words, given the key, you can't reconstruct the username or password.
	/// </summary>
	public class PasswordIdentity : IIdentity, IKeyProvider
	{
		private RsaAesCryptographyProvider provider;
		private string userName = "";
		private bool isAuthenticated = false;

		private byte[] cryptoKey;

		internal PasswordIdentity(string userName, string password, bool isAuthenticated, RsaAesCryptographyProvider provider)
		{
			Guard.ArgumentNotNullOrEmptyString(userName, "userName");
			Guard.ArgumentNotNullOrEmptyString(password, "password");
			Guard.ArgumentNotNull(provider, "provider");

			this.provider = provider;
			this.isAuthenticated = isAuthenticated;
			this.userName = userName;
			CryptNativeHelper crypto = new CryptNativeHelper(provider);
			this.cryptoKey = crypto.GetPasswordDerivedKey(password);
		}

		/// <summary>
		///		Constructor, which creates a new key using the information you provide.
		/// </summary>
		/// <param name="userName">The string that represents the user.</param>
		/// <param name="password">The password to use in creating the key.</param>
		/// <param name="provider">The provider that will be used when deriving the key.</param>
		public PasswordIdentity(string userName, string password, RsaAesCryptographyProvider provider) : this(userName, password, false, provider)
		{
		}

		/// <summary>
		///		Provides a cryptographic key with which the application can encrypt 
		///		or decrypt sensitive data. Since this key is based on user/pin combination, 
		///		it is not recommended to protect business data directly with this key; rather,
		///		the application should use this key to protect the actual keys used for business
		///		information, configuration, etc. In that way the user could change PIN with less
		///		risk of losing the business data.
		/// </summary>
		public byte[] CryptoKey
		{
			get { return cryptoKey; }
		}

		/// <summary>
		/// Member of IIdentity returns the user name provided when the identity was constructed
		/// </summary>
		public string Name
		{
			get { return userName; }
		}

		/// <summary>
		///		Gets the provider that was passed in the constructor.
		/// </summary>
		public RsaAesCryptographyProvider Provider
		{
			get { return provider; }
		}

		#region IIdentity Members

		public string AuthenticationType
		{
			get { return "PIN"; }
		}

		public bool IsAuthenticated
		{
			get { return this.isAuthenticated ; }
		}

		#endregion
	}
}
