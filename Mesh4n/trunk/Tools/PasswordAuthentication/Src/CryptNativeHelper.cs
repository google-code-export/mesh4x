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
using System.Runtime.InteropServices;
using System.Globalization;

namespace Microsoft.Practices.Mobile.PasswordAuthentication
{
	/// <summary>
	///		This class provides a wrapper around native calls to Crypt APIs that we need for authentication and
	///		key generation.
	/// </summary>
	public class CryptNativeHelper
	{
		private const ProviderType providerType = ProviderType.PROV_RSA_AES;
		private RsaAesCryptographyProvider provider;
		private IntPtr hProvider;

		/// <summary>
		///		Creates a new instance.
		/// </summary>
		/// <param name="provider">
		///		The cryptographic provider that provides both the key store and the algorithm.
		/// </param>
		public CryptNativeHelper(RsaAesCryptographyProvider provider)
		{
			Guard.ArgumentNotNull(provider, "provider");

			this.provider = provider;
			this.hProvider = provider.ProviderHandle;
		}

		/// <summary>
		///		This is a "managed" wrapper around the CryptExportKey function. It basically calls this function
		///		and returns a byte array with the key data.
		/// </summary>
		/// <param name="hKey">The private key to export.</param>
		/// <param name="hExchangeKey">
		///		The key used to encrypt the private key, or IntPtr.Zero if you don't want to encrypt the key.
		/// </param>
		/// <param name="type">The type of blob you want returned.</param>
		/// <returns>The key data.</returns>
		/// <exception cref="CryptNativeException">If one of the native calls failed.</exception>
		private byte[] ExportKeyBlob(IntPtr hKey, IntPtr hExchangeKey, BlobType type)
		{
			//
			// First ask for the length of the key, in bytes.
			//
			int dataLength = 0;
			IntPtr pData;
			if (!CryptExportKey(hKey, hExchangeKey, type, 0, IntPtr.Zero, ref dataLength))
				ThrowWin32Exception("CryptExportKey");

			//
			// Retrieve the key and copy it over into a managed array.
			//
			pData = Marshal.AllocHGlobal(dataLength);
			try
			{
				if (!CryptExportKey(hKey, hExchangeKey, type, 0, pData, ref dataLength))
					ThrowWin32Exception("CryptExportKey");

				byte[] data = new byte[dataLength];
				Marshal.Copy(pData, data, 0, dataLength);

				return data;
			}
			finally
			{
				Marshal.FreeHGlobal(pData);
			}
		}

		/// <summary>
		///		This method takes the key and returns a simple blob. A simple blob is basically a header, an
		///		algorithm ID, and then the key data itself. This is all defined in an article on MSDN:
		/// <para>http://msdn.microsoft.com/library/default.asp?url=/library/en-us/seccrypto/security/simple_key_blobs.asp</para>
		/// </summary>
		/// <param name="keyData"></param>
		/// <param name="algid"></param>
		/// <returns></returns>
		private byte[] GetKeyBlobFromKeyData(byte[] keyData, AlgorithmId algid)
		{
			byte[] blob = new byte[keyData.Length + 12];

			//
			// BLOBHEADER:
			//		Offset	Type	Name			Description
			//		------	----	----			-----------
			//		0		byte	bType			Type of Blobk, such as SIMPLEBLOB
			//		1		byte	bVersion		0x02
			//		2-3		short	reserved
			//		4-7		int		Algorithm ID.
			//
			blob[0] = (byte)BlobType.PLAINTEXTKEYBLOB;
			blob[1] = 2;
			byte[] algidBytes = CryptographyUtility.GetBytes((uint)algid);
			Array.Copy(algidBytes, 0, blob, 4, 4);

			//
			// SIMPLEBLOB format:
			//		Offset	Contents			Description
			//		------	--------			-----------
			//		0-7		BLOBHEADER
			//		8-11	ALG_ID				ID of algorithm used to encrypt the key
			//		11-		Key data
			//
			blob[8] = 32;							// This is what the value is when we export a simple key
			Array.Copy(keyData, 0, blob, 12, keyData.Length);

			return blob;
		}

		private IntPtr GetDerivedKey(string password)
		{
			IntPtr hHash;

			if (!CryptCreateHash(hProvider, AlgorithmId.CALG_SHA1, IntPtr.Zero, 0, out hHash))
				ThrowWin32Exception("CryptCreateHash");

			byte[] data = null;
			try
			{
				data = CryptographyUtility.GetBytes(password);
				if (!CryptHashData(hHash, data, data.Length, 0))
					ThrowWin32Exception("CryptHashData");

				IntPtr hKey;
				CryptGenFlags genFlags = CryptGenFlags.CRYPT_EXPORTABLE | CryptGenFlags.CRYPT_NO_SALT;
				if (!CryptDeriveKey(hProvider, AlgorithmId.CALG_AES_256, hHash, genFlags, out hKey))
					ThrowWin32Exception("CryptGenKey");

				return hKey;
			}
			finally
			{
				CryptographyUtility.ZeroOutBytes(data);
				CryptDestroyHash(hHash);
			}
		}

		/// <summary>
		///		Generates an keyed hash (HMAC) from data, salt, and a key. To obtain
		///		the same hash you must supply the same data, salt, and key. This function
		///		uses HMAC hashing with SHA1.
		/// </summary>
		/// <param name="data">The data to hash.</param>
		/// <param name="salt">
		///		Salt to add to the hash to mitigate dictionary attacks. Recommended length
		///		is 8 bytes (64 bits).
		/// </param>
		/// <param name="key">The key to use. Recommended length is 32 bytes (256 bits).</param>
		/// <returns>A keyed hash value.</returns>
		public byte[] GetHmacHash(byte[] data, byte[] salt, byte[] key)
		{
			//
			// Turns the provided key into a blob that can be imported into the key store and then import
			// this key into the key store.
			//
			IntPtr hKey;
			byte[] keyBlob = GetKeyBlobFromKeyData(key, AlgorithmId.CALG_AES_256);

			if (!CryptImportKey(hProvider, keyBlob, keyBlob.Length, IntPtr.Zero, CryptGenFlags.CRYPT_EXPORTABLE, out hKey))
				ThrowWin32Exception("CryptImportKey");

			try
			{
				//
				// Create an HMAC hash set to use the SHA1 algorithm. Then hash the data followed by the salt.
				//
				IntPtr hHash;
				if (!CryptCreateHash(hProvider, AlgorithmId.CALG_HMAC, hKey, 0, out hHash))
					ThrowWin32Exception("CryptCreateHash");

				try
				{
					int[] hmacInfo = new int[5];
					hmacInfo[0] = (int)AlgorithmId.CALG_SHA1;
					if (!CryptSetHashParam(hHash, HashParam.HP_HMAC_INFO, hmacInfo, 0))
						ThrowWin32Exception("CryptSetHashParam");

					if (!CryptHashData(hHash, data, data.Length, 0))
						ThrowWin32Exception("CryptHashData");

					if (!CryptHashData(hHash, salt, salt.Length, 0))
						ThrowWin32Exception("CryptHashData");

					int hashSize;
					int dataLength = 4;
					if (!CryptGetHashParam(hHash, HashParam.HP_HASHSIZE, out hashSize, ref dataLength, 0))
						ThrowWin32Exception("CryptGetHashParam");

					byte[] hashData = new byte[hashSize];
					dataLength = hashSize;
					if (!CryptGetHashParam(hHash, HashParam.HP_HASHVAL, hashData, ref dataLength, 0))
						ThrowWin32Exception("CryptGetHashParam");

					return hashData;
				}
				finally
				{
					CryptDestroyHash(hHash);
				}
			}
			finally
			{
				CryptDestroyKey(hKey);
			}
		}

		/// <summary>
		///		Obtains a key from a password that you supply.
		/// </summary>
		/// <param name="password">A password.</param>
		/// <returns>Returns a key derived from this password.</returns>
		public byte[] GetPasswordDerivedKey(string password)
		{
			IntPtr hUserKey = GetDerivedKey(password);
			try
			{
				byte[] data = ExportKeyBlob(hUserKey, IntPtr.Zero, BlobType.PLAINTEXTKEYBLOB);
				return ExtractKeyFromBlob(hUserKey, data);
			}
			finally
			{
				CryptDestroyKey(hUserKey);
			}
		}

		//
		// The key blob contains data in addition to the actual key. The following article defines the
		// format for the blob.
		//
		// http://msdn.microsoft.com/library/default.asp?url=/library/en-us/seccrypto/security/simple_key_blobs.asp
		//
		private byte[] ExtractKeyFromBlob(IntPtr hKey, byte[] blobData)
		{
			int keyLength;							// The length of the key, in bits
			int dataLength = 4;
			if (!CryptGetKeyParam(hKey, KeyParam.KP_KEYLEN, out keyLength, ref dataLength, 0))
				ThrowWin32Exception("CryptGetKeyParam");

			keyLength /= 8;							// Convert to key size in bytes
			byte[] keyData = new byte[keyLength];

			int index = 12;							// sizeof(BLOBHEADER) + sizeof(ALG_ID)
			Array.Copy(blobData, index, keyData, 0, keyData.Length);

			return keyData;
		}

		private static void ThrowWin32Exception(string functionName)
		{
			try
			{
				int error = Marshal.GetLastWin32Error();
				Marshal.ThrowExceptionForHR(error);
				throw new CryptNativeException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.Win32Error, functionName, error), error);
			}
			catch (Exception ex)
			{
				throw new CryptNativeException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.Win32HResultMessage, functionName, ex), ex);
			}
		}

		// exported key blob definitions
		private enum BlobType
		{
			SIMPLEBLOB = 0x1,
			PUBLICKEYBLOB = 0x6,
			PRIVATEKEYBLOB = 0x7,
			PLAINTEXTKEYBLOB = 0x8,
			OPAQUEKEYBLOB = 0x9,
			PUBLICKEYBLOBEX = 0xA,
			SYMMETRICWRAPKEYBLOB = 0xB,
			KEYSTATEBLOB = 0xC,
		}

		public enum ProviderType
		{
			PROV_RSA_FULL = 1,
			PROV_RSA_SIG = 2,
			PROV_DSS = 3,
			PROV_FORTEZZA = 4,
			PROV_MS_EXCHANGE = 5,
			PROV_SSL = 6,
			PROV_RSA_SCHANNEL = 12,
			PROV_DSS_DH = 13,
			PROV_EC_ECDSA_SIG = 14,
			PROV_EC_ECNRA_SIG = 15,
			PROV_EC_ECDSA_FULL = 16,
			PROV_EC_ECNRA_FULL = 17,
			PROV_DH_SCHANNEL = 18,
			PROV_SPYRUS_LYNKS = 20,
			PROV_RNG = 21,
			PROV_INTEL_SEC = 22,
			PROV_REPLACE_OWF = 23,
			PROV_RSA_AES = 24,
		}

		internal enum AcquireContextFlags : uint
		{
			None = 0,
			CRYPT_VERIFYCONTEXT = 0xF0000000,
			CRYPT_NEWKEYSET = 0x00000008,
			CRYPT_DELETEKEYSET = 0x00000010,
			CRYPT_MACHINE_KEYSET = 0x00000020,
			CRYPT_SILENT = 0x00000040,
		}

		private enum CryptGenFlags : uint
		{
			None = 0,
			CRYPT_EXPORTABLE = 1,
			CRYPT_NO_SALT = 0x00000010,
		}

		// Algorithm classes
		private const uint ALG_CLASS_ANY = (0);
		private const uint ALG_CLASS_SIGNATURE = (1 << 13);
		private const uint ALG_CLASS_MSG_ENCRYPT = (2 << 13);
		private const uint ALG_CLASS_DATA_ENCRYPT = (3 << 13);
		private const uint ALG_CLASS_HASH = (4 << 13);
		private const uint ALG_CLASS_KEY_EXCHANGE = (5 << 13);
		private const uint ALG_CLASS_ALL = (7 << 13);

		private const uint ALG_TYPE_ANY = 0;
		private const uint ALG_TYPE_DSS = (1 << 9);
		private const uint ALG_TYPE_RSA = (2 << 9);
		private const uint ALG_TYPE_BLOCK = (3 << 9);
		private const uint ALG_TYPE_STREAM = (4 << 9);
		private const uint ALG_TYPE_DH = (5 << 9);
		private const uint ALG_TYPE_SECURECHANNEL = (6 << 9);

		private const uint ALG_SID_ANY = 0;
		private const uint ALG_SID_MD2 = 1;
		private const uint ALG_SID_MD4 = 2;
		private const uint ALG_SID_MD5 = 3;
		private const uint ALG_SID_SHA = 4;
		private const uint ALG_SID_SHA1 = 4;
		private const uint ALG_SID_MAC = 5;
		private const uint ALG_SID_RIPEMD = 6;
		private const uint ALG_SID_RIPEMD160 = 7;
		private const uint ALG_SID_SSL3SHAMD5 = 8;
		private const uint ALG_SID_HMAC = 9;
		private const uint ALG_SID_TLS1PRF = 10;
		private const uint ALG_SID_HASH_REPLACE_OWF = 11;
		private const uint ALG_SID_SHA_256 = 12;
		private const uint ALG_SID_SHA_384 = 13;
		private const uint ALG_SID_SHA_512 = 14;

		// RC2 sub-ids
		private const uint ALG_SID_RC2 = 2;

		// Stream cipher sub-ids
		private const uint ALG_SID_RC4 = 1;
		private const uint ALG_SID_SEAL = 2;

		// Some RSA sub-ids
		private const uint ALG_SID_RSA_ANY = 0;
		private const uint ALG_SID_RSA_PKCS = 1;
		private const uint ALG_SID_RSA_MSATWORK = 2;
		private const uint ALG_SID_RSA_ENTRUST = 3;
		private const uint ALG_SID_RSA_PGP = 4;

		// Some DSS sub-ids
		private const uint ALG_SID_DSS_ANY = 0;
		private const uint ALG_SID_DSS_PKCS = 1;
		private const uint ALG_SID_DSS_DMS = 2;

		// Block cipher sub ids
		// DES sub_ids
		private const uint ALG_SID_DES = 1;
		private const uint ALG_SID_3DES = 3;
		private const uint ALG_SID_DESX = 4;
		private const uint ALG_SID_IDEA = 5;
		private const uint ALG_SID_CAST = 6;
		private const uint ALG_SID_SAFERSK64 = 7;
		private const uint ALG_SID_SAFERSK128 = 8;
		private const uint ALG_SID_3DES_112 = 9;
		private const uint ALG_SID_CYLINK_MEK = 12;
		private const uint ALG_SID_RC5 = 13;
		private const uint ALG_SID_AES_128 = 14;
		private const uint ALG_SID_AES_192 = 15;
		private const uint ALG_SID_AES_256 = 16;
		private const uint ALG_SID_AES = 17;

		// Fortezza sub-ids
		private const uint ALG_SID_SKIPJACK = 10;
		private const uint ALG_SID_TEK = 11;

		// Diffie-Hellman sub-ids
		private const uint ALG_SID_DH_SANDF = 1;
		private const uint ALG_SID_DH_EPHEM = 2;
		private const uint ALG_SID_AGREED_KEY_ANY = 3;
		private const uint ALG_SID_KEA = 4;

		private enum AlgorithmId : uint
		{
			CALG_MD2 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MD2),
			CALG_MD4 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MD4),
			CALG_MD5 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MD5),
			CALG_SHA = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA),
			CALG_SHA1 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA1),
			CALG_MAC = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MAC),
			CALG_RSA_SIGN = (ALG_CLASS_SIGNATURE | ALG_TYPE_RSA | ALG_SID_RSA_ANY),
			CALG_DSS_SIGN = (ALG_CLASS_SIGNATURE | ALG_TYPE_DSS | ALG_SID_DSS_ANY),
			CALG_NO_SIGN = (ALG_CLASS_SIGNATURE | ALG_TYPE_ANY | ALG_SID_ANY),
			CALG_RSA_KEYX = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_RSA | ALG_SID_RSA_ANY),
			CALG_DES = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_DES),
			CALG_3DES_112 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_3DES_112),
			CALG_3DES = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_3DES),
			CALG_DESX = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_DESX),
			CALG_RC2 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_RC2),
			CALG_RC4 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_STREAM | ALG_SID_RC4),
			CALG_SEAL = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_STREAM | ALG_SID_SEAL),
			CALG_DH_SF = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_DH | ALG_SID_DH_SANDF),
			CALG_DH_EPHEM = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_DH | ALG_SID_DH_EPHEM),
			CALG_AGREEDKEY_ANY = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_DH | ALG_SID_AGREED_KEY_ANY),
			CALG_KEA_KEYX = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_DH | ALG_SID_KEA),
			CALG_HUGHES_MD5 = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_ANY | ALG_SID_MD5),
			CALG_SKIPJACK = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_SKIPJACK),
			CALG_TEK = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_TEK),
			CALG_CYLINK_MEK = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_CYLINK_MEK),
			CALG_SSL3_SHAMD5 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SSL3SHAMD5),
			//CALG_SSL3_MASTER = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SSL3_MASTER),
			//CALG_SCHANNEL_MASTER_HASH = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SCHANNEL_MASTER_HASH),
			//CALG_SCHANNEL_MAC_KEY = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SCHANNEL_MAC_KEY),
			//CALG_SCHANNEL_ENC_KEY = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SCHANNEL_ENC_KEY),
			//CALG_PCT1_MASTER = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_PCT1_MASTER),
			//CALG_SSL2_MASTER = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SSL2_MASTER),
			//CALG_TLS1_MASTER = (ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_TLS1_MASTER),
			CALG_RC5 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_RC5),
			CALG_HMAC = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_HMAC),
			CALG_TLS1PRF = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_TLS1PRF),
			CALG_HASH_REPLACE_OWF = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_HASH_REPLACE_OWF),
			CALG_AES_128 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_AES_128),
			CALG_AES_192 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_AES_192),
			CALG_AES_256 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_AES_256),
			CALG_AES = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_AES),
			CALG_SHA_256 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA_256),
			CALG_SHA_384 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA_384),
			CALG_SHA_512 = (ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA_512),
		}

		private enum KeySpec
		{
			AT_KEYEXCHANGE = 1,
			AT_SIGNATURE = 2,
		}

		private enum KeyParam
		{
			KP_IV = 1,       // Initialization vector
			KP_SALT = 2,       // Salt value
			KP_PADDING = 3,       // Padding values
			KP_MODE = 4,       // Mode of the cipher
			KP_MODE_BITS = 5,       // Number of bits to feedback
			KP_PERMISSIONS = 6,       // Key permissions DWORD
			KP_ALGID = 7,       // Key algorithm
			KP_BLOCKLEN = 8,       // Block size of the cipher
			KP_KEYLEN = 9,       // Length of key in bits
		}

		[Flags()]
		public enum ProvParam
		{
			PP_ENUMALGS = 1,
			PP_ENUMCONTAINERS = 2,
			PP_IMPTYPE = 3,
			PP_NAME = 4,
			PP_VERSION = 5,
			PP_CONTAINER = 6,
			PP_CHANGE_PASSWORD = 7,
			PP_KEYSET_SEC_DESCR = 8,      // get/set security descriptor of keyset
			PP_CERTCHAIN = 9,     // for retrieving certificates from tokens
			PP_KEY_TYPE_SUBTYPE = 10,
			PP_PROVTYPE = 16,
			PP_KEYSTORAGE = 17,
			PP_APPLI_CERT = 18,
			PP_SYM_KEYSIZE = 19,
			PP_SESSION_KEYSIZE = 20,
			PP_UI_PROMPT = 21,
			PP_ENUMALGS_EX = 22,
			PP_ENUMMANDROOTS = 25,
			PP_ENUMELECTROOTS = 26,
			PP_KEYSET_TYPE = 27,
			PP_ADMIN_PIN = 31,
			PP_KEYEXCHANGE_PIN = 32,
			PP_SIGNATURE_PIN = 33,
			PP_SIG_KEYSIZE_INC = 34,
			PP_KEYX_KEYSIZE_INC = 35,
			PP_UNIQUE_CONTAINER = 36,
			PP_SGC_INFO = 37,
			PP_USE_HARDWARE_RNG = 38,
			PP_KEYSPEC = 39,
			PP_ENUMEX_SIGNING_PROT = 40,
			PP_CRYPT_COUNT_KEY_USE = 41,
		}

		private enum HashParam
		{
			HP_HASHVAL = 0x0002,  // Hash value
			HP_HASHSIZE = 0x0004,  // Hash value size
			HP_HMAC_INFO = 0x0005,  // information for creating an HMAC
		}

		private const uint CRYPT_FIRST = 1;

		private static readonly bool isDesktop = Environment.OSVersion.Platform != PlatformID.WinCE;
		private const string cryptDll = "coredll.dll";
		private const string advapiDll = "advapi32.dll";

		internal static bool CryptAcquireContext(out IntPtr phProv, string pszContainer, string pszProvider, ProviderType dwProvType, AcquireContextFlags dwFlags)
		{
			if (isDesktop)
				return CryptAcquireContextDt(out phProv, pszContainer, pszProvider, dwProvType, dwFlags);
			else
				return CryptAcquireContextCf(out phProv, pszContainer, pszProvider, dwProvType, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptAcquireContext", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptAcquireContextDt(out IntPtr phProv, string pszContainer, string pszProvider, ProviderType dwProvType, AcquireContextFlags dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptAcquireContext", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptAcquireContextCf(out IntPtr phProv, string pszContainer, string pszProvider, ProviderType dwProvType, AcquireContextFlags dwFlags);

		private bool CryptCreateHash(IntPtr hProv, AlgorithmId Algid, IntPtr hKey, uint dwFlags, out IntPtr phHash)
		{
			if (isDesktop)
				return CryptCreateHashDt(hProv, Algid, hKey, dwFlags, out phHash);
			else
				return CryptCreateHashCf(hProv, Algid, hKey, dwFlags, out phHash);
		}

		[DllImport(advapiDll, EntryPoint = "CryptCreateHash", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptCreateHashDt(IntPtr hProv, AlgorithmId Algid, IntPtr hKey, uint dwFlags, out IntPtr phHash);
		[DllImport(cryptDll, EntryPoint = "CryptCreateHash", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptCreateHashCf(IntPtr hProv, AlgorithmId Algid, IntPtr hKey, uint dwFlags, out IntPtr phHash);

		private static bool CryptDecrypt(IntPtr hKey, IntPtr hHash, bool Final, uint dwFlags, byte[] pbData, ref uint pdwDataLen)
		{
			if (isDesktop)
				return CryptDecryptDt(hKey, hHash, Final, dwFlags, pbData, ref pdwDataLen);
			else
				return CryptDecryptCf(hKey, hHash, Final, dwFlags, pbData, ref pdwDataLen);
		}

		[DllImport(advapiDll, EntryPoint = "CryptDecrypt", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDecryptDt(IntPtr hKey, IntPtr hHash, [MarshalAs(UnmanagedType.Bool)] bool Final, uint dwFlags, byte[] pbData, ref uint pdwDataLen);
		[DllImport(cryptDll, EntryPoint = "CryptDecrypt", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDecryptCf(IntPtr hKey, IntPtr hHash, [MarshalAs(UnmanagedType.Bool)] bool Final, uint dwFlags, byte[] pbData, ref uint pdwDataLen);

		private static bool CryptDeriveKey(IntPtr phProv, AlgorithmId Algid, IntPtr hBaseData, CryptGenFlags dwFlags, out IntPtr phKey)
		{
			if (isDesktop)
				return CryptDeriveKeyDt(phProv, Algid, hBaseData, dwFlags, out phKey);
			else
				return CryptDeriveKeyCf(phProv, Algid, hBaseData, dwFlags, out phKey);
		}

		[DllImport(advapiDll, EntryPoint = "CryptDeriveKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDeriveKeyDt(IntPtr phProv, AlgorithmId Algid, IntPtr hBaseData, CryptGenFlags dwFlags, out IntPtr phKey);
		[DllImport(cryptDll, EntryPoint = "CryptDeriveKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDeriveKeyCf(IntPtr phProv, AlgorithmId Algid, IntPtr hBaseData, CryptGenFlags dwFlags, out IntPtr phKey);

		private static bool CryptDestroyKey(IntPtr hKey)
		{
			if (isDesktop)
				return CryptDestroyKeyDt(hKey);
			else
				return CryptDestroyKeyCf(hKey);
		}

		[DllImport(advapiDll, EntryPoint = "CryptDestroyKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDestroyKeyDt(IntPtr hKey);
		[return: MarshalAs(UnmanagedType.Bool)]
		[DllImport(cryptDll, EntryPoint = "CryptDestroyKey", SetLastError = true)]
		private static extern bool CryptDestroyKeyCf(IntPtr hKey);

		private static bool CryptExportKey(IntPtr hKey, IntPtr hExpKey, BlobType dwBlobType, uint dwFlags, IntPtr pbData, ref int pdwbDataLen)
		{
			if (isDesktop)
				return CryptExportKeyDt(hKey, hExpKey, dwBlobType, dwFlags, pbData, ref pdwbDataLen);
			else
				return CryptExportKeyCf(hKey, hExpKey, dwBlobType, dwFlags, pbData, ref pdwbDataLen);
		}

		[DllImport(advapiDll, EntryPoint = "CryptExportKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptExportKeyDt(IntPtr hKey, IntPtr hExpKey, BlobType dwBlobType, uint dwFlags, IntPtr pbData, ref int pdwbDataLen);
		[DllImport(cryptDll, EntryPoint = "CryptExportKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptExportKeyCf(IntPtr hKey, IntPtr hExpKey, BlobType dwBlobType, uint dwFlags, IntPtr pbData, ref int pdwbDataLen);

		private static bool CryptGetHashParam(IntPtr hKey, HashParam dwParam, byte[] data, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetHashParamDt(hKey, dwParam, data, ref pdwDataLen, dwFlags);
			else
				return CryptGetHashParamCf(hKey, dwParam, data, ref pdwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetHashParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetHashParamDt(IntPtr hKey, HashParam dwParam, byte[] data, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetHashParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetHashParamCf(IntPtr hKey, HashParam dwParam, byte[] data, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetHashParam(IntPtr hKey, HashParam dwParam, out int data, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetHashParamDt(hKey, dwParam, out data, ref pdwDataLen, dwFlags);
			else
				return CryptGetHashParamCf(hKey, dwParam, out data, ref pdwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetHashParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetHashParamDt(IntPtr hKey, HashParam dwParam, out int data, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetHashParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetHashParamCf(IntPtr hKey, HashParam dwParam, out int data, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetKeyParam(IntPtr hKey, KeyParam dwParam, IntPtr pData, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetKeyParamDt(hKey, dwParam, pData, ref pdwDataLen, dwFlags);
			else
				return CryptGetKeyParamCf(hKey, dwParam, pData, ref pdwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetKeyParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetKeyParamDt(IntPtr hKey, KeyParam dwParam, IntPtr pData, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetKeyParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetKeyParamCf(IntPtr hKey, KeyParam dwParam, IntPtr pData, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetKeyParam(IntPtr hKey, KeyParam dwParam, byte[] data, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetKeyParamDt(hKey, dwParam, data, ref pdwDataLen, dwFlags);
			else
				return CryptGetKeyParamCf(hKey, dwParam, data, ref pdwDataLen, dwFlags);
		}
		[DllImport(advapiDll, EntryPoint = "CryptGetKeyParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetKeyParamDt(IntPtr hKey, KeyParam dwParam, byte[] data, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetKeyParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetKeyParamCf(IntPtr hKey, KeyParam dwParam, byte[] data, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetKeyParam(IntPtr hKey, KeyParam dwParam, out int pData, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetKeyParamDt(hKey, dwParam, out pData, ref pdwDataLen, dwFlags);
			else
				return CryptGetKeyParamCf(hKey, dwParam, out pData, ref pdwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetKeyParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetKeyParamDt(IntPtr hKey, KeyParam dwParam, out int pData, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetKeyParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetKeyParamCf(IntPtr hKey, KeyParam dwParam, out int pData, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetProvParam(IntPtr hProv, ProvParam dwParam, byte[] pbData, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetProvParamDt(hProv, dwParam, pbData, ref pdwDataLen, dwFlags);
			else
				return CryptGetProvParamCf(hProv, dwParam, pbData, ref pdwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetProvParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetProvParamDt(IntPtr hProv, ProvParam dwParam, byte[] pbData, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetProvParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetProvParamCf(IntPtr hProv, ProvParam dwParam, byte[] pbData, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetProvParam(IntPtr hProv, ProvParam dwParam, int[] pbData, ref int pdwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptGetProvParamDt(hProv, dwParam, pbData, ref pdwDataLen, dwFlags);
			else
				return CryptGetProvParamCf(hProv, dwParam, pbData, ref pdwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetProvParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetProvParamDt(IntPtr hProv, ProvParam dwParam, int[] pbData, ref int pdwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptGetProvParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetProvParamCf(IntPtr hProv, ProvParam dwParam, int[] pbData, ref int pdwDataLen, uint dwFlags);

		private static bool CryptGetUserKey(IntPtr hProv, KeySpec dwKeySpec, out IntPtr phUserKey)
		{
			if (isDesktop)
				return CryptGetUserKeyDt(hProv, dwKeySpec, out phUserKey);
			else
				return CryptGetUserKeyCf(hProv, dwKeySpec, out phUserKey);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGetUserKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetUserKeyDt(IntPtr hProv, KeySpec dwKeySpec, out IntPtr phUserKey);
		[DllImport(cryptDll, EntryPoint = "CryptGetUserKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGetUserKeyCf(IntPtr hProv, KeySpec dwKeySpec, out IntPtr phUserKey);

		private static bool CryptGenKey(IntPtr hProv, AlgorithmId algid, CryptGenFlags dwFlags, out IntPtr phKey)
		{
			if (isDesktop)
				return CryptGenKeyDt(hProv, algid, dwFlags, out phKey);
			else
				return CryptGenKeyCf(hProv, algid, dwFlags, out phKey);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGenKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGenKeyDt(IntPtr hProv, AlgorithmId algid, CryptGenFlags dwFlags, out IntPtr phKey);
		[DllImport(cryptDll, EntryPoint = "CryptGenKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGenKeyCf(IntPtr hProv, AlgorithmId algid, CryptGenFlags dwFlags, out IntPtr phKey);

		private static bool CryptGenKey(IntPtr hProv, KeySpec algid, CryptGenFlags dwFlags, out IntPtr phKey)
		{
			if (isDesktop)
				return CryptGenKeyDt(hProv, algid, dwFlags, out phKey);
			else
				return CryptGenKeyCf(hProv, algid, dwFlags, out phKey);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGenKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGenKeyDt(IntPtr hProv, KeySpec algid, CryptGenFlags dwFlags, out IntPtr phKey);
		[DllImport(cryptDll, EntryPoint = "CryptGenKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGenKeyCf(IntPtr hProv, KeySpec algid, CryptGenFlags dwFlags, out IntPtr phKey);

		private static bool CryptGenRandom(IntPtr hProv, int dwSize, byte[] buffer)
		{
			if (isDesktop)
				return CryptGenRandomDt(hProv, dwSize, buffer);
			else
				return CryptGenRandomCf(hProv, dwSize, buffer);
		}

		[DllImport(advapiDll, EntryPoint = "CryptGenRandom", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGenRandomDt(IntPtr hProv, int dwSize, byte[] buffer);
		[DllImport(cryptDll, EntryPoint = "CryptGenRandom", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptGenRandomCf(IntPtr hProv, int dwSize, byte[] buffer);

		private static bool CryptHashData(IntPtr hHash, byte[] pbData, int dwDataLen, uint dwFlags)
		{
			if (isDesktop)
				return CryptHashDataDt(hHash, pbData, dwDataLen, dwFlags);
			else
				return CryptHashDataCf(hHash, pbData, dwDataLen, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptHashData", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptHashDataDt(IntPtr hHash, byte[] pbData, int dwDataLen, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptHashData", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptHashDataCf(IntPtr hHash, byte[] pbData, int dwDataLen, uint dwFlags);

		private static bool CryptImportKey(IntPtr hProv, byte[] pbData, int dwDataLen, IntPtr hPubKey, CryptGenFlags dwFlags, out IntPtr phKey)
		{
			if (isDesktop)
				return CryptImportKeyDt(hProv, pbData, dwDataLen, hPubKey, dwFlags, out phKey);
			else
				return CryptImportKeyCf(hProv, pbData, dwDataLen, hPubKey, dwFlags, out phKey);
		}

		[DllImport(advapiDll, EntryPoint = "CryptImportKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptImportKeyDt(IntPtr hProv, byte[] pbData, int dwDataLen, IntPtr hPubKey, CryptGenFlags dwFlags, out IntPtr phKey);
		[DllImport(cryptDll, EntryPoint = "CryptImportKey", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptImportKeyCf(IntPtr hProv, byte[] pbData, int dwDataLen, IntPtr hPubKey, CryptGenFlags dwFlags, out IntPtr phKey);

		internal static bool CryptReleaseContext(IntPtr phProv, uint dwFlags)
		{
			if (isDesktop)
				return CryptReleaseContextDt(phProv, dwFlags);
			else
				return CryptReleaseContextCf(phProv, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptReleaseContext", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		internal static extern bool CryptReleaseContextDt(IntPtr phProv, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptReleaseContext", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		internal static extern bool CryptReleaseContextCf(IntPtr phProv, uint dwFlags);

		private static bool CryptSetHashParam(IntPtr hHash, HashParam dwParam, int[] pbData, uint dwFlags)
		{
			if (isDesktop)
				return CryptSetHashParamDt(hHash, dwParam, pbData, dwFlags);
			else
				return CryptSetHashParamCf(hHash, dwParam, pbData, dwFlags);
		}

		[DllImport(advapiDll, EntryPoint = "CryptSetHashParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptSetHashParamDt(IntPtr hHash, HashParam dwParam, int[] pbData, uint dwFlags);
		[DllImport(cryptDll, EntryPoint = "CryptSetHashParam", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptSetHashParamCf(IntPtr hHash, HashParam dwParam, int[] pbData, uint dwFlags);

		private static bool CryptDestroyHash(IntPtr hHash)
		{
			if (isDesktop)
				return CryptDestroyHashDt(hHash);
			else
				return CryptDestroyHashCf(hHash);
		}

		[DllImport(advapiDll, EntryPoint = "CryptDestroyHash", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDestroyHashDt(IntPtr hHash);
		[DllImport(cryptDll, EntryPoint = "CryptDestroyHash", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		private static extern bool CryptDestroyHashCf(IntPtr hHash);
	}
}
