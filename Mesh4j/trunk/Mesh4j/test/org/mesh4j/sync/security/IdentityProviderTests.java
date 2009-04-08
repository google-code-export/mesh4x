package org.mesh4j.sync.security;

import org.junit.Assert;
import org.junit.Test;

public class IdentityProviderTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenUserIsNull(){
		 new IdentityProvider(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenUserIsEmpty(){
		 new IdentityProvider("");
	}
	
	@Test
	public void shouldGetUser(){
		IdentityProvider identityProvider = new IdentityProvider("jmt");
		Assert.assertEquals("jmt", identityProvider.getAuthenticatedUser());
	}
}
