package com.mesh4j.sync.merge;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.feed.ItemXMLContent;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.TestHelper;

public class BehaviorTests {

	@Test(expected = IllegalArgumentException.class)
	public void mergeShouldThrowIfIncomingItemNull()  {
		MergeBehavior.merge(new Item(new NullContent("1"), new Sync("1")), null);
	}

	@Test
	public void mergeShouldNotThrowIfOriginalItemNull()  {
		MergeBehavior.merge(null, new Item(new NullContent("1"), new Sync("1")));
	}

	@Test
	public void mergeShouldAddWithoutConflict()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.now(), false);
		Content modelItem = new ItemXMLContent(sync.getId(), "foo", "bar",
				TestHelper.makeElement("<foo id='bar'/>"));
		Item remoteItem = new Item(modelItem, sync);
		MergeResult result = MergeBehavior.merge(null, remoteItem);
		Assert.assertEquals(MergeOperation.Added, result.getOperation());
		Assert.assertNotNull(result.getProposed());
	}

	@Test
	public void mergeShouldUpdateWithoutConflict()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes(1), false);
		Item originalItem = new Item(new ItemXMLContent(sync.getId(), "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), sync);

		// Simulate editing.
		sync = originalItem.getSync().clone().update("REMOTE\\kzu", TestHelper.now(), false);
		Item incomingItem = new Item(new ItemXMLContent(sync.getId(), "changed", ((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent)originalItem.getContent()).getPayload()), sync);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Updated, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		Assert.assertEquals("changed", ((ItemXMLContent)result.getProposed().getContent())
				.getTitle());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldDeleteWithoutConflict()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes(1), false);
		Item originalItem = new Item(new ItemXMLContent(sync.getId(), "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), sync);

		// Simulate editing.
		Sync incomingSync = sync.clone().update("REMOTE\\kzu", TestHelper.now(), true);
		Item incomingItem = new Item(originalItem.getContent(), incomingSync);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Updated, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		Assert.assertEquals(true, result.getProposed().getSync().isDeleted());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldConflictOnDeleteWithConflict()  {
		Sync localSync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes(2), false);
		Item originalItem = new Item(new ItemXMLContent(localSync.getId(), "foo",
				"bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		Item incomingItem = originalItem.clone();

		// Local editing.
		localSync = originalItem.getSync().update("mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		originalItem = new Item(new ItemXMLContent(localSync.getId(), "changed",
				((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent) originalItem
						.getContent()).getPayload()), localSync);

		// Remote editing.
		Sync remoteSync = incomingItem.getSync().update("REMOTE\\kzu", TestHelper.now(), false);
		remoteSync.setDeleted(true);
		incomingItem = new Item(incomingItem.getContent(), remoteSync);

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.Conflict, result.getOperation());
		Assert.assertNotNull(result.getProposed());
		Assert.assertEquals(true, result.getProposed().getSync().isDeleted());
		Assert.assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	@Test
	public void mergeShouldNoOpWithNoChanges()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.now(), false);
		Item item = new Item(new ItemXMLContent(sync.getId(), "foo", "bar", TestHelper
				.makeElement("<foo id='bar'/>")), sync);

		// Do a merge with the same item.
		MergeResult result = MergeBehavior.merge(item, item);

		Assert.assertEquals(MergeOperation.None, result.getOperation());
		Assert.assertNull(result.getProposed());
	}

	@Test
	public void mergeShouldNoOpOnUpdatedLocalItemWithUnchangedIncoming()
			 {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		Item originalItem = new Item(new ItemXMLContent(sync.getId(), "foo", "bar",
				TestHelper.makeElement("<foo id='bar'/>")), sync);

		Item incomingItem = originalItem.clone();

		// Simulate editing.
		sync = originalItem.getSync().update("mypc\\user", TestHelper.now(), false);
		originalItem = new Item(new ItemXMLContent(sync.getId(), "changed",
				((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent)originalItem
						.getContent()).getPayload()), sync);

		// Merge with the older incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		Assert.assertEquals(MergeOperation.None, result.getOperation());
		Assert.assertNull(result.getProposed());
	}

	@Test
	public void mergeShouldIncomingWinWithConflict()  {
		Sync localSync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((2)), false);
		Item originalItem = new Item(new ItemXMLContent(localSync.getId(), "foo",
				"bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		Item incomingItem = originalItem.clone();

		// Local editing.
		localSync = originalItem.getSync().update("mypc\\user", TestHelper.nowSubtractMinutes((1)), false);
		originalItem = new Item(new ItemXMLContent(localSync.getId(), "changed",
				((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent)originalItem
						.getContent()).getPayload()), localSync);

		// Remote editing.
		Sync remoteSync = incomingItem.getSync().update("REMOTE\\kzu", TestHelper.now(), false);
		incomingItem = new Item(new ItemXMLContent(localSync.getId(), "changed2",
				((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent)originalItem
						.getContent()).getPayload()), remoteSync);

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

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
		Sync localSync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes((2)), false);
		Item originalItem = new Item(new ItemXMLContent(localSync.getId(), "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		// Remote editing.
		Sync remoteSync = localSync.clone().update("REMOTE\\kzu", TestHelper.nowSubtractMinutes((1)), false);
		Item incomingItem = new Item(new ItemXMLContent(localSync.getId(), "changed2",
				((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent)originalItem
						.getContent()).getPayload()), remoteSync);

		// Local editing.
		localSync.update("mypc\\user", TestHelper.now(), false);
		originalItem = new Item(new ItemXMLContent(localSync.getId(), "changed",
				((ItemXMLContent)originalItem.getContent()).getDescription(), ((ItemXMLContent)originalItem
						.getContent()).getPayload()), localSync);

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

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
		Sync localSync = new Sync(TestHelper.newID(), by, TestHelper.nowSubtractMinutes((3)), false);
		Item originalItem = new Item(new ItemXMLContent(localSync.getId(), "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), localSync);

		// Remote editing.
		Sync remoteSync = localSync.clone().update("REMOTE\\kzu", TestHelper.nowSubtractMinutes((1)), false);
		Item incomingItem = new Item(new ItemXMLContent(remoteSync.getId(), "changed2", "changed233", TestHelper.makeElement("<foo id='barwqeqq'/>")), remoteSync);

		localSync.delete(by, TestHelper.now());

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		
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

//	@Test
//	public void updateShouldNotModifyArgument() {
//		Sync sync = new Sync(TestHelper.newID());
//		Sync expected = sync.update("foo", null, false);
//		Sync updated = sync.update("bar", null, false);
//
//		Assert.assertEquals("foo", expected.getLastUpdate().getBy());
//		Assert.assertNotSame(expected, updated);
//		Assert.assertEquals("bar", updated.getLastUpdate().getBy());
//	}
	
	@Test
	public void updateShouldModifySync() {
		Sync sync = new Sync(TestHelper.newID())
			.update("foo", null, false)
			.update("bar", null, false);

		Assert.assertEquals("bar", sync.getLastUpdate().getBy());
	}

	@Test
	public void updateShouldIncrementUpdatesByOne() {
		Sync sync = new Sync(TestHelper.newID());

		int original = sync.getUpdates();

		sync.update("foo", TestHelper.now(), false);

		Assert.assertEquals(original + 1, sync.getUpdates());
	}

	@Test
	public void updateShouldAddTopmostHistory() {
		Sync sync = new Sync(TestHelper.newID())
			.update("foo", TestHelper.now(), false)
			.update("bar", TestHelper.now(), false);

		Assert.assertEquals("bar", sync.getUpdatesHistory().pop()
				.getBy());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldThrowExceptionIfIdNull()  {
		new Sync(null, "mypc\\user", TestHelper.now(), true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldThrowExceptionIfIdEmpty()  {
		new Sync("", "mypc\\user", TestHelper.now(), true);
	}

	@Test
	public void createShouldNotThrowIfNullByWithWhen()  {
		new Sync(TestHelper.newID(), null, TestHelper.now(), true);
	}

	@Test
	public void createShouldNotThrowIfNullWhenWithBy()  {
		new Sync(TestHelper.newID(), "foo", null, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldThrowIfNullWhenAndBy()  {
		new Sync(TestHelper.newID(), null, null, true);
	}

	@Test
	public void createShouldReturnSyncWithId()  {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		Assert.assertEquals(id, sync.getId());
	}

	@Test
	public void createShouldReturnSyncWithUpdatesEqualsToOne()  {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		Assert.assertEquals(1, sync.getUpdates());
	}

	@Test
	public void createShouldHaveAHistory()  {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		List<History> histories = new ArrayList<History>(sync
				.getUpdatesHistory());
		Assert.assertEquals(1, histories.size());
	}

	@Test
	public void createShouldHaveHistorySequenceSameAsUpdateCount()
			 {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		History history = new ArrayList<History>(sync.getUpdatesHistory())
				.get(0);
		Assert.assertEquals(sync.getUpdates(), history.getSequence());
	}

	@Test
	public void createShouldHaveHistoryWhenEqualsTonow()  {
		String id = TestHelper.newID();
		Date time = TestHelper.now();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		History history = new ArrayList<History>(sync.getUpdatesHistory())
				.get(0);
		Assert.assertEquals(time.toString(), history.getWhen().toString());
	}

//	@Test(expected = IllegalArgumentException.class)
//	public void shouldThrowIfSyncNull()  {
//		getBehaviors().delete(null, "mypc\\user", TestHelper.now());
//	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIfByNull() {
		Sync sync = new Sync(TestHelper.newID());
		sync.delete(null, TestHelper.now());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIfWhenParameterNull() {
		Sync sync = new Sync(TestHelper.newID());
		sync.delete("mypc\\user", null);
	}

	@Test
	public void shouldIncrementUpdatesByOneOnDeletion() {
		Sync sync = new Sync(TestHelper.newID());
		int updates = sync.getUpdates();
		sync.delete("mypc\\user", TestHelper.now());
		Assert.assertEquals(updates + 1, sync.getUpdates());
	}

	@Test
	public void shouldDeletionAttributeBeTrue() {
		Sync sync = new Sync(TestHelper.newID());
		sync.delete("mypc\\user", TestHelper.now());
		Assert.assertEquals(true, sync.isDeleted());
	}

	@Test
	public void resolveShouldNotUpdateArgument() {
		Item item = new Item(new ItemXMLContent(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), new Sync(
				TestHelper.newID(), "one", TestHelper.now(), false));

		Item resolved = MergeBehavior.resolveConflicts(item, "two", TestHelper.now(), false);

		Assert.assertNotSame(item, resolved);
	}

	@Test
	public void resolveShouldUpdateEvenIfNoConflicts() {
		Item item = new Item(new ItemXMLContent(TestHelper.newID(), "foo", "bar",
				TestHelper.makeElement("<payload/>")), new Sync(
				TestHelper.newID(), "one", TestHelper.now(), false));

		Item resolved = MergeBehavior.resolveConflicts(item, "two", TestHelper.now(), false);

		Assert.assertNotSame(item, resolved);
		Assert.assertEquals(2, resolved.getSync().getUpdates());
		Assert.assertEquals("two", resolved.getSync().getLastUpdate().getBy());
	}

	@Test
	public void resolveShouldAddConflictItemHistoryWithoutIncrementingUpdates() {
		ItemXMLContent xml = new ItemXMLContent(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload/>"));
		Sync sync = new Sync(TestHelper.newID(), "one", TestHelper
				.nowSubtractMinutes((10)), false);
		Sync conflictSync = new Sync(sync.getId(), "two", TestHelper
				.nowSubtractHours(1), false);
		sync.getConflicts().add(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = MergeBehavior.resolveConflicts(conflicItem, "one", TestHelper.now(), false);

		Assert.assertEquals(2, resolvedItem.getSync().getUpdates());
		Assert.assertEquals(3, resolvedItem.getSync().getUpdatesHistory()
				.size());
	}

	@Test
	public void resolveShouldRemoveConflicts() {
		ItemXMLContent xml = new ItemXMLContent(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload/>"));
		Sync sync = new Sync(TestHelper.newID(), "one", TestHelper
				.nowSubtractMinutes(10), false);
		Sync conflictSync = new Sync(sync.getId(), "two", TestHelper
				.nowSubtractHours(1), false);
		sync.getConflicts().add(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = MergeBehavior.resolveConflicts(conflicItem, "one", TestHelper.now(), false);

		Assert.assertEquals(0, resolvedItem.getSync().getConflicts().size());
	}

	@Test
	public void resolveShouldNotAddConflictItemHistoryIfSubsumed()
			 {
		ItemXMLContent xml = new ItemXMLContent(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload/>"));
		Sync sync = new Sync(TestHelper.newID(), "one", TestHelper.now(), false);
		Sync conflictSync = sync.clone();
		
		// Add subsuming update
		sync.update("one", TestHelper.nowAddDays(1), false);

		conflictSync.update("two", TestHelper.nowAddMinutes(5), false);

		sync.getConflicts().add(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = MergeBehavior.resolveConflicts(conflicItem, "one",
				TestHelper.now(), false);

		Assert.assertEquals(3, resolvedItem.getSync().getUpdates());
		// there would otherwise be 3 updates to the original item + 2
		// on the conflict.
		Assert.assertEquals(4, resolvedItem.getSync().getUpdatesHistory()
				.size());
	}
}