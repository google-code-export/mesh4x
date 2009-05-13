package org.mesh4j.sync.adapters.folder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.adapters.file.FileSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class FolderSyncAdapterFactoryTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFolderNameIsNull(){
		FolderSyncAdapterFactory.createFolderAdapter(null, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFolderNameIsEmpty(){
		FolderSyncAdapterFactory.createFolderAdapter("", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdentityProviderIsNull(){
		FolderSyncAdapterFactory.createFolderAdapter(TestHelper.baseDirectoryForTest(), null, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdGeneratorIsNull(){
		FolderSyncAdapterFactory.createFolderAdapter(TestHelper.baseDirectoryForTest(), NullIdentityProvider.INSTANCE, null);
	}


	@Test
	public void shouldCreateAdapterCreateFolderAndSyncFileIfDoesNotExists() throws IOException{
		String folderName = TestHelper.baseDirectoryForTest() + File.separator + IdGenerator.INSTANCE.newID();
		File folder = new File(folderName);
		
		File syncFile = new File(folderName + File.separator + folder.getName() + "_sync.xml");
		
		Assert.assertFalse(folder.exists());
		Assert.assertFalse(syncFile.exists());
		
		SplitAdapter adapter = FolderSyncAdapterFactory.createFolderAdapter(folderName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		Assert.assertTrue(adapter.getContentAdapter() instanceof FolderContentAdapter);
		FolderContentAdapter contentAdapter = (FolderContentAdapter)adapter.getContentAdapter();
		Assert.assertEquals(folder.getCanonicalPath(), contentAdapter.getFolder().getCanonicalPath());
		Assert.assertNotNull(contentAdapter.getFilenameFilter());
		FilenameFilter filter = contentAdapter.getFilenameFilter();
		Assert.assertFalse(filter.accept(folder, syncFile.getName()));
		
		Assert.assertTrue(adapter.getSyncRepository() instanceof FileSyncRepository);
		FileSyncRepository syncRepo = (FileSyncRepository) adapter.getSyncRepository();
		Assert.assertEquals(syncFile.getCanonicalPath(), syncRepo.getFile().getCanonicalPath());
		
		Assert.assertTrue(folder.exists());
		Assert.assertTrue(syncFile.exists());
		
		syncFile.delete();
		folder.delete();
	}

	@Test
	public void shouldCreateAdapter() throws IOException{
		String folderName = TestHelper.baseDirectoryForTest() + File.separator + IdGenerator.INSTANCE.newID();
		File folder = new File(folderName);
		folder.mkdirs();
		
		File syncFile = new File(folderName + File.separator + folder.getName() + "_sync.xml");
		FileSyncRepository.initializeFile(syncFile);
		
		Assert.assertTrue(folder.exists());
		Assert.assertTrue(syncFile.exists());
		
		SplitAdapter adapter = FolderSyncAdapterFactory.createFolderAdapter(folderName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Assert.assertTrue(adapter.getContentAdapter() instanceof FolderContentAdapter);
		FolderContentAdapter contentAdapter = (FolderContentAdapter)adapter.getContentAdapter();
		Assert.assertEquals(folder.getCanonicalPath(), contentAdapter.getFolder().getCanonicalPath());
		Assert.assertNotNull(contentAdapter.getFilenameFilter());
		FilenameFilter filter = contentAdapter.getFilenameFilter();
		Assert.assertFalse(filter.accept(folder, syncFile.getName()));
		
		Assert.assertTrue(adapter.getSyncRepository() instanceof FileSyncRepository);
		FileSyncRepository syncRepo = (FileSyncRepository) adapter.getSyncRepository();
		Assert.assertEquals(syncFile.getCanonicalPath(), syncRepo.getFile().getCanonicalPath());
		
		Assert.assertTrue(folder.exists());
		Assert.assertTrue(syncFile.exists());
		
		syncFile.delete();
		folder.delete();
	}

}
