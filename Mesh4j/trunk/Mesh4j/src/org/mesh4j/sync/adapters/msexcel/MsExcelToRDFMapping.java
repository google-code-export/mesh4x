package org.mesh4j.sync.adapters.msexcel;

import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

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
	

	@SuppressWarnings("unchecked")
	public static RDFSchema extractRDFSchema(IMsExcel excel, String sheetName, String rdfURL){
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(rdfURL, "rdfURL");
		
		RDFSchema rdfSchema = new RDFSchema(sheetName, rdfURL+sheetName+"#", sheetName);
		
		String cellName;
		
		HSSFWorkbook workbook = excel.getWorkbook();
		HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		HSSFCell cell;

		HSSFRow headerRow = sheet.getRow(sheet.getFirstRowNum());
		HSSFRow dataRow = sheet.getRow(sheet.getLastRowNum());
		int cellType;
		for (Iterator<HSSFCell> iterator = dataRow.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			
			cellName = headerRow.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
			cellType = cell.getCellType();
			if(HSSFCell.CELL_TYPE_STRING == cellType){
				rdfSchema.addStringProperty(cellName, cellName, "en");
			} else if(HSSFCell.CELL_TYPE_BOOLEAN == cellType){
				rdfSchema.addBooleanProperty(cellName, cellName, "en");
			} else if(HSSFCell.CELL_TYPE_NUMERIC == cellType){
				if(HSSFDateUtil.isCellDateFormatted(cell)) {
					rdfSchema.addDateTimeProperty(cellName, cellName, "en");
				} else {
					rdfSchema.addLongProperty(cellName, cellName, "en");
		        }
			}
		}
		
		return rdfSchema;
	}

	@SuppressWarnings("unchecked")
	public RDFInstance converRowToRDF(HSSFRow headerRow, HSSFRow row) {
		
		Element payload = DocumentHelper.createElement(rdfSchema.getOntologyClassName());
		
		HSSFCell cell;
		HSSFCell cellHeader;
		String columnName;
		Object columnValue;
		Element fieldElement;
		String id = "";
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			cellHeader = headerRow.getCell(cell.getColumnIndex());
			
			columnName = cellHeader.getRichStringCellValue().getString();
			columnValue = MsExcelUtils.getCellValue(cell);
			
			fieldElement = payload.addElement(columnName);
			
			if(columnValue instanceof Date){
				fieldElement.addText(DateHelper.formatW3CDateTime((Date)columnValue));
			} else {
				fieldElement.addText(String.valueOf(columnValue));
			}
			
			if(columnName.equals(this.idColumnName)){
				id = String.valueOf(columnValue);
			}
		}

		try{
			RDFInstance instance = this.rdfSchema.createNewInstanceFromPlainXML(id, payload.asXML(), ISchema.EMPTY_FORMATS);
			return instance;
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public void appliesRDFToRow(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, RDFInstance rdfInstance) {
		HSSFCell cell;
		Object propertyValue;
		String propertyType;
		
		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			propertyValue = rdfInstance.getPropertyValue(propertyName);
						
			if(propertyValue != null){
				HSSFRow headerRow = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
				HSSFCell headerCell = MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, propertyName);
				
				cell = row.getCell(headerCell.getColumnIndex());
				propertyType = rdfInstance.getPropertyType(propertyName);
				if(cell == null){
					cell = createCell(wb, row, headerCell.getColumnIndex(), propertyType);
				}
				MsExcelUtils.setCellValue(cell, propertyValue);	
			}			
		}		
	}
	
	// TODO (JMT) RDF: improve MSExcel to RDF type mapper
	private HSSFCell createCell(HSSFWorkbook wb, HSSFRow row, int columnIndex, String propertyType) {

		if(IRDFSchema.XLS_STRING.equals(propertyType)){
			return row.createCell(columnIndex, HSSFCell.CELL_TYPE_STRING);			
		}else if(IRDFSchema.XLS_BOOLEAN.equals(propertyType)){
			return row.createCell(columnIndex, HSSFCell.CELL_TYPE_BOOLEAN);
		}else if(IRDFSchema.XLS_INTEGER.equals(propertyType) 
				|| IRDFSchema.XLS_LONG.equals(propertyType)
				|| IRDFSchema.XLS_DOUBLE.equals(propertyType)
				|| IRDFSchema.XLS_DECIMAL.equals(propertyType)){
			return row.createCell(columnIndex, HSSFCell.CELL_TYPE_NUMERIC);
		}else if(IRDFSchema.XLS_DATETIME.equals(propertyType)){
			HSSFCellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(wb.createDataFormat().getFormat("m/d/yy h:mm"));
		    
		    HSSFCell cell = row.createCell(columnIndex, HSSFCell.CELL_TYPE_NUMERIC);
		    cell.setCellStyle(cellStyle);
		    return cell;
		} else {
			return row.createCell(columnIndex, HSSFCell.CELL_TYPE_STRING);
		}
	}

	public void createDataSource(String fileName) throws Exception {
		HSSFWorkbook workbook = createDataSource();			
		MsExcelUtils.flush(workbook, fileName);		
	}

	public HSSFWorkbook createDataSource() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(this.rdfSchema.getOntologyNameSpace());
		HSSFRow headerRow = sheet.createRow(0);
		HSSFCell headerCell;
		
		int size = this.rdfSchema.getPropertyCount();
		String propertyName;
		for (int j = 0; j < size; j++) {
			propertyName = this.rdfSchema.getPropertyName(size - 1 - j);	
			
			headerCell = headerRow.createCell(j);
			headerCell.setCellValue(new HSSFRichTextString(propertyName));			
		}
		return workbook;
	}
	
	@Override
	public Element convertRowToXML(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row){
		HSSFRow headerRow = sheet.getRow(sheet.getFirstRowNum());
		RDFInstance rdfInstance = this.converRowToRDF(headerRow, row);
		return XMLHelper.parseElement(rdfInstance.asXML());
	}
	
	@Override
	public void appliesXMLToRow(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, Element rdfElement){
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
}