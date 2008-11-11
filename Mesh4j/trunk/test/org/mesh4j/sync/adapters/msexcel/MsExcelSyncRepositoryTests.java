package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

public class MsExcelSyncRepositoryTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileNameIsNull(){
		new MsExcelSyncRepository(null, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenFileNameIsEmpty(){
		new MsExcelSyncRepository("", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenIdentityProviderIsNull(){
		new MsExcelSyncRepository("myfile.xml", null, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSyncRepoFailsWhenIdGeneratorIsNull(){
		new MsExcelSyncRepository("myfile.xml", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test
	public void shouldCreateSyncRepoCreateWorkbookWhenFileDoesNotExist(){
		String fileName = TestHelper.fileName("myExcel.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HSSFWorkbook workbook = repo.getWorkbook();
		Assert.assertNotNull(workbook);
		assertValidSyncWorkbook(workbook);
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbook() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		HSSFWorkbook workbook = makeValidSyncWorkbook();
		workbook.write(new FileOutputStream(fileName));
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HSSFWorkbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
		Assert.assertEquals(workbook.getSheet(MsExcelSyncRepository.SHEET_NAME).getLastRowNum(), workbook1.getSheet(MsExcelSyncRepository.SHEET_NAME).getLastRowNum());
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbookAndAddSyncSheet() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel3.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook.write(new FileOutputStream(fileName));
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HSSFWorkbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbookAndAddSyncRowHeader() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel1.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook.createSheet(MsExcelSyncRepository.SHEET_NAME);
		workbook.write(new FileOutputStream(fileName));
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HSSFWorkbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
	}
	
	@Test
	public void shouldCreateSyncRepoLoadWorkbookAndAddSyncCells() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel2.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(MsExcelSyncRepository.SHEET_NAME);
		sheet.createRow(0);
		workbook.write(new FileOutputStream(fileName));
		Assert.assertTrue(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HSSFWorkbook workbook1 = repo.getWorkbook();
		Assert.assertNotNull(workbook1);
		assertValidSyncWorkbook(workbook1);
	}
	
	@Test
	public void shouldGenereateNEWid(){
		
		MyIdentityProvider myIdGenerator = new MyIdentityProvider();
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository("myexcel.xls", NullIdentityProvider.INSTANCE, myIdGenerator);
		String syncId = repo.newSyncID(null);
		String syncId1 = repo.newSyncID(null);
		
		Assert.assertNotNull(syncId);
		Assert.assertNotNull(syncId1);
		Assert.assertFalse(syncId.equals(syncId1));
		Assert.assertTrue(myIdGenerator.callWasExecuted);
	}
	
	@Test
	public void shouldGetSyncReturnsNullWhenSheetIsEmpty(){
		String fileName = TestHelper.fileName("myExcel4.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SyncInfo syncInfo = repo.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(syncInfo);
	}
	
	@Test
	public void shouldGetSyncReturnsNullWhenSyncIdDoesNotExistInSheet(){
		String fileName = TestHelper.fileName("myExcel5.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		SyncInfo syncInfo = repo.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(syncInfo);
	}
	
	@Test
	public void shouldGetSync(){
		String fileName = TestHelper.fileName("myExcel6.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		String syncIdlocal = IdGenerator.INSTANCE.newID();
		addNewSyncRow(new Sync(syncIdlocal, "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		String syncId = IdGenerator.INSTANCE.newID();
		addNewSyncRow(new Sync(syncId, "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());				
		
		syncIdlocal = IdGenerator.INSTANCE.newID();
		addNewSyncRow(new Sync(syncIdlocal, "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		SyncInfo syncInfo = repo.get(syncId);
		Assert.assertNotNull(syncInfo);
	}
	
	@Test
	public void shouldGetAllSyncs(){
		String fileName = TestHelper.fileName("myExcel7.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(3, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllReturnsEmpty(){
		String fileName = TestHelper.fileName("myExcel8.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed1", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed2", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed3", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllReturnsOneRow(){
		String fileName = TestHelper.fileName("myExcel9.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed1", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed3", "1", 123, repo.getWorkbook());
		
		List<SyncInfo> syncInfos = repo.getAll("myFeed");
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(1, syncInfos.size());
	}
	
	@Test
	public void shouldAdd(){
		String fileName = TestHelper.fileName("myExcel10.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());				
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
	
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
	public void shouldUpdate(){
		String fileName = TestHelper.fileName("myExcel11.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(sync, "myFeed", "1", 123, repo.getWorkbook());
	
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
	public void shouldFileDoesNotCreatedBecauseEndSyncIsNotExecuted(){
		String fileName = TestHelper.fileName("myExcel11.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(sync, "myFeed", "1", 123, repo.getWorkbook());
	
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
	public void shouldFileCreatedWhenEndSyncIsExecuted(){
		String fileName = TestHelper.fileName("myExcel11.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(sync, "myFeed", "1", 123, repo.getWorkbook());
	
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
		
		repo.endSync();
		
		Assert.assertTrue(file.exists());
	}
	
	@Test
	public void shouldFileUpdatedWhenEndSyncIsExecuted() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel12.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelSyncRepository repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		addNewSyncRow(new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false), "myFeed", "1", 123, repo.getWorkbook());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		addNewSyncRow(sync, "myFeed", "1", 123, repo.getWorkbook());
	
		Assert.assertEquals(3, repo.getAll("myFeed").size());
		
		repo.getWorkbook().write(new FileOutputStream(file));
		Assert.assertTrue(file.exists());
		
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
		
		repo = new MsExcelSyncRepository(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
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
	
	private void addNewSyncRow(Sync sync, String entityName, String entityId, int version, HSSFWorkbook workbook){
		HSSFSheet sheet = workbook.getSheet(MsExcelSyncRepository.SHEET_NAME);
		HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(sync.getId()));
		
		cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(entityName));
		
		cell = row.createCell(2, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(entityId));
		
		cell = row.createCell(3, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(String.valueOf(version)));
		
		cell = row.createCell(4, HSSFCell.CELL_TYPE_STRING);
		
		Element syncElement = SyncInfoParser.convertSync2Element(sync, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		cell.setCellValue(new HSSFRichTextString(syncElement.asXML()));
	}
	
	private void assertValidSyncWorkbook(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet(MsExcelSyncRepository.SHEET_NAME);
		Assert.assertNotNull(sheet);
		
		HSSFRow row = sheet.getRow(0);
		Assert.assertNotNull(row);
		
		HSSFCell cell = row.getCell(0);
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
	
	private HSSFWorkbook makeValidSyncWorkbook() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(MsExcelSyncRepository.SHEET_NAME);
		
		HSSFRow row = sheet.createRow(0);
				
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(MsExcelSyncRepository.COLUMN_NAME_SYNC_ID));
		
		cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(MsExcelSyncRepository.COLUMN_NAME_ENTITY_NAME));
		
		cell = row.createCell(2, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(MsExcelSyncRepository.COLUMN_NAME_ENTITY_ID));
		
		cell = row.createCell(3, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(MsExcelSyncRepository.COLUMN_NAME_VERSION));
		
		cell = row.createCell(4, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(MsExcelSyncRepository.COLUMN_NAME_SYNC));
		
		return workbook;
	}
}
