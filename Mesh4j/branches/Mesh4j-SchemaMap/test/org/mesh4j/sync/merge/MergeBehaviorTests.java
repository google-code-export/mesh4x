package org.mesh4j.sync.merge;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.validations.MeshException;


public class MergeBehaviorTests {

	@Test
	public void ShouldWinLatestUpdateWithoutConflicts() {
		Sync sa = new Sync(TestHelper.newID(), "kzu", TestHelper.now(),
				false);
		Sync sb = sa.clone();

		sb = sb.update("vga", TestHelper.nowAddSeconds(5), false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Updated, result.getOperation());
		Assert
				.assertEquals("b", ((XMLContent)result.getProposed().getContent())
						.getTitle());
		Assert.assertEquals("vga", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void ShouldNoOpForEqualItem() {
		Sync sa = new Sync(TestHelper.newID(), "kzu", TestHelper.now(),
				false);
		Sync sb = sa.clone();

		Item originalItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);
		Item incomingItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sb);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.None, result.getOperation());
	}

	@Test
	public void ShouldAddWithoutConflicts() {
		Sync sa = new Sync(TestHelper.newID());
		sa.update("kzu", TestHelper.now(), false);

		Item incomingItem = new Item(new XMLContent(TestHelper.newID(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);
		MergeResult result = MergeBehavior.merge(null, incomingItem);

		Assert.assertEquals(MergeOperation.Added, result.getOperation());
	}

	@Test(expected = MeshException.class)
	public void ShouldThrowIfSyncNoHistory() {
		Sync sa = new Sync(TestHelper.newID());

		Sync sb = sa.clone();

		Item originalItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);
		MergeBehavior.merge(originalItem, incomingItem);
	}

	@Test
	public void ShouldWinLatestUpdateWithConflicts() {
		Sync sa = new Sync(TestHelper.newID(), "kzu", TestHelper
				.nowSubtractSeconds(10), false);

		Sync sb = sa.clone();
		sb = sb.update("vga", TestHelper.nowAddSeconds(50), false);
		sa = sa.update("kzu", TestHelper.nowAddSeconds(100), false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert
				.assertEquals("a", ((XMLContent)result.getProposed().getContent())
						.getTitle());
		Assert.assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
	}

	@Test
	public void ShouldWinLatestUpdateWithConflictsPreserved() {
		Sync sa = new Sync(TestHelper.newID());
		sa = sa.update("kzu", TestHelper.now(), false);

		Sync sb = sa.clone();

		sb = sb.update("vga", TestHelper.nowAddSeconds(50), false);
		sa = sa.update("kzu", TestHelper.nowAddSeconds(100), false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		Item incomingItem = new Item(new XMLContent(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert
				.assertEquals("a", ((XMLContent)result.getProposed().getContent())
						.getTitle());
		Assert.assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());

		// Merge the winner with conflict with the local no-conflict one.
		// Should be an update.
		result = MergeBehavior.merge(originalItem, result.getProposed());

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert
				.assertEquals("a", ((XMLContent)result.getProposed().getContent())
						.getTitle());
		Assert.assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
	}

	@Test
	public void ShouldMergeNoneIfEqualItem() {
		Date now = TestHelper.now();
		Sync sa = new Sync(TestHelper.newID(), "kzu", now, false);
		Sync sb = sa.update("kzu", now, false);

		Item originalItem = new Item(new XMLContent(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		MergeBehavior.merge(originalItem, originalItem);

	}
}
