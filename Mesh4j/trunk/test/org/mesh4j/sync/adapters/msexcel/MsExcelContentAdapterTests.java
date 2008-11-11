package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsExcelContentAdapterTests {
	
	// TODO (JMT) add tests

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
		excel.beginSync();
		excel.endSync();
		
		Assert.assertTrue(file.exists());
		
		HSSFWorkbook wookbook = new HSSFWorkbook(new FileInputStream(file));
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
