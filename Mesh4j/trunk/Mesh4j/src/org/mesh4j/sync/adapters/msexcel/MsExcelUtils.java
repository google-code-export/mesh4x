package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
		HSSFCell cell;
		for (Iterator<HSSFCell> iterator = rowHeader.cellIterator(); iterator.hasNext();) {
			HSSFCell cellHeader = iterator.next();
			String cellHeaderName = cellHeader.getRichStringCellValue().getString();
			if(columnName.equals(cellHeaderName)){
				cell = row.getCell(cellHeader.getColumnIndex());
				return cell;
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
	
	public static Object getCellValue(HSSFCell cell) {
		int type = cell.getCellType();

		if(HSSFCell.CELL_TYPE_STRING == type){
			return cell.getRichStringCellValue().getString();
		} else if(HSSFCell.CELL_TYPE_BOOLEAN == type){
			return cell.getBooleanCellValue();
		} else if(HSSFCell.CELL_TYPE_NUMERIC == type){
			if(HSSFDateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return cell.getNumericCellValue();
		    }
		} else {
			return null;
		}
	}

	public static void setCellValue(HSSFCell cell, Object value) {
		int type = cell.getCellType();

		if(HSSFCell.CELL_TYPE_STRING == type){
			cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
		} else if(HSSFCell.CELL_TYPE_BOOLEAN == type){
			cell.setCellValue((Boolean)value);
		} else if(HSSFCell.CELL_TYPE_NUMERIC == type){
			if(HSSFDateUtil.isCellDateFormatted(cell)) {
				cell.setCellValue((Date) value);
			} else {
				cell.setCellValue(((Number) value).doubleValue());
		    }
		} else {
			cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
		}
	}
}

