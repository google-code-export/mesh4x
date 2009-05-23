package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsExcelSyncRepositoryXLSXTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileNameIsNull(){
		new MsExcelSyncRepository(null, "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileNameIsEmpty(){
		new MsExcelSyncRepository(new MsExcel(""), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenSheetNameIsNull(){
		new MsExcelSyncRepository(new MsExcel("myfile.xml"), null, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenSheetNameIsEmpty(){
		new MsExcelSyncRepository(new MsExcel("myfile.xml"), "", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenIdentityProviderIsNull(){
		new MsExcelSyncRepository(new MsExcel("myfile.xml"), "user_sync", null, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenIdGeneratorIsNull(){
		new MsExcelSyncRepository(new MsExcel("myfile.xml"), "user_sync", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test
	public void shouldCreateSyncRepoCreateWorkbookWhenFileDoesNotExist() throws IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Workbook workbook = repo.getWorkbook();
		Assert.assertNotNull(workbook);
		assertValidSyncWorkbook(workbook);
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbook() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		Workbook workbook = makeValidSyncWorkbook();
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Workbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
		Assert.assertEquals(workbook.getSheet("user_sync").getLastRowNum(), workbook1.getSheet("user_sync").getLastRowNum());
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbookAndAddSyncSheet() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		Workbook workbook = new HSSFWorkbook();
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Workbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbookAndAddSyncRowHeader() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		Workbook workbook = new HSSFWorkbook();
		workbook.createSheet("user_sync");
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Workbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbookAndAddSyncCells() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("user_sync");
		sheet.createRow(0);
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Workbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
	}
	
	@Test
	public void shouldGenereateNEWid(){
		
		MyIdentityProvider myIdGenerator = new MyIdentityProvider();
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel("myExcel.xlsx"), "user_sync", NullIdentityProvider.INSTANCE, myIdGenerator);
		String syncId = repo.newSyncID(null);
		String syncId1 = repo.newSyncID(null);
		
		Assert.assertNotNull(syncId);
		Assert.assertNotNull(syncId1);
		Assert.assertFalse(syncId.equals(syncId1));
		Assert.assertTrue(myIdGenerator.callWasExecuted);
	}
	
	@Test
	public void shouldGetSyncReturnsNullWhenSheetIsEmpty() throws IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SyncInfo syncInfo = repo.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(syncInfo);
	}
	
	@Test
	public void shouldGetSyncReturnsNullWhenSyncIdDoesNotExistInSheet() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(3, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		SyncInfo syncInfo = repo.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(syncInfo);
	}
	
	@Test
	public void shouldGetSync() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		String syncIdlocal = IdGenerator.INSTANCE.newID();
		addNewSyncRow(1, new Sync(syncIdlocal, "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		String syncId = IdGenerator.INSTANCE.newID();
		addNewSyncRow(2, new Sync(syncId, "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());				
		
		syncIdlocal = IdGenerator.INSTANCE.newID();
		addNewSyncRow(3, new Sync(syncIdlocal, "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		SyncInfo syncInfo = repo.get(syncId);
		Assert.assertNotNull(syncInfo);
	}
	
	@Test
	public void shouldGetAllSyncs() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(3, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(3, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllReturnsEmpty() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		repo.beginSync();
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed1", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed2", "1", 123, repo.getWorkbook());
		addNewSyncRow(3, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed3", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
		
		repo.endSync();
		
	}
	
	@Test
	public void shouldGetAllReturnsElements() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		repo.beginSync();
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed1", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed2", "1", 123, repo.getWorkbook());
		addNewSyncRow(3, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed3", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
		
		repo.endSync();
		
		repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		repo.beginSync();
		
		addNewSyncRow(4, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed1", "1", 123, repo.getWorkbook());
		addNewSyncRow(5, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed2", "1", 123, repo.getWorkbook());
		addNewSyncRow(6, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed3", "1", 123, repo.getWorkbook());
		
		syncInfos = repo.getAll("myFeed1");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		repo.endSync();
	}
	
	@Test
	public void shouldGetAllReturnsOneRow() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed1", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(3, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed3", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(1, syncInfos.size());
	}
	
	@Test
	public void shouldAdd() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());				
		addNewSyncRow(3, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
	
		Assert.assertEquals(3, repo.getAll("myFeed").size());
		
		SyncInfo syncInfo = new SyncInfo(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123);
		repo.save(syncInfo);

		Assert.assertEquals(4, repo.getAll("myFeed").size());

		SyncInfo syncInfo1 = repo.get(syncInfo.getSyncId());
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
	}

	@Test
	public void shouldUpdate() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(3, sync, "myFeed", "1", 123, repo.getWorkbook());
	
		Assert.assertEquals(3, repo.getAll("myFeed").size());
		
		sync.update("jit", new Date());
		SyncInfo syncInfo = new SyncInfo(sync, "myFeed", "1", 123);
		repo.save(syncInfo);

		Assert.assertEquals(3, repo.getAll("myFeed").size());

		SyncInfo syncInfo1 = repo.get(syncInfo.getSyncId());
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
	}
	
	@Test
	public void shouldFileDoesNotCreatedBecauseEndSyncIsNotExecuted() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(3, sync, "myFeed", "1", 123, repo.getWorkbook());
	
		Assert.assertEquals(3, repo.getAll("myFeed").size());
		
		sync.update("jit", new Date());
		SyncInfo syncInfo = new SyncInfo(sync, "myFeed", "1", 123);
		repo.save(syncInfo);

		Assert.assertEquals(3, repo.getAll("myFeed").size());

		SyncInfo syncInfo1 = repo.get(syncInfo.getSyncId());
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
		
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void shouldFileCreatedWhenEndSyncIsExecuted() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), "user_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(3, sync, "myFeed", "1", 123, repo.getWorkbook());
	
		Assert.assertEquals(3, repo.getAll("myFeed").size());
		
		sync.update("jit", new Date());
		SyncInfo syncInfo = new SyncInfo(sync, "myFeed", "1", 123);
		repo.save(syncInfo);

		Assert.assertEquals(3, repo.getAll("myFeed").size());

		SyncInfo syncInfo1 = repo.get(syncInfo.getSyncId());
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
		
		Assert.assertFalse(file.exists());
		
		repo.beginSync();
		repo.endSync();
		
		Assert.assertTrue(file.exists());
	}
	
	@Test
	public void shouldFileUpdatedWhenEndSyncIsExecuted() throws Exception{
		String sheetName = "user_sync";
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xlsx");
		
		Workbook workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(file.getCanonicalPath());		
		MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		
		addNewSyncRow(1, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, workbook);
		addNewSyncRow(2, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, workbook);
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(3, sync, "myFeed", "1", 123, workbook);
	
		MsExcelUtils.flush(workbook, file.getCanonicalPath());
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), sheetName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		repo.beginSync();
		
		sync.update("jit", new Date());
		SyncInfo syncInfo = new SyncInfo(sync, "myFeed", "1", 123);
		repo.save(syncInfo);

		Assert.assertEquals(3, repo.getAll("myFeed").size());

		SyncInfo syncInfo1 = repo.get(syncInfo.getSyncId());
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
		
		repo.endSync();
		
		Assert.assertTrue(file.exists());
		
		
		syncInfo1 = repo.get(syncInfo.getSyncId());
		
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
		
		
		repo = new MsExcelSyncRepository(new MsExcel(file.getAbsolutePath()), sheetName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		syncInfo1 = repo.get(syncInfo.getSyncId());
		
		Assert.assertNotNull(syncInfo1);
		
		Assert.assertEquals(syncInfo.getSync(), syncInfo1.getSync());
		Assert.assertEquals(syncInfo.getSyncId(), syncInfo1.getSyncId());
		Assert.assertEquals(syncInfo.getType(), syncInfo1.getType());
		Assert.assertEquals(syncInfo.getId(), syncInfo1.getId());
		Assert.assertEquals(syncInfo.getVersion(), syncInfo1.getVersion());
	}
	
	// PRIVATE METHODS
	
	private class MyIdentityProvider implements IIdGenerator{
		
		boolean callWasExecuted = false;
		
		@Override
		public String newID() {
			this.callWasExecuted = true;
			return IdGenerator.INSTANCE.newID();
		}
		
	}
	
	private void addNewSyncRow(int index, Sync sync, String entityName, String entityId, int version, Workbook workbook) throws Exception{
		Sheet sheet = workbook.getSheet("user_sync");
		Row row = sheet.createRow(index);
		
		Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, sync.getId()));
		
		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, entityName));
		
		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, entityId));
		
		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, String.valueOf(version)));
		
		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		
		Element syncElement = SyncInfoParser.convertSync2Element(sync, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, syncElement.asXML()));
	}
	
	private void assertValidSyncWorkbook(Workbook workbook) {
		Sheet sheet = workbook.getSheet("user_sync");
		Assert.assertNotNull(sheet);
		
		Row row = sheet.getRow(0);
		Assert.assertNotNull(row);
		
		Cell cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals(MsExcelSyncRepository.COLUMN_NAME_SYNC_ID, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(1);
		Assert.assertNotNull(cell);
		Assert.assertEquals(MsExcelSyncRepository.COLUMN_NAME_ENTITY_NAME, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(2);
		Assert.assertNotNull(cell);
		Assert.assertEquals(MsExcelSyncRepository.COLUMN_NAME_ENTITY_ID, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(3);
		Assert.assertNotNull(cell);
		Assert.assertEquals(MsExcelSyncRepository.COLUMN_NAME_VERSION, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(4);
		Assert.assertNotNull(cell);
		Assert.assertEquals(MsExcelSyncRepository.COLUMN_NAME_SYNC, cell.getRichStringCellValue().getString());
	}
	
	private Workbook makeValidSyncWorkbook() {
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet("user_sync");
		
		Row row = sheet.createRow(0);
				
		Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, MsExcelSyncRepository.COLUMN_NAME_SYNC_ID));
		
		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, MsExcelSyncRepository.COLUMN_NAME_ENTITY_NAME));
		
		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, MsExcelSyncRepository.COLUMN_NAME_ENTITY_ID));
		
		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, MsExcelSyncRepository.COLUMN_NAME_VERSION));
		
		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, MsExcelSyncRepository.COLUMN_NAME_SYNC));
		
		return workbook;
	}
}
