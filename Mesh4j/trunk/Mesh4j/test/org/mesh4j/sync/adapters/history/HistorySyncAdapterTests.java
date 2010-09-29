package org.mesh4j.sync.adapters.history;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.filter.NullFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.security.NullIdentityProvider;

public class HistorySyncAdapterTests {

	private static Item ITEM;
	
	@Before
	public void setUp(){
		ITEM = new Item(new NullContent("1"), new Sync("1", "jmt", new Date(), true));
	}
	
	@Test
	public void shouldAddGenerateHistory(){
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("me", NullIdentityProvider.INSTANCE);
		
		HistorySyncAdapter ha = new HistorySyncAdapter(adapter, new InMemoryHistoryRepository());
		ha.add(ITEM);
		
		List<HistoryChange> history = ha.getHistories(ITEM.getSyncId());
		Assert.assertNotNull(history);
		Assert.assertEquals(1, history.size());
		
		HistoryChange historyChange = history.get(0);
		Assert.assertEquals(ITEM.getLastUpdate(), historyChange.getSyncHistory());
		Assert.assertEquals(ITEM.getContent().getPayload().asXML(), historyChange.getPayload());
		Assert.assertEquals(HistoryType.ADD, historyChange.getHistoryType());
	}
	
	@Test
	public void shouldDeleteGenerateHistory(){
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("me", NullIdentityProvider.INSTANCE, ITEM);
		
		HistorySyncAdapter ha = new HistorySyncAdapter(adapter, new InMemoryHistoryRepository());
		ha.delete(ITEM.getSyncId());
		
		List<HistoryChange> history = ha.getHistories(ITEM.getSyncId());
		Assert.assertNotNull(history);
		Assert.assertEquals(1, history.size());
		
		HistoryChange historyChange = history.get(0);
		Assert.assertEquals(ITEM.getLastUpdate(), historyChange.getSyncHistory());
		Assert.assertEquals(ITEM.getContent().getPayload().asXML(), historyChange.getPayload());
		Assert.assertEquals(HistoryType.DELETE, historyChange.getHistoryType());
	}
	
	@Test
	public void shouldDeleteNoGenerateHistoryWhenItemDoesNotExists(){
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("me", NullIdentityProvider.INSTANCE);
		
		HistorySyncAdapter ha = new HistorySyncAdapter(adapter, new InMemoryHistoryRepository());
		ha.delete(ITEM.getSyncId());
		
		List<HistoryChange> history = ha.getHistories(ITEM.getSyncId());
		Assert.assertNull(history);
	}
		
	@Test
	public void shouldUpdateGenerateHistory(){
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("me", NullIdentityProvider.INSTANCE, ITEM);
		
		HistorySyncAdapter ha = new HistorySyncAdapter(adapter, new InMemoryHistoryRepository());
		ha.update(ITEM);
		
		List<HistoryChange> history = ha.getHistories(ITEM.getSyncId());
		Assert.assertNotNull(history);
		Assert.assertEquals(1, history.size());
		
		HistoryChange historyChange = history.get(0);
		Assert.assertEquals(ITEM.getLastUpdate(), historyChange.getSyncHistory());
		Assert.assertEquals(ITEM.getContent().getPayload().asXML(), historyChange.getPayload());
		Assert.assertEquals(HistoryType.UPDATE, historyChange.getHistoryType());
	}
	
	@Test
	public void shouldUpdateResolveConflictsGenerateHistory(){
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("me", NullIdentityProvider.INSTANCE, ITEM);
		
		HistorySyncAdapter ha = new HistorySyncAdapter(adapter, new InMemoryHistoryRepository());
		ha.update(ITEM, true);
		
		List<HistoryChange> history = ha.getHistories(ITEM.getSyncId());
		Assert.assertNotNull(history);
		Assert.assertEquals(1, history.size());
		
		HistoryChange historyChange = history.get(0);
		Assert.assertEquals(ITEM.getLastUpdate(), historyChange.getSyncHistory());
		Assert.assertEquals(ITEM.getContent().getPayload().asXML(), historyChange.getPayload());
		Assert.assertEquals(HistoryType.UPDATE, historyChange.getHistoryType());
	}
		
	@Test
	public void shouldGetHistories(){
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("me", NullIdentityProvider.INSTANCE);
		
		HistorySyncAdapter ha = new HistorySyncAdapter(adapter, new InMemoryHistoryRepository());
		ha.add(ITEM);
		ha.update(ITEM);
		ha.update(ITEM, true);
		ha.update(ITEM, false);
		ha.delete(ITEM.getSyncId());
		
		List<HistoryChange> histories = ha.getHistories(ITEM.getSyncId());
		Assert.assertEquals(5, histories.size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenSyncAdapterIsNull(){
		new HistorySyncAdapter(null, new InMemoryHistoryRepository());
	}
	
	@Test
	public void shouldHistoryAdapterImplementsISyncAdapter(){
		HistorySyncAdapter ha = new HistorySyncAdapter(new MockSyncAdapter(), new InMemoryHistoryRepository());
		Assert.assertTrue(ha instanceof ISyncAdapter);
	}
	
	@Test
	public void shouldHistoryAdapterImplementsISyncAware(){
		HistorySyncAdapter ha = new HistorySyncAdapter(new MockSyncAdapter(), new InMemoryHistoryRepository());
		Assert.assertTrue(ha instanceof ISyncAware);
	}
	
	@Test
	public void shouldHistoryAdapterNotImplementsISupportMerge(){
		HistorySyncAdapter ha = new HistorySyncAdapter(new MockSyncAdapter(), new InMemoryHistoryRepository());
		Assert.assertFalse(ha instanceof ISupportMerge);
	}
	
	@Test
	public void shouldHistoryAdapterCallAdd(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		ha.add(ITEM);
		Assert.assertTrue(mock.addWasCalled);
		Assert.assertEquals(ITEM, mock.item);
	}
	
	@Test
	public void shouldHistoryAdapterCallDelete(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		ha.delete("1");
		Assert.assertTrue(mock.deleteWasCalled);
		Assert.assertEquals("1", mock.id);
	}
	
	@Test
	public void shouldHistoryAdapterCallGet(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		ha.get("1");
		Assert.assertTrue(mock.getWasCalled);
		Assert.assertEquals("1", mock.id);
	}
	
	@Test
	public void shouldHistoryAdapterCallGetAll(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		ha.getAll();
		Assert.assertTrue(mock.getAllWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterCallGetAllFilter(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		IFilter<Item> filter = new NullFilter<Item>();
		ha.getAll(filter);
		Assert.assertTrue(mock.getAllFilterWasCalled);
		Assert.assertEquals(filter, mock.filter);
	}
	
	@Test
	public void shouldHistoryAdapterCallGetAllSince(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		Date since = new Date();
		ha.getAllSince(since);
		Assert.assertTrue(mock.getAllSinceWasCalled);
		Assert.assertEquals(since, mock.since);
	}
	
	@Test
	public void shouldHistoryAdapterCallGetAllSinceFilter(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		IFilter<Item> filter = new NullFilter<Item>();
		Date since = new Date();
		ha.getAllSince(since, filter);
		Assert.assertTrue(mock.getAllSinceFilterWasCalled);
		Assert.assertEquals(filter, mock.filter);
		Assert.assertEquals(since, mock.since);
	}
	
	@Test
	public void shouldHistoryAdapterCallGetConflicts(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.getConflicts();
		Assert.assertTrue(mock.getConflictsWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterCallGetFriendlyName(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.getFriendlyName();
		Assert.assertTrue(mock.getFriendlyNameWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterCallUpdate(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.update(ITEM);
		Assert.assertTrue(mock.updateWasCalled);
		Assert.assertEquals(ITEM, mock.item);
	}
	
	@Test
	public void shouldHistoryAdapterNoCallBeginSync(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.beginSync();
		Assert.assertFalse(mock.beginSyncWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterNoCallEndSync(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.endSync();
		Assert.assertFalse(mock.endSyncWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterCallBeginSync(){
		MockSyncAdapter mock = new MockSyncAdapterWithSyncAware();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.beginSync();
		Assert.assertTrue(mock.beginSyncWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterCallEndSync(){
		MockSyncAdapter mock = new MockSyncAdapterWithSyncAware();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.endSync();
		Assert.assertTrue(mock.endSyncWasCalled);
	}
	
	@Test
	public void shouldHistoryAdapterCallUpdateResolveConflicts(){
		MockSyncAdapter mock = new MockSyncAdapter();
		HistorySyncAdapter ha = new HistorySyncAdapter(mock, new InMemoryHistoryRepository());
		
		ha.update(ITEM, true);
		Assert.assertTrue(mock.updateResolveConflictsWasCalled);
		Assert.assertEquals(ITEM, mock.item);
		Assert.assertTrue(mock.resolveConflicts);
		
		mock.item = null;
		ha.update(ITEM, false);
		Assert.assertTrue(mock.updateResolveConflictsWasCalled);
		Assert.assertEquals(ITEM, mock.item);
		Assert.assertFalse(mock.resolveConflicts);
	}
	
	private class MockSyncAdapter implements ISyncAdapter{

		private boolean addWasCalled = false;
		private boolean deleteWasCalled = false;
		private boolean getWasCalled = false;
		private boolean getAllWasCalled = false;
		private boolean getAllFilterWasCalled = false;
		private boolean getAllSinceWasCalled = false;
		private boolean getAllSinceFilterWasCalled = false;
		private boolean getConflictsWasCalled = false;
		private boolean getFriendlyNameWasCalled = false;
		private boolean updateWasCalled = false;
		private boolean updateResolveConflictsWasCalled = false;
		private Item item;
		private String id;
		private boolean resolveConflicts;
		private IFilter<Item> filter;
		private Date since;
		private boolean beginSyncWasCalled = false;
		private boolean endSyncWasCalled = false;
		
		
		@Override
		public void add(Item item) {
			addWasCalled = true;
			this.item = item;
		}

		@Override
		public void delete(String id) {
			deleteWasCalled = true;
			this.id = id;
		}
		
		@Override
		public Item get(String id) {
			getWasCalled = true;
			this.id = id;
			return null;
		}

		@Override
		public List<Item> getAll() {
			getAllWasCalled = true;
			return null;
		}

		@Override
		public List<Item> getAll(IFilter<Item> filter) {
			getAllFilterWasCalled = true;
			this.filter = filter;
			return null;
		}

		@Override
		public List<Item> getAllSince(Date since) {
			getAllSinceWasCalled = true;
			this.since = since;
			return null;
		}

		@Override
		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
			getAllSinceFilterWasCalled = true;
			this.filter = filter;
			this.since = since;
			return null;
		}

		@Override
		public List<Item> getConflicts() {
			getConflictsWasCalled = true;
			return null;
		}

		@Override
		public String getFriendlyName() {
			getFriendlyNameWasCalled = true;
			return null;
		}

		@Override
		public void update(Item item) {
			updateWasCalled = true;		
			this.item = item;
		}

		@Override
		public void update(Item item, boolean resolveConflicts) {
			updateResolveConflictsWasCalled = true;
			this.item = item;
			this.resolveConflicts = resolveConflicts;
		}
		
		public void beginSync() {
			beginSyncWasCalled = true;			
		}

		public void endSync() {
			endSyncWasCalled = true;			
		}
		
		public ISchema getSchema() {
			return null;
		}
	}

	private class MockSyncAdapterWithSyncAware extends MockSyncAdapter implements ISyncAware{
		
		@Override
		public void beginSync() {
			super.beginSync();		
		}

		@Override
		public void endSync() {
			super.endSync();			
		}
	}

}
