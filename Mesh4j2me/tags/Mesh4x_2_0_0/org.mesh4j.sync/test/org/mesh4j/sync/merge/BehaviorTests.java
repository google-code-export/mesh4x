package org.mesh4j.sync.merge;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.util.Date;

import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;

public class BehaviorTests extends TestCase{

	public BehaviorTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public BehaviorTests(String name) {
		super(name);
	}
	
	public BehaviorTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new BehaviorTests("mergeShouldThrowIfIncomingItemNull", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldThrowIfIncomingItemNull();}}))); 
		suite.addTest((new BehaviorTests("mergeShouldNotThrowIfOriginalItemNull", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldNotThrowIfOriginalItemNull();}})));
		suite.addTest((new BehaviorTests("mergeShouldAddWithoutConflict", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldAddWithoutConflict();}})));
		suite.addTest((new BehaviorTests("mergeShouldUpdateWithoutConflict", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldUpdateWithoutConflict();}}))); 
		suite.addTest((new BehaviorTests("mergeShouldDeleteWithoutConflict", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldDeleteWithoutConflict() ;}}))); 
		suite.addTest((new BehaviorTests("mergeShouldConflictOnDeleteWithConflict", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldConflictOnDeleteWithConflict();}}))); 
		suite.addTest((new BehaviorTests("mergeShouldNoOpWithNoChanges", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldNoOpWithNoChanges();}}))); 
		suite.addTest((new BehaviorTests("mergeShouldNoOpOnUpdatedLocalItemWithUnchangedIncoming", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldNoOpOnUpdatedLocalItemWithUnchangedIncoming();}}))); 
		suite.addTest((new BehaviorTests("mergeShouldIncomingWinWithConflict", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldIncomingWinWithConflict();}}))); 
		suite.addTest((new BehaviorTests("mergeShouldLocalWinWithConflict", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldLocalWinWithConflict() ;}}))); 
		suite.addTest((new BehaviorTests("mergeShouldConflictWithDeletedLocalItem", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).mergeShouldConflictWithDeletedLocalItem();}})));  
		suite.addTest((new BehaviorTests("updateShouldModifySync", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).updateShouldModifySync() ;}}))); 
		suite.addTest((new BehaviorTests("updateShouldIncrementUpdatesByOne", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).updateShouldIncrementUpdatesByOne();}}))); 
		suite.addTest((new BehaviorTests("updateShouldAddTopmostHistory", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).updateShouldAddTopmostHistory() ;}}))); 
		suite.addTest((new BehaviorTests("createShouldThrowExceptionIfIdNull", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldThrowExceptionIfIdNull();}})));  
		suite.addTest((new BehaviorTests("createShouldThrowExceptionIfIdEmpty", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldThrowExceptionIfIdEmpty() ;}}))); 
		suite.addTest((new BehaviorTests("createShouldNotThrowIfNullByWithWhen", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc). createShouldNotThrowIfNullByWithWhen() ;}}))); 
		suite.addTest((new BehaviorTests("createShouldNotThrowIfNullWhenWithBy", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldNotThrowIfNullWhenWithBy() ;}}))); 
		suite.addTest((new BehaviorTests("createShouldThrowIfNullWhenAndBy", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldThrowIfNullWhenAndBy();}}))); 
		suite.addTest((new BehaviorTests("createShouldReturnSyncWithId", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldReturnSyncWithId() ;}}))); 
		suite.addTest((new BehaviorTests("createShouldReturnSyncWithUpdatesEqualsToOne", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldReturnSyncWithUpdatesEqualsToOne();}}))); 
		suite.addTest((new BehaviorTests("createShouldHaveAHistory", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldHaveAHistory() ;}}))); 
		suite.addTest((new BehaviorTests("createShouldHaveHistorySequenceSameAsUpdateCount", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldHaveHistorySequenceSameAsUpdateCount();}}))); 
		suite.addTest((new BehaviorTests("createShouldHaveHistoryWhenEqualsTonow", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).createShouldHaveHistoryWhenEqualsTonow() ;}}))); 
		suite.addTest((new BehaviorTests("shouldThrowIfByNull", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).shouldThrowIfByNull() ;}}))); 
		suite.addTest((new BehaviorTests("shouldThrowIfWhenParameterNull", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).shouldThrowIfWhenParameterNull();}})));  
		suite.addTest((new BehaviorTests("shouldIncrementUpdatesByOneOnDeletion", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).shouldIncrementUpdatesByOneOnDeletion();}}))); 
		suite.addTest((new BehaviorTests("shouldDeletionAttributeBeTrue", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).shouldDeletionAttributeBeTrue() ;}}))); 
		suite.addTest((new BehaviorTests("resolveShouldNotUpdateArgument", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).resolveShouldNotUpdateArgument() ;}}))); 
		suite.addTest((new BehaviorTests("resolveShouldUpdateEvenIfNoConflicts", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).resolveShouldUpdateEvenIfNoConflicts();}})));  
		suite.addTest((new BehaviorTests("resolveShouldAddConflictItemHistoryWithoutIncrementingUpdates", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).resolveShouldAddConflictItemHistoryWithoutIncrementingUpdates();}})));  
		suite.addTest((new BehaviorTests("resolveShouldRemoveConflicts", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).resolveShouldRemoveConflicts() ;}}))); 
		suite.addTest((new BehaviorTests("resolveShouldNotAddConflictItemHistoryIfSubsumed", new TestMethod(){public void run(TestCase tc){((BehaviorTests)tc).resolveShouldNotAddConflictItemHistoryIfSubsumed();}}))); 
		return suite;
	}
	
	public void mergeShouldThrowIfIncomingItemNull()  {
		try{
			MergeBehavior.merge(new Item(new NullContent("1"), new Sync("1")), null);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}
	
	public void mergeShouldNotThrowIfOriginalItemNull()  {
		MergeBehavior.merge(null, new Item(new NullContent("1"), new Sync("1")));
	}

	public void mergeShouldAddWithoutConflict()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.now(), false);
		IContent modelItem = new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar'/>");
		Item remoteItem = new Item(modelItem, sync);
		MergeResult result = MergeBehavior.merge(null, remoteItem);
		assertEquals(MergeOperation.Added, result.getOperation());
		assertNotNull(result.getProposed());
	}

	public void mergeShouldUpdateWithoutConflict()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes(1), false);
		Item originalItem = new Item(new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar'/>"), sync);

		// Simulate editing.
		sync = originalItem.getSync().clone().update("REMOTE\\kzu", TestHelper.now(), false);
		Item incomingItem = new Item(new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar333'/>"), sync);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Updated, result.getOperation());
		assertNotNull(result.getProposed());
		assertEquals("<foo id='bar333'/>", result.getProposed().getContent().getPayload());
		assertEquals("REMOTE\\kzu", result.getProposed().getSync().getLastUpdate().getBy());
	}

	public void mergeShouldDeleteWithoutConflict()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes(1), false);
		Item originalItem = new Item(new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar'/>"), sync);

		// Simulate editing.
		Sync incomingSync = sync.clone().update("REMOTE\\kzu", TestHelper.now(), true);
		Item incomingItem = new Item(originalItem.getContent(), incomingSync);

		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Updated, result.getOperation());
		assertNotNull(result.getProposed());
		assertEquals(true, result.getProposed().getSync().isDeleted());
		assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	public void mergeShouldConflictOnDeleteWithConflict()  {
		Sync localSync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes(2), false);
		Item originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bar'/>"), localSync);

		Item incomingItem = originalItem.clone();

		// Local editing.
		localSync = originalItem.getSync().update("mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bar'/>"), localSync);

		// Remote editing.
		Sync remoteSync = incomingItem.getSync().update("REMOTE\\kzu", TestHelper.now(), false);
		remoteSync.setDeleted(true);
		incomingItem = new Item(incomingItem.getContent(), remoteSync);

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertNotNull(result.getProposed());
		assertEquals(true, result.getProposed().getSync().isDeleted());
		assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
	}

	public void mergeShouldNoOpWithNoChanges()  {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.now(), false);
		Item item = new Item(new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar'/>"), sync);

		// Do a merge with the same item.
		MergeResult result = MergeBehavior.merge(item, item);

		assertEquals(MergeOperation.None, result.getOperation());
		assertNull(result.getProposed());
	}

	public void mergeShouldNoOpOnUpdatedLocalItemWithUnchangedIncoming()
			 {
		Sync sync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((1)), false);
		Item originalItem = new Item(new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar'/>"), sync);

		Item incomingItem = originalItem.clone();

		// Simulate editing.
		sync = originalItem.getSync().update("mypc\\user", TestHelper.now(), false);
		originalItem = new Item(new XMLContent(sync.getId(), "title", "desc", "", "<foo id='bar'/>"), sync);

		// Merge with the older incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.None, result.getOperation());
		assertNull(result.getProposed());
	}

	public void mergeShouldIncomingWinWithConflict()  {
		Sync localSync = new Sync(TestHelper.newID(), "mypc\\user",
				TestHelper.nowSubtractMinutes((2)), false);
		Item originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bar'/>"), localSync);

		Item incomingItem = originalItem.clone();

		// Local editing.
		localSync = originalItem.getSync().update("mypc\\user", TestHelper.nowSubtractMinutes((1)), false);
		originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bar'/>"), localSync);

		// Remote editing.
		Sync remoteSync = incomingItem.getSync().update("REMOTE\\kzu", TestHelper.now(), false);
		incomingItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='ewwewqeweq'/>"), remoteSync);

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertNotNull(result.getProposed());
		// Remote item won
		assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getLastUpdate().getBy());
		assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
		assertEquals("mypc\\user", result.getProposed().getSync()
				.getConflicts().elementAt(0).getSync().getLastUpdate().getBy());
	}

	public void mergeShouldLocalWinWithConflict()  {
		Sync localSync = new Sync(TestHelper.newID(), "mypc\\user", TestHelper.nowSubtractMinutes((2)), false);
		Item originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bar'/>"), localSync);

		// Remote editing.
		Sync remoteSync = localSync.clone().update("REMOTE\\kzu", TestHelper.nowSubtractMinutes((1)), false);
		Item incomingItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='barfdjwbfjb'/>"), remoteSync);

		// Local editing.
		localSync.update("mypc\\user", TestHelper.now(), false);
		originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bare2343'/>"), localSync);

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);

		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertNotNull(result.getProposed());
		// Local item won
		assertEquals("mypc\\user", result.getProposed().getSync()
				.getLastUpdate().getBy());
		assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
		assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getConflicts().elementAt(0).getSync().getLastUpdate().getBy());
	}

	public void mergeShouldConflictWithDeletedLocalItem()  {
		String by = "jmt";
		Sync localSync = new Sync(TestHelper.newID(), by, TestHelper.nowSubtractMinutes((3)), false);
		Item originalItem = new Item(new XMLContent(localSync.getId(), "title", "desc", "", "<foo id='bar'/>"), localSync);

		// Remote editing.
		Sync remoteSync = localSync.clone().update("REMOTE\\kzu", TestHelper.nowSubtractMinutes((1)), false);
		Item incomingItem = new Item(new XMLContent(remoteSync.getId(), "title", "desc", "", "<foo id='barwqeqq'/>"), remoteSync);

		localSync.delete(by, TestHelper.now());

		// Merge conflicting changed incoming item.
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		
		assertEquals(MergeOperation.Conflict, result.getOperation());
		assertNotNull(result.getProposed());
		// Local item won
		assertEquals(by, result.getProposed()
				.getSync().getLastUpdate().getBy());
		assertEquals(1, result.getProposed().getSync().getConflicts()
				.size());
		assertEquals("REMOTE\\kzu", result.getProposed().getSync()
				.getConflicts().elementAt(0).getSync().getLastUpdate().getBy());
		assertTrue(result.getProposed().getSync().isDeleted());
	}

	public void updateShouldModifySync() {
		Sync sync = new Sync(TestHelper.newID())
			.update("foo", null, false)
			.update("bar", null, false);

		assertEquals("bar", sync.getLastUpdate().getBy());
	}

	public void updateShouldIncrementUpdatesByOne() {
		Sync sync = new Sync(TestHelper.newID());

		int original = sync.getUpdates();

		sync.update("foo", TestHelper.now(), false);

		assertEquals(original + 1, sync.getUpdates());
	}

	public void updateShouldAddTopmostHistory() {
		Sync sync = new Sync(TestHelper.newID())
			.update("foo", TestHelper.now(), false)
			.update("bar", TestHelper.now(), false);

		assertEquals("bar", sync.getUpdatesHistory().pop()
				.getBy());
	}

	public void createShouldThrowExceptionIfIdNull()  {
		try{
			new Sync(null, "mypc\\user", TestHelper.now(), true);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void createShouldThrowExceptionIfIdEmpty()  {
		try{
			new Sync("", "mypc\\user", TestHelper.now(), true);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void createShouldNotThrowIfNullByWithWhen()  {
		new Sync(TestHelper.newID(), null, TestHelper.now(), true);
	}

	public void createShouldNotThrowIfNullWhenWithBy()  {
		new Sync(TestHelper.newID(), "foo", null, true);
	}

	public void createShouldThrowIfNullWhenAndBy()  {
		try{
			new Sync(TestHelper.newID(), null, null, true);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void createShouldReturnSyncWithId()  {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		assertEquals(id, sync.getId());
	}

	public void createShouldReturnSyncWithUpdatesEqualsToOne()  {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		assertEquals(1, sync.getUpdates());
	}

	public void createShouldHaveAHistory()  {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		assertEquals(1, sync.getUpdatesHistory().size());
	}

	public void createShouldHaveHistorySequenceSameAsUpdateCount()
			 {
		String id = TestHelper.newID();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		History history = sync.getUpdatesHistory().elementAt(0);
		assertEquals(sync.getUpdates(), history.getSequence());
	}

	public void createShouldHaveHistoryWhenEqualsTonow()  {
		String id = TestHelper.newID();
		Date time = TestHelper.now();
		Sync sync = new Sync(id, "mypc\\user", TestHelper.now(), true);
		History history = sync.getUpdatesHistory().elementAt(0);
		assertEquals(time.toString(), history.getWhen().toString());
	}

	public void shouldThrowIfByNull() {
		try{
			Sync sync = new Sync(TestHelper.newID());
			sync.delete(null, TestHelper.now());
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void shouldThrowIfWhenParameterNull() {
		try{
			Sync sync = new Sync(TestHelper.newID());
			sync.delete("mypc\\user", null);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void shouldIncrementUpdatesByOneOnDeletion() {
		Sync sync = new Sync(TestHelper.newID());
		int updates = sync.getUpdates();
		sync.delete("mypc\\user", TestHelper.now());
		assertEquals(updates + 1, sync.getUpdates());
	}

	public void shouldDeletionAttributeBeTrue() {
		Sync sync = new Sync(TestHelper.newID());
		sync.delete("mypc\\user", TestHelper.now());
		assertEquals(true, sync.isDeleted());
	}

	public void resolveShouldNotUpdateArgument() {
		String syncId = TestHelper.newID();
		Content xml = new XMLContent(syncId, "title", "desc", "", "<payload/>");
		
		Item item = new Item(xml, new Sync(syncId, "one", TestHelper.now(), false));

		Item resolved = MergeBehavior.resolveConflicts(item, "two", TestHelper.now(), false);

		assertTrue(item != resolved);
	}

	public void resolveShouldUpdateEvenIfNoConflicts() {
		String syncId = TestHelper.newID();
		Content xml = new XMLContent(syncId, "title", "desc", "", "<payload/>");
		Item item = new Item(xml, 
				new Sync(syncId, "one", TestHelper.now(), false));

		Item resolved = MergeBehavior.resolveConflicts(item, "two", TestHelper.now(), false);

		assertTrue(item != resolved);
		assertEquals(2, resolved.getSync().getUpdates());
		assertEquals("two", resolved.getSync().getLastUpdate().getBy());
	}

	public void resolveShouldAddConflictItemHistoryWithoutIncrementingUpdates() {
		String syncId = TestHelper.newID();
		Content xml = new XMLContent(syncId, "title", "desc", "", "<payload/>");
		Sync sync = new Sync(syncId, "one", TestHelper
				.nowSubtractMinutes((10)), false);
		Sync conflictSync = new Sync(sync.getId(), "two", TestHelper
				.nowSubtractHours(1), false);
		sync.getConflicts().addElement(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = MergeBehavior.resolveConflicts(conflicItem, "one", TestHelper.now(), false);

		assertEquals(2, resolvedItem.getSync().getUpdates());
		assertEquals(3, resolvedItem.getSync().getUpdatesHistory().size());
	}

	public void resolveShouldRemoveConflicts() {
		String syncId = TestHelper.newID();
		Content xml = new XMLContent(syncId, "title", "desc", "", "<payload/>");
		Sync sync = new Sync(syncId, "one", TestHelper.nowSubtractMinutes(10), false);
		Sync conflictSync = new Sync(sync.getId(), "two", TestHelper
				.nowSubtractHours(1), false);
		sync.getConflicts().addElement(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = MergeBehavior.resolveConflicts(conflicItem, "one", TestHelper.now(), false);

		assertEquals(0, resolvedItem.getSync().getConflicts().size());
	}

	public void resolveShouldNotAddConflictItemHistoryIfSubsumed(){
		String syncId = TestHelper.newID();
		Content xml = new XMLContent(syncId, "title", "desc", "", "<payload/>");
		Sync sync = new Sync(syncId, "one", TestHelper.now(), false);
		Sync conflictSync = sync.clone();
		
		// Add subsuming update
		sync.update("one", TestHelper.nowAddDays(1), false);

		conflictSync.update("two", TestHelper.nowAddMinutes(5), false);

		sync.getConflicts().addElement(new Item(xml.clone(), conflictSync));

		Item conflicItem = new Item(xml, sync);
		Item resolvedItem = MergeBehavior.resolveConflicts(conflicItem, "one",
				TestHelper.now(), false);

		assertEquals(3, resolvedItem.getSync().getUpdates());
		// there would otherwise be 3 updates to the original item + 2
		// on the conflict.
		assertEquals(4, resolvedItem.getSync().getUpdatesHistory()
				.size());
	}
}