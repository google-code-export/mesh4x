package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelUtils {

	public static Workbook getOrCreateWorkbookIfAbsent(String fileName) throws Exception{
		Workbook workbook = null;
		File file = new File(fileName);
		if(!file.exists()){
			if(fileName.toUpperCase().endsWith(".XLS")){
				workbook = new HSSFWorkbook();
			}else if(fileName.toUpperCase().endsWith(".XLSX")){
				workbook = new XSSFWorkbook();
			}
			return workbook;
		} else {
			FileInputStream is = new FileInputStream(file);
			try{			
				workbook = WorkbookFactory.create(is);
			}catch (Exception e) {
				throw new MeshException(e);
			}finally{
				if(is != null){
					try{
						is.close();
					}catch (Exception e) {
						throw new MeshException(e);
					}
				}
			}
		}
		return workbook;
	}
	
	public static void flush(Workbook workbook, String fileName) {
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
	
	
	public static Row getRow(Sheet worksheet, int columnIndex, String value) {
		Row row;
		Cell cellId;
		String cellValue;
		for (int i = worksheet.getFirstRowNum()+1; i <= worksheet.getLastRowNum(); i++) {
			row = worksheet.getRow(i);
			if(row != null){
				cellId = row.getCell(columnIndex);
				if(cellId != null && cellId.getCellType() != Cell.CELL_TYPE_BLANK){
					cellValue = String.valueOf(getCellValue(cellId));
					if(value.equals(cellValue)){
						return row;
					}
				}
			}
		}
		return null;
	}
	
	public static Cell getCell(Sheet worksheet, Row row, String columnName) {
		Row rowHeader = worksheet.getRow(0);
		Cell cell;
		for (Iterator<Cell> iterator = rowHeader.cellIterator(); iterator.hasNext();) {
			Cell cellHeader = iterator.next();
			String cellHeaderName = cellHeader.getRichStringCellValue().getString();
			if(columnName.equals(cellHeaderName)){
				cell = row.getCell(cellHeader.getColumnIndex());
				return cell;
			}
		}
		return null;
	}
	
	public static Sheet getOrCreateSheetIfAbsent(Workbook workbook, String sheetName){
		Sheet worksheet = null;
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
	
	public static Row getOrCreateRowHeaderIfAbsent(Sheet worksheet){
		Row row = worksheet.getRow(0);
		if(row == null){
			row = worksheet.createRow(0);
		}
		return row;
	}
	
	public static Cell getOrCreateCellStringIfAbsent(Workbook workbook,Row row, String value){
		
		for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
			Cell cell = iterator.next();
			String cellValue = cell.getRichStringCellValue().getString();
			if(cellValue.equals(value)){
				return cell;
			}
		}
		
		Cell cell = row.createCell(row.getPhysicalNumberOfCells(), Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook, value));
		return cell;
	}

	public static void updateOrCreateCellStringIfAbsent(Workbook workbook, Row row, int columnIndex, String value) {
		Cell cell = row.getCell(columnIndex);
		if(cell == null){
			cell = row.createCell(columnIndex, Cell.CELL_TYPE_STRING);
		}
		cell.setCellValue(getRichTextString(workbook, value));
	}

	public static boolean isPhantomRow(Row row) {
		if(row == null){
			return true;
		}
		
		for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
			Cell cell = iterator.next();
			if(Cell.CELL_TYPE_BLANK != cell.getCellType()){
				return false;
			}
		}
		return true;
	}
	
	public static Object getCellValue(Cell cell) {
		int type = cell.getCellType();

		if(Cell.CELL_TYPE_STRING == type){
			return cell.getRichStringCellValue().getString();
		} else if(Cell.CELL_TYPE_BOOLEAN == type){
			return cell.getBooleanCellValue();
		} else if(Cell.CELL_TYPE_NUMERIC == type){
			if(DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				Double doubleCellValue = new Double(cell.getNumericCellValue());
				if(doubleCellValue.doubleValue() - doubleCellValue.longValue() == 0){
					return doubleCellValue.longValue();
				} else {
					return doubleCellValue;	
				}				
		    }
		} else {
			return null;
		}
	}

	public static void setCellValue(Workbook workbook, Cell cell, int type, Object value) {
	 	
		if(Cell.CELL_TYPE_STRING == type){
			cell.setCellValue(getRichTextString(workbook, String.valueOf(value)));
		} else if(Cell.CELL_TYPE_BOOLEAN == type){
			cell.setCellValue(value instanceof String ? Boolean.valueOf((String) value): (Boolean)value);
		} else if(Cell.CELL_TYPE_NUMERIC == type){
			if(DateUtil.isCellDateFormatted(cell)) {
				cell.setCellValue((Date) value);
			} else {
				cell.setCellValue(value instanceof String ? Long.valueOf((String) value) : ((Number) value).doubleValue());
		    }
		} else {
			cell.setCellValue(getRichTextString(workbook, String.valueOf(value)));
		}
		
	}
	
	public static RichTextString getRichTextString(Workbook workbook,String value){
		return workbook.getCreationHelper().createRichTextString(value);
	}
}

