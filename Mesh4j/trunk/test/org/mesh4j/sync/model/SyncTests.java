package org.mesh4j.sync.model;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;


public class SyncTests {

	@Test
	public void ShouldEqualNullSyncToNull() {
		Assert.assertTrue((Sync) null == null);
	}

	@Test
	public void ShouldNotEqualNullOperator() {
		Assert.assertFalse(null == new Sync("foo"));
		Assert.assertFalse(new Sync("foo") == null);
	}

	@Test
	public void ShouldNotEqualNull() {
		Assert.assertFalse(new Sync("foo").equals((Object) null));
		Assert.assertFalse(new Sync("foo").equals((Sync) null));
	}

	@Test
	public void ShouldEqualIfSameId() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId());

		Assert.assertEquals(s1, s2);
	}

	@Test
	public void ShouldNotEqualIfDifferentId() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(TestHelper.newID());

		Assert.assertNotSame(s1, s2);
		Assert.assertFalse(s1 == s2);
		Assert.assertTrue(s1 != s2);
	}

	@Test
	public void ShouldNotEqualIfDifferentUpdates() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId()).update("jmt", new Date());

		Assert.assertNotSame(s1, s2);
		Assert.assertFalse(s1 == s2);
		Assert.assertTrue(s1 != s2);
	}

	@Test
	public void ShouldNotEqualIfDifferentDeleted() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId());
		s2.setDeleted(true);

		Assert.assertNotSame(s1, s2);
		Assert.assertFalse(s1 == s2);
		Assert.assertTrue(s1 != s2);
	}

	@Test
	public void ShouldNotEqualIfDifferentNoConflicts() {
		Sync s1 = new Sync(TestHelper.newID());
		s1.markWithConflicts();
		Sync s2 = new Sync(s1.getId());
		s2.markWithoutConflicts();

		Assert.assertNotSame(s1, s2);
		Assert.assertFalse(s1 == s2);
		Assert.assertTrue(s1 != s2);
	}

	@Test
	public void ShouldEqualIfEqualHistory() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = s1.clone();

		Date when = TestHelper.now();
		s1.update("foo", when);
		s2.update("foo", when);

		Assert.assertEquals(s1, s2);
	}

	@Test
	public void ShouldNotEqualIfDifferentHistory() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = s1.clone();

		Date when = TestHelper.now();
		s1.update("kzu", when);
		s2.update("vga", when);

		Assert.assertNotSame(s1, s2);
		Assert.assertFalse(s1 == s2);
		Assert.assertTrue(s1 != s2);
	}
	
	@Test
	public void shouldNoPurgue(){
		Sync sync = new Sync("1", "jmt", TestHelper.now(), true);
		
		Assert.assertNotNull(sync.getLastUpdate());
		Assert.assertEquals(1, sync.getUpdates());
		
		sync.purgue();
		
		Assert.assertNotNull(sync.getLastUpdate());
		Assert.assertEquals(1, sync.getUpdates());
	}

	@Test
	public void shouldPurgue(){
		Sync sync = new Sync("1", "jmt", TestHelper.now(), false);
		sync.update("jmt1", TestHelper.now());
		sync.update("jmt2", TestHelper.now());
		
		History history = sync.getLastUpdate();
		Assert.assertNotNull(history);
		Assert.assertEquals(3, sync.getUpdates());
		
		sync.purgue();
		
		Assert.assertEquals(1, sync.getUpdates());			
		Assert.assertNotNull(sync.getLastUpdate());
		Assert.assertSame(history, sync.getLastUpdate());
	
	}
}
