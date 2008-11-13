package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.DateHelper;

public class MsExcelContentAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenSheetNameIsNull(){
		new MsExcelContentAdapter(new MsExcel("myfile.xls"), null, "oid");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenSheetNameIsEmpty(){
		new MsExcelContentAdapter(new MsExcel("myfile.xls"), "", "oid");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenIDColumnNameIsNull(){
		new MsExcelContentAdapter(new MsExcel("myfile.xls"), "test", null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenIDColumnNameIsEmpty(){
		new MsExcelContentAdapter(new MsExcel("myfile.xls"), "test", "");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenFileNameIsNull(){
		new MsExcelContentAdapter(null, "test", "oid");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenFileNameIsEmpty(){
		new MsExcelContentAdapter(new MsExcel(""), "test", "oid");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenLastUpdateColumnNameIsEmpty(){
		new MsExcelContentAdapter(new MsExcel("myfile.xls"), "test", "oid", "");
	}
	
	
	@Test
	public void shouldAdapterCreateFile() throws FileNotFoundException, IOException{
		
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), sheetName, idColumn);
		excel.beginSync();
		excel.endSync();
		
		Assert.assertTrue(file.exists());
		
		HSSFWorkbook wookbook = new HSSFWorkbook(new FileInputStream(file));
		validateWorkbook(sheetName, idColumn, wookbook);
	}
	
	@Test
	public void shouldAdapterCreateSheet() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook.createSheet("test");
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), sheetName, idColumn);
		validateWorkbook(sheetName, idColumn, excel.getWorkbook());
	}

	@Test
	public void shouldAdapterCreateRowHeader() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook.createSheet(sheetName);
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), sheetName, idColumn);
		validateWorkbook(sheetName, idColumn, excel.getWorkbook());
	}

	@Test
	public void shouldAdapterCreateCellID() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.createRow(0);
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), sheetName, idColumn);
		validateWorkbook(sheetName, idColumn, excel.getWorkbook());
	}

	@Test
	public void shouldGetTypeReturnsSheetName(){
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel("myfile.xmls"), "test", "oid");
		Assert.assertEquals("test", adapter.getType());
	}

	@Test
	public void shouldFileDoesNotCreatedBecauseEndSyncIsNotExecuted(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		adapter.getWorkbook().createSheet("Test");
		
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void shouldFileCreatedWhenEndSyncIsExecuted(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		adapter.getWorkbook().createSheet("Test");
		
		Assert.assertFalse(file.exists());
		
		adapter.beginSync();
		adapter.endSync();
		
		Assert.assertTrue(file.exists());
	}
	
	@Test
	public void shouldFileUpdatedWhenEndSyncIsExecuted() throws FileNotFoundException, IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");	
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		adapter.getWorkbook().createSheet("test");

		MsExcelUtils.flush(adapter.getWorkbook(), file.getAbsolutePath());
		Assert.assertTrue(file.exists());
		
		adapter.getWorkbook().createSheet("test1");
		adapter.beginSync();
		adapter.endSync();

		Assert.assertTrue(file.exists());
		adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");		
	
		Assert.assertNotNull(adapter.getWorkbook().getSheet("test"));
		Assert.assertNotNull(adapter.getWorkbook().getSheet("test1"));
		Assert.assertNotNull(adapter.getWorkbook().getSheet("sheet"));
	}
	
	@Test
	public void shouldGetReturnsNullBecauseItemDoesNotExistsOnSheet(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		
		Assert.assertNull(adapter.get("1"));
			
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertNull(adapter.get("1"));
	}
	
	@Test
	public void shouldGetReturnsItem(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		
		Assert.assertNull(adapter.get("1"));
		
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");

		IContent content = adapter.get("1");
		Assert.assertNotNull(content);
		Assert.assertEquals("<sheet><id>1</id><name>jct</name></sheet>", content.getPayload().asXML());
	}
	
	@Test
	public void shouldGetAllReturnsEmpty(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
	}
	
	@Test
	public void shouldGetAllReturnsItems(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");
		
		List<IContent> result = adapter.getAll(new Date()); 
		Assert.assertEquals(5, result.size());

		Assert.assertEquals("<sheet><id>1</id><name>jct</name></sheet>", result.get(0).getPayload().asXML());
		Assert.assertEquals("1", result.get(0).getId());		
		Assert.assertEquals("<sheet><id>2</id><name>jmt</name></sheet>", result.get(1).getPayload().asXML());
		Assert.assertEquals("2", result.get(1).getId());
		Assert.assertEquals("<sheet><id>3</id><name>jit</name></sheet>", result.get(2).getPayload().asXML());
		Assert.assertEquals("3", result.get(2).getId());
		Assert.assertEquals("<sheet><id>4</id><name>bit</name></sheet>", result.get(3).getPayload().asXML());
		Assert.assertEquals("4", result.get(3).getId());
		Assert.assertEquals("<sheet><id>5</id><name>msa</name></sheet>", result.get(4).getPayload().asXML());
		Assert.assertEquals("5", result.get(4).getId());
	}

	@Test
	public void shouldGetAllReturnsItemsFilteringBySinceDate(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");

		Date since = TestHelper.makeDate(2008, 11, 10, 1, 1, 1, 0);
		Date lastUpdate1 = TestHelper.makeDate(2008, 11, 11, 1, 1, 1, 0);
		Date lastUpdate2 = TestHelper.makeDate(2008, 11, 15, 1, 1, 1, 0);
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id", "lastUpdate");
		addEntityHeader(adapter, "sheet", "name", "lastUpdate");
		addEntity(adapter, "sheet", "1", "jct", lastUpdate1);
		addEntity(adapter, "sheet", "2", "jmt", lastUpdate2);
		addEntity(adapter, "sheet", "3", "jit", TestHelper.makeDate(2008, 10, 10, 1, 1, 1, 0));
		addEntity(adapter, "sheet", "4", "bit", TestHelper.makeDate(2008, 10, 10, 1, 1, 1, 0));
		addEntity(adapter, "sheet", "5", "msa", TestHelper.makeDate(2008, 10, 10, 1, 1, 1, 0));
		
		List<IContent> result = adapter.getAll(since); 
		Assert.assertEquals(2, result.size());

		Assert.assertEquals("<sheet><id>1</id><lastUpdate>"+DateHelper.formatW3CDateTime(lastUpdate1)+"</lastUpdate><name>jct</name></sheet>", result.get(0).getPayload().asXML());
		Assert.assertEquals("1", result.get(0).getId());		
		Assert.assertEquals("<sheet><id>2</id><lastUpdate>"+DateHelper.formatW3CDateTime(lastUpdate2)+"</lastUpdate><name>jmt</name></sheet>", result.get(1).getPayload().asXML());
		Assert.assertEquals("2", result.get(1).getId());
	}
	
	@Test 
	public void shouldDeleteNoProduceChangesWhenItemDoesNotExist() throws DocumentException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		String xml = "<sheet><id>45</id><name>jjl</name></sheet>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		IContent content = new EntityContent(payload, "sheet", "45");
		adapter.delete(content);

		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
	}
	
	@Test 
	public void shouldDelete() throws DocumentException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		String xml = "<sheet><id>5</id><name>msa</name></sheet>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		IContent content = new EntityContent(payload, "sheet", "5");
		adapter.delete(content);

		Assert.assertEquals(5, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
	}

	@Test
	public void shouldSave() throws DocumentException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		String xml = "<sheet><id>6</id><name>mrs</name></sheet>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		IContent content = new EntityContent(payload, "sheet", "6");
		adapter.save(content);

		Assert.assertEquals(7, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		HSSFSheet sheet = adapter.getWorkbook().getSheet("sheet");
		HSSFRow row = sheet.getRow(sheet.getPhysicalNumberOfRows()-1);
		Assert.assertEquals("6", row.getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals("mrs", row.getCell(1).getRichStringCellValue().getString());
	}

	@Test
	public void shouldUpdate() throws DocumentException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		addEntity(adapter, "sheet", "3", "jit");
		addEntity(adapter, "sheet", "4", "bit");
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		String xml = "<sheet><id>5</id><name>mrs</name></sheet>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		IContent content = new EntityContent(payload, "sheet", "5");
		adapter.save(content);

		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		HSSFSheet sheet = adapter.getWorkbook().getSheet("sheet");
		HSSFRow row = sheet.getRow(sheet.getPhysicalNumberOfRows()-1);
		Assert.assertEquals("5", row.getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals("mrs", row.getCell(1).getRichStringCellValue().getString());
	}

	@Test
	public void shouldBeginSyncRegisterAndMoveToBottomPhantomRows(){
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addPhantomRow(adapter, "sheet", 2);
		addEntity(adapter, "sheet", "3", "jit");
		addPhantomRow(adapter, "sheet", 2);
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getCellType());
		Assert.assertEquals("1", adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_BLANK, adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getCellType());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getCellType());
		Assert.assertEquals("3", adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_BLANK, adapter.getWorkbook().getSheet("sheet").getRow(4).getCell(0).getCellType());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0).getCellType());
		Assert.assertEquals("5", adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0).getRichStringCellValue().getString());
				
		adapter.beginSync();
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getCellType());
		Assert.assertEquals("1", adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getCellType());
		Assert.assertEquals("3", adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getCellType());
		Assert.assertEquals("5", adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getRichStringCellValue().getString());
		Assert.assertNull(adapter.getWorkbook().getSheet("sheet").getRow(4).getCell(0));
		Assert.assertNull(adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0));
		
		Assert.assertEquals(2, adapter.getNumberOfPhantomRows());
		
		adapter.endSync();
	}
	
	@Test
	public void shouldDeleteRegisterAndMoveToBottomPhantomRow() throws DocumentException{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addEntity(adapter, "sheet", "2", "jmt");
		
		adapter.beginSync();
		
		Assert.assertEquals(3, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		String xml = "<sheet><id>1</id><name>jct</name></sheet>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		IContent content = new EntityContent(payload, "sheet", "1");
		
		Assert.assertEquals(0, adapter.getNumberOfPhantomRows());
		
		adapter.delete(content);

		Assert.assertEquals(1, adapter.getNumberOfPhantomRows());
		Assert.assertEquals(3, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getCellType());
		Assert.assertEquals("2", adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getRichStringCellValue().getString());
		Assert.assertNull(adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0));
	}
	
	@Test
	public void shouldAddUsePhantomRows() throws DocumentException{
	File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter(new MsExcel(file.getAbsolutePath()), "sheet", "id");
		addEntityHeader(adapter, "sheet", "name");
		addEntity(adapter, "sheet", "1", "jct");
		addPhantomRow(adapter, "sheet", 2);
		addEntity(adapter, "sheet", "3", "jit");
		addPhantomRow(adapter, "sheet", 2);
		addEntity(adapter, "sheet", "5", "msa");
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getCellType());
		Assert.assertEquals("1", adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_BLANK, adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getCellType());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getCellType());
		Assert.assertEquals("3", adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_BLANK, adapter.getWorkbook().getSheet("sheet").getRow(4).getCell(0).getCellType());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0).getCellType());
		Assert.assertEquals("5", adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0).getRichStringCellValue().getString());
				
		adapter.beginSync();
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getCellType());
		Assert.assertEquals("1", adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getCellType());
		Assert.assertEquals("3", adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getCellType());
		Assert.assertEquals("5", adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getRichStringCellValue().getString());
		Assert.assertNull(adapter.getWorkbook().getSheet("sheet").getRow(4).getCell(0));
		Assert.assertNull(adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0));
		
		Assert.assertEquals(2, adapter.getNumberOfPhantomRows());
		
		String xml = "<sheet><id>32</id><name>dnkndfk</name></sheet>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		IContent content = new EntityContent(payload, "sheet", "32");
		
		adapter.save(content);
		
		Assert.assertEquals(6, adapter.getWorkbook().getSheet("sheet").getPhysicalNumberOfRows());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getCellType());
		Assert.assertEquals("1", adapter.getWorkbook().getSheet("sheet").getRow(1).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getCellType());
		Assert.assertEquals("3", adapter.getWorkbook().getSheet("sheet").getRow(2).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getCellType());
		Assert.assertEquals("5", adapter.getWorkbook().getSheet("sheet").getRow(3).getCell(0).getRichStringCellValue().getString());
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, adapter.getWorkbook().getSheet("sheet").getRow(4).getCell(0).getCellType());
		Assert.assertEquals("32", adapter.getWorkbook().getSheet("sheet").getRow(4).getCell(0).getRichStringCellValue().getString());
		Assert.assertNull(adapter.getWorkbook().getSheet("sheet").getRow(5).getCell(0));

		Assert.assertEquals(1, adapter.getNumberOfPhantomRows());		
	}
	
	// PRIVATE METHODS
	
	private void addEntityHeader(MsExcelContentAdapter adapter, String sheetName, String columnName) {
		HSSFSheet sheet = adapter.getWorkbook().getSheet(sheetName);
		HSSFRow row = sheet.getRow(0);
		HSSFCell cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(columnName));
	}
	
	private void addEntityHeader(MsExcelContentAdapter adapter, String sheetName, String columnName, String lastUpdateColumnName) {
		HSSFSheet sheet = adapter.getWorkbook().getSheet(sheetName);
		HSSFRow row = sheet.getRow(0);

		HSSFCell cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(lastUpdateColumnName));
		
		cell = row.createCell(2, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(columnName));
	}
	
	private void addEntity(MsExcelContentAdapter adapter, String sheetName, String id, String name) {
		HSSFSheet sheet = adapter.getWorkbook().getSheet(sheetName);
		HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(id));
		
		cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(name));
	}
	
	private void addEntity(MsExcelContentAdapter adapter, String sheetName, String id, String name, Date lastUpdate) {
		HSSFSheet sheet = adapter.getWorkbook().getSheet(sheetName);
		HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(id));
	
		cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(DateHelper.formatW3CDateTime(lastUpdate)));
		
		cell = row.createCell(2, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(name));
		
	
	}
	
	private void validateWorkbook(String sheetName, String idColumn, HSSFWorkbook wookbook) {
		HSSFSheet sheet = wookbook.getSheet(sheetName);
		Assert.assertNotNull(sheet);
		Assert.assertEquals(0, sheet.getLastRowNum());
		
		HSSFRow row = sheet.getRow(0);
		Assert.assertNotNull(row);
		Assert.assertEquals(1, row.getPhysicalNumberOfCells());
		
		HSSFCell cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertNotNull(cell.getRichStringCellValue());
		Assert.assertEquals(idColumn, cell.getRichStringCellValue().getString());
	}
	
	private void addPhantomRow(MsExcelContentAdapter adapter, String sheetName, int numOfCell) {
		HSSFSheet sheet = adapter.getWorkbook().getSheet(sheetName);
		HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		for (int i = 0; i < numOfCell; i++) {
			row.createCell(i, HSSFCell.CELL_TYPE_BLANK);
		}
	}
}

