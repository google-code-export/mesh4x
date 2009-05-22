package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelUtilsTests {

	@Test(expected=MeshException.class)
	public void shouldFlushFailWhenWorkbookIsNull(){
		MsExcelUtils.flush(null, TestHelper.fileName("file.xls"));
	}

	@Test(expected=MeshException.class)
	public void shouldFlushFailWhenFileNameIsNull(){
		MsExcelUtils.flush(new HSSFWorkbook(), null);
	}

	@Test(expected=MeshException.class)
	public void shouldFlushFailWhenFileNameIsEmpty(){
		MsExcelUtils.flush(new HSSFWorkbook(), "  ");
	}

	@Test
	public void shouldFlushCreateNewFile() throws FileNotFoundException, IOException{
		String sheetName = "example";
		String cellValue = "cellValueExample";
		
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
	
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(sheetName);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(cellValue));
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		
		Assert.assertTrue(file.exists());
		
		workbook = new HSSFWorkbook(new FileInputStream(file));
		sheet = workbook.getSheet(sheetName);
		Assert.assertNotNull(sheet);
		Assert.assertEquals(0, sheet.getLastRowNum());
		
		row = sheet.getRow(0);
		Assert.assertNotNull(row);
		Assert.assertEquals(1, row.getPhysicalNumberOfCells());
		
		cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertNotNull(cell.getRichStringCellValue());
		Assert.assertEquals(cellValue, cell.getRichStringCellValue().getString());

	}

	@Test
	public void shouldFlushUpdateFile() throws FileNotFoundException, IOException{
		String sheetName = "example";
		String cellValue = "cellValueExample";
		
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
	
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(sheetName);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(cellValue));
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		
		Assert.assertTrue(file.exists());
		
		workbook = new HSSFWorkbook(new FileInputStream(file));
		sheet = workbook.getSheet(sheetName);
		row = sheet.createRow(1);
		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(cellValue));
		MsExcelUtils.flush(workbook, file.getAbsolutePath());
		
		Assert.assertTrue(file.exists());
		
		workbook = new HSSFWorkbook(new FileInputStream(file));
		sheet = workbook.getSheet(sheetName);
		Assert.assertNotNull(sheet);
		Assert.assertEquals(1, sheet.getLastRowNum());
		
		row = sheet.getRow(0);
		Assert.assertNotNull(row);
		Assert.assertEquals(1, row.getPhysicalNumberOfCells());
		
		cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertNotNull(cell.getRichStringCellValue());
		Assert.assertEquals(cellValue, cell.getRichStringCellValue().getString());
		
		row = sheet.getRow(1);
		Assert.assertNotNull(row);
		Assert.assertEquals(1, row.getPhysicalNumberOfCells());
		
		cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertNotNull(cell.getRichStringCellValue());
		Assert.assertEquals(cellValue, cell.getRichStringCellValue().getString());
	}
	
	@Test
	public void shouldGetRow(){
		Sheet sheet = makeDefaultWorkbook().getSheetAt(0);
		Row row = MsExcelUtils.getRow(sheet, 3, "VAL23");
		Assert.assertNotNull(row);
	}
	
	@Test
	public void shouldGetRowReturnsNull(){
		Sheet sheet = makeDefaultWorkbook().getSheetAt(0);
		Row row = MsExcelUtils.getRow(sheet, 3, "dsadfdaf");
		Assert.assertNull(row);
	}

	@Test
	public void shouldGetCell(){
		Sheet sheet = makeDefaultWorkbook().getSheetAt(0);

		Row row = sheet.getRow(1);
		Assert.assertNotNull(row);
		
		Cell cell = MsExcelUtils.getCell(sheet, row, "COL4");
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL4", cell.getRichStringCellValue().getString());
	}
	
	@Test
	public void shouldGetCellReturnsNull(){
		Sheet sheet = makeDefaultWorkbook().getSheetAt(0);

		Row row = sheet.getRow(1);
		Assert.assertNotNull(row);
		
		Cell cell = MsExcelUtils.getCell(sheet, row, "fhejsfbjes");
		Assert.assertNull(cell);
	}
	
	@Test
	public void shouldCreateWookbookWhenFileDoesNotExist() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("myExcel.xls");
		
		Workbook workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(file.getAbsolutePath());
		Assert.assertNotNull(workbook);
		Assert.assertFalse(file.exists());
	}

	@Test
	public void shouldLoadWookbookWhenFileExist() throws Exception{
		String fileName = TestHelper.fileName("myExcel.xls");
		Workbook workbook = makeDefaultWorkbook();
		MsExcelUtils.flush(workbook, fileName);
			
		File file = new File(fileName);
		Assert.assertTrue(file.exists());
		
		workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(fileName);
		Assert.assertNotNull(workbook);
		Assert.assertTrue(file.exists());
		
		assertDefaultWorkbook(workbook);
	}

	@Test 
	public void shouldGetSheet(){
		HSSFWorkbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
		
		Sheet sheet1 = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, "EXAMPLE");
		Assert.assertNotNull(sheet1);
		
		Assert.assertEquals(sheet, sheet1);
	}
	
	@Test 
	public void shouldAddSheet(){
		HSSFWorkbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE1");
		Assert.assertNull(sheet);
		
		Sheet sheet1 = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, "EXAMPLE1");
		Assert.assertNotNull(sheet1);
		
		sheet = workbook.getSheet("EXAMPLE1");
		Assert.assertNotNull(sheet);
		
		Assert.assertEquals(sheet, sheet1);
	}
	
	@Test 
	public void shouldAddSheetWhenWorkbookIsEmpty(){
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNull(sheet);
		
		Sheet sheet1 = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, "EXAMPLE");
		Assert.assertNotNull(sheet1);
		
		sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
		
		Assert.assertEquals(sheet, sheet1);
	}
	
	@Test
	public void shouldGetRowHeader(){
		HSSFWorkbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
	
		Row originalRow = sheet.getRow(0);
		Assert.assertNotNull(originalRow);
		
		Row row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
	
		Assert.assertNotNull(row);
		Assert.assertEquals(originalRow, row);
	}
	
	@Test
	public void shouldGetRowHeaderCreateRowWhenHeaderDoesNotExist(){
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
	
		Assert.assertNull(sheet.getRow(0));
		
		Row row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
	
		Assert.assertNotNull(row);
		Assert.assertEquals(row, sheet.getRow(0));
	}
	
	@Test
	public void shouldGetOrCreateCellExecuteGet(){
		Workbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
	
		Row row = sheet.getRow(1);
		Assert.assertNotNull(row);
		
		Cell cell = MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,row, "VAL0");
	
		Assert.assertNotNull(cell);
		Assert.assertEquals(cell, row.getCell(0));
	}
	
	@Test
	public void shouldGetOrCreateCellExecuteCreate(){
		Workbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
	
		Row row = sheet.getRow(1);
		Assert.assertNotNull(row);
		
		Cell cell = MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,row, "VAL?");
	
		Assert.assertNotNull(cell);
		Assert.assertEquals(cell, row.getCell(row.getPhysicalNumberOfCells()-1));
	}
	
	@Test
	public void shouldUpdateOrCreateCellExecuteUpdate(){
		Workbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Row row = sheet.getRow(1);
		Cell cell = row.getCell(2);
		Assert.assertNotNull(cell);
		
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook,row, 2, "NEW_VALUE");
		
		cell = row.getCell(2);
		Assert.assertNotNull(cell);
		
		Assert.assertEquals("NEW_VALUE", cell.getRichStringCellValue().getString());
	}
	
	@Test
	public void shouldUpdateOrCreateCellExecuteCreate(){
		Workbook workbook = makeDefaultWorkbook();
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Row row = sheet.getRow(1);
		Cell cell = row.getCell(5);
		Assert.assertNull(cell);
		
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook,row, 5, "NEW_VALUE");
		
		cell = row.getCell(5);
		Assert.assertNotNull(cell);
		
		Assert.assertEquals("NEW_VALUE", cell.getRichStringCellValue().getString());
	}
		
	// PRIVATE METHODS
	
	private void assertDefaultWorkbook(Workbook workbook) {
		Sheet sheet = workbook.getSheet("EXAMPLE");
		Assert.assertNotNull(sheet);
		
		Row row = sheet.getRow(0);
		Assert.assertNotNull(row);
		
		Cell cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals("COL0", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(1);
		Assert.assertNotNull(cell);
		Assert.assertEquals("COL1", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(2);
		Assert.assertNotNull(cell);
		Assert.assertEquals("COL2", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(3);
		Assert.assertNotNull(cell);
		Assert.assertEquals("COL3", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(4);
		Assert.assertNotNull(cell);
		Assert.assertEquals("COL4", cell.getRichStringCellValue().getString());
		
		row = sheet.getRow(1);
		Assert.assertNotNull(row);
		
		cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL0", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(1);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL1", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(2);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL2", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(3);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL3", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(4);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL4", cell.getRichStringCellValue().getString());
		
		row = sheet.getRow(2);
		Assert.assertNotNull(row);
		
		cell = row.getCell(0);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL20", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(1);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL21", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(2);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL22", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(3);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL23", cell.getRichStringCellValue().getString());
		
		cell = row.getCell(4);
		Assert.assertNotNull(cell);
		Assert.assertEquals("VAL24", cell.getRichStringCellValue().getString());
	}

	private HSSFWorkbook makeDefaultWorkbook() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("EXAMPLE");
		Row row = sheet.createRow(0);
		
		Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("COL0"));
		
		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("COL1"));
		
		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("COL2"));
		
		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("COL3"));
		
		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("COL4"));
		
		row = sheet.createRow(1);
		
		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL0"));
		
		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL1"));
		
		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL2"));
		
		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL3"));
		
		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL4"));

		row = sheet.createRow(2);
		
		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL20"));
		
		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL21"));
		
		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL22"));
		
		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL23"));
		
		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("VAL24"));
		return workbook;
	}

}
