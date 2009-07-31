package org.mesh4j.sync.security;

import junit.framework.Assert;

import org.junit.Test;

public class LoggedInIdentityProviderTests {

	@Test
	public void shouldGetLoggedUserName(){
		String userName = LoggedInIdentityProvider.getUserName();
		Assert.assertEquals(System.getProperty("user.name"), userName);
	}
	
	@Test
	public void shouldGetHostName(){
		String hostName = LoggedInIdentityProvider.getHostName();
		Assert.assertNotNull(hostName);
	}
	
	@Test
	public void shouldGetAuthenticatedUser(){
		String userName = LoggedInIdentityProvider.getUserName();
		String hostName = LoggedInIdentityProvider.getHostName();
		LoggedInIdentityProvider identityProvider = new LoggedInIdentityProvider();
		
		Assert.assertEquals(hostName+"_"+userName, identityProvider.getAuthenticatedUser());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenDataSourceIsNull(){
		new LoggedInIdentityProvider(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenDataSourceIsEmpty(){
		new LoggedInIdentityProvider("");
	}
	
	@Test
	public void shouldGetAuthenticatedUserWithDataSource(){
		String userName = LoggedInIdentityProvider.getUserName();
		String hostName = LoggedInIdentityProvider.getHostName();
		String datasource = "msAccess:mydb.mdb";
		LoggedInIdentityProvider identityProvider = new LoggedInIdentityProvider(datasource);
		
		Assert.assertEquals(hostName+"_"+userName+"_"+datasource, identityProvider.getAuthenticatedUser());
	}
}
