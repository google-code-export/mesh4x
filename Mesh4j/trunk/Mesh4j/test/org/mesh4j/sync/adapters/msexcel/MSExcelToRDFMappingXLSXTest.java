package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.util.Date;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.DateHelper;

public class MSExcelToRDFMappingXLSXTest {

	private static final String SHEET_NAME = "Oswego";
	private static final String COLUMN_DATE_ONSET = "DateOnset";
	private static final String COLUMN_ILL = "ILL";
	private static final String COLUMN_SEX = "SEX";
	private static final String COLUMN_AGE = "AGE";
	private static final String COLUMN_CODE = "Code";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_OTHER = "ThisColumnNotExist";
	
	@Test
	public void shouldExtractRDFSchemaFromExcelFile() throws Exception{
		
		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public Workbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}
			@Override public String getFileName() {return "myFile.xlsx";}			
		};
		
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME, "http://localhost:8080/mesh4x/feeds/MyExample");
		
		System.out.println(rdfSchema.asXML());
		
		Assert.assertNotNull(rdfSchema);		
		
		Assert.assertEquals(6, rdfSchema.getPropertyCount());
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType(COLUMN_NAME));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType(COLUMN_CODE));
		Assert.assertEquals(IRDFSchema.XLS_DOUBLE, rdfSchema.getPropertyType(COLUMN_AGE));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType(COLUMN_SEX));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType(COLUMN_ILL));
		Assert.assertEquals(IRDFSchema.XLS_DATETIME, rdfSchema.getPropertyType(COLUMN_DATE_ONSET));
		Assert.assertNull(rdfSchema.getPropertyType(COLUMN_OTHER));
			
	}
	
	@Test
	public void shouldGetExcelRowsAsRDFIndividual() throws Exception{
		
		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public Workbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}	
			@Override public String getFileName() {return "myFile.xlsx";}
		};
		
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME, "http://localhost:8080/mesh4x/feeds/MyExample");
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(rdfSchema, COLUMN_CODE);
				
		Workbook workbook = getDefaultWorkbook();
		Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, SHEET_NAME);
		Row row = sheet.getRow(sheet.getLastRowNum());
		Row headerRow = sheet.getRow(sheet.getFirstRowNum());
		
		RDFInstance rdfInstance = mapper.converRowToRDF(headerRow, row);
		Assert.assertNotNull(rdfInstance);
		
		System.out.println(rdfInstance.asXML());
		
		Assert.assertEquals(6, rdfInstance.getPropertyCount());
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(0)), rdfInstance.getPropertyValue(COLUMN_NAME));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(1)), rdfInstance.getPropertyValue(COLUMN_CODE));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(2)), rdfInstance.getPropertyValue(COLUMN_AGE));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(3)), rdfInstance.getPropertyValue(COLUMN_SEX));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(4)), rdfInstance.getPropertyValue(COLUMN_ILL));
		Assert.assertEquals(DateHelper.formatW3CDateTime((Date)MsExcelUtils.getCellValue(row.getCell(5))), DateHelper.formatW3CDateTime((Date)rdfInstance.getPropertyValue(COLUMN_DATE_ONSET)));
		Assert.assertNull(rdfInstance.getPropertyValue(COLUMN_OTHER));
	}
	
	@Test
	public void shouldCreateExcelRowFromRDFindividual(){
		
		String sheetName = SHEET_NAME;
		
		RDFSchema schema = new RDFSchema(sheetName, "http://mesh4x/epiinfo/"+sheetName+"#", sheetName);
		schema.addStringProperty(COLUMN_NAME, "name", "en");
		schema.addStringProperty(COLUMN_CODE, "code", "en");
		schema.addIntegerProperty(COLUMN_AGE, "age", "en");
		schema.addStringProperty(COLUMN_SEX, "sex", "en");
		schema.addBooleanProperty(COLUMN_ILL, "ill", "en");
		schema.addDateTimeProperty(COLUMN_DATE_ONSET, "dateOnset", "en");
		RDFInstance rdfInstance = schema.createNewInstance("uri:urn:P1");
		
		long millis = System.currentTimeMillis();
		String name = "Name: " + millis;
		String code = "P1";
		Long age = 1l;
		String sex = "sex: " + millis;
		Boolean ill = true;
		Date dateOnset = new Date();
		
		rdfInstance.setProperty(COLUMN_NAME, name);
		rdfInstance.setProperty(COLUMN_CODE, code);
		rdfInstance.setProperty(COLUMN_AGE, age);
		rdfInstance.setProperty(COLUMN_SEX, sex);
		rdfInstance.setProperty(COLUMN_ILL, ill);
		rdfInstance.setProperty(COLUMN_DATE_ONSET, dateOnset);
		
		// Excel
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		Row headerRow = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,headerRow, COLUMN_NAME);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,headerRow, COLUMN_CODE);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,headerRow, COLUMN_AGE);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,headerRow, COLUMN_SEX);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,headerRow, COLUMN_ILL);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbook,headerRow, COLUMN_DATE_ONSET);
		
		Row row = sheet.createRow(1);
		
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(schema, COLUMN_CODE);
		mapper.appliesRDFToRow(workbook, sheet, row, rdfInstance);
		
		Assert.assertEquals(6, row.getLastCellNum());
		Assert.assertTrue(name.equals(MsExcelUtils.getCellValue(row.getCell(0))));
		Assert.assertTrue(code.equals(MsExcelUtils.getCellValue(row.getCell(1))));
		
		System.out.println("cell value is: "+MsExcelUtils.getCellValue(row.getCell(2)).getClass());
		Assert.assertTrue(age.equals(((Number)MsExcelUtils.getCellValue(row.getCell(2))).longValue()));
		
		Assert.assertTrue(sex.equals(MsExcelUtils.getCellValue(row.getCell(3))));
		Assert.assertTrue(ill.equals(MsExcelUtils.getCellValue(row.getCell(4))));
		Assert.assertTrue(dateOnset.getTime() == ((Date)MsExcelUtils.getCellValue(row.getCell(5))).getTime());
	}
	
	@Test 
	public void shouldChangeExcelRowFromRDFIndividual() throws Exception{
		
		Workbook workbook = getDefaultWorkbook();
		Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, SHEET_NAME);
		Row row = sheet.getRow(sheet.getLastRowNum());
		Row headerRow = sheet.getRow(sheet.getFirstRowNum());
		
		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public Workbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}		
			@Override public String getFileName() {return "myFile.xlsx";}
		};
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME, "http://localhost:8080/mesh4x/feeds/MyExample");
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(rdfSchema, COLUMN_CODE);
		
		RDFInstance rdfInstance = mapper.converRowToRDF(headerRow, row);
		Assert.assertNotNull(rdfInstance);
		
		
		//apply changes
		
		long millis = System.currentTimeMillis();
		String name = "Name: " + millis;
		String code = "Code: " + millis;
		Long age = ((Number)MsExcelUtils.getCellValue(row.getCell(2))).longValue() + 1;
		String sex = "sex: " + millis;
		Boolean ill = ((Boolean)MsExcelUtils.getCellValue(row.getCell(4))) ? false : true;
		Date dateOnset = new Date();
		
		rdfInstance.setProperty(COLUMN_NAME, name);
		rdfInstance.setProperty(COLUMN_CODE, code);
		rdfInstance.setProperty(COLUMN_AGE, age);
		rdfInstance.setProperty(COLUMN_SEX, sex);
		rdfInstance.setProperty(COLUMN_ILL, ill);
		rdfInstance.setProperty(COLUMN_DATE_ONSET, dateOnset);
		
		mapper.appliesRDFToRow(workbook, sheet, row, rdfInstance);
		
		Assert.assertEquals(6, row.getLastCellNum());
		Assert.assertTrue(name.equals(MsExcelUtils.getCellValue(row.getCell(0))));
		Assert.assertTrue(code.equals(MsExcelUtils.getCellValue(row.getCell(1))));
		Assert.assertTrue(age.equals(((Number)MsExcelUtils.getCellValue(row.getCell(2))).longValue()));
		Assert.assertTrue(sex.equals(MsExcelUtils.getCellValue(row.getCell(3))));
		Assert.assertTrue(ill.equals(MsExcelUtils.getCellValue(row.getCell(4))));
		Assert.assertTrue(dateOnset.getTime() == ((Date)MsExcelUtils.getCellValue(row.getCell(5))).getTime());
	}
	
	@Test
	public void shouldCreateExcelFileFromRDFSchema() throws Exception{

		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public Workbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}	
			@Override public String getFileName() {return "myFile.xlsx";}
		};
		
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME, "http://localhost:8080/mesh4x/feeds/MyExample");
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(rdfSchema, COLUMN_CODE);
		
		// create new file
		String newFileName = TestHelper.fileName("testRDFExcel"+IdGenerator.INSTANCE.newID()+".xlsx");
		mapper.createDataSource(newFileName);
		
		Workbook wb = new XSSFWorkbook(new File(newFileName).getAbsolutePath());
		Sheet sheet = wb.getSheet(SHEET_NAME);
		Assert.assertNotNull(sheet);
		
		Row row = sheet.getRow(0);
		Assert.assertNotNull(row);
		
		Cell cell = row.getCell(0);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_NAME, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(1);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_CODE, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(2);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_AGE, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(3);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_SEX, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(4);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_ILL, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(5);
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_DATE_ONSET, cell.getRichStringCellValue().getString());
	}
	
	protected Workbook getDefaultWorkbook() {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(SHEET_NAME);
		Row rowHeader = sheet.createRow(0);
		
		Cell cell = rowHeader.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, COLUMN_NAME));
		
		cell = rowHeader.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, COLUMN_CODE));
		
		cell = rowHeader.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, COLUMN_AGE));
		
		cell = rowHeader.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, COLUMN_SEX));
		
		cell = rowHeader.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, COLUMN_ILL));
		
		cell = rowHeader.createCell(5, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, COLUMN_DATE_ONSET));
	    
		Row rowData = sheet.createRow(1);
		
		cell = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "juan"));
		
		cell = rowData.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "P1"));
		
		cell = rowData.createCell(2, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(30);
		
		cell = rowData.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "male"));
		
		cell = rowData.createCell(4, Cell.CELL_TYPE_BOOLEAN);
		cell.setCellValue(true);
		
		cell = rowData.createCell(5, Cell.CELL_TYPE_NUMERIC);
		CellStyle cellStyle = workbook.createCellStyle();
	    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"));
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(new Date());

		return workbook;
	}
}
