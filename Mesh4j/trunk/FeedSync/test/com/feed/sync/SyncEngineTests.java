package com.feed.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.ItemMergeResult;
import com.feed.sync.Predicate;
import com.feed.sync.PreviewBehavior;
import com.feed.sync.PreviewImportHandler;
import com.feed.sync.Repository;
import com.feed.sync.SyncEngine;
import com.feed.sync.behavior.Behaviors;
import com.feed.sync.model.History;
import com.feed.sync.model.Item;
import com.feed.sync.model.Sync;
import com.feed.sync.observer.ItemObserver;
import com.feed.sync.predicate.DeletedPredicate;
import com.feed.sync.utils.test.MockRepository;
import com.feed.sync.utils.test.TestHelper;
import com.feed.sync.utils.test.XmlItem;

public class SyncEngineTests {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfNullLeftRepo() {
		new SyncEngine(null, new MockRepository());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfNullRightRepo() {
		new SyncEngine(new MockRepository(), null);
	}

	@Test
	public void ShouldAddNewItems() {
		MockRepository left = new MockRepository(createItem("fizz", TestHelper
				.newID(), new History("kzu")));
		MockRepository right = new MockRepository(createItem("buzz", TestHelper
				.newID(), new History("vga")));

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize();

		Assert.assertEquals(0, conflicts.size());
		Assert.assertEquals(2, left.getItems().size());
		Assert.assertEquals(2, right.getItems().size());
	}

	@Test
	public void ShouldMergeChangesBothWays() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu"));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga"));

		MockRepository left = new MockRepository(new Item[] {
				new Item(a.getModelItem(), a.getSync().update("kzu",
						TestHelper.now())), b });

		MockRepository right = new MockRepository(new Item[] {
				a,
				new Item(b.getModelItem(), b.getSync().update("vga",
						TestHelper.now())) });

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize();

		Assert.assertEquals(0, conflicts.size());
		Assert.assertEquals(2, right.getItems().get(a.getSync().getId())
				.getSync().getUpdates());
		Assert.assertEquals(2, left.getItems().get(b.getSync().getId())
				.getSync().getUpdates());
	}

	@Test
	public void ShouldMarkItemDeleted() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu"));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga"));

		MockRepository left = new MockRepository(new Item[] { a, b });
		MockRepository right = new MockRepository(new Item[] {
				a,
				new Item(b.getModelItem(), b.getSync().update("vga",
						TestHelper.now(), true)) });

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize();

		Assert.assertEquals(0, conflicts.size());

		List<Item> deletedItems = left.getAll(new DeletedPredicate<Item>());
		Assert.assertEquals(1, deletedItems.size());
	}

	@Test
	public void ShouldSynchronizeSince() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu",
				TestHelper.nowSubtractDays(1)));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga",
				TestHelper.nowSubtractDays(1)));

		MockRepository left = new MockRepository(a);
		MockRepository right = new MockRepository(b);

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize(TestHelper.now());

		Assert.assertEquals(0, conflicts.size());
		Assert.assertEquals(1, left.getItems().size());
		Assert.assertEquals(1, right.getItems().size());
	}

	@Test
	public void ShouldGenerateConflict() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu"));
		TestHelper.sleep(1000);

		MockRepository left = new MockRepository(new Item(a.getModelItem(), a
				.getSync().update("kzu", TestHelper.now())));
		TestHelper.sleep(1000);

		MockRepository right = new MockRepository(new Item(a.getModelItem(), a
				.getSync().update("vga", TestHelper.now())));

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize();

		Assert.assertEquals(1, conflicts.size());
		Assert.assertEquals(1, left.getItems().get(a.getSyncId()).getSync()
				.getConflicts().size());
		Assert.assertEquals(1, right.getItems().get(a.getSyncId()).getSync()
				.getConflicts().size());
	}

	@Test
	public void ShouldImportUpdateWithConflictLeft() {
		MockRepository left = new MockRepository();
		MockRepository right = new MockRepository();
		String by = left.getCurrentAuthor();
		SyncEngine engine = new SyncEngine(left, right);

		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, by, TestHelper.nowSubtractMinutes(2),
				false);
		Item item = new Item(new XmlItem(id, "foo", "bar", TestHelper
				.makeElement("<foo id='bar'/>")), sync);

		left.add(item);
		right.add(item);

		Item incomingItem = item.clone();

		// Local editing.
		item = new Item(new XmlItem(id, "changed", item.getModelItem()
				.getDescription(), (Element)item.getModelItem().getPayload()), getBehaviors()
				.update(item.getSync(), by, TestHelper.nowSubtractMinutes(1),
						false));

		left.update(item);

		// Conflicting remote editing.
		incomingItem = new Item(new XmlItem(id, "remote", item.getModelItem()
				.getDescription(), (Element)item.getModelItem().getPayload()), getBehaviors()
				.update(incomingItem.getSync(), "REMOTE\\kzu",
						TestHelper.now(), false));

		right.update(incomingItem);

		List<Item> conflicts = engine.synchronize();

		Assert.assertEquals(1, conflicts.size());
		Assert.assertEquals(1, left.getAll().size());
		Assert.assertEquals("remote", left.get(id).getModelItem().getTitle());
		Assert.assertEquals("REMOTE\\kzu", left.get(id).getSync()
				.getLastUpdate().getBy());

		Assert.assertEquals(1, left.getConflicts().size());
		Assert.assertEquals(1, right.getConflicts().size());
	}

	@Test
	public void ShouldCallMergeIfRepositorySupportsIt() {
		MockMergeRepository left = new MockMergeRepository();
		MockMergeRepository right = new MockMergeRepository();
		SyncEngine engine = new SyncEngine(left, right);

		engine.synchronize();

		Assert.assertTrue(left.mergeCalled());
		Assert.assertTrue(right.mergeCalled());
	}

	@Test
	public void ShouldCallImportPreviewHandler() {

		MockPreviewImportHandler previewHandler = new MockPreviewImportHandler();

		SyncEngine engine = new SyncEngine(new MockRepository("left"),
				new MockRepository("right"));
		engine.synchronize(previewHandler, PreviewBehavior.Left);
		Assert.assertTrue(previewHandler.previewWasCalled("left"));
		Assert.assertFalse(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.synchronize(previewHandler, PreviewBehavior.Right);
		Assert.assertFalse(previewHandler.previewWasCalled("left"));
		Assert.assertTrue(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.synchronize(previewHandler, PreviewBehavior.Both);
		Assert.assertTrue(previewHandler.previewWasCalled("left"));
		Assert.assertTrue(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.synchronize(previewHandler, PreviewBehavior.None);
		Assert.assertFalse(previewHandler.previewWasCalled("left"));
		Assert.assertFalse(previewHandler.previewWasCalled("right"));

	}

	@Test
	public void ShouldReportImportProgress() {
		MockRepository left = new MockRepository();
		MockRepository right = new MockRepository();
		SyncEngine engine = new SyncEngine(left, right);
		String by = left.getCurrentAuthor();

		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, by, TestHelper.nowSubtractMinutes(2),
				false);
		Item item = new Item(new XmlItem(id, "foo", "bar", TestHelper
				.makeElement("<foo id='bar'/>")), sync);

		left.add(item);

		id = TestHelper.newID();
		sync = getBehaviors()
				.create(id, by, TestHelper.nowSubtractMinutes(2), false);
		item = new Item(new XmlItem(id, "foo", "bar", TestHelper
				.makeElement("<foo id='bar'/>")), sync);

		right.add(item);

		MockItemReceivedObserver itemReceivedObserver = new MockItemReceivedObserver();
		MockItemSentObserver itemSenObserver = new MockItemSentObserver();

		engine.addItemReceivedObserver(itemReceivedObserver);
		engine.addItemSentObserver(itemSenObserver);

		engine.synchronize();

		Assert.assertEquals(2, left.getItems().size());
		Assert.assertEquals(2, right.getItems().size());

		// Receives the item that was sent first plus the existing remote one.
		Assert.assertEquals(2, itemReceivedObserver.getNumberOfreceivedItems());
		Assert.assertEquals(1, itemSenObserver.getNumberOfSentItems());
	}

	@Test
	public void ShouldNotSendReceivedItemIfModifiedBeforeSince() {
		
		MockRepository left = new MockRepository();
		MockRepository right = new MockRepository();
		SyncEngine engine = new SyncEngine(left, right);

		Date nowSubtract2Minutes = TestHelper.nowSubtractMinutes(2);
		Date nowSubtract5Minutes = TestHelper.nowSubtractMinutes(5);
		Date nowSubtract2Days = TestHelper.nowSubtractDays(2);
		
		Assert.assertTrue(nowSubtract5Minutes.before(nowSubtract2Minutes));
		Assert.assertTrue(nowSubtract2Days.before(nowSubtract5Minutes));
		
		String by = "jmt";
			
		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, by, nowSubtract2Minutes, false);
		Item item = new Item(new XmlItem(id, "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), sync);
		left.add(item);

		id = TestHelper.newID();
		sync = getBehaviors().create(id, by, nowSubtract2Days, false);
		item = new Item(new XmlItem(id, "foo", "bar", TestHelper.makeElement("<foo id='bar'/>")), sync);
		right.add(item);

		MockItemReceivedObserver itemReceivedObserver = new MockItemReceivedObserver();
		MockItemSentObserver itemSenObserver = new MockItemSentObserver();

		engine.addItemReceivedObserver(itemReceivedObserver);
		engine.addItemSentObserver(itemSenObserver);

		engine.synchronize(nowSubtract5Minutes);

		// No new item would have been received from target as it was
		// modified in the past.
		Assert.assertEquals(1, left.getItems().size());
		// Local item was sent.
		Assert.assertEquals(2, right.getItems().size());
		// We would have received the same item we sent, as we're first
		// sending and then receiving.
		Assert.assertEquals(1, itemReceivedObserver.getNumberOfreceivedItems());
		Assert.assertEquals(1, itemSenObserver.getNumberOfSentItems());
	}

	private Item createItem(String title, String id, History history) {
		return createItem(title, id, history, new History[0]);
	}

	private Item createItem(String title, String id, History history,
			History[] otherHistory) {
		XmlItem xml = new XmlItem(TestHelper.newID(), title, null, TestHelper
				.makeElement("<payload/>"));
		Sync sync = getBehaviors().create(id, history.getBy(), history.getWhen(),
				false);
		for (History h : otherHistory) {
			sync = sync.update(h.getBy(), h.getWhen());
		}

		return new Item(xml, sync);
	}

	private Behaviors getBehaviors() {
		return Behaviors.INSTANCE;
	}

	private class MockMergeRepository implements Repository {

		private boolean mergeCalled;

		public String getFriendlyName() {
			return "MockMerge";
		}

		public boolean supportsMerge() {
			return true;
		}

		public Item get(String id) {
			return null;
		}

		public List<Item> getAll() {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since) {
			return new ArrayList<Item>();
		}

		public List<Item> getConflicts() {
			return new ArrayList<Item>();
		}

		public void delete(String id) {
		}

		public void update(Item item) {
		}

		public void update(Item item, boolean resolveConflicts) {
		}

		public List<Item> merge(List<Item> items) {
			mergeCalled = true;
			return new ArrayList<Item>();
		}

		public void add(Item item) {
		}

		public List<Item> getAll(Predicate<Item> filter) {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since, Predicate<Item> filter) {
			return new ArrayList<Item>();
		}

		public boolean mergeCalled() {
			return mergeCalled;
		}
	}

	private class MockPreviewImportHandler implements PreviewImportHandler {

		private Set<String> repositories = new HashSet<String>();

		@Override
		public List<ItemMergeResult> preview(Repository targetRepository,
				List<ItemMergeResult> mergedItems) {

			repositories.add(targetRepository.getFriendlyName());
			return mergedItems;
		}

		public boolean previewWasCalled(String repositoryFriendlyName) {
			return this.repositories.contains(repositoryFriendlyName);
		}

		public void reset() {
			this.repositories.clear();
		}

	}

	private class MockItemReceivedObserver implements ItemObserver {

		private int received = 0;

		public void notifyItemNovelty(Item item) {
			this.received++;
		}

		public int getNumberOfreceivedItems() {
			return received;
		}
	}

	private class MockItemSentObserver implements ItemObserver {

		private int sent = 0;

		public void notifyItemNovelty(Item item) {
			this.sent++;
		}

		public int getNumberOfSentItems() {
			return sent;
		}
	}
}
