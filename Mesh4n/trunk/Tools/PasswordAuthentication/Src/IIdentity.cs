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
	public interface IIdentity
	{
		/// <summary>
		///		Reports the type of authentication being provided by the object that implements this interface.
		/// </summary>
		string AuthenticationType { get; }

		/// <summary>
		///		Reports whether or not the identity is authenticated.
		/// </summary>
		bool IsAuthenticated { get; }

		/// <summary>
		///		Name of the user represented by the identity.
		/// </summary>
		string Name { get; }
	}
}
