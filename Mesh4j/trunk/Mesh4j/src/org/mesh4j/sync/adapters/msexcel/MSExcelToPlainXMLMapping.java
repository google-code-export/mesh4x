package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;

public class MSExcelToPlainXMLMapping implements IMsExcelToXMLMapping {

	// MODEL VARIABLES
	private String idColumnName;
	private String lastUpdateColumnName;
	
	// BUSINESS METHODS
	public MSExcelToPlainXMLMapping(String idColumnName, String lastUpdateColumnName) {
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		if(lastUpdateColumnName != null){
			Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		}		

		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void appliesXMLToRow(Workbook wb, Sheet sheet, Row row, Element payload){
		
		Row rowHeader = sheet.getRow(0);
		Cell cellHeader;
		
		Element child;
		for (Iterator<Element> iterator = payload.elementIterator(); iterator.hasNext();) {
			child = (Element) iterator.next();
			Cell cell = MsExcelUtils.getCell(sheet, row, child.getName());
			if(cell == null){
				cellHeader = MsExcelUtils.getOrCreateCellStringIfAbsent(wb, rowHeader, child.getName());
				cell = row.createCell(cellHeader.getColumnIndex());
			}
			this.setCellValue(wb, cell, child.getText());
			
		}
	}
	
	private void setCellValue(Workbook wb, Cell cell, String valueAsString) {
		if(Cell.CELL_TYPE_BOOLEAN == cell.getCellType()){
			cell.setCellValue(Boolean.valueOf(valueAsString));
		} else if(Cell.CELL_TYPE_NUMERIC == cell.getCellType()){
			if(DateUtil.isCellDateFormatted(cell)){
				Date date = DateHelper.parseW3CDateTime(valueAsString);
				cell.setCellValue(date);
			} else {
				Double num = Double.valueOf(valueAsString);
				cell.setCellValue(num);				
			}
		} else {
			cell.setCellValue(MsExcelUtils.getRichTextString(wb, valueAsString));
		}
		
	}

	@Override
	public Element convertRowToXML(Workbook wb, Sheet sheet, Row row){
		String sheetName = wb.getSheetName(wb.getSheetIndex(sheet));
		
		Element payload = DocumentHelper.createElement(sheetName);
		
		Row rowHeader = sheet.getRow(0);
		
		Cell cell;
		Cell cellHeader;
		String columnName;
		Object columnValue;
		Element fieldElement;
		
		for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			cellHeader = rowHeader.getCell(cell.getColumnIndex());
			
			columnName = cellHeader.getRichStringCellValue().getString();
			columnValue = MsExcelUtils.getCellValue(cell); 
			  
			fieldElement = payload.addElement(columnName);
			
			if(columnValue instanceof Date){
				fieldElement.addText(DateHelper.formatW3CDateTime((Date)columnValue));
			} else {
				fieldElement.addText(String.valueOf(columnValue));
			}
		}
		return payload;
	}

	@Override
	public String getIdColumnName() {
		return this.idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return this.lastUpdateColumnName;
	}

	@Override
	public ISchema getSchema() {
		return null;
	}

	@Override
	public Workbook createDataSource(String fileName) throws Exception {
		File file = new File(fileName);
		if(file.exists()){
			return MsExcelUtils.getOrCreateWorkbookIfAbsent(fileName);
		} else {
			Workbook workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(fileName);
			MsExcelUtils.flush(workbook, fileName);  // discard workbook for bug in POI library
			return MsExcelUtils.getOrCreateWorkbookIfAbsent(fileName);
		}
	}

	@Override
	public String getIdColumnValue(Sheet sheet, Row row) {
		Cell cell = MsExcelUtils.getCell(sheet, row, this.getIdColumnName());
		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK){
			Object cellValue = MsExcelUtils.getCellValue(cell);
			return String.valueOf(cellValue);
		} else {
			return null;
		}
	}
	
	@Override
	public Date getLastUpdateColumnValue(Sheet sheet, Row row) {
		Cell cell = MsExcelUtils.getCell(sheet, row, this.getLastUpdateColumnName());
		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)){
			return cell.getDateCellValue();
		} else {
			return null;
		}
	}
}
