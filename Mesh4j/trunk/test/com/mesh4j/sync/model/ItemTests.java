package com.mesh4j.sync.model;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.test.utils.TestHelper;

public class ItemTests {
	
	@Test
	public void ShouldAllowNullXmlItem() {
		// A null XML item is one with sync info and
		// no payload, typically a deleted item.
		new Item(null, new Sync(TestHelper.newID()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowNullSync() {
		new Item(new XMLContent(TestHelper.newID(), "a", "b", TestHelper
				.makeElement("<c/>")), null);
	}

	@Test
	public void ShouldEqualSameObject() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "a", "b",
				TestHelper.makeElement("<c/>")), new Sync(TestHelper.newID()));
		Item obj2 = obj1;

		this.assertEquals(obj1, obj2);
	}

	@Test
	public void ShouldNotEqualNull() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "a", "b",
				TestHelper.makeElement("<c/>")), new Sync(TestHelper.newID()));
		Item obj2 = null;

		this.assertNotEquals(obj1, obj2);
	}

	@Test
	public void ShouldNotEqualDifferentSync() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), new Sync(TestHelper.newID()));
		Item obj2 = new Item(obj1.getContent(), new Sync(TestHelper.newID()));

		this.assertNotEquals(obj1, obj2);
	}

	@Test
	public void ShouldNotEqualDifferentItem() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), new Sync(TestHelper.newID()));
		Item obj2 = new Item(new XMLContent(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload id='2'/>")), obj1.getSync());

		this.assertNotEquals(obj1, obj2);
	}

	@Test
	public void ShouldNotEqualWithOneNullXmlItem() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), new Sync(TestHelper.newID()));
		Item obj2 = new Item(null, obj1.getSync());

		this.assertNotEquals(obj1, obj2);
	}

	@Test
	public void ShouldEqualWithBothNullXmlItemAndSameSync() {
		Item obj1 = new Item(null, new Sync(TestHelper.newID()));
		Item obj2 = new Item(null, obj1.getSync());

		this.assertEquals(obj1, obj2);
	}

	@Test
	public void ShouldEqualWithEqualItemAndSync() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId());

		Item obj1 = new Item(new XMLContent(s1.getId(), "foo", "bar", TestHelper
				.makeElement("<payload/>")), s1);
		Item obj2 = new Item(new XMLContent(s1.getId(), "foo", "bar", TestHelper
				.makeElement("<payload/>")), s2);

		this.assertEquals(obj1, obj2);
	}

	@Test
	public void ShouldGetSameHashcodeWithEqualItemAndSync() {
		
		String id = TestHelper.newID();
		Sync s1 = new Sync(id);
		Sync s2 = new Sync(id);

		Item obj1 = new Item(
				new XMLContent(id, "foo", "bar", TestHelper.makeElement("<payload/>")), s1);
		Item obj2 = new Item(
				new XMLContent(id, "foo", "bar", TestHelper.makeElement("<payload/>")), s2);

		Assert.assertEquals(obj1.hashCode(), obj2.hashCode());
	}

	@Test
	public void ShouldEqualClonedItem() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "a", "b",
				TestHelper.makeElement("<c/>")), new Sync(TestHelper.newID()));
		Item obj2 = obj1.clone();

		this.assertEquals(obj1, obj2);
	}

	@Test
	public void ShouldEqualClonedCloneableItem() {
		Item obj1 = new Item(new XMLContent(TestHelper.newID(), "a", "b",
				TestHelper.makeElement("<c/>")), new Sync(TestHelper.newID()));
		Item obj2 = obj1.clone();

		this.assertEquals(obj1, obj2);
	}

	private void assertEquals(Item obj1, Item obj2) {
		Assert.assertTrue(obj1.equals(obj2));
	}

	private void assertNotEquals(Item obj1, Item obj2) {
		Assert.assertNotSame(obj1, obj2);
		Assert.assertFalse(obj1.equals(obj2));
	}
}
