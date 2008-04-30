package com.mesh4j.sync.security;

public class Security {

	// TODO (JMT) inject security module -> Spring?
	public static String getAuthenticatedUser(){
		return "jmt";		
	}
}
