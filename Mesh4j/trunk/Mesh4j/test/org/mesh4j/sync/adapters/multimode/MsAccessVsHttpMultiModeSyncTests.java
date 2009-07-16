package org.mesh4j.sync.adapters.multimode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateMsAccessToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsAccessVsHttpMultiModeSyncTests {
	
	@Test
	public void shouldSyncMultiTables() throws IOException{
		String serverURL = "http://localhost:8080/mesh4x/feeds";
		String fileName= getMsAccessFileNameToTest();
		
		File file = new File(fileName);
		String meshGroup = file.getName().substring(0, file.getName().length() -4);
		
		SyncProcess syncProcess = SyncProcess.makeSyncProcessForSyncMsAccessVsHttp(fileName, serverURL, meshGroup, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
				
		// assert sycn process creation
		Set<String> tables = MsAccessHibernateSyncAdapterFactory.getTableNames(fileName);
		for (String tableName : tables) {
			String rdfClassName = MsAccessRDFSchemaGenerator.getEntityName(tableName);
			
			SyncTask syncTask = syncProcess.getSyncTask(rdfClassName);
			
			Assert.assertNotNull(syncTask);
			Assert.assertTrue(syncTask.isReadyToSync());

			HttpSyncAdapter target = (HttpSyncAdapter) syncTask.getTarget();
			Assert.assertEquals(serverURL+"/"+meshGroup+"/"+rdfClassName, target.getURL());
			
			SplitAdapter source = (SplitAdapter) syncTask.getSource();
			
			Assert.assertNotNull(source);
			Assert.assertEquals(rdfClassName, source.getContentAdapter().getType());
			Assert.assertTrue(source.getContentAdapter() instanceof HibernateContentAdapter);
			Assert.assertTrue(((HibernateContentAdapter)source.getContentAdapter()).getMapping() instanceof HibernateMsAccessToRDFMapping);


		}

		// sync
		syncProcess.synchronize(null); 
				
		// assert sync result
		List<SyncTask> syncTasks = syncProcess.getSyncTasks();
		for (SyncTask syncTask : syncTasks) {
			Assert.assertFalse(syncTask.isFailed());
			Assert.assertFalse(syncTask.isError());
			Assert.assertTrue(syncTask.isSuccessfully());
			
			TestHelper.assertSyncResult(syncTask.getSyncEngine(), syncTask.getConflicts());
		}
	}
	
	private String getMsAccessFileNameToTest() {
		try{
			String localFileName = this.getClass().getResource("DevDB2003.mdb").getFile();
			String fileName = TestHelper.fileName("msAccess"+IdGenerator.INSTANCE.newID().substring(0, 5)+".mdb");
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
