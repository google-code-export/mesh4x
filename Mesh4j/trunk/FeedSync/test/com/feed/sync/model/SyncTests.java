package com.feed.sync.model;

import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.model.History;
import com.feed.sync.model.Sync;
import com.feed.sync.utils.test.TestHelper;

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
		// TODO (?): Assert.assertTrue(s1 == s2);
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
		Sync s2 = new Sync(s1.getId(), 2);

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

		History history = new History("foo");
		s1.addHistory(history);
		s2.addHistory(history);

		Assert.assertEquals(s1, s2);
		// TODO (?): Assert.assertTrue(s1 == s2);
	}

	@Test
	public void ShouldNotEqualIfDifferentHistory() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = s1.clone();

		s1.addHistory(new History("kzu"));
		s2.addHistory(new History("vga"));

		Assert.assertNotSame(s1, s2);
		Assert.assertFalse(s1 == s2);
		Assert.assertTrue(s1 != s2);
	}
}
