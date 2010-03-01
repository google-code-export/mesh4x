package org.mesh4j.sync.adapters.msexcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.payload.schema.rdf.SchemaMappedRDFSchema;
import org.mesh4j.sync.validations.Guard;

import com.hp.hpl.jena.rdf.model.Resource;

public class MsExcelToRDFMapping extends AbstractRDFIdentifiableMapping implements IMsExcelToXMLMapping{

	// BUSINESS METHODs
	public MsExcelToRDFMapping(IRDFSchema rdfSchema) {
		super(rdfSchema);
	}
	
	public static RDFSchema extractRDFSchema(IMsExcel excel, String sheetName, String[] identifiablePropertyNames, String lastUpdateColumnName, String rdfURL){
		return extractRDFSchema(excel, sheetName, Arrays.asList(identifiablePropertyNames), lastUpdateColumnName, rdfURL, null, null);
	}
	
	/**
	 * this one is for different schema
	 * @param excel
	 * @param sheetName
	 * @param identifiablePropertyNames
	 * @param lastUpdateColumnName
	 * @param rdfURL
	 * @param syncSchema
	 * @param schemaConvertMap
	 * @return
	 */
	public static RDFSchema extractRDFSchema(IMsExcel excel, String sheetName, String[] identifiablePropertyNames, String lastUpdateColumnName, String rdfURL,
			Map<String, Resource> syncSchema, Map<String, String> schemaConvertMap){
		return extractRDFSchema(excel, sheetName, Arrays.asList(identifiablePropertyNames), lastUpdateColumnName, rdfURL, syncSchema, schemaConvertMap);
	}
	
	public static RDFSchema extractRDFSchema(IMsExcel excel, String sheetName, List<String> identifiablePropertyNames, String lastUpdateColumnName, String rdfURL,
			Map<String, Resource> syncSchema, Map<String, String> schemaConvertMap){
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(rdfURL, "rdfURL");
		
		String entityName = getEntityName(sheetName);
		
		//RDFSchema rdfSchema = new RDFSchema(entityName, rdfURL+"/"+entityName+"#", entityName);
		
		RDFSchema rdfSchema;
		if(syncSchema != null && schemaConvertMap != null)
			rdfSchema = new SchemaMappedRDFSchema(entityName, rdfURL+"/"+entityName+"#", entityName, syncSchema, schemaConvertMap);
		else
			rdfSchema = new RDFSchema(entityName, rdfURL+"/"+entityName+"#", entityName);
		
		String cellName;
		String propertyName;
		
		Workbook workbook = excel.getWorkbook();
		Sheet sheet = getSheet(workbook, sheetName);
				
		Cell cell;

		Row headerRow = sheet.getRow(sheet.getFirstRowNum());
		Row dataRow = sheet.getRow(sheet.getLastRowNum());
		// it has been found that sometimes manually deleting last row clears
		// data from cells not the column itself
		// and thats why its not safe to attempt creation of rdfSchema from last
		// row, before proceed with last row check if cells are available
		// otherwise pick the previous row
		int lastRowIndex = sheet.getLastRowNum();
		while(!dataRow.cellIterator().hasNext() && lastRowIndex > 0){
			dataRow = sheet.getRow(--lastRowIndex);
		}
		
		for (Iterator<Cell> iterator = dataRow.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			
			cellName = headerRow.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
			int cellType = cell.getCellType();
			propertyName = RDFSchema.normalizePropertyName(cellName);
			
			if(Cell.CELL_TYPE_STRING == cellType || Cell.CELL_TYPE_FORMULA == cellType){
				rdfSchema.addStringProperty(propertyName, cellName, IRDFSchema.DEFAULT_LANGUAGE);
			} else if(Cell.CELL_TYPE_BOOLEAN == cellType){
				rdfSchema.addBooleanProperty(propertyName, cellName, IRDFSchema.DEFAULT_LANGUAGE);
			} else if(Cell.CELL_TYPE_NUMERIC == cellType){
				if(DateUtil.isCellDateFormatted(cell)) {
					rdfSchema.addDateTimeProperty(propertyName, cellName, IRDFSchema.DEFAULT_LANGUAGE);
				} else {
					rdfSchema.addDoubleProperty(propertyName, cellName, IRDFSchema.DEFAULT_LANGUAGE);
		        }
			}
		}
		
		rdfSchema.setIdentifiablePropertyNames(identifiablePropertyNames);
		rdfSchema.setVersionPropertyName(lastUpdateColumnName);
		return rdfSchema;
	}

	public RDFInstance converRowToRDF(Sheet sheet, Row headerRow, Row row) {
		
		// obtains properties values
		Cell cell;
		String cellName;
		String propertyName;
		Object cellValue;
		Object propertyValue;

		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			cellName = headerRow.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
			propertyName = RDFSchema.normalizePropertyName(cellName);
			cellValue = MsExcelUtils.getCellValue(cell);
			
			propertyValue = this.rdfSchema.cannonicaliseValue(propertyName, cellValue);
			if(propertyValue != null){
				propertyValues.put(propertyName, propertyValue);
			}
		}
		
		// create rdf instance
		String id = getId(sheet, row);
		
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, propertyValues);
		return rdfInstance;
	}

	public void appliesRDFToRow(Workbook wb, Sheet sheet, Row row, RDFInstance rdfInstance) {
	
		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			Object propertyValue = rdfInstance.getPropertyValue(propertyName);
						
			if(propertyValue != null){
				Row headerRow = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
				String propertyLabel = rdfInstance.getPropertyLabel(propertyName);
				Cell headerCell = getCellOrCreateHeaderCell(wb, headerRow, propertyName, propertyLabel);
				
				Cell cell = row.getCell(headerCell.getColumnIndex());
				String propertyType = rdfInstance.getPropertyType(propertyName);
				if(cell == null){
					cell = createCell(wb, row, headerCell.getColumnIndex(), propertyType);
				}
				MsExcelUtils.setCellValue(wb, cell, getCellType(propertyType), propertyValue);	
			}			
		}		
	}
	
	private Cell getCellOrCreateHeaderCell(Workbook wb, Row row, String propertyName, String propertyLabel) {
		Cell cell = MsExcelUtils.getCellString(wb, row, propertyName);
		if(cell == null){
			if(propertyLabel.equals(propertyName)){
				cell = MsExcelUtils.createCellString(wb, row, propertyName);
			} else {
				if(propertyLabel != null){
					cell = MsExcelUtils.getCellString(wb, row, propertyLabel);
				}
				
				if(cell == null){
					cell = MsExcelUtils.createCellString(wb, row, propertyName);	
				}
			}
		}
		return cell;
	}

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

	public void createDataSource(IMsExcel excel) {
		excel.setDirty();
		Workbook workbook = excel.getWorkbook();

		Sheet sheet = workbook.createSheet(this.rdfSchema.getOntologyClassName());
		Row headerRow = sheet.createRow(0);
		Cell headerCell;
		
		int size = this.rdfSchema.getPropertyCount();
		for (int j = 0; j < size; j++) {
			String propertyName = this.rdfSchema.getPropertyName(size - 1 - j);	
			String label = this.rdfSchema.getPropertyLabel(propertyName);
			headerCell = headerRow.createCell(j);
			headerCell.setCellValue(MsExcelUtils.getRichTextString(workbook, label));			
		}
		excel.flush();
	}
	
	@Override
	public Element convertRowToXML(Workbook wb, Sheet sheet, Row row){
		Row headerRow = sheet.getRow(sheet.getFirstRowNum());
		RDFInstance rdfInstance = this.converRowToRDF(sheet, headerRow, row);
		return rdfInstance.asElementRDFXML();
	}
	
	@Override
	public void appliesXMLToRow(Workbook wb, Sheet sheet, Row row, Element rdfElement){
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromRDFXML(rdfElement);
		this.appliesRDFToRow(wb, sheet, row, rdfInstance);
	}

	@Override
	public String getId(Sheet sheet, Row row) {
		List<String> idColumnNames = this.rdfSchema.getIdentifiablePropertyNames();
		List<String> idValues = new ArrayList<String>();
		
		for (String idColumnName : idColumnNames) {
			//this is needed for identifiable property including space char,
			idColumnName = idColumnName.replaceAll(" ", "_"); // without this a property name including
															  // space character will not be available
			String label = this.rdfSchema.getPropertyLabel(idColumnName);
			String idCellValue = getCellValue(sheet, row, idColumnName, label);
			if(idCellValue == null){
				return null;
			} else {
				idValues.add(idCellValue);
			}
		}
		return makeId(idValues);		
	}

	private String getCellValue(Sheet sheet, Row row, String columnName, String label) {
		Cell cell = getCell(sheet, row, columnName, label);
		if(cell != null){
 			Object cellValue = MsExcelUtils.getCellValue(cell);
			return String.valueOf(rdfSchema.cannonicaliseValue(columnName, cellValue));
		} else {
			return null;
		}
	}
	
	private Cell getCell(Sheet sheet, Row row, String columnName, String label) {
		Cell cell = MsExcelUtils.getCell(sheet, row, columnName);
		if(cell == null){
			cell = MsExcelUtils.getCell(sheet, row, label);
		}
		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK){
 			return cell;
		} else {
			return null;
		}
	}
	
	@Override
	public Date getLastUpdate(Sheet sheet, Row row) {
		Cell cell = getCell(sheet, row, this.rdfSchema.getVersionPropertyName(), this.rdfSchema.getPropertyLabel(this.rdfSchema.getVersionPropertyName()));
		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)){
			return cell.getDateCellValue();
		} else {
			return null;
		}
	}

	@Override
	public Row getRow(Sheet sheet, String id) {
		List<String> propertyNames = this.rdfSchema.getIdentifiablePropertyNames();
		String[] values = this.getIds(id);
		
		for (int i = sheet.getFirstRowNum()+1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if(row != null){
				int ok = 0;
				for (int j = 0; j < propertyNames.size(); j++) {
					String propertyName = propertyNames.get(j);
					 
					//this is needed for identifiable property including space char,
					propertyName = propertyName.replaceAll(" ", "_"); // without this a property name including
																	  // space character will not be available
					
					Cell cellId = MsExcelUtils.getCell(sheet, row, propertyName);
					if(cellId == null){
						String label = this.rdfSchema.getPropertyLabel(propertyName);
						cellId = MsExcelUtils.getCell(sheet, row, label);
					}
					
					if(cellId != null && cellId.getCellType() != Cell.CELL_TYPE_BLANK){
						Object cellValue = MsExcelUtils.getCellValue(cellId);
						String idCellValue = String.valueOf(rdfSchema.cannonicaliseValue(propertyName, cellValue));
						if(values[j].equals(idCellValue)){
							ok = ok +1;
						}
					}
				}
				if(ok == propertyNames.size()){
					return row;
				}
			}
		}
		return null;
	}

	@Override
	public void initializeHeaderRow(Workbook wb, Sheet sheet, Row row) {
		for (String propertyName : this.rdfSchema.getIdentifiablePropertyNames()) {
			String label = this.rdfSchema.getPropertyLabel(propertyName);
			getCellOrCreateHeaderCell(wb, row, propertyName, label);
		}
		
		if(this.rdfSchema.getVersionPropertyName() != null){
			String propertyName = this.rdfSchema.getVersionPropertyName();
			String label = this.rdfSchema.getPropertyLabel(propertyName);
			getCellOrCreateHeaderCell(wb, row, propertyName, label);

		}
	}

	public static String getEntityName(String sheetName) {
		return sheetName.trim().replaceAll(" ", "_");
	}

	@Override
	public Sheet getSheet(Workbook workbook) {
		return getSheet(workbook, this.getType()) ;
	}
	
	public static Sheet getSheet(Workbook workbook, String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if(sheet == null){
			String sheetNameWithBlanks = sheetName.replaceAll("_", " ");
			sheet = workbook.getSheet(sheetNameWithBlanks);
		}

		if(sheet == null){
			sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		}
		return sheet;
	}
}