package org.mesh4j.sync.adapters.folder;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.file.FileSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;

public class SyncTests {

	@Test
	public void shouldSyncFolders() throws IOException{
		
		// create adapter source
		File folderSource = makeNewFolder("source_" + IdGenerator.INSTANCE.newID());
		File syncFileSource = makeNewFile(folderSource, IdGenerator.INSTANCE.newID()+"_sync.xml", false);
		File fileSource = makeNewFile(folderSource, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileSource);
		
		File subFolderSource = makeNewFolder(folderSource, IdGenerator.INSTANCE.newID());
		File fileSource2 = makeNewFile(subFolderSource, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileSource2);
		
		File subSubFolderSource = makeNewFolder(subFolderSource, IdGenerator.INSTANCE.newID());
		File fileSource3 = makeNewFile(subSubFolderSource, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileSource3);

		FilesFilter filterSource = new FilesFilter();
		filterSource.excludeFileName(syncFileSource.getName());
		FolderContentAdapter folderContentSource = new FolderContentAdapter(folderSource, filterSource);
		FileSyncRepository syncRepoSource = new FileSyncRepository(syncFileSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SplitAdapter adapterSource = new SplitAdapter(syncRepoSource, folderContentSource, NullIdentityProvider.INSTANCE);
		
		// create adapter target
		File folderTarget = makeNewFolder("target_" + IdGenerator.INSTANCE.newID());
		File syncFileTarget= makeNewFile(folderTarget, IdGenerator.INSTANCE.newID()+"_sync.xml", false);
		File fileTarget = makeNewFile(folderTarget, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileTarget);
		
		FilesFilter filterTarget = new FilesFilter();
		filterTarget.excludeFileName(syncFileTarget.getName());
		FolderContentAdapter folderContentTarget = new FolderContentAdapter(folderTarget, filterTarget);
		FileSyncRepository syncRepoTarget = new FileSyncRepository(syncFileTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SplitAdapter adapterTarget = new SplitAdapter(syncRepoTarget, folderContentTarget, NullIdentityProvider.INSTANCE);
	
		// sync
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.syncAndAssert(syncEngine);
		
		// delete all
		deleteAll(folderSource);
		deleteAll(folderTarget);
		
	}
	
	@Test
	public void shouldSyncFolderVsFeedFile() throws IOException{
		
		// create adapter source
		File folderSource = makeNewFolder("sourceFeed_" + IdGenerator.INSTANCE.newID());
		File syncFileSource = makeNewFile(folderSource, IdGenerator.INSTANCE.newID()+"_sync.xml", false);
		File fileSource = makeNewFile(folderSource, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileSource);
		
		File subFolderSource = makeNewFolder(folderSource, IdGenerator.INSTANCE.newID());
		File fileSource2 = makeNewFile(subFolderSource, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileSource2);
		
		File subSubFolderSource = makeNewFolder(subFolderSource, IdGenerator.INSTANCE.newID());
		File fileSource3 = makeNewFile(subSubFolderSource, IdGenerator.INSTANCE.newID(), true);
		Assert.assertNotNull(fileSource3);

		FilesFilter filterSource = new FilesFilter();
		filterSource.excludeFileName(syncFileSource.getName());
		filterSource.excludeFileName("feed.xml");
		FolderContentAdapter folderContentSource = new FolderContentAdapter(folderSource, filterSource);
		FileSyncRepository syncRepoSource = new FileSyncRepository(syncFileSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SplitAdapter adapterSource = new SplitAdapter(syncRepoSource, folderContentSource, NullIdentityProvider.INSTANCE);
		
		// create adapter target
		String fileName = folderSource.getCanonicalPath() + File.separator + "feed.xml";
		Feed feed = new Feed("test", "test", "");
		FeedAdapter adapterTarget = new FeedAdapter(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
	
		// sync
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.syncAndAssert(syncEngine);
		
		// delete all
		deleteAll(folderSource);

	}
	
	
	// AUX METHODS
	private File makeNewFolder(String folderName) {
		File folder = new File(TestHelper.baseDirectoryRootForTest()+ File.separator + folderName);
		if(!folder.exists()){
			folder.mkdir();
		}
		return folder;
	}
	
	private File makeNewFile(File folder, String fileName, boolean mustInitialize) throws IOException {
		File file = FileUtils.getFile(folder.getCanonicalPath(), fileName);
		if(!file.exists() && mustInitialize){
			FileUtils.write(file.getCanonicalPath(), fileName.getBytes());
		}
		return file;
	}
	
	private File makeNewFolder(File folder, String folderName) throws IOException {
		File subFolder = new File(folder.getCanonicalPath() + File.separator + folderName);
		if(!subFolder.exists()){
			subFolder.mkdir();
		}
		return subFolder;
	}
	
	private void deleteAll(File folder) {
		File[] files = folder.listFiles();
		for (File file : files) {
			if(file.isFile()){
				file.delete();
			} else {
				deleteAll(file);
			}
		}
		
		folder.delete();
	}
}
