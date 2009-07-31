package org.mesh4j.sync.adapters.feed.zip;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;

public class ZipFeedsOpaqueSyncAdapterTests {
	
	private ZipFeedsSyncAdapter adapter;
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenZipAdapterIsNull(){
		new ZipFeedsOpaqueSyncAdapter(null);
	}
	
	
	@Test
	public void shouldDeleteNothingTodo() throws IOException {
		
		List<Item> items = adapter.getAll();
		
		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		Item item = items.get(0);
		Assert.assertFalse(item.isDeleted());
		opaqueAdapter.delete(item.getSyncId());
		Assert.assertFalse(adapter.get(item.getSyncId()).isDeleted());
		
	}

	@Test
	public void shouldGetReturnsNull() throws IOException {
		
		List<Item> items = adapter.getAll();
		Item item = items.get(0);
		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		
		Assert.assertNull(opaqueAdapter.get(item.getSyncId()));
	}

	@Test
	public void shouldGetAllReturnsEmptyCollection() throws IOException {
		
		Assert.assertEquals(6, adapter.getAll().size());

		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		
		Assert.assertEquals(0, opaqueAdapter.getAll().size());

	}

	@Test
	public void shouldGetAllFilterReturnsEmptyCollection() throws IOException {
		IFilter<Item> filter = new IFilter<Item>(){
			@Override
			public boolean applies(Item obj) {
				return true;
			}
		};
		
		
		Assert.assertEquals(6, adapter.getAll(filter).size());

		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		
		Assert.assertEquals(0, opaqueAdapter.getAll(filter).size());
	}

	@Test
	public void shouldGetAllSinceEmptyCollection() throws IOException {
		
		Assert.assertEquals(6, adapter.getAllSince(null).size());

		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		
		Assert.assertEquals(0, opaqueAdapter.getAllSince(null).size());
	}

	@Test
	public void shouldGetAllSinceFilterReturnsEmptyCollection() throws IOException {
		
		IFilter<Item> filter = new IFilter<Item>(){
			@Override
			public boolean applies(Item obj) {
				return true;
			}
		};
		
		
		Assert.assertEquals(6, adapter.getAllSince(null, filter).size());

		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		
		Assert.assertEquals(0, opaqueAdapter.getAllSince(null, filter).size());
	}

	@Test
	public void shouldGetConflictsReturnsEmptyCollection() throws IOException {
		
		Assert.assertEquals(0, adapter.getConflicts().size());

		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		
		Assert.assertEquals(0, opaqueAdapter.getConflicts().size());
	}
	
	@Test
	public void shouldAddCreateNewSyncAdapter(){
		String type = "myType_"+IdGenerator.INSTANCE.newID();
		String syncId = IdGenerator.INSTANCE.newID();
		Element payload = DocumentHelper.createElement(type);
		
		XMLContent content = new XMLContent(syncId, "title", "desc", payload);
		Item item = new Item(content, new Sync(syncId, "jmt", new Date(), false));
		
		Assert.assertNull(adapter.getCompositeAdapter().getAdapter(type));
		
		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		opaqueAdapter.add(item);
	
		IIdentifiableSyncAdapter syncAdapter = adapter.getCompositeAdapter().getAdapter(type); 
		Assert.assertNotNull(syncAdapter);
		Assert.assertEquals(1, syncAdapter.getAll().size());
		Assert.assertTrue(item.equals(syncAdapter.get(syncId)));
	}
	
	@Test
	public void shouldAddUpdateNewSyncAdapter(){
		String type = "myType_"+IdGenerator.INSTANCE.newID();
		String syncId = IdGenerator.INSTANCE.newID();
		Element payload = DocumentHelper.createElement(type);
		
		XMLContent content = new XMLContent(syncId, "title", "desc", payload);
		Item item = new Item(content, new Sync(syncId, "jmt", new Date(), false));
		
		Assert.assertNull(adapter.getCompositeAdapter().getAdapter(type));
		
		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		opaqueAdapter.update(item);
	
		IIdentifiableSyncAdapter syncAdapter = adapter.getCompositeAdapter().getAdapter(type); 
		Assert.assertNotNull(syncAdapter);
		Assert.assertEquals(1, syncAdapter.getAll().size());
		Assert.assertTrue(item.equals(syncAdapter.get(syncId)));
	}
	
	@Test
	public void shouldAddUpdateConflictsNewSyncAdapter(){
		String type = "myType_"+IdGenerator.INSTANCE.newID();
		String syncId = IdGenerator.INSTANCE.newID();
		Element payload = DocumentHelper.createElement(type);
		
		XMLContent content = new XMLContent(syncId, "title", "desc", payload);
		Item item = new Item(content, new Sync(syncId, "jmt", new Date(), false));
		
		Assert.assertNull(adapter.getCompositeAdapter().getAdapter(type));
		
		ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(adapter);
		opaqueAdapter.update(item, true);
	
		IIdentifiableSyncAdapter syncAdapter = adapter.getCompositeAdapter().getAdapter(type); 
		Assert.assertNotNull(syncAdapter);
		Assert.assertEquals(1, syncAdapter.getAll().size());
		Assert.assertTrue(item.equals(syncAdapter.get(syncId)));
	}

	@Before
	public void beginSync() throws IOException{
		
		// create zip file
		String localFileName = this.getClass().getResource("myZip.zip").getFile();
		byte[] bytes = FileUtils.read(localFileName);
		
		File zipFile = TestHelper.makeFileAndDeleteIfExists("myZip.zip");
		FileUtils.write(zipFile.getCanonicalPath(), bytes);
		
		this.adapter = new ZipFeedsSyncAdapter(zipFile.getCanonicalPath(), NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		this.adapter.beginSync();
	}
	
	@After
	public void endSync(){
		this.adapter.endSync();
	}
}
