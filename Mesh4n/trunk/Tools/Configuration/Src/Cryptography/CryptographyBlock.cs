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

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		This class helps manage data that needs to be encrypted and stored. When you encrypt data,
	///		you need to save the initialization vector along with the encrypted data (they key should
	///		not be store, or at least somewhere else.
	/// </summary>
	public class CryptographyBlock
	{
		private SymmetricAlgorithm algorithm;
		private byte[] key;

		/// <summary>
		///		The constructor.
		/// </summary>
		/// <param name="algorithm">
		///		An instance of a symmetric algorithm that will be used to encrypt/decrypt data.
		/// </param>
		/// <param name="key">The key to use for encyrption/decryption.</param>
		public CryptographyBlock(SymmetricAlgorithm algorithm, byte[] key)
		{
			Guard.ArgumentNotNull(algorithm, "algorithm");
			Guard.ArgumentNotNull(key, "key");

			this.algorithm = algorithm;
			this.key = key;
		}

		/// <summary>
		///		Decrypts a block of data that was originally encrypted with this class' <see cref="Encrypt"/>
		///		method.
		/// </summary>
		/// <param name="encryptedBlock">The block of data to decrypt.</param>
		/// <returns></returns>
		/// <exception cref="ArgumentNullException">If the parameter is null</exception>
		/// <exception cref="CryptographicException">This exception will be thrown if there is a problem with the encrypted data.</exception>
		/// <exception cref="ArgumentOutOfRange">Thrown if the data supplied is shorter than the required header.</exception>
		public byte[] Decrypt(byte[] encryptedBlock)
		{
			Guard.ArgumentNotNull(encryptedBlock, "encryptedBlock");

			SubArray<byte> version = new SubArray<byte>(encryptedBlock, 0, 2);
			if (!CryptographyUtility.CompareBytes(version, new byte[] { 1, 0 }))
				throw new ArgumentException(Properties.Resources.BlockVersionMismatch, "encryptedBlock");

			int ivLength = (int)encryptedBlock[2];
			SubArray<byte> iv = new SubArray<byte>(encryptedBlock, 3, ivLength);
			SubArray<byte> data = new SubArray<byte>(encryptedBlock, 3 + ivLength);

			using (ICryptoTransform transform = algorithm.CreateDecryptor(key, iv))
			{
				return CryptographyUtility.Transform(transform, data);
			}
		}

		/// <summary>
		///		Encrypts a block of data and returns a block that includes a version number,
		///		the initialization vector, and the encrypted block.
		/// </summary>
		/// <param name="data">The data to encrypt.</param>
		/// <param name="iv">The initialization vector to use for encrypting the data.</param>
		/// <returns>A block with all the data required to decrypt, except for the key.</returns>
		/// <exception cref="ArgumentNullException">If one of the parameters is null</exception>
		public byte[] Encrypt(byte[] data, byte[] iv)
		{
			Guard.ArgumentNotNull(data, "data");
			Guard.ArgumentNotNull(iv, "iv");

			using (ICryptoTransform transform = algorithm.CreateEncryptor(key, iv))
			{
				byte[] encrypted = CryptographyUtility.Transform(transform, data);
				return BuildDataBlock(1, iv, encrypted);
			}
		}

		//
		// An encrypted "block" consists of the following:
		//
		//		version				2 bytes
		//		iv length			1 byte
		//		iv
		//		encrypted data
		//
		private byte[] BuildDataBlock(byte version, byte[] iv, byte[] data)
		{
			byte[] versionData = { version, 0 };
			byte[] dataBlock = CryptographyUtility.CombineBytes(versionData, new byte[] { (byte)iv.Length });
			dataBlock = CryptographyUtility.CombineBytes(dataBlock, iv);
			dataBlock = CryptographyUtility.CombineBytes(dataBlock, data);

			return dataBlock;
		}
	}
}
