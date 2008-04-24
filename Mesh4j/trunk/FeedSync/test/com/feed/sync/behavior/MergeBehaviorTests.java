package com.feed.sync.behavior;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.ItemMergeResult;
import com.feed.sync.MergeOperation;
import com.feed.sync.behavior.Behaviors;
import com.feed.sync.model.Item;
import com.feed.sync.model.Sync;
import com.feed.sync.utils.test.TestHelper;
import com.feed.sync.utils.test.XmlItem;

public class MergeBehaviorTests {

	@Test
	public void ShouldWinLatestUpdateWithoutConflicts() {
		Sync sa = getBehaviors().create(TestHelper.newID(), "kzu", TestHelper.now(),
				false);
		Sync sb = sa.clone();

		sb = getBehaviors().update(sb, "vga", TestHelper.nowAddSeconds(5), false);

		Item originalItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		Item incomingItem = new Item(new XmlItem(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);

		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Updated, result.getOperation());
		Assert
				.assertEquals("b", result.getProposed().getModelItem()
						.getTitle());
		Assert.assertEquals("vga", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void ShouldNoOpForEqualItem() {
		Sync sa = getBehaviors().create(TestHelper.newID(), "kzu", TestHelper.now(),
				false);
		Sync sb = sa.clone();

		Item originalItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);
		Item incomingItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sb);

		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.None, result.getOperation());
	}

	@Test
	public void ShouldAddWithoutConflicts() {
		Sync sa = new Sync(TestHelper.newID());
		getBehaviors().update(sa, "kzu", TestHelper.now(), false);

		Item incomingItem = new Item(new XmlItem(TestHelper.newID(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);
		ItemMergeResult result = getBehaviors().merge(null, incomingItem);

		Assert.assertEquals(MergeOperation.Added, result.getOperation());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfSyncNoHistory() {
		Sync sa = new Sync(TestHelper.newID());

		Sync sb = sa.clone();

		Item originalItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);
		// TODO (?): originalItem.getSync().ItemHash =
		// originalItem.getModelItem().GetHashCode();

		Item incomingItem = new Item(new XmlItem(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);
		getBehaviors().merge(originalItem, incomingItem);
	}

	@Test
	public void ShouldWinLatestUpdateWithConflicts() {
		Sync sa = getBehaviors().create(TestHelper.newID(), "kzu", TestHelper
				.nowSubtractSeconds(10), false);

		Sync sb = sa.clone();
		sb = getBehaviors().update(sb, "vga", TestHelper.nowAddSeconds(50), false);
		sa = getBehaviors().update(sa, "kzu", TestHelper.nowAddSeconds(100), false);

		Item originalItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);
		// TODO (?): originalItem.getSync().ItemHash =
		// originalItem.getModelItem().GetHashCode();

		Item incomingItem = new Item(new XmlItem(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);
		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert
				.assertEquals("a", result.getProposed().getModelItem()
						.getTitle());
		Assert.assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
	}

	@Test
	public void ShouldWinLatestUpdateWithConflictsPreserved() {
		Sync sa = new Sync(TestHelper.newID());
		sa = getBehaviors().update(sa, "kzu", TestHelper.now(), false);

		Sync sb = sa.clone();

		sb = getBehaviors().update(sb, "vga", TestHelper.nowAddSeconds(50), false);
		sa = getBehaviors().update(sa, "kzu", TestHelper.nowAddSeconds(100), false);

		Item originalItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		Item incomingItem = new Item(new XmlItem(sb.getId(), "b", "b",
				TestHelper.makeElement("<payload/>")), sb);

		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);
		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert
				.assertEquals("a", result.getProposed().getModelItem()
						.getTitle());
		Assert.assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());

		// Merge the winner with conflict with the local no-conflict one.
		// Should be an update.
		result = getBehaviors().merge(originalItem, result.getProposed());

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert
				.assertEquals("a", result.getProposed().getModelItem()
						.getTitle());
		Assert.assertEquals("kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
	}

	@Test
	public void ShouldMergeNoneIfEqualItem() {
		Date now = TestHelper.now();
		Sync sa = getBehaviors().create(TestHelper.newID(), "kzu", now, false);
		Sync sb = getBehaviors().update(sa, "kzu", now, false);

		Item originalItem = new Item(new XmlItem(sb.getId(), "a", "a",
				TestHelper.makeElement("<payload/>")), sa);

		getBehaviors().merge(originalItem, originalItem);

	}

	private Behaviors getBehaviors() {
		return Behaviors.INSTANCE;
	}
}
