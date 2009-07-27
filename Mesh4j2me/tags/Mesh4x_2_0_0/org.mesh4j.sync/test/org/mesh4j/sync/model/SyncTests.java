package org.mesh4j.sync.model;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.util.Date;

import org.mesh4j.sync.test.utils.TestHelper;

public class SyncTests extends TestCase{

	public SyncTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public SyncTests(String name) {
		super(name);
	}
	
	public SyncTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new SyncTests("shouldEqualNullSyncToNull", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldEqualNullSyncToNull();}})));
		suite.addTest((new SyncTests("shouldNotEqualNullOperator", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualNullOperator();}})));
		suite.addTest((new SyncTests("shouldNotEqualNull", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualNull();}})));
		suite.addTest((new SyncTests("shouldEqualIfSameId", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldEqualIfSameId();}})));
		suite.addTest((new SyncTests("shouldNotEqualIfDifferentId", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualIfDifferentId();}})));
		suite.addTest((new SyncTests("shouldNotEqualIfDifferentUpdates", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualIfDifferentUpdates();}})));
		suite.addTest((new SyncTests("shouldNotEqualIfDifferentDeleted", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualIfDifferentDeleted();}})));
		suite.addTest((new SyncTests("shouldNotEqualIfDifferentNoConflicts", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualIfDifferentNoConflicts();}})));
		suite.addTest((new SyncTests("shouldEqualIfEqualHistory", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldEqualIfEqualHistory();}})));
		suite.addTest((new SyncTests("shouldNotEqualIfDifferentHistory", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNotEqualIfDifferentHistory();}})));
		suite.addTest((new SyncTests("shouldNoPurgue", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldNoPurgue();}})));
		suite.addTest((new SyncTests("shouldPurgue", new TestMethod(){public void run(TestCase tc){((SyncTests)tc).shouldPurgue();}})));
		return suite;
	}

	public void shouldEqualNullSyncToNull() {
		assertTrue((Sync) null == null);
	}

	public void shouldNotEqualNullOperator() {
		assertEquals(false, null == new Sync("foo"));
		assertEquals(false, new Sync("foo") == null);
	}

	public void shouldNotEqualNull() {
		assertEquals(false, new Sync("foo").equals((Object) null));
		assertEquals(false, new Sync("foo").equals((Sync) null));
	}

	public void shouldEqualIfSameId() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId());

		assertEquals(s1, s2);
	}

	public void shouldNotEqualIfDifferentId() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(TestHelper.newID());

		assertEquals(false, s1 == s2);
		assertTrue(s1 != s2);
	}

	public void shouldNotEqualIfDifferentUpdates() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId()).update("jmt", new Date());

		assertEquals(false, s1 == s2);
		assertTrue(s1 != s2);
	}

	public void shouldNotEqualIfDifferentDeleted() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId());
		s2.setDeleted(true);

		assertEquals(false, s1 == s2);
		assertTrue(s1 != s2);
	}

	public void shouldNotEqualIfDifferentNoConflicts() {
		Sync s1 = new Sync(TestHelper.newID());
		s1.markWithConflicts();
		Sync s2 = new Sync(s1.getId());
		s2.markWithoutConflicts();

		assertEquals(false, s1 == s2);
		assertTrue(s1 != s2);
	}

	public void shouldEqualIfEqualHistory() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = s1.clone();

		Date when = TestHelper.now();
		s1.update("foo", when);
		s2.update("foo", when);

		assertEquals(s1, s2);
	}

	public void shouldNotEqualIfDifferentHistory() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = s1.clone();

		Date when = TestHelper.now();
		s1.update("kzu", when);
		s2.update("vga", when);

		assertEquals(false, s1 == s2);
		assertTrue(s1 != s2);
	}
	
	public void shouldNoPurgue(){
		Sync sync = new Sync("1", "jmt", TestHelper.now(), true);
		
		assertNotNull(sync.getLastUpdate());
		assertEquals(1, sync.getUpdates());
		
		sync.purgue();
		
		assertNotNull(sync.getLastUpdate());
		assertEquals(1, sync.getUpdates());
	}

	public void shouldPurgue(){
		Sync sync = new Sync("1", "jmt", TestHelper.now(), false);
		sync.update("jmt1", TestHelper.now());
		sync.update("jmt2", TestHelper.now());
		
		History history = sync.getLastUpdate();
		assertNotNull(history);
		assertEquals(3, sync.getUpdates());
		
		sync.purgue();
		
		assertEquals(1, sync.getUpdates());			
		assertNotNull(sync.getLastUpdate());
		assertSame(history, sync.getLastUpdate());
	
	}
}
