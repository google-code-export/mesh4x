package org.mesh4j.sync.merge;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.util.Date;

import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.validations.MeshException;

public class MergeBehaviorTests  extends TestCase{

	public MergeBehaviorTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public MergeBehaviorTests(String name) {
		super(name);
	}
	
	public MergeBehaviorTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new MergeBehaviorTests("ShouldWinLatestUpdateWithoutConflicts", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldWinLatestUpdateWithoutConflicts();}})));
		suite.addTest((new MergeBehaviorTests("ShouldNoOpForEqualItem", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldNoOpForEqualItem();}})));
		suite.addTest((new MergeBehaviorTests("ShouldAddWithoutConflicts", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldAddWithoutConflicts();}})));
		suite.addTest((new MergeBehaviorTests("ShouldThrowIfSyncNoHistory", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldThrowIfSyncNoHistory();}})));
		suite.addTest((new MergeBehaviorTests("ShouldWinLatestUpdateWithConflicts", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldWinLatestUpdateWithConflicts();}})));
		suite.addTest((new MergeBehaviorTests("ShouldWinLatestUpdateWithConflictsPreserved", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldWinLatestUpdateWithConflictsPreserved();}})));
		suite.addTest((new MergeBehaviorTests("ShouldMergeNoneIfEqualItem", new TestMethod(){public void run(TestCase tc){((MergeBehaviorTests)tc).ShouldMergeNoneIfEqualItem();}})));
 		return suite;
	}
	
	public void ShouldWinLatestUpdateWithoutConflicts() {
		Sync sa = new Sync(TestHelper.newID(), "kzu", TestHelper.now(),
				false);
		Sync sb = sa.clone();

		sb = sb.update("vga", TestHelper.nowAddSeconds(5), false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<newPayload/>"), sb);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Updated, result.getOperation());
		assertEquals("<newPayload/>", result.getProposed().getContent().getPayload());
		assertEquals("vga", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	public void ShouldNoOpForEqualItem() {
		Sync sa = new Sync(TestHelper.newID(), "kzu", TestHelper.now(),
				false);
		Sync sb = sa.clone();

		Item originalItem = new Item(new XMLContent(sa.getId(), "title", "desc", "", "<payload/>"), sa);
		Item incomingItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sb);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.None, result.getOperation());
	}

	public void ShouldAddWithoutConflicts() {
		Sync sa = new Sync(TestHelper.newID());
		sa.update("kzu", TestHelper.now(), false);

		Item incomingItem = new Item(new XMLContent(sa.getId(), "title", "desc", "", "<payload/>"), sa);
		MergeResult result = MergeBehavior.merge(null, incomingItem);

		assertEquals(MergeOperation.Added, result.getOperation());
	}

	public void ShouldThrowIfSyncNoHistory() {
		
		Sync sa = new Sync(TestHelper.newID());

		Sync sb = sa.clone();

		Item originalItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sb);

		try{
			MergeBehavior.merge(originalItem, incomingItem);
			fail("expected MeshException");
		} catch(MeshException e){
			// right test
		}
	}

	public void ShouldWinLatestUpdateWithConflicts() {
		Sync sa = new Sync(TestHelper.newID(), "kzu", TestHelper
				.nowSubtractSeconds(10), false);

		Sync sb = sa.clone();
		sb = sb.update("vga", TestHelper.nowAddSeconds(50), false);
		sa = sa.update("kzu", TestHelper.nowAddSeconds(100), false);
		
		Item originalItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sa);
		Item incomingItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sb);
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertEquals("<payload/>", result.getProposed().getContent().getPayload());
		assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
	}

	public void ShouldWinLatestUpdateWithConflictsPreserved() {
		Sync sa = new Sync(TestHelper.newID());
		sa = sa.update("kzu", TestHelper.now(), false);

		Sync sb = sa.clone();

		sb = sb.update("vga", TestHelper.nowAddSeconds(50), false);
		sa = sa.update("kzu", TestHelper.nowAddSeconds(100), false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sb);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertEquals("<payload/>", result.getProposed().getContent().getPayload());
		assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());

		// Merge the winner with conflict with the local no-conflict one.
		// Should be an update.
		result = MergeBehavior.merge(originalItem, result.getProposed());

		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertEquals("<payload/>", result.getProposed().getContent().getPayload());
		assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
	}

	public void ShouldMergeNoneIfEqualItem() {
		Date now = TestHelper.now();
		Sync sa = new Sync(TestHelper.newID(), "kzu", now, false);
		Sync sb = sa.update("kzu", now, false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "title", "desc", "", "<payload/>"), sa);

		MergeBehavior.merge(originalItem, originalItem);

	}
}
