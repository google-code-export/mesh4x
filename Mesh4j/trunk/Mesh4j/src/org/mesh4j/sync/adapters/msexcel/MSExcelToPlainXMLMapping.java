package org.mesh4j.sync.adapters.msexcel;

import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
	public void appliesXMLToRow(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, Element payload){
		
		HSSFRow rowHeader = sheet.getRow(0);
		HSSFCell cellHeader;
		
		Element child;
		for (Iterator<Element> iterator = payload.elementIterator(); iterator.hasNext();) {
			child = (Element) iterator.next();
			HSSFCell cell = MsExcelUtils.getCell(sheet, row, child.getName());
			if(cell == null){
				cellHeader = MsExcelUtils.getOrCreateCellStringIfAbsent(rowHeader, child.getName());
				cell = row.createCell(cellHeader.getColumnIndex());
			}
			this.setCellValue(cell, child.getText());
			
		}
	}
	
	private void setCellValue(HSSFCell cell, String valueAsString) {
		if(HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()){
			cell.setCellValue(Boolean.valueOf(valueAsString));
		} else if(HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()){
			if(HSSFDateUtil.isCellDateFormatted(cell)){
				Date date = DateHelper.parseW3CDateTime(valueAsString);
				cell.setCellValue(date);
			} else {
				Double num = Double.valueOf(valueAsString);
				cell.setCellValue(num);				
			}
		} else {
			cell.setCellValue(new HSSFRichTextString(valueAsString));
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Element convertRowToXML(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row){
		String sheetName = wb.getSheetName(wb.getSheetIndex(sheet));
		
		Element payload = DocumentHelper.createElement(sheetName);
		
		HSSFRow rowHeader = sheet.getRow(0);
		
		HSSFCell cell;
		HSSFCell cellHeader;
		String columnName;
		Object columnValue;
		Element fieldElement;
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
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
	public void createDataSource(String fileName) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();			
		MsExcelUtils.flush(workbook, fileName);		
	}

}
