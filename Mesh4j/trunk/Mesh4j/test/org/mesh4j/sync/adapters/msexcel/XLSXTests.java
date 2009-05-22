package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class XLSXTests {

	@Test
	public void shouldWriteRead() throws Exception{
		File file = TestHelper.makeFileAndDeleteIfExists("example.xlsx");
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("a");
		
		Row rowHeader = sheet.createRow(0);
		Cell cell = rowHeader.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString("String"));
		
		cell = rowHeader.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString("Boolean"));
		
		cell = rowHeader.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString("Number"));
		
		cell = rowHeader.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString("Date"));
		
		cell = rowHeader.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString("xml"));
		
		Row rowData = sheet.createRow(1);
		cell = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString("uno"));
		
		cell = rowData.createCell(1, Cell.CELL_TYPE_BOOLEAN);
		cell.setCellValue(true);

		cell = rowData.createCell(2, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(new Double(3.65d));

		cell = rowData.createCell(3, Cell.CELL_TYPE_NUMERIC);
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"));
		cell.setCellStyle(style);
		cell.setCellValue(new Date());

		SyncInfo syncInfo = new SyncInfo(new Sync(IdGenerator.INSTANCE.newID(), LoggedInIdentityProvider.getUserName(), new Date(), false), "type", "1", IdGenerator.INSTANCE.newID().hashCode());
		SyncInfoParser parser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, new LoggedInIdentityProvider(), IdGenerator.INSTANCE);
		Element element = parser.convertSyncInfo2Element(syncInfo);
		
		cell = rowData.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook.getCreationHelper().createRichTextString(element.asXML()));
		
		sheet = workbook.getSheet("a");
		cell = sheet.getRow(1).getCell(3);
		Assert.assertTrue(DateUtil.isCellDateFormatted(cell));
		
		FileOutputStream fos = new FileOutputStream(file);
		try{
			workbook.write(fos);
		}finally{
			fos.close();
		}
		
		sheet = workbook.getSheet("a");
		cell = sheet.getRow(1).getCell(3);
		Assert.assertTrue(DateUtil.isCellDateFormatted(cell));
		
		Workbook workbook2 = WorkbookFactory.create(new FileInputStream(file));
		sheet = workbook2.getSheet("a");
		
		rowData = sheet.createRow(2);
		cell = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook2.getCreationHelper().createRichTextString("uno"));
		
		cell = rowData.createCell(1, Cell.CELL_TYPE_BOOLEAN);
		cell.setCellValue(true);

		cell = rowData.createCell(2, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(new Double(3.65d));

		cell = rowData.createCell(3, Cell.CELL_TYPE_NUMERIC);
		style = workbook2.createCellStyle();
		style.setDataFormat(workbook2.createDataFormat().getFormat("m/d/yy h:mm"));
		cell.setCellStyle(style);
		cell.setCellValue(new Date());

		syncInfo = new SyncInfo(new Sync(IdGenerator.INSTANCE.newID(), LoggedInIdentityProvider.getUserName(), new Date(), false), "type", "1", IdGenerator.INSTANCE.newID().hashCode());
		element = parser.convertSyncInfo2Element(syncInfo);
		
		cell = rowData.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(workbook2.getCreationHelper().createRichTextString(element.asXML()));
		
		sheet = workbook2.getSheet("a");
		cell = sheet.getRow(1).getCell(3);
		Assert.assertTrue(DateUtil.isCellDateFormatted(cell));
		
		FileOutputStream fos2 = new FileOutputStream(file);
		try{
			workbook2.write(fos2);
		}finally{
			fos2.close();
		}
		
		Workbook workbook3 = WorkbookFactory.create(new FileInputStream(file));
		sheet = workbook3.getSheet("a");
		cell = sheet.getRow(1).getCell(3);
		Assert.assertTrue(DateUtil.isCellDateFormatted(cell));
		
	}
}
