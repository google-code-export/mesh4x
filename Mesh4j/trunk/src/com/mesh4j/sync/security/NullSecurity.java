package com.mesh4j.sync.security;

public class NullSecurity implements ISecurity{
	
	public static final NullSecurity INSTANCE = new NullSecurity();

	public String getAuthenticatedUser(){
		return "admin";
	}
}
