package org.mesh4j.sync.security;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class IdentityProviderTests extends TestCase{

	public IdentityProviderTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public IdentityProviderTests(String name) {
		super(name);
	}
	
	public IdentityProviderTests() {
		super();
	}
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new IdentityProviderTests("shouldReturnsDefaultIdentity", new TestMethod(){public void run(TestCase tc){((IdentityProviderTests)tc).shouldReturnsDefaultIdentity();}})));
		suite.addTest((new IdentityProviderTests("shouldAtomicDefaultIdentity", new TestMethod(){public void run(TestCase tc){((IdentityProviderTests)tc).shouldAtomicDefaultIdentity();}})));
		return suite;
	}
	
	public void shouldReturnsDefaultIdentity(){
		assertNotNull(NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
	
	public void shouldAtomicDefaultIdentity(){
		assertNotNull(NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		assertEquals("admin", NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
}
