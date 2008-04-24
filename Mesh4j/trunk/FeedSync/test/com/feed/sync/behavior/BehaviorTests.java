package com.feed.sync.behavior;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.ItemMergeResult;
import com.feed.sync.MergeOperation;
import com.feed.sync.behavior.Behaviors;
import com.feed.sync.model.History;
import com.feed.sync.model.IModelItem;
import com.feed.sync.model.Item;
import com.feed.sync.model.NullModelItem;
import com.feed.sync.model.Sync;
import com.feed.sync.utils.test.TestHelper;
import com.feed.sync.utils.test.XmlItem;

public class BehaviorTests {

	@Test(expected = IllegalArgumentException.class)
	public void mergeShouldThrowIfIncomingItemNull()  {
		getBehaviors().merge(new Item(new NullModelItem("1"), new Sync("1")), null);
	}

	@Test
	public void mergeShouldNotThrowIfOriginalItemNull()  {
		getBehaviors().merge(null, new Item(new NullModelItem("1"), new Sync("1")));
	}

	@Test
	public void mergeShouldAddWithoutConflict()  {
		Sync sync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.now(), false);
		IModelItem modelItem = new XmlItem(sync.getId(), "foo", "bar",
				TestHelper.makeElement("<foo id='bar'/>"));
		Item remoteItem = new Item(modelItem, sync);
		ItemMergeResult result = getBehaviors().merge(null, remoteItem);
		Assert.assertEquals(MergeOperation.Added, result.getOperation());
		Assert.assertNotNull(result.getProposed());
	}

	@Test
	public void mergeShouldUpdateWithoutConflict()  {
		Sync sync = getBehaviors().create(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes(1), false);
		Item originalItem = new Item(new XmlItem(sync.getId(), "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), sync);

		// Simulate editing.
		sync = getBehaviors().update(originalItem.getSync(), "REMOTE\\kzu", TestHelper.now(), false);
		Item incomingItem = new Item(new XmlItem(sync.getId(), "changed", originalItem.getModelItem().getDescription(), (Element)originalItem.getModelItem().getPayload()), sync);

		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Updated, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		Assert.assertEquals("changed", result.getProposed().getModelItem()
				.getTitle());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldDeleteWithoutConflict()  {
		Sync sync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes(1), false);
		Item originalItem = new Item(new XmlItem(sync.getId(), "foo", "bar",
				TestHelper.makeElement("<foo id='bar'/>")), sync);

		// Simulate editing.
		sync = getBehaviors().update(originalItem.getSync(), "REMOTE\\kzu",
				TestHelper.now(), true);
		Item incomingItem = new Item(originalItem.getModelItem(), sync);

		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Updated, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		Assert.assertEquals(true, result.getProposed().getSync().isDeleted());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldConflictOnDeleteWithConflict()  {
		Sync localSync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes(2), false);
		Item originalItem = new Item(new XmlItem(localSync.getId(), "foo",
				"bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		Item incomingItem = originalItem.clone();

		// Local editing.
		localSync = getBehaviors().update(originalItem.getSync(), "mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		originalItem = new Item(new XmlItem(localSync.getId(), "changed",
				originalItem.getModelItem().getDescription(), (Element) originalItem
						.getModelItem().getPayload()), localSync);

		// Remote editing.
		Sync remoteSync = getBehaviors().update(incomingItem.getSync(),
				"REMOTE\\kzu", TestHelper.now(), false);
		remoteSync.setDeleted(true);
		incomingItem = new Item(incomingItem.getModelItem(), remoteSync);

		// Merge conflicting changed incoming item.
		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		Assert.assertEquals(true, result.getProposed().getSync().isDeleted());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldNoOpWithNoChanges()  {
		Sync sync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.now(), false);
		Item item = new Item(new XmlItem(sync.getId(), "foo", "bar", TestHelper
				.makeElement("<foo id='bar'/>")), sync);

		// Do a merge with the same item.
		ItemMergeResult result = getBehaviors().merge(item, item);

		Assert.assertEquals(MergeOperation.None, result.getOperation());
		Assert.assertNull(result.getProposed());
	}

	@Test
	public void mergeShouldNoOpOnUpdatedLocalItemWithUnchangedIncoming()
			 {
		Sync sync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		Item originalItem = new Item(new XmlItem(sync.getId(), "foo", "bar",
				TestHelper.makeElement("<foo id='bar'/>")), sync);

		Item incomingItem = originalItem.clone();

		// Simulate editing.
		sync = getBehaviors().update(originalItem.getSync(), "mypc\\user",
				TestHelper.now(), false);
		originalItem = new Item(new XmlItem(sync.getId(), "changed",
				originalItem.getModelItem().getDescription(), (Element)originalItem
						.getModelItem().getPayload()), sync);

		// Merge with the older incoming item.
		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.None, result.getOperation());
		Assert.assertNull(result.getProposed());
	}

	@Test
	public void mergeShouldIncomingWinWithConflict()  {
		Sync localSync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((2)), false);
		Item originalItem = new Item(new XmlItem(localSync.getId(), "foo",
				"bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		Item incomingItem = originalItem.clone();

		// Local editing.
		localSync = getBehaviors().update(originalItem.getSync(), "mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		originalItem = new Item(new XmlItem(localSync.getId(), "changed",
				originalItem.getModelItem().getDescription(), (Element)originalItem
						.getModelItem().getPayload()), localSync);

		// Remote editing.
		Sync remoteSync = getBehaviors().update(incomingItem.getSync(),
				"REMOTE\\kzu", TestHelper.now(), false);
		incomingItem = new Item(new XmlItem(localSync.getId(), "changed2",
				originalItem.getModelItem().getDescription(), (Element)originalItem
						.getModelItem().getPayload()), remoteSync);

		// Merge conflicting changed incoming item.
		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		// Remote item won
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
		Assert.assertEquals("mypc\\user", result.getProposed().getSync()
				.getConflicts().get(0).getSync().getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldLocalWinWithConflict()  {
		Sync localSync = getBehaviors().create(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((2)), false);
		Item originalItem = new Item(new XmlItem(localSync.getId(), "foo",
				"bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		// Remote editing.
		Sync remoteSync = getBehaviors().update(localSync, "REMOTE\\kzu", TestHelper
				.nowSubtractMinutes((1)), false);
		Item incomingItem = new Item(new XmlItem(localSync.getId(), "changed2",
				originalItem.getModelItem().getDescription(), (Element)originalItem
						.getModelItem().getPayload()), remoteSync);

		// Local editing.
		localSync = getBehaviors().update(originalItem.getSync(), "mypc\\user",
				TestHelper.now(), false);
		originalItem = new Item(new XmlItem(localSync.getId(), "changed",
				originalItem.getModelItem().getDescription(), (Element)originalItem
						.getModelItem().getPayload()), localSync);

		// Merge conflicting changed incoming item.
		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		// Local item won
		Assert.assertEquals("mypc\\user", result.getProposed().getSync()
				.getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getConflicts().get(0).getSync().getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldConflictWithDeletedLocalItem()  {
		String by = "jmt";
		Sync localSync = getBehaviors().create(TestHelper.newID(), by, TestHelper.nowSubtractMinutes((3)), false);
		Item originalItem = new Item(new XmlItem(localSync.getId(), "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		// Remote editing.
		Sync remoteSync = getBehaviors().update(localSync, "REMOTE\\kzu", TestHelper.nowSubtractMinutes((1)), false);
		Item incomingItem = new Item(new XmlItem(remoteSync.getId(), "changed2", "changed233", TestHelper.makeElement("<foo id='barwqeqq'/>")), remoteSync);

		localSync = getBehaviors().delete(localSync, by, TestHelper.now());
		originalItem = new Item(originalItem.getModelItem(), localSync);

		// Merge conflicting changed incoming item.
		ItemMergeResult result = getBehaviors().merge(originalItem, incomingItem);

		
		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		// Local item won
		Assert.assertEquals(by, result.getProposed()
				.getSync().getLastUpdate().getBy());
		Assert.assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getConflicts().get(0).getSync().getLastUpdate().getBy());
		Assert.assertTrue(result.getProposed().getSync().isDeleted());
	}

	@Test
	public void updateShouldNotModifyArgument() {
		Sync expected = getBehaviors().update(new Sync(TestHelper.newID()), "foo",
				null, false);

		Sync updated = getBehaviors().update(expected, "bar", null, false);

		Assert.assertEquals("foo", expected.getLastUpdate().getBy());
		Assert.assertNotSame(expected, updated);
		Assert.assertEquals("bar", updated.getLastUpdate().getBy());
	}

	@Test
	public void updateShouldIncrementUpdatesByOne() {
		Sync sync = new Sync(TestHelper.newID());

		int original = sync.getUpdates();

		Sync updated = getBehaviors().update(sync, "foo", TestHelper.now(), false);

		Assert.assertEquals(original + 1, updated.getUpdates());
	}

	@Test
	public void updateShouldAddTopmostHistory() {
		Sync sync = new Sync(TestHelper.newID());
		sync = getBehaviors().update(sync, "foo", TestHelper.now(), false);
		sync = getBehaviors().update(sync, "bar", TestHelper.now(), false);

		Assert.assertEquals("bar", sync.getUpdatesHistory().pop()
				.getBy());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldThrowExceptionIfIdNull()  {
		getBehaviors().create(null, "mypc\\user", TestHelper.now(), true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldThrowExceptionIfIdEmpty()  {
		getBehaviors().create("", "mypc\\user", TestHelper.now(), true);
	}

	@Test
	public void createShouldNotThrowIfNullByWithWhen()  {
		getBehaviors().create(TestHelper.newID(), null, TestHelper.now(), true);
	}

	@Test
	public void createShouldNotThrowIfNullWhenWithBy()  {
		getBehaviors().create(TestHelper.newID(), "foo", null, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldThrowIfNullWhenAndBy()  {
		getBehaviors().create(TestHelper.newID(), null, null, true);
	}

	@Test
	public void createShouldReturnSyncWithId()  {
		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, "mypc\\user", TestHelper.now(), true);
		Assert.assertEquals(id, sync.getId());
	}

	@Test
	public void createShouldReturnSyncWithUpdatesEqualsToOne()  {
		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, "mypc\\user", TestHelper.now(), true);
		Assert.assertEquals(1, sync.getUpdates());
	}

	@Test
	public void createShouldHaveAHistory()  {
		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, "mypc\\user", TestHelper.now(), true);
		List<History> histories = new ArrayList<History>(sync
				.getUpdatesHistory());
		Assert.assertEquals(1, histories.size());
	}

	@Test
	public void createShouldHaveHistorySequenceSameAsUpdateCount()
			 {
		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, "mypc\\user", TestHelper.now(), true);
		History history = new ArrayList<History>(sync.getUpdatesHistory())
				.get(0);
		Assert.assertEquals(sync.getUpdates(), history.getSequence());
	}

	@Test
	public void createShouldHaveHistoryWhenEqualsTonow()  {
		String id = TestHelper.newID();
		Date time = TestHelper.now();
		Sync sync = getBehaviors().create(id, "mypc\\user", TestHelper.now(), true);
		History history = new ArrayList<History>(sync.getUpdatesHistory())
				.get(0);
		Assert.assertEquals(time.toString(), history.getWhen().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIfSyncNull()  {
		getBehaviors().delete(null, "mypc\\user", TestHelper.now());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIfByNull() {
		getBehaviors().delete(new Sync(TestHelper.newID()), null, TestHelper.now());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIfWhenParameterNull() {
		getBehaviors().delete(new Sync(TestHelper.newID()), "mypc\\user", null);
	}

	@Test
	public void shouldIncrementUpdatesByOneOnDeletion() {
		Sync sync = new Sync(TestHelper.newID());
		int updates = sync.getUpdates();
		sync = getBehaviors().delete(sync, "mypc\\user", TestHelper.now());
		Assert.assertEquals(updates + 1, sync.getUpdates());
	}

	@Test
	public void shouldDeletionAttributeBeTrue() {
		Sync sync = new Sync(TestHelper.newID());
		sync = getBehaviors().delete(sync, "mypc\\user", TestHelper.now());
		Assert.assertEquals(true, sync.isDeleted());
	}

	@Test
	public void resolveShouldNotUpdateArgument() {
		Item item = new Item(new XmlItem(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), getBehaviors().create(
				TestHelper.newID(), "one", TestHelper.now(), false));

		Item resolved = getBehaviors().resolveConflicts(item, "two", TestHelper
				.now(), false);

		Assert.assertNotSame(item, resolved);
	}

	@Test
	public void resolveShouldUpdateEvenIfNoConflicts() {
		Item item = new Item(new XmlItem(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), getBehaviors().create(
				TestHelper.newID(), "one", TestHelper.now(), false));

		Item resolved = getBehaviors().resolveConflicts(item, "two", TestHelper
				.now(), false);

		Assert.assertNotSame(item, resolved);
		Assert.assertEquals(2, resolved.getSync().getUpdates());
		Assert.assertEquals("two", resolved.getSync().getLastUpdate().getBy());
	}

	@Test
	public void resolveShouldAddConflictItemHistoryWithoutIncrementingUpdates() {
		XmlItem xml = new XmlItem(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload/>"));
		Sync sync = getBehaviors().create(TestHelper.newID(), "one", TestHelper
				.nowSubtractMinutes((10)), false);
		Sync conflictSync = getBehaviors().create(sync.getId(), "two", TestHelper
				.nowSubtractHours(1), false);
		sync.getConflicts().add(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = getBehaviors().resolveConflicts(conflicItem, "one",
				TestHelper.now(), false);

		Assert.assertEquals(2, resolvedItem.getSync().getUpdates());
		Assert.assertEquals(3, resolvedItem.getSync().getUpdatesHistory()
				.size());
	}

	@Test
	public void resolveShouldRemoveConflicts() {
		XmlItem xml = new XmlItem(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload/>"));
		Sync sync = getBehaviors().create(TestHelper.newID(), "one", TestHelper
				.nowSubtractMinutes(10), false);
		Sync conflictSync = getBehaviors().create(sync.getId(), "two", TestHelper
				.nowSubtractHours(1), false);
		sync.getConflicts().add(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = getBehaviors().resolveConflicts(conflicItem, "one",
				TestHelper.now(), false);

		Assert.assertEquals(0, resolvedItem.getSync().getConflicts().size());
	}

	@Test
	public void resolveShouldNotAddConflictItemHistoryIfSubsumed()
			 {
		XmlItem xml = new XmlItem(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload/>"));
		Sync sync = getBehaviors().create(TestHelper.newID(), "one", TestHelper
				.now(), false);
		Sync conflictSync = sync.clone();
		// Add subsuming update
		sync = getBehaviors().update(sync, "one", TestHelper.nowAddDays(1), false);

		conflictSync = getBehaviors().update(conflictSync, "two", TestHelper
				.nowAddMinutes(5), false);

		sync.getConflicts().add(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = getBehaviors().resolveConflicts(conflicItem, "one",
				TestHelper.now(), false);

		Assert.assertEquals(3, resolvedItem.getSync().getUpdates());
		// there would otherwise be 3 updates to the original item + 2
		// on the conflict.
		Assert.assertEquals(4, resolvedItem.getSync().getUpdatesHistory()
				.size());
	}

	private Behaviors getBehaviors() {
		return Behaviors.INSTANCE;
	}
}