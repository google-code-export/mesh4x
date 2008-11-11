package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;

// TODO (JMT) add tests: get/getAll/save/delete/normalize

public class MsExcelContentAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenSheetNameIsNull(){
		new MsExcelContentAdapter(null, "oid", "myfile.xls");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenSheetNameIsEmpty(){
		new MsExcelContentAdapter("", "oid", "myfile.xls");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenIDColumnNameIsNull(){
		new MsExcelContentAdapter("test", null, "myfile.xls");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenIDColumnNameIsEmpty(){
		new MsExcelContentAdapter("test", "", "myfile.xls");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenFileNameIsNull(){
		new MsExcelContentAdapter("test", "oid", null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenFileNameIsEmpty(){
		new MsExcelContentAdapter("test", "oid", "");
	}

	@Test
	public void shouldAdapterCreateFile() throws FileNotFoundException, IOException{
		
		String fileName = TestHelper.fileName("myExcel.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(sheetName, idColumn, fileName);
		excel.endSync();
		
		Assert.assertTrue(file.exists());
		
		HSSFWorkbook wookbook = new HSSFWorkbook(new FileInputStream(file));
		validateWorkbook(sheetName, idColumn, wookbook);
	}
	
	@Test
	public void shouldAdapterCreateSheet() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook.createSheet("test");
		workbook.write(new FileOutputStream(file));
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(sheetName, idColumn, fileName);
		validateWorkbook(sheetName, idColumn, excel.getWorkbook());
	}

	@Test
	public void shouldAdapterCreateRowHeader() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel2.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		HSSFWorkbook wookbook = new HSSFWorkbook();
		wookbook.createSheet(sheetName);
		wookbook.write(new FileOutputStream(file));
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(sheetName, idColumn, fileName);
		validateWorkbook(sheetName, idColumn, excel.getWorkbook());
	}


	@Test
	public void shouldAdapterCreateCellID() throws FileNotFoundException, IOException{
		String fileName = TestHelper.fileName("myExcel4.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		String sheetName = "sheet";
		String idColumn = "oid";
		
		HSSFWorkbook wookbook = new HSSFWorkbook();
		HSSFSheet sheet = wookbook.createSheet(sheetName);
		sheet.createRow(0);
		wookbook.write(new FileOutputStream(file));
		
		MsExcelContentAdapter excel = new MsExcelContentAdapter(sheetName, idColumn, fileName);
		validateWorkbook(sheetName, idColumn, excel.getWorkbook());
	}

	@Test
	public void shouldGetTypeReturnsSheetName(){
		MsExcelContentAdapter adapter = new MsExcelContentAdapter("test", "oid", "myfile.xmls");
		Assert.assertEquals("test", adapter.getType());
	}

	@Test
	public void shouldFileDoesNotCreatedBecauseEndSyncIsNotExecuted(){
		String fileName = TestHelper.fileName("myExcel11.xls");
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter("sheet", "id" , fileName);
		adapter.getWorkbook().createSheet("Test");
		
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
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter("sheet", "id" , fileName);
		adapter.getWorkbook().createSheet("Test");
		
		Assert.assertFalse(file.exists());
		
		adapter.endSync();
		
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
		
		MsExcelContentAdapter adapter = new MsExcelContentAdapter("sheet", "id" , fileName);
		adapter.getWorkbook().createSheet("test");

		adapter.getWorkbook().write(new FileOutputStream(file));
		Assert.assertTrue(file.exists());
		
		adapter.getWorkbook().createSheet("test1");		
		adapter.endSync();

		Assert.assertTrue(file.exists());
		adapter = new MsExcelContentAdapter("sheet", "id" , fileName);
		
	
		Assert.assertNotNull(adapter.getWorkbook().getSheet("test"));
		Assert.assertNotNull(adapter.getWorkbook().getSheet("test1"));
		Assert.assertNotNull(adapter.getWorkbook().getSheet("sheet"));
	}
	
	
	// PRIVATE METHODS
	
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
}

