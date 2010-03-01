package org.mesh4j.sync.adapters.feed.zip;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.ZipUtils;

public class ZipFeedsSyncAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenFileNameIsNull(){
		new ZipFeedsSyncAdapter(null, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenFileNameIsEmpty(){
		new ZipFeedsSyncAdapter("", NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenFileNameIsNotZip(){
		new ZipFeedsSyncAdapter("myFile.txt", NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenIdentityProviderIsNull(){
		new ZipFeedsSyncAdapter("myFile.zip", null, TestHelper.baseDirectoryForTest());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenBaseDirIsNull(){
		new ZipFeedsSyncAdapter("myFile.zip", NullIdentityProvider.INSTANCE, null);
	}
		
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenBaseDirIsEmpty(){
		new ZipFeedsSyncAdapter("myFile.zip", NullIdentityProvider.INSTANCE, "");
	}
	
	@Test
	public void shouldCreate() throws IOException{
		String zipFileName = FileUtils.getFileName(TestHelper.baseDirectoryForTest(), "myFile.zip");
		ZipFeedsSyncAdapter zipAdapter = new ZipFeedsSyncAdapter(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		Assert.assertNotNull(zipAdapter);
		Assert.assertNull(zipAdapter.getCompositeAdapter());
		
		Assert.assertNotNull(zipAdapter.getTempFolder());
		Assert.assertFalse(zipAdapter.getTempFolder().exists());
		Assert.assertEquals(new File(TestHelper.baseDirectoryForTest() + "//myFile").getCanonicalPath(), zipAdapter.getTempFolder().getCanonicalPath());
		Assert.assertEquals("myFile", zipAdapter.getTempFolder().getName());
	}
	
	@Test
	public void shouldBeginEndSyncNewEmptyFile(){
		String zipFileName = FileUtils.getFileName(TestHelper.baseDirectoryForTest(), "myFile.zip");
		ZipFeedsSyncAdapter zipAdapter = new ZipFeedsSyncAdapter(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		Assert.assertNotNull(zipAdapter);
		Assert.assertNull(zipAdapter.getCompositeAdapter());
		Assert.assertNotNull(zipAdapter.getTempFolder());
		Assert.assertFalse(zipAdapter.getTempFolder().exists());
		
		zipAdapter.beginSync();
		
		Assert.assertTrue(zipAdapter.getTempFolder().exists());
		Assert.assertEquals(0, zipAdapter.getTempFolder().list().length);
		Assert.assertNotNull(zipAdapter.getCompositeAdapter());
		Assert.assertEquals(0, zipAdapter.getCompositeAdapter().getAdapters().size());
		Assert.assertTrue(zipAdapter.getCompositeAdapter().getOpaqueAdapter() instanceof ZipFeedsOpaqueSyncAdapter);
		Assert.assertEquals(zipAdapter, ((ZipFeedsOpaqueSyncAdapter)zipAdapter.getCompositeAdapter().getOpaqueAdapter()).getZipAdapter());
		
		zipAdapter.endSync();

		Assert.assertFalse(zipAdapter.getZipFile().exists());  // no entries, empty file
		Assert.assertFalse(zipAdapter.getTempFolder().exists());
	}
	
	@Test
	public void shouldBeginEndSync() throws IOException{
		
		// create zip file
		String localFileName = this.getClass().getResource("myZip.zip").getFile();
		File localZipFile = new File(localFileName);
		byte[] bytes = FileUtils.read(localFileName);
		
		File zipFile = TestHelper.makeFileAndDeleteIfExists("myZip.zip");
		FileUtils.write(zipFile.getCanonicalPath(), bytes);
		
		// create adapter
		ZipFeedsSyncAdapter zipAdapter = new ZipFeedsSyncAdapter(zipFile.getCanonicalPath(), NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		Assert.assertNotNull(zipAdapter);
		Assert.assertNull(zipAdapter.getCompositeAdapter());
		Assert.assertNotNull(zipAdapter.getTempFolder());
		Assert.assertFalse(zipAdapter.getTempFolder().exists());
		Assert.assertTrue(zipAdapter.getZipFile().exists());
		
		// begin sync - read composite adapter, create tmp folder
		zipAdapter.beginSync();
		
		Assert.assertTrue(zipAdapter.getTempFolder().exists());
		Assert.assertEquals(3, zipAdapter.getTempFolder().list().length);
		Assert.assertNotNull(zipAdapter.getCompositeAdapter());
		Assert.assertEquals(3, zipAdapter.getCompositeAdapter().getAdapters().size());
		Assert.assertTrue(zipAdapter.getCompositeAdapter().getOpaqueAdapter() instanceof ZipFeedsOpaqueSyncAdapter);
		Assert.assertEquals(zipAdapter, ((ZipFeedsOpaqueSyncAdapter)zipAdapter.getCompositeAdapter().getOpaqueAdapter()).getZipAdapter());
		
		Assert.assertEquals(2, zipAdapter.getCompositeAdapter().getAdapter("sheet1").getAll().size());
		Assert.assertEquals(2, zipAdapter.getCompositeAdapter().getAdapter("sheet2").getAll().size());
		Assert.assertEquals(2, zipAdapter.getCompositeAdapter().getAdapter("sheet3").getAll().size());
		Assert.assertEquals(6, zipAdapter.getAll().size());
		
		// end sync - remove tmp folder, write zip
		zipAdapter.endSync();

		Assert.assertTrue(zipAdapter.getZipFile().exists());
		Assert.assertFalse(zipAdapter.getTempFolder().exists());

		// entries
		Map<String, byte[]> localEntries = ZipUtils.getEntries(localZipFile);
		Map<String, byte[]> newEntries = ZipUtils.getEntries(zipAdapter.getZipFile());
		Assert.assertEquals(localEntries.size(), newEntries.size());
		
		for (String entry : localEntries.keySet()) {
			Assert.assertEquals(entry, new String(localEntries.get(entry)), new String(newEntries.get(entry)));
		}
		
	}
}