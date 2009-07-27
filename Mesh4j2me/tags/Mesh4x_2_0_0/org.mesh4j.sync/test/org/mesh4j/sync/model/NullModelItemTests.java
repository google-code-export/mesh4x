package org.mesh4j.sync.model;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class NullModelItemTests extends TestCase{

	public NullModelItemTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public NullModelItemTests(String name) {
		super(name);
	}
	
	public NullModelItemTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new NullModelItemTests("ShouldThrowIfNullId", new TestMethod(){public void run(TestCase tc){((NullModelItemTests)tc).ShouldThrowIfNullId();}})));
		suite.addTest((new NullModelItemTests("ShouldThrowIfEmptyId", new TestMethod(){public void run(TestCase tc){((NullModelItemTests)tc).ShouldThrowIfEmptyId();}})));
		suite.addTest((new NullModelItemTests("ShouldEqualHash", new TestMethod(){public void run(TestCase tc){((NullModelItemTests)tc).ShouldEqualHash();}})));
		suite.addTest((new NullModelItemTests("ShouldNotEqualDifferentId", new TestMethod(){public void run(TestCase tc){((NullModelItemTests)tc).ShouldNotEqualDifferentId();}})));
		suite.addTest((new NullModelItemTests("ShouldNotEqualDifferentHash", new TestMethod(){public void run(TestCase tc){((NullModelItemTests)tc).ShouldNotEqualDifferentHash();}})));
		suite.addTest((new NullModelItemTests("ShouldNotEqualNull", new TestMethod(){public void run(TestCase tc){((NullModelItemTests)tc).ShouldNotEqualNull();}})));
		return suite;
	}

	public void ShouldThrowIfNullId()
	{
		try{
			new NullContent(null);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void ShouldThrowIfEmptyId()
	{
		try{
			new NullContent("");
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void ShouldEqualHash()
	{
		IContent item1 = new NullContent("1");
		IContent item2 = new NullContent("1");

		assertEquals(item1.hashCode(), item2.hashCode());
	}

	public void ShouldNotEqualDifferentId()
	{
		IContent item1 = new NullContent("1");
		IContent item2 = new NullContent("2");

		assertTrue(item1 != item2);
		assertTrue(item1.hashCode() != item2.hashCode());
	}

	public void ShouldNotEqualDifferentHash()
	{
		IContent item1 = new NullContent("1");
		IContent item2 = new NullContent("12");
		
		assertTrue(item1.hashCode() != item2.hashCode());
	}

	public void ShouldNotEqualNull()
	{
		IContent item1 = new NullContent("1");

		assertTrue(item1 != null);
	}
}
