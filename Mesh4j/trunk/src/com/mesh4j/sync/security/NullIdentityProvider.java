package com.mesh4j.sync.security;

public class NullIdentityProvider implements IIdentityProvider{
	
	public static final NullIdentityProvider INSTANCE = new NullIdentityProvider();

	public String getAuthenticatedUser(){
		return "admin";
	}
}
