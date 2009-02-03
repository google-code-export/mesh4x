package org.mesh4j.sync.security;

import org.junit.Assert;
import org.junit.Test;

public class IdentityProviderTests {

	@Test
	public void shouldReturnsDefaultIdentity(){
		Assert.assertNotNull(NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		Assert.assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
	
	@Test
	public void shouldAtomicDefaultIdentity(){
		Assert.assertNotNull(NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		Assert.assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		Assert.assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		Assert.assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
	
	@Test
	public void shouldReturnsLoggedInIdentity(){
		LoggedInIdentityProvider identityProvider = new LoggedInIdentityProvider();
		
		String identity = identityProvider.getAuthenticatedUser();
		Assert.assertNotNull(identity);
		Assert.assertTrue(identity.trim().length() > 0);
	}

	@Test
	public void shouldAtomicLoggedInIdentity(){
		LoggedInIdentityProvider identityProvider = new LoggedInIdentityProvider();
		
		String identity = identityProvider.getAuthenticatedUser();
		Assert.assertNotNull(identity);
		Assert.assertTrue(identity.trim().length() > 0);
		
		Assert.assertEquals(identity, identityProvider.getAuthenticatedUser());		
		Assert.assertEquals(identity, identityProvider.getAuthenticatedUser());
	}

	
}
