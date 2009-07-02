package org.mesh4j.sync.model;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.test.utils.TestHelper;


public class ItemTests  extends TestCase{

	public ItemTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public ItemTests(String name) {
		super(name);
	}
	
	public ItemTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new ItemTests("ShouldAllowNullItem", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldAllowNullItem();}})));
		suite.addTest((new ItemTests("ShouldThrowNullSync", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldThrowNullSync();}})));
		suite.addTest((new ItemTests("ShouldEqualSameObject", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldEqualSameObject();}})));
		suite.addTest((new ItemTests("ShouldNotEqualNull", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldNotEqualNull();}})));
		suite.addTest((new ItemTests("ShouldNotEqualDifferentSync", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldNotEqualDifferentSync();}})));
		suite.addTest((new ItemTests("ShouldNotEqualDifferentItem", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldNotEqualDifferentItem();}})));
		suite.addTest((new ItemTests("ShouldNotEqualWithOneNullItem", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldNotEqualWithOneNullItem();}})));
		suite.addTest((new ItemTests("ShouldEqualWithBothNullXmlItemAndSameSync", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldEqualWithBothNullXmlItemAndSameSync();}})));
		suite.addTest((new ItemTests("ShouldEqualWithEqualItemAndSync", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldEqualWithEqualItemAndSync();}})));
		suite.addTest((new ItemTests("ShouldGetSameHashcodeWithEqualItemAndSync", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldGetSameHashcodeWithEqualItemAndSync();}})));
		suite.addTest((new ItemTests("ShouldEqualClonedItem", new TestMethod(){public void run(TestCase tc){((ItemTests)tc).ShouldEqualClonedItem();}})));
		return suite;
	}
	
	public void ShouldAllowNullItem() {
		// A null XML item is one with sync info and
		// no payload, typically a deleted item.
		Item item = new Item(null, new Sync(TestHelper.newID()));
		assertEquals(item.getContent().getPayload(), NullContent.PAYLOAD);
	}

	public void ShouldThrowNullSync() {
		try{
			new Item(new XMLContent(TestHelper.newID(), "title", "desc", "", NullContent.PAYLOAD), null);
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void ShouldEqualSameObject() {
		String syncId = TestHelper.newID();
		Item obj1 = new Item(new XMLContent(syncId, "title", "desc", "", TestHelper.DEFAUT_PLACEMARK), new Sync(syncId));
		Item obj2 = obj1;
		this.assertEqualsItem(obj1, obj2);
	}

	public void ShouldNotEqualNull() {
		String syncId = TestHelper.newID();
		Item obj1 = new Item(new XMLContent(syncId, "title", "desc", "", TestHelper.DEFAUT_PLACEMARK), new Sync(syncId));
		Item obj2 = null;

		this.assertNotEqualsItem(obj1, obj2);
	}

	public void ShouldNotEqualDifferentSync() {
		String syncId = TestHelper.newID();
		Item obj1 = new Item(new XMLContent(syncId, "title", "desc", "", TestHelper.DEFAUT_PLACEMARK), new Sync(syncId));
		Item obj2 = new Item(obj1.getContent(), new Sync(TestHelper.newID()));

		this.assertNotEqualsItem(obj1, obj2);
	}

	public void ShouldNotEqualDifferentItem() {
		String syncId = TestHelper.newID();
		Item obj1 = new Item(new XMLContent(syncId, "title", "desc", "", TestHelper.DEFAUT_PLACEMARK), new Sync(syncId));
		Item obj2 = new Item(new XMLContent(syncId, "title", "desc", "", "<payload id='2'/>"), obj1.getSync());

		this.assertNotEqualsItem(obj1, obj2);
	}

	public void ShouldNotEqualWithOneNullItem() {
		String syncId = TestHelper.newID();
		Item obj1 = new Item(new XMLContent(syncId, "title", "desc", "", "<payload/>"), new Sync(syncId));
		Item obj2 = new Item(null, obj1.getSync());

		this.assertNotEqualsItem(obj1, obj2);
	}

	public void ShouldEqualWithBothNullXmlItemAndSameSync() {
		Item obj1 = new Item(null, new Sync(TestHelper.newID()));
		Item obj2 = new Item(null, obj1.getSync());

		this.assertEqualsItem(obj1, obj2);
	}

	public void ShouldEqualWithEqualItemAndSync() {
		Sync s1 = new Sync(TestHelper.newID());
		Sync s2 = new Sync(s1.getId());

		Item obj1 = new Item(new XMLContent(s1.getId(), "title", "desc", "", "<payload/>"), s1);
		Item obj2 = new Item(new XMLContent(s2.getId(), "title", "desc", "", "<payload/>"), s2);

		this.assertEqualsItem(obj1, obj2);
	}

	public void ShouldGetSameHashcodeWithEqualItemAndSync() {
		
		String syncId = TestHelper.newID();
		Sync s1 = new Sync(syncId);
		Sync s2 = new Sync(syncId);

		Item obj1 = new Item(new XMLContent(s1.getId(), "title", "desc", "", "<payload/>"), s1);
		Item obj2 = new Item(new XMLContent(s2.getId(), "title", "desc", "", "<payload/>"), s2);

		assertEquals(obj1.hashCode(), obj2.hashCode());
	}

	public void ShouldEqualClonedItem() {
		String syncId = TestHelper.newID();
		
		Item obj1 = new Item(new XMLContent(syncId, "title", "desc", "", "<c/>"), new Sync(syncId));
		Item obj2 = obj1.clone();

		this.assertEqualsItem(obj1, obj2);
	}

	private void assertEqualsItem(Item obj1, Item obj2) {
		assertTrue(obj1.equals(obj2));
	}

	private void assertNotEqualsItem(Item obj1, Item obj2) {
		assertTrue(obj1 != obj2);
		assertEquals(false, obj1.equals(obj2));
	}
}
