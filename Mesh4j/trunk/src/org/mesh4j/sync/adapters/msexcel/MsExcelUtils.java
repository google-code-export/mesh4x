package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelUtils {

	public static void flush(HSSFWorkbook workbook, String fileName) {
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
			workbook.write(fos);
		}catch (Exception e) {
			throw new MeshException(e);
		}finally{
			if(fos != null){
				try{
					fos.close();
				}catch (Exception e) {
					throw new MeshException(e);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Element translate(HSSFSheet worksheet, HSSFRow row, String elementName) {
		Element payload = DocumentHelper.createElement(elementName);
		HSSFRow rowHeader = worksheet.getRow(0);
		
		HSSFCell cell;
		HSSFCell cellHeader;
		String columnName;
		String columnValue;
		Element header;
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			cellHeader = rowHeader.getCell(cell.getColumnIndex());
			
			columnName = cellHeader.getRichStringCellValue().getString();
			columnValue = cell.getRichStringCellValue().getString();		// TODO (JMT) data type formatters
			
			header = payload.addElement(columnName);
			header.addText(columnValue);
		}
		return payload;
	}
	
	public static HSSFRow getRow(HSSFSheet worksheet, int columnIndex, String value) {
		HSSFRow row;
		HSSFCell cellId;
		String cellValue;
		for (int i = worksheet.getFirstRowNum()+1; i <= worksheet.getLastRowNum(); i++) {
			row = worksheet.getRow(i);
			if(row != null){
				cellId = row.getCell(columnIndex);
				if(cellId != null && cellId.getCellType() != HSSFCell.CELL_TYPE_BLANK){
					cellValue = cellId.getRichStringCellValue().getString();
					if(value.equals(cellValue)){
						return row;
					}
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static HSSFCell getCell(HSSFSheet worksheet, HSSFRow row, String columnName) {
		HSSFRow rowHeader = worksheet.getRow(0);
		for (Iterator<HSSFCell> iterator = rowHeader.cellIterator(); iterator.hasNext();) {
			HSSFCell cellHeader = iterator.next();
			String cellHeaderName = cellHeader.getRichStringCellValue().getString();
			if(columnName.equals(cellHeaderName)){
				return row.getCell(cellHeader.getColumnIndex());
			}
		}
		return null;
	}
	
	public static HSSFWorkbook getOrCreateWorkbookIfAbsent(String fileName) throws FileNotFoundException, IOException{
		HSSFWorkbook workbook = null;
		File file = new File(fileName);
		if(!file.exists()){
			workbook = new HSSFWorkbook();
		} else {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		}
		return workbook;
	}
	
	public static HSSFSheet getOrCreateSheetIfAbsent(HSSFWorkbook workbook, String sheetName){
		HSSFSheet worksheet = null;
		if(workbook.getNumberOfSheets() == 0){
			worksheet = workbook.createSheet(sheetName);
		} else {
			worksheet = workbook.getSheet(sheetName);
			if(worksheet == null){
				worksheet = workbook.createSheet(sheetName);
			}
		}
		return worksheet;
	}
	
	public static HSSFRow getOrCreateRowHeaderIfAbsent(HSSFSheet worksheet){
		HSSFRow row = worksheet.getRow(0);
		if(row == null){
			row = worksheet.createRow(0);
		}
		return row;
	}
	
	@SuppressWarnings("unchecked")
	public static HSSFCell getOrCreateCellStringIfAbsent(HSSFRow row, String value){
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			HSSFCell cell = iterator.next();
			String cellValue = cell.getRichStringCellValue().getString();
			if(cellValue.equals(value)){
				return cell;
			}
		}
		
		HSSFCell cell = row.createCell(row.getPhysicalNumberOfCells(), HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(value));
		return cell;
	}

	public static void updateOrCreateCellStringIfAbsent(HSSFRow row, int columnIndex, String value) {
		HSSFCell cell = row.getCell(columnIndex);
		if(cell == null){
			cell = row.createCell(columnIndex, HSSFCell.CELL_TYPE_STRING);
		}
		cell.setCellValue(new HSSFRichTextString(value));
	}

	@SuppressWarnings("unchecked")
	public static void updateRow(HSSFSheet worksheet, HSSFRow row, Element payload) {
		
		HSSFRow rowHeader = worksheet.getRow(0);
		HSSFCell cellHeader;
		
		Element child;
		for (Iterator<Element> iterator = payload.elementIterator(); iterator.hasNext();) {
			child = (Element) iterator.next();
			HSSFCell cell = getCell(worksheet, row, child.getName());
			if(cell == null){
				cellHeader = getOrCreateCellStringIfAbsent(rowHeader, child.getName());
				cell = row.createCell(cellHeader.getColumnIndex());
			}
			cell.setCellValue(new HSSFRichTextString(child.getText()));     // TODO (JMT) data type formatters
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean isPhantomRow(HSSFRow row) {
		if(row == null){
			return true;
		}
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			HSSFCell cell = iterator.next();
			if(HSSFCell.CELL_TYPE_BLANK != cell.getCellType()){
				return false;
			}
		}
		return true;
	}
}
