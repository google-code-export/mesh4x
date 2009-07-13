package org.mesh4j.sync.adapters.composite;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class IdentifiableSyncAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenTypeIsNull(){
		new IdentifiableSyncAdapter(null, new MockSyncAdapter());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenTypeIsEmpty(){
		new IdentifiableSyncAdapter("", new MockSyncAdapter());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenISyncAdapterIsNull(){
		new IdentifiableSyncAdapter("myType", null);
	}

	@Test
	public void shouldCreate(){
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	
		
		Assert.assertEquals("myType", adapter.getType());
		Assert.assertEquals(syncAdapter, adapter.getSyncAdapter());
		Assert.assertEquals(syncAdapter.getFriendlyName(), adapter.getFriendlyName());
	}
	
	
	@Test
	public void shouldAdd() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		Item item = makeItem();
		adapter.add(item);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("add", item));
	}

	@Test
	public void shouldDelete() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		String id = "1";
		adapter.delete(id);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("delete", id));
	}

	@Test
	public void shouldGet() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		String id = "1";
		adapter.get(id);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("get", id));
	}

	@Test
	public void shouldGetAll() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		adapter.getAll();		
		Assert.assertTrue(syncAdapter.wasMethodCalled("getAll"));
	}

	@Test
	public void shouldGetAllFilter() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		IFilter<Item> filter = new IFilter<Item>(){@Override public boolean applies(Item obj) {return false;}};
		
		adapter.getAll(filter);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("getAllFilter", filter));
	}

	@Test
	public void shouldGetAllSince() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		Date date = new Date();
		adapter.getAllSince(date);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("getAllSince", date));
	}

	@Test
	public void shouldGetAllSinceFilter() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		IFilter<Item> filter = new IFilter<Item>(){@Override public boolean applies(Item obj) {return false;}};
		Date date = new Date();
		adapter.getAllSince(date, filter);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("getAllSinceFilter", date, filter));
	}

	@Test
	public void shouldGetConflicts() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		adapter.getConflicts();		
		Assert.assertTrue(syncAdapter.wasMethodCalled("getConflicts"));
	}

	@Test
	public void shouldUpdate() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		Item item = makeItem();
		adapter.update(item);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("update", item));
	}

	@Test
	public void shouldUpdateResolveConflicts() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		Item item = makeItem();
		adapter.update(item, true);		
		Assert.assertTrue(syncAdapter.wasMethodCalled("updateResolveConflicts", item, true));
	}

	@Test
	public void shouldBeginSyncNoWasCalledWhenISyncAdpaterNotImplementsISyncAware() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		adapter.beginSync();	
		Assert.assertFalse(syncAdapter.wasMethodCalled("beginSync"));
	}

	@Test
	public void shouldEndSyncNoWasCalledWhenISyncAdpaterNotImplementsISyncAware() {
		MockSyncAdapter syncAdapter = new MockSyncAdapter();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		adapter.endSync();	
		Assert.assertFalse(syncAdapter.wasMethodCalled("endSync"));	
	}
	
	@Test
	public void shouldBeginSync() {
		MockSyncAdapterWithSyncAware syncAdapter = new MockSyncAdapterWithSyncAware();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		adapter.beginSync();	
		Assert.assertTrue(syncAdapter.wasMethodCalled("beginSync"));
	}

	@Test
	public void shouldEndSync() {
		MockSyncAdapterWithSyncAware syncAdapter = new MockSyncAdapterWithSyncAware();
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("myType", syncAdapter);	

		adapter.endSync();	
		Assert.assertTrue(syncAdapter.wasMethodCalled("endSync"));	
	}

	private class MockSyncAdapter implements ISyncAdapter {
		protected HashMap<String, Object[]> calls = new HashMap<String, Object[]>();

		@Override public void add(Item item) {this.calls.put("add", new Object[]{item});}
		@Override public void delete(String id) {this.calls.put("delete", new Object[]{id});}
		@Override public Item get(String id) {this.calls.put("get", new Object[]{id}); return null;}
		@Override public List<Item> getAll() {this.calls.put("getAll", new Object[0]); return null;}
		@Override public List<Item> getAll(IFilter<Item> filter) {this.calls.put("getAllFilter", new Object[]{filter}); return null;}
		@Override public List<Item> getAllSince(Date since) {this.calls.put("getAllSince", new Object[]{since}); return null;}
		@Override public List<Item> getAllSince(Date since, IFilter<Item> filter) {this.calls.put("getAllSinceFilter", new Object[]{since, filter}); return null;}
		@Override public List<Item> getConflicts() {this.calls.put("getConflicts", new Object[0]); return null;}
		@Override public String getFriendlyName() {this.calls.put("getFriendlyName", new Object[0]); return "Mock";}
		@Override public void update(Item item) {this.calls.put("update", new Object[]{item}); }
		@Override public void update(Item item, boolean resolveConflicts) {this.calls.put("updateResolveConflicts", new Object[]{item, resolveConflicts}); }

		public boolean wasMethodCalled(String methodName, Object... parameters) {
			Object[] params = this.calls.get(methodName);
			if(params == null){
				return false;
			}
			if(params.length != parameters.length){
				return false;
			}
			
			for (int i = 0; i < params.length; i++) {
				if(params[i] != parameters[i]){
					return false;
				}
			}
			return true;
		}
	}
	
	private class MockSyncAdapterWithSyncAware extends MockSyncAdapter implements ISyncAware {
		@Override public void beginSync() {this.calls.put("beginSync", new Object[0]); }
		@Override public void endSync() {this.calls.put("endSync", new Object[0]); }
	}

	private Item makeItem(){
		
		Element payload = XMLHelper.parseElement("<foo>bar</foo>");
		String syncId = IdGenerator.INSTANCE.newID();
		String description = "Id: " + syncId + " Version: " + XMLHelper.canonicalizeXML(payload).hashCode();
		XMLContent content = new XMLContent(syncId, syncId, description, payload);
		Sync sync = new Sync(syncId, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		return item;
	}
}
