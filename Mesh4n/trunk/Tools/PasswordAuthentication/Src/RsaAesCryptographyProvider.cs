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
	///		This abstract class handles much of the work around acquiring and releasong of cryptographic
	///		contexts. Currently the only subclass is the <see cref="AesRsaCryptographyProvider"/> class.
	/// </summary>
	public class RsaAesCryptographyProvider : IDisposable
	{
		private CryptNativeHelper.ProviderType providerType;
		private string containerName;
		private IntPtr hProvider;
		private bool deleteKeysetOnDispose = true;

		/// <summary>
		///		Constructs this instance, then calls the <see cref="AcquireContext"/> method.
		/// </summary>
		/// <param name="providerType">Type of the provider you want to create.</param>
		/// <param name="containerName">Name of the container you want to acquire.</param>
		public RsaAesCryptographyProvider(string containerName)
		{
			this.containerName = containerName;
			this.providerType = CryptNativeHelper.ProviderType.PROV_RSA_AES;
			this.hProvider = AcquireContext();
		}

		~RsaAesCryptographyProvider()
		{
			Dispose();
		}

		/// <summary>
		///		Gets or sets whether to delete the keystore when this class is disposed.
		/// </summary>
		public bool DeleteKeysetOnDispose
		{
			get { return deleteKeysetOnDispose; }
			set { deleteKeysetOnDispose = value; }
		}

		/// <summary>
		///		This method is called by the constructor to acquire the context. By default, it
		///		will attempt to open the keystore with the name you provided in the constructor.
		///		If a keystore with that name doesn't exist, one will be created.
		/// </summary>
		/// <returns>Handle to the provider acquired with a call to CryptAcquireContext.</returns>
		protected IntPtr AcquireContext()
		{
			IntPtr hProvider;

			//
			// Acquire access to the keystore, if it already exists. Otherwise, create the keystore.
			//
			bool result = CryptNativeHelper.CryptAcquireContext(out hProvider, containerName, null,
								providerType, CryptNativeHelper.AcquireContextFlags.None);

			if (!result)
				result = CryptNativeHelper.CryptAcquireContext(out hProvider, containerName, null,
								providerType, CryptNativeHelper.AcquireContextFlags.CRYPT_NEWKEYSET);

			if (result)
				return hProvider;
			else
				return IntPtr.Zero;
		}

		/// <summary>
		///		Release the keystore. If the <see cref="DeleteKeysetOnDispose"/> property is set to
		///		true (the default), the keystore will be deleted at the same time.
		/// </summary>
		public virtual void Dispose()
		{
			if (hProvider != IntPtr.Zero)
			{
				CryptNativeHelper.CryptReleaseContext(hProvider, 0);
				if (deleteKeysetOnDispose)
					CryptNativeHelper.CryptAcquireContext(out hProvider, containerName, null,
								providerType, CryptNativeHelper.AcquireContextFlags.CRYPT_DELETEKEYSET);
				hProvider = IntPtr.Zero;

				GC.SuppressFinalize(this);
			}
		}

		/// <summary>
		///		Handle to the provider instance returned by the AcquireContext call in the constructor.
		/// </summary>
		public IntPtr ProviderHandle
		{
			get { return hProvider; }
		}
	}
}
