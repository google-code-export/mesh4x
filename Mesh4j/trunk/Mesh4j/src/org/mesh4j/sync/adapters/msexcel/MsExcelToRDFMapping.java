package org.mesh4j.sync.adapters.msexcel;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class MsExcelToRDFMapping implements IMsExcelToXMLMapping{

	// MODEL VARIABLES
	private IRDFSchema rdfSchema;

	private String idColumnName;
	private String lastUpdateColumnName = null;
	
	
	// BUSINESS METHODs
	public MsExcelToRDFMapping(IRDFSchema schema, String idColumnName) {
		super();
		this.rdfSchema = schema;
		this.idColumnName = idColumnName;
	}
	
	public static RDFSchema extractRDFSchema(IMsExcel excel, String sheetName, String rdfURL){
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(rdfURL, "rdfURL");
		
		RDFSchema rdfSchema = new RDFSchema(sheetName, rdfURL+"/"+sheetName+"#", sheetName);
		
		String cellName;
		
		Workbook workbook = excel.getWorkbook();
		Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		Cell cell;

		Row headerRow = sheet.getRow(sheet.getFirstRowNum());
		Row dataRow = sheet.getRow(sheet.getLastRowNum());
		int cellType;
		for (Iterator<Cell> iterator = dataRow.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			
			cellName = headerRow.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
			cellType = cell.getCellType();
			if(Cell.CELL_TYPE_STRING == cellType){
				rdfSchema.addStringProperty(cellName, cellName, "en");
			} else if(Cell.CELL_TYPE_BOOLEAN == cellType){
				rdfSchema.addBooleanProperty(cellName, cellName, "en");
			} else if(Cell.CELL_TYPE_NUMERIC == cellType){
				if(DateUtil.isCellDateFormatted(cell)) {
					rdfSchema.addDateTimeProperty(cellName, cellName, "en");
				} else {
					rdfSchema.addDoubleProperty(cellName, cellName, "en");
		        }
			}
		}
		
		return rdfSchema;
	}

	public RDFInstance converRowToRDF(Row headerRow, Row row) {
		
		// obtains properties values
		Cell cell;
		String cellName;
		Object cellValue;
		Object propertyValue;

		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			cellName = headerRow.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
			cellValue = MsExcelUtils.getCellValue(cell);
			
			propertyValue = rdfSchema.cannonicaliseValue(cellName, cellValue);
			if(propertyValue != null){
				propertyValues.put(cellName, propertyValue);
			}
		}
		
		// create rdf instance
		String id = String.valueOf(propertyValues.get(this.idColumnName));
		
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, propertyValues);
		return rdfInstance;
	}

	public void appliesRDFToRow(Workbook wb, Sheet sheet, Row row, RDFInstance rdfInstance) {
		Cell cell;
		Object propertyValue;
		String propertyType;
		
		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			propertyValue = rdfInstance.getPropertyValue(propertyName);
						
			if(propertyValue != null){
				Row headerRow = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
				Cell headerCell = MsExcelUtils.getOrCreateCellStringIfAbsent(wb, headerRow, propertyName);
				
				cell = row.getCell(headerCell.getColumnIndex());
				propertyType = rdfInstance.getPropertyType(propertyName);
				if(cell == null){
					cell = createCell(wb, row, headerCell.getColumnIndex(), propertyType);
				}
				MsExcelUtils.setCellValue(wb, cell, getCellType(propertyType), propertyValue);	
			}			
		}		
	}
	
	// TODO (JMT) RDF: improve MSExcel to RDF type mapper
	private int getCellType(String propertyType) {

		if(IRDFSchema.XLS_STRING.equals(propertyType)){
			return Cell.CELL_TYPE_STRING;			
		}else if(IRDFSchema.XLS_BOOLEAN.equals(propertyType)){
			return Cell.CELL_TYPE_BOOLEAN;
		}else if(IRDFSchema.XLS_INTEGER.equals(propertyType) 
				|| IRDFSchema.XLS_LONG.equals(propertyType)
				|| IRDFSchema.XLS_DOUBLE.equals(propertyType)
				|| IRDFSchema.XLS_DECIMAL.equals(propertyType)
				|| IRDFSchema.XLS_FLOAT.equals(propertyType)){
			return Cell.CELL_TYPE_NUMERIC;
		}else if(IRDFSchema.XLS_DATETIME.equals(propertyType)){
			return Cell.CELL_TYPE_NUMERIC;
		} else {
			return Cell.CELL_TYPE_STRING;
		}
	}
	
	// TODO (JMT) RDF: improve MSExcel to RDF type mapper
	private Cell createCell(Workbook wb, Row row, int columnIndex, String propertyType) {

		if(IRDFSchema.XLS_STRING.equals(propertyType)){
			return row.createCell(columnIndex, Cell.CELL_TYPE_STRING);			
		}else if(IRDFSchema.XLS_BOOLEAN.equals(propertyType)){
			return row.createCell(columnIndex, Cell.CELL_TYPE_BOOLEAN);
		}else if(IRDFSchema.XLS_INTEGER.equals(propertyType) 
				|| IRDFSchema.XLS_LONG.equals(propertyType)
				|| IRDFSchema.XLS_DOUBLE.equals(propertyType)
				|| IRDFSchema.XLS_DECIMAL.equals(propertyType)
				|| IRDFSchema.XLS_FLOAT.equals(propertyType)){
			return row.createCell(columnIndex, Cell.CELL_TYPE_NUMERIC);
		}else if(IRDFSchema.XLS_DATETIME.equals(propertyType)){
			CellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(wb.createDataFormat().getFormat("m/d/yyyy h:mm"));
		    
		    Cell cell = row.createCell(columnIndex, Cell.CELL_TYPE_NUMERIC);
		    cell.setCellStyle(cellStyle);
		    return cell;
		} else {
			return row.createCell(columnIndex, Cell.CELL_TYPE_STRING);
		}
	}

	@Override
	public void createDataSource(IMsExcel excel) {
		excel.setDirty();
		Workbook workbook = excel.getWorkbook();

		Sheet sheet = workbook.createSheet(this.rdfSchema.getOntologyClassName());
		Row headerRow = sheet.createRow(0);
		Cell headerCell;
		
		int size = this.rdfSchema.getPropertyCount();
		String propertyName;
		for (int j = 0; j < size; j++) {
			propertyName = this.rdfSchema.getPropertyName(size - 1 - j);	
			
			headerCell = headerRow.createCell(j);
			headerCell.setCellValue(MsExcelUtils.getRichTextString(workbook, propertyName));			
		}
		excel.flush();
	}
	
	@Override
	public Element convertRowToXML(Workbook wb, Sheet sheet, Row row){
		Row headerRow = sheet.getRow(sheet.getFirstRowNum());
		RDFInstance rdfInstance = this.converRowToRDF(headerRow, row);
		return XMLHelper.parseElement(rdfInstance.asXML());
	}
	
	@Override
	public void appliesXMLToRow(Workbook wb, Sheet sheet, Row row, Element rdfElement){
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromRDFXML(rdfElement.asXML());
		this.appliesRDFToRow(wb, sheet, row, rdfInstance);
	}


	@Override
	public String getIdColumnName() {
		return this.idColumnName;
	}


	@Override
	public String getLastUpdateColumnName() {
		return this.lastUpdateColumnName;
	}
	
	public void setLastUpdateColumnName(String columnName) {
		this.lastUpdateColumnName = columnName;
	}

	@Override
	public IRDFSchema getSchema() {
		return rdfSchema;
	}

	@Override
	public String getIdColumnValue(Sheet sheet, Row row) {
		Cell cell = MsExcelUtils.getCell(sheet, row, this.getIdColumnName());
		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK){
			Object cellValue = MsExcelUtils.getCellValue(cell);
			return String.valueOf(rdfSchema.cannonicaliseValue(this.getIdColumnName(), cellValue));
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