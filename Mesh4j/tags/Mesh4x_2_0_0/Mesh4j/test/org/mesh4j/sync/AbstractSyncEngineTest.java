package org.mesh4j.sync;

import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.filter.DeletedFilter;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.observer.IObserverItem;
import org.mesh4j.sync.test.utils.TestHelper;


public abstract class AbstractSyncEngineTest {

	@Test
	public void ShouldAddNewItems() {
		ISyncAdapter left = this.makeLeftRepository(createItem("fizz", TestHelper.newID(), new History("kzu")));
		ISyncAdapter right = this.makeRightRepository(createItem("buzz", TestHelper.newID(), new History("vga")));

		SyncEngine syncEngine = new SyncEngine(left, right);

		TestHelper.assertSync(syncEngine);
	}

	protected abstract ISyncAdapter makeLeftRepository(Item ... items);
	protected abstract ISyncAdapter makeRightRepository(Item ... items);

	@Test
	public void ShouldMergeChangesBothWays() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu"));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga"));

		ISyncAdapter left = this.makeLeftRepository(
			new Item(
				a.getContent(), 
				a.getSync().clone().update("kzu", TestHelper.now())
			), 
			b 
		);

		ISyncAdapter right = this.makeRightRepository(
			a,
			new Item(
				b.getContent(), 
				b.getSync().clone().update("vga", TestHelper.now())
			) 	
		);

		SyncEngine syncEngine = new SyncEngine(left, right);

		TestHelper.assertSync(syncEngine);
	}

	@Test
	public void ShouldMarkItemDeleted() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu"));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga"));
		Item bDeleted = new Item(new NullContent(b.getSyncId()), b.getSync().clone().update("vga", TestHelper.now(), true));
		
		ISyncAdapter left = this.makeLeftRepository(a, b);
		ISyncAdapter right = this.makeRightRepository(
				a,
				bDeleted
		);

		SyncEngine syncEngine = new SyncEngine(left, right);

		TestHelper.assertSync(syncEngine);

		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		
		List<Item> deletedItems = left.getAll(new DeletedFilter<Item>());
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		
		Assert.assertEquals(1, deletedItems.size());
	}

	@Test
	public void ShouldSynchronizeSince() {
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu", TestHelper.nowSubtractDays(1)));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga", TestHelper.nowSubtractDays(1)));

		ISyncAdapter left = this.makeLeftRepository(a);
		ISyncAdapter right = this.makeRightRepository(b);

		SyncEngine syncEngine = new SyncEngine(left, right);

		TestHelper.assertSync(syncEngine);
	}

	@Test
	public void ShouldGenerateConflict() {
		
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu"));
		
		TestHelper.sleep(1000);
		ISyncAdapter left = this.makeLeftRepository(new Item(a.getContent(), a.getSync().clone().update("kzu", TestHelper.now())));
		
		TestHelper.sleep(1000);
		ISyncAdapter right = this.makeRightRepository(new Item(a.getContent(), a.getSync().clone().update("vga", TestHelper.now())));

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize();

		Assert.assertEquals(1, conflicts.size());
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}
		
		Assert.assertEquals(1, right.get(a.getSyncId()).getSync().getConflicts().size());
		Assert.assertEquals(1, left.get(a.getSyncId()).getSync().getConflicts().size());
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
	}

	@Test
	public void ShouldImportUpdateWithConflictLeft() {
		ISyncAdapter left = this.makeLeftRepository();
		ISyncAdapter right = this.makeRightRepository();
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}
		
		String by = "jmt";

		String id = TestHelper.newID();
		Sync sync = new Sync(id, by, TestHelper.nowSubtractMinutes(2), false);
		Element element = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user></payload>");
		Item item = new Item(new XMLContent(id, "foo", "bar", element), sync);

		left.add(item);
		right.add(item);

		Item incomingItem = item.clone();

		// Local editing.
		XMLContent xmlItem = (XMLContent) item.getContent();
		item = new Item(new XMLContent(id, "changed", ((XMLContent)item.getContent())
				.getDescription(), xmlItem.getPayload()), item.getSync().clone().update(by, TestHelper.nowSubtractMinutes(1), false));

		left.update(item);

		// Conflicting remote editing.
		xmlItem = (XMLContent) item.getContent();
		element = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>remote</name><pass>123</pass></user></payload>");
		incomingItem = new Item(new XMLContent(id, "remote", ((XMLContent)item.getContent())
				.getDescription(), element), incomingItem.getSync().clone().update("REMOTE\\kzu", TestHelper.now(), false));

		right.update(incomingItem);

		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
		
		SyncEngine engine = new SyncEngine(left, right);
		List<Item> conflicts = engine.synchronize();

		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}
		
		Assert.assertEquals(1, conflicts.size());
		Assert.assertEquals(1, left.getAll().size());
		Assert.assertEquals("remote", getUserName(left.get(id)));
		Assert.assertEquals("REMOTE\\kzu", left.get(id).getSync()
				.getLastUpdate().getBy());

		Assert.assertEquals(1, left.getConflicts().size());
		Assert.assertEquals(1, right.getConflicts().size());
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
	}

	protected abstract String getUserName(Item item);

	@Test
	public void ShouldReportImportProgress() {
		ISyncAdapter left = this.makeLeftRepository();
		ISyncAdapter right = this.makeRightRepository();
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}
		
		String by = "jmt";

		String id = TestHelper.newID();
		Sync sync = new Sync(id, by, TestHelper.nowSubtractMinutes(2), false);
		Element element = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user></payload>");
		Item item = new Item(new XMLContent(id, "foo", "bar", element), sync);

		left.add(item);

		id = TestHelper.newID();
		sync = new Sync(id, by, TestHelper.nowSubtractMinutes(2), false);
		element = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user></payload>");
		item = new Item(new XMLContent(id, "foo", "bar", element), sync);

		right.add(item);

		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
		
		SyncEngine engine = new SyncEngine(left, right);
		MockItemReceivedObserver itemReceivedObserver = new MockItemReceivedObserver();
		MockItemSentObserver itemSenObserver = new MockItemSentObserver();

		engine.registerItemReceivedObserver(itemReceivedObserver);
		engine.registerItemSentObserver(itemSenObserver);

		engine.synchronize();

		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}
		
		Assert.assertEquals(2, left.getAll().size());
		Assert.assertEquals(2, right.getAll().size());

		// Receives the item that was sent first plus the existing remote one.
		Assert.assertEquals(2, itemReceivedObserver.getNumberOfreceivedItems());
		Assert.assertEquals(1, itemSenObserver.getNumberOfSentItems());
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
	}

	@Test
	public void ShouldNotSendReceivedItemIfModifiedBeforeSince() {
		
		ISyncAdapter left = this.makeLeftRepository();
		ISyncAdapter right = this.makeRightRepository();
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}

		Date nowSubtract2Minutes = TestHelper.nowSubtractMinutes(2);
		Date nowSubtract5Minutes = TestHelper.nowSubtractMinutes(5);
		Date nowSubtract2Days = TestHelper.nowSubtractDays(2);
		
		Assert.assertTrue(nowSubtract5Minutes.before(nowSubtract2Minutes));
		Assert.assertTrue(nowSubtract2Days.before(nowSubtract5Minutes));
		
		String by = "jmt";
			
		String id = TestHelper.newID();
		Sync sync = new Sync(id, by, nowSubtract2Minutes, false);
		Element element = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user></payload>");
		Item item = new Item(new XMLContent(id, "foo", "bar", element), sync);
		left.add(item);

		id = TestHelper.newID();
		sync = new Sync(id, by, nowSubtract2Days, false);
		element = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user></payload>");
		item = new Item(new XMLContent(id, "foo", "bar", element), sync);
		right.add(item);
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
		
		MockItemReceivedObserver itemReceivedObserver = new MockItemReceivedObserver();
		MockItemSentObserver itemSenObserver = new MockItemSentObserver();
		SyncEngine engine = new SyncEngine(left, right);

		engine.registerItemReceivedObserver(itemReceivedObserver);
		engine.registerItemSentObserver(itemSenObserver);

		engine.synchronize(nowSubtract5Minutes);

		
		if(left instanceof ISyncAware){
			((ISyncAware)left).beginSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).beginSync();
		}
		
		// No new item would have been received from target as it was
		// modified in the past.
		Assert.assertEquals(1, left.getAll().size());
		// Local item was sent.
		Assert.assertEquals(2, right.getAll().size());
		// We would have received the same item we sent, as we're first
		// sending and then receiving.
		Assert.assertEquals(1, itemReceivedObserver.getNumberOfreceivedItems());
		Assert.assertEquals(1, itemSenObserver.getNumberOfSentItems());
		
		if(left instanceof ISyncAware){
			((ISyncAware)left).endSync();
		}
		if(right instanceof ISyncAware){
			((ISyncAware)right).endSync();
		}
	}
	
	private Item createItem(String title, String id, History history) {
		return createItem(title, id, history, new History[0]);
	}

	private Item createItem(String title, String id, History history,
			History[] otherHistory) {
		
		Element e = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+title+"</name><pass>123</pass></user></payload>");
		XMLContent xml = new XMLContent(TestHelper.newID(), title, null, e);
		Sync sync = new Sync(id, history.getBy(), history.getWhen(),
				false);
		for (History h : otherHistory) {
			sync.update(h.getBy(), h.getWhen());
		}

		return new Item(xml, sync);
	}
	
	private class MockItemReceivedObserver implements IObserverItem {

		private int received = 0;

		public void notifyItemNovelty(Item item) {
			this.received++;
		}

		public int getNumberOfreceivedItems() {
			return received;
		}
	}

	private class MockItemSentObserver implements IObserverItem {

		private int sent = 0;

		public void notifyItemNovelty(Item item) {
			this.sent++;
		}

		public int getNumberOfSentItems() {
			return sent;
		}
	}
}
