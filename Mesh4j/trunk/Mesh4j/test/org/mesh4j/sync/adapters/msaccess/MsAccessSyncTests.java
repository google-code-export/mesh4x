package org.mesh4j.sync.adapters.msaccess;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateMappingGenerator;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccess;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessJackcessSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsAccessSyncTests {

	@Test
	public void executeSyncWithHibernate() throws Exception{

		String sourceFileName = getMsAccessFileNameToTest("epiinfo.mdb");
		String targetFileName = getMsAccessFileNameToTest("epiinfoEmpty.mdb");
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), null);
		
		ISyncAdapter syncAdapterA = factory.createSyncAdapterFromFile(
			"Oswego",
			sourceFileName, 
			"Oswego",
			NullIdentityProvider.INSTANCE);
			
		ISyncAdapter syncAdapterB = factory.createSyncAdapterFromFile(
			"Oswego",
			targetFileName, 
			"Oswego",
			NullIdentityProvider.INSTANCE);
	
		SyncEngine syncEngine = new SyncEngine(syncAdapterA, syncAdapterB);
		
		TestHelper.assertSync(syncEngine);
		
		FileUtils.delete(sourceFileName);
		FileUtils.delete(targetFileName);
	}
	
	@Test
	public void executeSyncWithJackcess() throws Exception{
			
		MsAccess msaccessSource = new MsAccess(getMsAccessFileNameToTest("epiinfo2000.mdb"));
		SplitAdapter syncAdapterA = MsAccessJackcessSyncAdapterFactory.createSyncAdapter(
			msaccessSource, 
			"Oswego",
			NullIdentityProvider.INSTANCE,
			"http://localhost:8080/mesh4x/feeds");
		
		MsAccess msaccessTarget = new MsAccess(getMsAccessFileNameToTest("epiinfo2000empty.mdb"));
		SplitAdapter syncAdapterB = MsAccessJackcessSyncAdapterFactory.createSyncAdapter(
			msaccessTarget, 
			"Oswego",
			NullIdentityProvider.INSTANCE,
			"http://localhost:8080/mesh4x/feeds");
	
		SyncEngine syncEngine = new SyncEngine(syncAdapterA, syncAdapterB);
		
		TestHelper.assertSync(syncEngine);
		TestHelper.assertSync(syncEngine);
		
		FileUtils.delete(msaccessSource.getFileName());
		FileUtils.delete(msaccessTarget.getFileName());

	}
	
	@Test
	public void shouldCreateSyncTableIfAbsent() throws Exception{
		String mdbFileName = getMsAccessFileNameToTest("epiinfo.mdb");
		String syncTableName = MsAccessHibernateMappingGenerator.getSyncTableName(IdGenerator.INSTANCE.newID().substring(0, 5));
		
		Assert.assertFalse(MsAccessHelper.existTable(mdbFileName, syncTableName));
		MsAccessHelper.createSyncTableIfAbsent(mdbFileName, syncTableName);
		Assert.assertTrue(MsAccessHelper.existTable(mdbFileName, syncTableName));
		
		Set<String> columnNames = MsAccessHelper.getTableColumnNames(mdbFileName, syncTableName);
		
		Assert.assertEquals(5, columnNames.size());
		Assert.assertTrue(columnNames.contains("sync_id"));
		Assert.assertTrue(columnNames.contains("entity_name"));
		Assert.assertTrue(columnNames.contains("entity_id"));
		Assert.assertTrue(columnNames.contains("entity_version"));
		Assert.assertTrue(columnNames.contains("sync_data"));
		
		FileUtils.delete(mdbFileName);
	}

	@Test
	public void shouldNotCreateSyncTableIfNotAbsent() throws Exception{
		String mdbFileName = getMsAccessFileNameToTest("epiinfoEmpty.mdb");
		String syncTableName = MsAccessHibernateMappingGenerator.getSyncTableName("Oswego");
		
		Assert.assertTrue(MsAccessHelper.existTable(mdbFileName, syncTableName));
		MsAccessHelper.createSyncTableIfAbsent(mdbFileName, syncTableName);
		Assert.assertTrue(MsAccessHelper.existTable(mdbFileName, syncTableName));
		
		Set<String> columnNames = MsAccessHelper.getTableColumnNames(mdbFileName, syncTableName);
		
		Assert.assertEquals(5, columnNames.size());
		Assert.assertTrue(columnNames.contains("sync_id"));
		Assert.assertTrue(columnNames.contains("entity_name"));
		Assert.assertTrue(columnNames.contains("entity_id"));
		Assert.assertTrue(columnNames.contains("entity_version"));
		Assert.assertTrue(columnNames.contains("sync_data"));
		
		FileUtils.delete(mdbFileName);
	}
	
	private String getMsAccessFileNameToTest(String localName) {
		try{
			String localFileName = this.getClass().getResource(localName).getFile();
			String fileName = TestHelper.fileName(localName+IdGenerator.INSTANCE.newID()+".mdb");
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
}
