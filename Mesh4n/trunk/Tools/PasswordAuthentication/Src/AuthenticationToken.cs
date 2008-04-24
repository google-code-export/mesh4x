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

namespace Microsoft.Practices.Mobile.PasswordAuthentication
{
	/// <summary>
	///		This class allows you to check a username/password using a saved hash value instead of
	///		saving the username/password.
	/// </summary>
	public class AuthenticationToken
	{
		private const int saltLength = 8;
		private readonly byte[] version = new byte[] { 1, 0 };
		private byte[] tokenData;

		/// <summary>
		///		Creates a new token from an existing identity. Use this constructor to obtain a token that you
		///		can save for later authentication.
		/// </summary>
		/// <param name="identity"></param>
		public AuthenticationToken(PasswordIdentity identity)
		{
			Guard.ArgumentNotNull(identity, "identity");

			//S = create a random salt 
			//U = get user from identitty
			//K = get key from id
			//H = HMACSHA(U, K, S)
			//token data = S + H

			byte[] salt = CryptographyUtility.GetRandomBytes(saltLength);
			tokenData = CalculateToken(identity, salt, identity.Provider);
		}

		/// <summary>
		///		Creates a new instance from a saved token. Use this constructor when you need to authenticate
		///		an existing user against a saved token.
		/// </summary>
		/// <param name="token"></param>
		public AuthenticationToken(string token)
		{
			Guard.ArgumentNotNullOrEmptyString(token, "token");

			this.tokenData = Convert.FromBase64String(token);

			for (int i = 0; i < version.Length; i++)
			{
				if (tokenData[i] != version[i])
					throw new ArgumentException(Properties.Resources.InvalidVersion);
			}
		}

		/// <summary>
		///		Checks to see if the user name and password provided result in the same hash as this
		///		token.
		/// </summary>
		/// <param name="user">Name used to authenticate.</param>
		/// <param name="password">The password used to authenticate.</param>
		/// <param name="provider"></param>
		/// <returns></returns>
		public PasswordIdentity Authenticate(string user, string password, RsaAesCryptographyProvider provider)
		{
			Guard.ArgumentNotNullOrEmptyString(user, "user");
			Guard.ArgumentNotNullOrEmptyString(password, "password");
			Guard.ArgumentNotNull(provider, "provider");

			//
			// Breaks apart the token data so we can use the salt again.
			//
			SubArray<byte> version = new SubArray<byte>(tokenData, 0, 2);
			SubArray<byte> salt = new SubArray<byte>(tokenData, version.Count, saltLength);

			PasswordIdentity identity = new PasswordIdentity(user, password, true, provider);
			byte[] newToken = CalculateToken(identity, salt.Array, provider);

			if (CryptographyUtility.CompareBytes(newToken, tokenData))
				return identity;
			else
				return null;
		}

		/// <summary>
		///		The "token" created from the identity. You can save this value for later use in
		///		authenticating a user, in conjunction with decrypting data.
		/// </summary>
		public string TokenData
		{
			get { return Convert.ToBase64String(tokenData); }
		}

		/// <summary>
		///		Creates a token from a user name, password-derived key, and salt.
		/// </summary>
		/// <param name="identity">The identity that provides the password-derived key and user name.</param>
		/// <param name="salt">Some random data.</param>
		/// <param name="provider">The cryptographic provider used for cyptographic functions.</param>
		/// <returns>Returns a byte array that is a concatination of the version (2 bytes), the salt, and the hash.</returns>
		private byte[] CalculateToken(PasswordIdentity identity, byte[] salt, RsaAesCryptographyProvider provider)
		{
			Guard.ArgumentNotNull(identity, "identity");
			Guard.ArgumentNotNull(salt, "salt");
			Guard.ArgumentNotNull(provider, "provider");

			CryptNativeHelper crypto = new CryptNativeHelper(provider);

			byte[] user = CryptographyUtility.GetBytes(identity.Name);
			byte[] hash = crypto.GetHmacHash(user, salt, identity.CryptoKey);
			byte[] header = CryptographyUtility.CombineBytes(version, salt);
			byte[] token = CryptographyUtility.CombineBytes(header, hash);
			return token;
		}
	}
}
