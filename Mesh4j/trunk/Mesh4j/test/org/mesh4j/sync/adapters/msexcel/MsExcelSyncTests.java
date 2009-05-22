package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsExcelSyncTests {
	
	@Test
	public void executeSync(){
		
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;
		String sheetName = "patient";
		String idColumnName = "id";
		
		MsExcel excelA = new MsExcel(TestHelper.fileName("fileA.xls"));
		MsExcelSyncRepository syncRepoA = new MsExcelSyncRepository(excelA, sheetName+"_sync", identityProvider, idGenerator);
		
		MSExcelToPlainXMLMapping mapperA = new MSExcelToPlainXMLMapping(idColumnName, null);
		MsExcelContentAdapter contentAdapterA = new MsExcelContentAdapter(excelA, mapperA, sheetName);
		SplitAdapter splitAdapterA = new SplitAdapter(syncRepoA, contentAdapterA, identityProvider);

		MsExcel excelB = new MsExcel(TestHelper.fileName("fileB.xls"));
		MsExcelSyncRepository syncRepoB = new MsExcelSyncRepository(excelB, sheetName+"_sync", identityProvider, idGenerator);
		
		MSExcelToPlainXMLMapping mapperB = new MSExcelToPlainXMLMapping(idColumnName, null);
		MsExcelContentAdapter contentAdapterB = new MsExcelContentAdapter(excelB, mapperB, sheetName);
		SplitAdapter splitAdapterB = new SplitAdapter(syncRepoB, contentAdapterB, identityProvider);		

		SyncEngine syncEngine = new SyncEngine(splitAdapterA, splitAdapterB);
		
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		
	}

	@Test
	public void shouldSync() throws DocumentException, IOException{
		
		String sheetName = "patient";
		String idColumnName = "id";
		
		SplitAdapter adapterA = makeSplitAdapter(sheetName, idColumnName, "excelA.xls", "syncA.xls", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, true);
		makeHeader(adapterA);
		adapterA.add(makeNewItem());
		adapterA.add(makeNewItem());
		adapterA.add(makeNewItem());
		adapterA.add(makeNewItem());
		
		SplitAdapter adapterB = makeSplitAdapter(sheetName, idColumnName, "excelB.xls", "syncB.xls", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, true);
		makeHeader(adapterB);
		adapterB.add(makeNewItem());
		
		SyncEngine syncEngine = new SyncEngine(adapterA, adapterB);
		
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	@Test
	public void shouldSyncSameFile() throws DocumentException, IOException{
		
		String sheetName = "patient";
		String idColumnName = "id";
		
		SplitAdapter adapterA = makeSplitAdapter(sheetName, idColumnName, "dataAndSyncA.xls", "dataAndSyncA.xls", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, true);
		makeHeader(adapterA);
		adapterA.add(makeNewItem());
		adapterA.add(makeNewItem());
		adapterA.add(makeNewItem());
		adapterA.add(makeNewItem());
		
		SplitAdapter adapterB = makeSplitAdapter(sheetName, idColumnName, "dataAndSyncB.xls", "dataAndSyncB.xls", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, true);
		
		SyncEngine syncEngine = new SyncEngine(adapterA, adapterB);
		
		TestHelper.syncAndAssert(syncEngine);		
		
		// no changes or updates are produced
		TestHelper.syncAndAssert(syncEngine);
		
		adapterA = makeSplitAdapter(sheetName, idColumnName, "dataAndSyncA.xls", "dataAndSyncA.xls", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, false);		
		adapterB = makeSplitAdapter(sheetName, idColumnName, "dataAndSyncB.xls", "dataAndSyncB.xls", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, false);
		syncEngine = new SyncEngine(adapterA, adapterB);
		
		// no changes or updates are produced
		TestHelper.syncAndAssert(syncEngine);		
		TestHelper.syncAndAssert(syncEngine);
		
		List<Item> items = adapterA.getAll();
		for (Item item : items) {
			Assert.assertEquals(1, item.getSync().getUpdates());
			Assert.assertEquals(1, item.getSync().getUpdatesHistory().size());
			Assert.assertEquals(1, item.getLastUpdate().getSequence());
		}
	}
	
	// PRIVATE METHODS
	private void makeHeader(SplitAdapter adapter) {
		Workbook workbook = ((MsExcelContentAdapter)adapter.getContentAdapter()).getWorkbook();
		Sheet sheet = ((MsExcelContentAdapter)adapter.getContentAdapter()).getSheet();
		Row row = sheet.getRow(0);
			
		Cell cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "name"));
			
		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "age"));
		
		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "country"));
		
		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "city"));
	}
	
	private Item makeNewItem() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
		String xml = "<patient><id>"+id+"</id><name>marcelo</name><age>33</age><country>Argentina</country><city>Brandsen</city></patient>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		
		IContent content = new EntityContent(payload, "patient", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);
		return new Item(content, sync);
	}

	private SplitAdapter makeSplitAdapter(String sheetName, String idColumnName, String contentFileName, String syncFileName, IIdentityProvider identityProvider, IdGenerator idGenerator, boolean mustDeleteFile) throws IOException {
		
		MsExcel contentExcel = null;
		MsExcel syncExcel = null;
		if(contentFileName.equals(syncFileName)){
			File file = getFile(contentFileName, mustDeleteFile);
			contentExcel = new MsExcel(file.getAbsolutePath());
			syncExcel = contentExcel;
		} else {
			File fileData = getFile(contentFileName, mustDeleteFile);
			File fileSync = getFile(syncFileName, mustDeleteFile);
			
			contentExcel = new MsExcel(fileData.getAbsolutePath());
			syncExcel = new MsExcel(fileSync.getAbsolutePath());
		}
		
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(syncExcel, sheetName+"_sync", identityProvider, idGenerator);
		MSExcelToPlainXMLMapping mapper = new MSExcelToPlainXMLMapping(idColumnName, null);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(contentExcel, mapper, sheetName);

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		return splitAdapter;
	}

	private File getFile(String fileName, boolean mustDeleteFile) throws IOException {
		if(mustDeleteFile){
			return TestHelper.makeFileAndDeleteIfExists(fileName);
		}else {
			return new File(TestHelper.fileName(fileName));
		}
	}
}
