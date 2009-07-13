package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccess;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessSyncRepository;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MsAccessSyncRepositoryTests {
	

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileNameIsNull(){
		new MsAccessSyncRepository(null, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileNameIsEmpty(){
		new MsAccessSyncRepository(new MsAccess(""), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenTableNameIsNull(){
		new MsAccessSyncRepository(new MsAccess("myfile.mdb"), null, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenTableNameIsEmpty(){
		new MsAccessSyncRepository(new MsAccess("myfile.mdb"), "", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenIdentityProviderIsNull(){
		new MsAccessSyncRepository(new MsAccess("myfile.mdb"), "user_sync", null, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenIdGeneratorIsNull(){
		new MsAccessSyncRepository(new MsAccess("myfile.mdb"), "user_sync", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileIsNotMDB(){
		new MsAccessSyncRepository(new MsAccess("myfile.mdz"), "user_sync", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileDoesNotExist(){
		new MsAccessSyncRepository(new MsAccess("myfile.mdb"), "user_sync", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test
	public void shouldCreateSyncRepoAutomaticalyAddSyncTable() throws Exception{
		File file = makeEmptyMDBFile();
				
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		msaccess.open();
		
		Table table = msaccess.getTable("user_sync");
		Assert.assertNull(table);
		msaccess.close();
		
		new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		msaccess.open();
		table = msaccess.getTable("user_sync");
		Assert.assertNotNull(table);
		msaccess.close();
		
		FileUtils.delete(file);
	}

	@Test
	public void shouldGenereateNEWid() throws IOException{
		
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MyIdentityProvider myIdGenerator = new MyIdentityProvider();
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, myIdGenerator);
		String syncId = repo.newSyncID(null);
		String syncId1 = repo.newSyncID(null);
		
		Assert.assertNotNull(syncId);
		Assert.assertNotNull(syncId1);
		Assert.assertFalse(syncId.equals(syncId1));
		Assert.assertTrue(myIdGenerator.callWasExecuted);
		
		FileUtils.delete(file);
	}
	
	@Test
	public void shouldGetSyncReturnsNullWhenTableIsEmpty() throws IOException{
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		repo.beginSync();
		
		SyncInfo syncInfo = repo.get(IdGenerator.INSTANCE.newID());
		repo.endSync();
		
		Assert.assertNull(syncInfo);
		
		FileUtils.delete(file);
		
	}
	
	@Test
	public void shouldGetSyncReturnsNullWhenSyncIdDoesNotExistInTable() throws Exception{
		
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		msaccess.open();
		
		Table table = msaccess.getTable("user_sync");
		table.addRow(table.asRow(makeNewSyncInfoRowMap()));
		table.addRow(table.asRow(makeNewSyncInfoRowMap()));
		Assert.assertEquals(2, table.getRowCount());
		
		msaccess.close();
		
		repo.beginSync();
				
		SyncInfo syncInfo = repo.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(syncInfo);
		
		repo.endSync();
		
		FileUtils.delete(file);
	}
	
	@Test
	public void shouldGetSync() throws Exception{
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		msaccess.open();
		
		SyncInfo syncInfo = makeNewSyncInfo();
		
		Table table = msaccess.getTable("user_sync");
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo)));
		table.addRow(table.asRow(makeNewSyncInfoRowMap()));
		Assert.assertEquals(2, table.getRowCount());
		
		msaccess.close();
		
		repo.beginSync();
				
		SyncInfo syncInfo2 = repo.get(syncInfo.getSyncId());

		repo.endSync();

		assertSyncInfo(syncInfo, syncInfo2);
		
		FileUtils.delete(file);
	}

	
	@Test
	public void shouldGetAllSyncs() throws Exception{
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		msaccess.open();
		
		SyncInfo syncInfo = makeNewSyncInfo();
		SyncInfo syncInfo2 = makeNewSyncInfo();
		
		Table table = msaccess.getTable("user_sync");
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo)));
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo2)));
		Assert.assertEquals(2, table.getRowCount());
		
		msaccess.close();
		
		repo.beginSync();
				
		List<SyncInfo> syncinfos = repo.getAll("user");

		repo.endSync();

		Assert.assertNotNull(syncinfos);
		Assert.assertEquals(2, syncinfos.size());
		
		SyncInfo syncInfoLoaded1 = syncinfos.get(0);
		SyncInfo syncInfoLoaded2 = syncinfos.get(1);
		
		assertSyncInfo(syncInfo, syncInfoLoaded1);
		assertSyncInfo(syncInfo2, syncInfoLoaded2);
		
		FileUtils.delete(file);
	}
	
	@Test
	public void shouldGetAllReturnsEmptyWhenTypeIsNotEqual() throws Exception{
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		msaccess.open();
		
		SyncInfo syncInfo = makeNewSyncInfo();
		SyncInfo syncInfo2 = makeNewSyncInfo();
		
		Table table = msaccess.getTable("user_sync");
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo)));
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo2)));
		Assert.assertEquals(2, table.getRowCount());
		
		msaccess.close();
		
		repo.beginSync();
				
		List<SyncInfo> syncinfos = repo.getAll("user123");

		repo.endSync();
		
		Assert.assertNotNull(syncinfos);
		Assert.assertEquals(0, syncinfos.size());
		
		FileUtils.delete(file);
	}
	
	@Test
	public void shouldAdd() throws Exception{
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		msaccess.open();
		
		SyncInfo syncInfo = makeNewSyncInfo();
		SyncInfo syncInfo2 = makeNewSyncInfo();
		SyncInfo syncInfo3 = makeNewSyncInfo();
		
		Table table = msaccess.getTable("user_sync");
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo)));
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo2)));
		Assert.assertEquals(2, table.getRowCount());
		
		msaccess.close();
		
		repo.beginSync();
		repo.save(syncInfo3);
		repo.endSync();
		
		repo.beginSync();
		List<SyncInfo> syncinfos = repo.getAll("user");
		repo.endSync();

		Assert.assertNotNull(syncinfos);
		Assert.assertEquals(3, syncinfos.size());
		
		SyncInfo syncInfoLoaded1 = syncinfos.get(0);
		SyncInfo syncInfoLoaded2 = syncinfos.get(1);
		SyncInfo syncInfoLoaded3 = syncinfos.get(2);
		
		assertSyncInfo(syncInfo, syncInfoLoaded1);
		assertSyncInfo(syncInfo2, syncInfoLoaded2);
		assertSyncInfo(syncInfo3, syncInfoLoaded3);
		
		FileUtils.delete(file);
	}

	@Test
	public void shouldUpdate() throws Exception{
		File file = makeEmptyMDBFile();
		
		MsAccess msaccess = new MsAccess(file.getAbsolutePath());
		
		MsAccessSyncRepository repo = new MsAccessSyncRepository(msaccess, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		msaccess.open();
		
		SyncInfo syncInfo = makeNewSyncInfo();
		SyncInfo syncInfo2 = makeNewSyncInfo();
		
		Table table = msaccess.getTable("user_sync");
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo)));
		table.addRow(table.asRow(makeNewSyncInfoRowMap(syncInfo2)));
		Assert.assertEquals(2, table.getRowCount());
		
		msaccess.close();
		
		syncInfo2.setVersion(555);
		
		repo.beginSync();
		repo.save(syncInfo2);
		repo.endSync();
		
		repo.beginSync();
		List<SyncInfo> syncinfos = repo.getAll("user");
		repo.endSync();

		Assert.assertNotNull(syncinfos);
		Assert.assertEquals(2, syncinfos.size());
		
		SyncInfo syncInfoLoaded1 = syncinfos.get(0);
		SyncInfo syncInfoLoaded2 = syncinfos.get(1);
		
		assertSyncInfo(syncInfo, syncInfoLoaded1);
		assertSyncInfo(syncInfo2, syncInfoLoaded2);
		
		FileUtils.delete(file);
	}
	
	
	// PRIVATE METHODS
	
	private Map<String, Object> makeNewSyncInfoRowMap() {
		SyncInfo syncInfo = makeNewSyncInfo();
		return makeNewSyncInfoRowMap(syncInfo);
	}
	
	private Map<String, Object> makeNewSyncInfoRowMap(SyncInfo syncInfo) {
		LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
		rowMap.put( MsAccessSyncRepository.COLUMN_NAME_SYNC_ID, syncInfo.getSyncId());
		rowMap.put( MsAccessSyncRepository.COLUMN_NAME_ENTITY_NAME, syncInfo.getType());
		rowMap.put( MsAccessSyncRepository.COLUMN_NAME_ENTITY_ID, syncInfo.getId());
		rowMap.put( MsAccessSyncRepository.COLUMN_NAME_VERSION, String.valueOf(syncInfo.getVersion()));	
		
		Element syncElement = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		String xml = syncElement.asXML();
		rowMap.put( MsAccessSyncRepository.COLUMN_NAME_SYNC, xml);
		return rowMap;
	}

	private SyncInfo makeNewSyncInfo() {
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		SyncInfo syncInfo = new SyncInfo(sync, "user", IdGenerator.INSTANCE.newID(), 33);
		return syncInfo;
	}

	private class MyIdentityProvider implements IIdGenerator{
		
		boolean callWasExecuted = false;
		
		@Override
		public String newID() {
			this.callWasExecuted = true;
			return IdGenerator.INSTANCE.newID();
		}
	}

	private void assertSyncInfo(SyncInfo syncInfo, SyncInfo syncInfo2) {
		Assert.assertNotNull(syncInfo2);
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo2.getSyncId());
		Assert.assertEquals(syncInfo.getId(), syncInfo2.getId());
		Assert.assertEquals(syncInfo.getType(), syncInfo2.getType());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo2.getVersion());
		Assert.assertTrue(syncInfo.getSync().equals(syncInfo2.getSync()));
	}
	
	private File makeEmptyMDBFile() throws IOException {
		String fileName = TestHelper.fileName("syncrepo_"+IdGenerator.INSTANCE.newID()+".mdb");
		File file = new File(fileName);
		Database db = Database.create(file);
		db.close();
		return file;
	}
}
