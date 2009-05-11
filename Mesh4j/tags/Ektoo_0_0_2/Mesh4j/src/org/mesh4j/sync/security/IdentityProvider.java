package org.mesh4j.sync.security;

import org.mesh4j.sync.validations.Guard;

public class IdentityProvider implements IIdentityProvider {

	// MODEL VARIABLES
	private String authenticatedUser;
		
	// BUSINESS METHODS
	public IdentityProvider(String authenticatedUser){
		Guard.argumentNotNullOrEmptyString(authenticatedUser, "AuthenticatedUser");
		this.authenticatedUser = authenticatedUser;
	}
	
	@Override
	public String getAuthenticatedUser() {
		return this.authenticatedUser;
	}

}
