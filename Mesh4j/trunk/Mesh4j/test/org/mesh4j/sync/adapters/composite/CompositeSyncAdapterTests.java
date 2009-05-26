package org.mesh4j.sync.adapters.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class CompositeSyncAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenNameIsNull(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE);
		new CompositeSyncAdapter(null, opaqueAdapter, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenNameIsEmpty(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE);
		new CompositeSyncAdapter("", opaqueAdapter, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenOpaqueAdapterIsNull(){
		new CompositeSyncAdapter("test", null, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenIdentityProviderIsNull(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE);
		new CompositeSyncAdapter("test", opaqueAdapter, null);
	}
	
	@Test
	public void shouldGetName(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter composite = new CompositeSyncAdapter("test", opaqueAdapter, NullIdentityProvider.INSTANCE, new IdentifiableSyncAdapter("type", "id", opaqueAdapter));
		Assert.assertEquals("test", composite.getFriendlyName());
	}

	@Test
	public void shouldGetAuthenticatedUser(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter composite = new CompositeSyncAdapter("test", opaqueAdapter, NullIdentityProvider.INSTANCE, new IdentifiableSyncAdapter("type", "id", opaqueAdapter));
		Assert.assertEquals(NullIdentityProvider.INSTANCE.getAuthenticatedUser(), composite.getAuthenticatedUser());
	}
	
	@Test
	public void shouldAddItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item foo = makeItem("foo");
		composite.add(foo);
		
		Assert.assertEquals(0, opaqueAdapter.getAll().size());
		Assert.assertEquals(1, adapter.getAll().size());
		Assert.assertEquals(1, composite.getAll().size());
		TestHelper.assertItem(foo, composite, adapter);

	}

	@Test
	public void shouldAddOpaqueItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item bar = makeItem("bar");
		composite.add(bar);
		
		Assert.assertEquals(1, opaqueAdapter.getAll().size());
		Assert.assertEquals(0, adapter.getAll().size());
		Assert.assertEquals(1, composite.getAll().size());
		TestHelper.assertItem(bar, composite, opaqueAdapter);	
	}
	
	@Test
	public void shouldUpdateItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);

		Item foo = makeItem("foo");
		composite.add(foo);

		Element payload = TestHelper.makeElement("<payload><foo><id>"+foo.getSyncId()+"</id><name>jmt</name></foo></payload>");
		Item fooUpdated = new Item(new XMLContent(foo.getSyncId(), foo.getSyncId(), "", payload), foo.getSync().clone().update("jmt", new Date()));
		composite.update(fooUpdated);
		
		Assert.assertEquals(0, opaqueAdapter.getAll().size());
		Assert.assertEquals(1, adapter.getAll().size());
		Assert.assertEquals(1, composite.getAll().size());
		TestHelper.assertItem(fooUpdated, composite, adapter);
	}

	@Test
	public void shouldUpdateOpaqueItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);

		Item bar = makeItem("bar");
		composite.add(bar);

		Element payload = TestHelper.makeElement("<payload><bar><id>"+bar.getSyncId()+"</id><name>jmt</name></bar></payload>");
		Item barUpdated = new Item(new XMLContent(bar.getSyncId(), bar.getSyncId(), "", payload), bar.getSync().clone().update("jmt", new Date()));
		composite.update(barUpdated);
		
		Assert.assertEquals(1, opaqueAdapter.getAll().size());
		Assert.assertEquals(0, adapter.getAll().size());
		Assert.assertEquals(1, composite.getAll().size());
		TestHelper.assertItem(barUpdated, composite, opaqueAdapter);
	}
	
	@Test
	public void shouldDeleteItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		
		composite.add(foo);
		composite.add(bar);
		
		Assert.assertEquals(1, opaqueAdapter.getAll().size());
		Assert.assertEquals(1, adapter.getAll().size());
		Assert.assertEquals(2, composite.getAll().size());
		TestHelper.assertItem(foo, composite, adapter);
		TestHelper.assertItem(bar, composite, opaqueAdapter);
		
		composite.delete(foo.getSyncId());

		Assert.assertEquals(1, opaqueAdapter.getAll().size());
		Assert.assertEquals(0, adapter.getAll().size());
		Assert.assertEquals(1, composite.getAll().size());
		TestHelper.assertItem(bar, composite, opaqueAdapter);
	}

	@Test
	public void shouldDeleteOpaqueItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		
		composite.add(foo);
		composite.add(bar);
		
		Assert.assertEquals(1, opaqueAdapter.getAll().size());
		Assert.assertEquals(1, adapter.getAll().size());
		Assert.assertEquals(2, composite.getAll().size());
		TestHelper.assertItem(foo, composite, adapter);
		TestHelper.assertItem(bar, composite, opaqueAdapter);
		
		composite.delete(bar.getSyncId());

		Assert.assertEquals(0, opaqueAdapter.getAll().size());
		Assert.assertEquals(1, adapter.getAll().size());
		Assert.assertEquals(1, composite.getAll().size());
		TestHelper.assertItem(foo, composite, adapter);	
	}
	
	@Test
	public void shouldGetItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		
		composite.add(foo);
		composite.add(bar);

		Assert.assertTrue(foo.equals(composite.get(foo.getSyncId())));
	}

	@Test
	public void shouldGetOpaqueItem(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		
		composite.add(foo);
		composite.add(bar);

		Assert.assertTrue(bar.equals(composite.get(bar.getSyncId())));
	}
	
	@Test
	public void shouldGetAll(){
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		
		composite.add(foo);
		composite.add(bar);

		Assert.assertEquals(1, opaqueAdapter.getAll().size());
		Assert.assertEquals(1, adapter.getAll().size());
		
		List<Item> items = composite.getAll();
		Assert.assertEquals(2, items.size());
		
		boolean okFoo = false;
		boolean okBar = false;
		
		for (Item item : items) {
			if(item.getSyncId().equals(foo.getSyncId())){
				Assert.assertTrue(foo.equals(item));
				okFoo = true;
			}
			if(item.getSyncId().equals(bar.getSyncId())){
				Assert.assertTrue(bar.equals(item));
				okBar = true;
			}
		}
		Assert.assertTrue(okFoo && okBar);
	}
	
	@Test
	public void shouldGetAllSinceDateAndFilter() throws InterruptedException{
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		IIdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter("foo", "id", new InMemorySyncAdapter("foo", NullIdentityProvider.INSTANCE));
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapter);

		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		final Item fooOld = makeItem("foo", date);
		final Item barOld = makeItem("bar", date);
		final Item fooOld1 = makeItem("foo", date);
		
		composite.add(fooOld);
		composite.add(barOld);
		composite.add(fooOld1);
		
		Date since = new Date();
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		final Item bar1 = makeItem("bar");
		
		composite.add(bar1);
		composite.add(foo);
		composite.add(bar);
		
		IFilter<Item> filter = new IFilter<Item>(){
			@Override
			public boolean applies(Item item) {
				return fooOld.getSyncId().equals(item.getSyncId()) ||
						fooOld1.getSyncId().equals(item.getSyncId()) ||
						barOld.getSyncId().equals(item.getSyncId()) ||
						bar1.getSyncId().equals(item.getSyncId());
			}			
		};

		Assert.assertEquals(3, opaqueAdapter.getAll().size());
		Assert.assertEquals(3, adapter.getAll().size());
		Assert.assertEquals(6, composite.getAll().size());
		
		List<Item> itemsByFilter = composite.getAll(filter);
		Assert.assertEquals(4, itemsByFilter.size());
	
		List<Item> itemsBySince = composite.getAllSince(since);
		Assert.assertEquals(3, itemsBySince.size());
		
		List<Item> items = composite.getAllSince(since, filter);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(bar1.equals(items.get(0)));
	}
	
	@Test
	public void shouldBeginSync(){
		MockSyncAdapter opaqueMock = new MockSyncAdapter();
		MockSyncAdapter adapterMock = new MockSyncAdapter();
		
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueMock, NullIdentityProvider.INSTANCE, adapterMock);
		
		Assert.assertFalse(opaqueMock.beginSyncWasCalled());
		Assert.assertFalse(adapterMock.beginSyncWasCalled());

		composite.beginSync();
		
		Assert.assertTrue(opaqueMock.beginSyncWasCalled());
		Assert.assertTrue(adapterMock.beginSyncWasCalled());
	}
	
	@Test
	public void shouldEndSync(){
		MockSyncAdapter opaqueMock = new MockSyncAdapter();
		MockSyncAdapter adapterMock = new MockSyncAdapter();
		
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueMock, NullIdentityProvider.INSTANCE, adapterMock);
		
		Assert.assertFalse(opaqueMock.endSyncWasCalled());
		Assert.assertFalse(adapterMock.endSyncWasCalled());

		composite.endSync();
		
		Assert.assertTrue(opaqueMock.endSyncWasCalled());
		Assert.assertTrue(adapterMock.endSyncWasCalled());	
	}
	
	@Test
	public void shouldBeginSyncNoCalled(){
		MockSyncAdapterWithoutSyncAware opaqueMock = new MockSyncAdapterWithoutSyncAware();
		MockSyncAdapterWithoutSyncAware adapterMock = new MockSyncAdapterWithoutSyncAware();
		
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueMock, NullIdentityProvider.INSTANCE, adapterMock);
		
		Assert.assertFalse(opaqueMock.beginSyncWasCalled());
		Assert.assertFalse(adapterMock.beginSyncWasCalled());

		composite.beginSync();
		
		Assert.assertFalse(opaqueMock.beginSyncWasCalled());
		Assert.assertFalse(adapterMock.beginSyncWasCalled());
	}
	
	@Test
	public void shouldEndSyncNoCalled(){
		MockSyncAdapterWithoutSyncAware opaqueMock = new MockSyncAdapterWithoutSyncAware();
		MockSyncAdapterWithoutSyncAware adapterMock = new MockSyncAdapterWithoutSyncAware();
		
		CompositeSyncAdapter composite = new CompositeSyncAdapter("composite", opaqueMock, NullIdentityProvider.INSTANCE, adapterMock);
		
		Assert.assertFalse(opaqueMock.endSyncWasCalled());
		Assert.assertFalse(adapterMock.endSyncWasCalled());

		composite.endSync();
		
		Assert.assertFalse(opaqueMock.endSyncWasCalled());
		Assert.assertFalse(adapterMock.endSyncWasCalled());	
	}
	
	@Test
	public void shouldSyncMeshGroupWithCompositeAdapterVsOpaqueAdapter(){
		
		Item foo1 = makeItem("foo");
		Item foo2 = makeItem("foo");
		Item foo3 = makeItem("foo");
		Item foo4 = makeItem("foo");

		Item bar1 = makeItem("bar");
		Item bar2 = makeItem("bar");
		Item bar3 = makeItem("bar");
		Item bar4 = makeItem("bar");
		
		ArrayList<Item> items1 = new ArrayList<Item>();
		items1.add(foo1);
		items1.add(foo2);
		items1.add(foo3);

		ArrayList<Item> items2 = new ArrayList<Item>();
		items2.add(bar1);
		items2.add(bar2);
		items2.add(bar3);
		
		InMemorySyncAdapter adapterSourceM1 = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE, items1);
		IdentifiableSyncAdapter adapterSource1 = new IdentifiableSyncAdapter("foo", "id", adapterSourceM1);
		
		InMemorySyncAdapter adapterSourceM2 = new InMemorySyncAdapter("source2", NullIdentityProvider.INSTANCE, items2);
		IdentifiableSyncAdapter adapterSource2 = new IdentifiableSyncAdapter("bar", "id", adapterSourceM2);
		
		ISyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapterSource1, adapterSource2);
		
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("source3", NullIdentityProvider.INSTANCE);
		adapterTarget.add(foo4);
		adapterTarget.add(bar4);
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(8, adapterSource.getAll().size());
		Assert.assertEquals(4, adapterSourceM1.getAll().size());
		Assert.assertEquals(4, adapterSourceM2.getAll().size());
		Assert.assertEquals(8, adapterTarget.getAll().size());
		
		TestHelper.assertItem(foo1, adapterSource1, adapterSourceM1, adapterSource, adapterTarget);
		TestHelper.assertItem(foo2, adapterSource1, adapterSourceM1, adapterSource, adapterTarget);
		TestHelper.assertItem(foo3, adapterSource1, adapterSourceM1, adapterSource, adapterTarget);
		TestHelper.assertItem(foo4, adapterSource1, adapterSourceM1, adapterSource, adapterTarget);
		TestHelper.assertItem(bar1, adapterSource2, adapterSourceM2, adapterSource, adapterTarget);
		TestHelper.assertItem(bar2, adapterSource2, adapterSourceM2, adapterSource, adapterTarget);
		TestHelper.assertItem(bar3, adapterSource2, adapterSourceM2, adapterSource, adapterTarget);
		TestHelper.assertItem(bar4, adapterSource2, adapterSourceM2, adapterSource, adapterTarget);
		
	}

	@Test
	public void shouldSyncMeshGroupWithCompositeAdapterVsCompositeAdapter(){
		
		Item foo1 = makeItem("foo");
		Item foo2 = makeItem("foo");
		Item foo3 = makeItem("foo");
		Item foo4 = makeItem("foo");

		Item bar1 = makeItem("bar");
		Item bar2 = makeItem("bar");
		Item bar3 = makeItem("bar");
		Item bar4 = makeItem("bar");
		
		ArrayList<Item> items1 = new ArrayList<Item>();
		items1.add(foo1);
		items1.add(foo2);
		items1.add(foo3);

		ArrayList<Item> items2 = new ArrayList<Item>();
		items2.add(bar1);
		items2.add(bar2);
		items2.add(bar3);
		
		InMemorySyncAdapter adapterSourceM1 = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE, items1);
		IdentifiableSyncAdapter adapterSource1 = new IdentifiableSyncAdapter("foo", "id", adapterSourceM1);
		
		InMemorySyncAdapter adapterSourceM2 = new InMemorySyncAdapter("source2", NullIdentityProvider.INSTANCE, items2);
		IdentifiableSyncAdapter adapterSource2 = new IdentifiableSyncAdapter("bar", "id", adapterSourceM2);
		
		ISyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapterSource1, adapterSource2);
		
		ArrayList<Item> items3 = new ArrayList<Item>();
		items3.add(foo4);

		ArrayList<Item> items4 = new ArrayList<Item>();
		items4.add(bar4);
		
		InMemorySyncAdapter adapterTargetM3 = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE, items3);
		IdentifiableSyncAdapter adapterTarget1 = new IdentifiableSyncAdapter("foo", "id", adapterTargetM3);
		
		InMemorySyncAdapter adapterTargetM4 = new InMemorySyncAdapter("target2", NullIdentityProvider.INSTANCE, items4);
		IdentifiableSyncAdapter adapterTarget2 = new IdentifiableSyncAdapter("bar", "id", adapterTargetM4);
		
		ISyncAdapter opaqueAdapter2 = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = new CompositeSyncAdapter("composite target", opaqueAdapter2, NullIdentityProvider.INSTANCE, adapterTarget1, adapterTarget2);
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(8, adapterSource.getAll().size());
		Assert.assertEquals(4, adapterSourceM1.getAll().size());
		Assert.assertEquals(4, adapterSourceM2.getAll().size());
		Assert.assertEquals(8, adapterTarget.getAll().size());
		Assert.assertEquals(4, adapterTargetM3.getAll().size());
		Assert.assertEquals(4, adapterTargetM4.getAll().size());
		
		TestHelper.assertItem(foo1, adapterSource1, adapterSourceM1, adapterTarget1, adapterTargetM3, adapterSource, adapterTarget);
		TestHelper.assertItem(foo2, adapterSource1, adapterSourceM1, adapterTarget1, adapterTargetM3, adapterSource, adapterTarget);
		TestHelper.assertItem(foo3, adapterSource1, adapterSourceM1, adapterTarget1, adapterTargetM3, adapterSource, adapterTarget);
		TestHelper.assertItem(foo4, adapterSource1, adapterSourceM1, adapterTarget1, adapterTargetM3, adapterSource, adapterTarget);
		TestHelper.assertItem(bar1, adapterSource2, adapterSourceM2, adapterTarget2, adapterTargetM4, adapterSource, adapterTarget);
		TestHelper.assertItem(bar2, adapterSource2, adapterSourceM2, adapterTarget2, adapterTargetM4, adapterSource, adapterTarget);
		TestHelper.assertItem(bar3, adapterSource2, adapterSourceM2, adapterTarget2, adapterTargetM4, adapterSource, adapterTarget);
		TestHelper.assertItem(bar4, adapterSource2, adapterSourceM2, adapterTarget2, adapterTargetM4, adapterSource, adapterTarget);

	}

	@Test
	public void shouldSyncCompositeAdapterAddItemsToOpaqueSubAdapter(){
		
		InMemorySyncAdapter adapterM1 = new InMemorySyncAdapter("source1", NullIdentityProvider.INSTANCE);
		IdentifiableSyncAdapter adapterSource1 = new IdentifiableSyncAdapter("foo", "id", adapterM1);
		
		InMemorySyncAdapter adapterM2 = new InMemorySyncAdapter("source2", NullIdentityProvider.INSTANCE);
		IdentifiableSyncAdapter adapterSource2 = new IdentifiableSyncAdapter("bar", "id", adapterM2);

		ISyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter compositeAdapter = new CompositeSyncAdapter("composite", opaqueAdapter, NullIdentityProvider.INSTANCE, adapterSource1, adapterSource2);

		
		Assert.assertEquals(0, compositeAdapter.getAll().size());
		Assert.assertEquals(0, adapterM1.getAll().size());
		Assert.assertEquals(0, adapterM2.getAll().size());
		Assert.assertEquals(0, opaqueAdapter.getAll().size());
		
		Item foo = makeItem("foo");
		Item bar = makeItem("bar");
		Item myFoo = makeItem("myFoo");
		Item myBar = makeItem("myBar");
		
		compositeAdapter.add(foo);
		compositeAdapter.add(bar);
		compositeAdapter.add(myFoo);
		compositeAdapter.add(myBar);
		
		Assert.assertEquals(4, compositeAdapter.getAll().size());
		Assert.assertEquals(1, adapterM1.getAll().size());
		Assert.assertEquals(1, adapterM2.getAll().size());
		Assert.assertEquals(2, opaqueAdapter.getAll().size());
		
		TestHelper.assertItem(foo, compositeAdapter, adapterM1, adapterSource1);
		TestHelper.assertItem(bar, compositeAdapter, adapterM2, adapterSource2);
		TestHelper.assertItem(myFoo, compositeAdapter, opaqueAdapter);
		TestHelper.assertItem(myBar, compositeAdapter, opaqueAdapter);
		
	}
	
	private Item makeItem(String headerNode) {
		return  makeItem(headerNode, new Date());
	}
	
	private Item makeItem(String headerNode, Date date) {
		String id = IdGenerator.INSTANCE.newID();
		Element payload = TestHelper.makeElement("<payload><"+headerNode+"><id>"+id+"</id><name>"+id+"</name></"+headerNode+"></payload>");
		Item item = new Item(new XMLContent(id, id, id, payload), new Sync(id).update(NullIdentityProvider.INSTANCE.getAuthenticatedUser(), date));
		return item;
	}
	
	private class MockSyncAdapterWithoutSyncAware implements IIdentifiableSyncAdapter{
		private boolean beginWasCalled = false;
		private boolean endWasCalled = false;
		public void beginSync() { this.beginWasCalled = true; }
		public boolean beginSyncWasCalled() {return this.beginWasCalled;}
		public void endSync() {this.endWasCalled = true; }
		public boolean endSyncWasCalled() {return this.endWasCalled;}
		@Override public String getIdName() {return null;}
		@Override public String getType() {return null;}
		@Override public void add(Item item) {}
		@Override public void delete(String id) {}
		@Override public Item get(String id) {return null;}
		@Override public List<Item> getAll() {return null;}
		@Override public List<Item> getAll(IFilter<Item> filter) {return null;}
		@Override public List<Item> getAllSince(Date since) {return null;}
		@Override public List<Item> getAllSince(Date since, IFilter<Item> filter) {return null;}
		@Override public List<Item> getConflicts() {return null;}
		@Override public String getFriendlyName() {return null;}
		@Override public void update(Item item) {}
		@Override public void update(Item item, boolean resolveConflicts) {}		
	}
	
	private class MockSyncAdapter extends MockSyncAdapterWithoutSyncAware implements ISyncAware{
		@Override public void endSync() {super.endSync();}		
		@Override public void beginSync() {super.beginSync();}
	}
}
