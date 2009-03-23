package org.mesh4j.sync.adapters.msexcel;

import java.io.FileInputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;

public class MSExcelToRDFMappingTest {

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
			@Override public HSSFWorkbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}			
		};
		
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME);
		
		System.out.println(rdfSchema.asXML());
		
		Assert.assertNotNull(rdfSchema);		
		
		Assert.assertEquals(6, rdfSchema.getPropertyCount());
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType(COLUMN_NAME));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType(COLUMN_CODE));
		Assert.assertEquals(IRDFSchema.XLS_INTEGER, rdfSchema.getPropertyType(COLUMN_AGE));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType(COLUMN_SEX));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType(COLUMN_ILL));
		Assert.assertEquals(IRDFSchema.XLS_DATETIME, rdfSchema.getPropertyType(COLUMN_DATE_ONSET));
		Assert.assertNull(rdfSchema.getPropertyType(COLUMN_OTHER));
			
	}
	
	@Test
	public void shouldGetExcelRowsAsRDFIndividual() throws Exception{
		
		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public HSSFWorkbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}			
		};
		
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME);
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(rdfSchema, COLUMN_CODE);
				
		HSSFWorkbook workbook = getDefaultWorkbook();
		HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, SHEET_NAME);
		HSSFRow row = sheet.getRow(sheet.getLastRowNum());
		HSSFRow headerRow = sheet.getRow(sheet.getFirstRowNum());
		
		RDFInstance rdfInstance = mapper.converRowToRDF(headerRow, row);
		Assert.assertNotNull(rdfInstance);
		
		System.out.println(rdfInstance.asXML());
		
		Assert.assertEquals(6, rdfInstance.getPropertyCount());
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(0)), rdfInstance.getPropertyValue(COLUMN_NAME));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(1)), rdfInstance.getPropertyValue(COLUMN_CODE));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(2)), rdfInstance.getPropertyValue(COLUMN_AGE));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(3)), rdfInstance.getPropertyValue(COLUMN_SEX));
		Assert.assertEquals(MsExcelUtils.getCellValue(row.getCell(4)), rdfInstance.getPropertyValue(COLUMN_ILL));
		Assert.assertEquals(((Date)MsExcelUtils.getCellValue(row.getCell(5))).getTime(), ((Date)rdfInstance.getPropertyValue(COLUMN_DATE_ONSET)).getTime());
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
		Double age = 1d;
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
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		HSSFRow headerRow = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
		MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, COLUMN_NAME);
		MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, COLUMN_CODE);
		MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, COLUMN_AGE);
		MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, COLUMN_SEX);
		MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, COLUMN_ILL);
		MsExcelUtils.getOrCreateCellStringIfAbsent(headerRow, COLUMN_DATE_ONSET);
		
		HSSFRow row = sheet.createRow(1);
		
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(schema, COLUMN_CODE);
		mapper.appliesRDFToRow(workbook, sheet, row, rdfInstance);
		
		Assert.assertEquals(6, row.getLastCellNum());
		Assert.assertTrue(name.equals(MsExcelUtils.getCellValue(row.getCell(0))));
		Assert.assertTrue(code.equals(MsExcelUtils.getCellValue(row.getCell(1))));
		Assert.assertTrue(age.equals(MsExcelUtils.getCellValue(row.getCell(2))));
		Assert.assertTrue(sex.equals(MsExcelUtils.getCellValue(row.getCell(3))));
		Assert.assertTrue(ill.equals(MsExcelUtils.getCellValue(row.getCell(4))));
		Assert.assertTrue(dateOnset.getTime() == ((Date)MsExcelUtils.getCellValue(row.getCell(5))).getTime());
	}
	
	@Test 
	public void shouldChangeExcelRowFromRDFIndividual() throws Exception{
		
		HSSFWorkbook workbook = getDefaultWorkbook();
		HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, SHEET_NAME);
		HSSFRow row = sheet.getRow(sheet.getLastRowNum());
		HSSFRow headerRow = sheet.getRow(sheet.getFirstRowNum());
		
		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public HSSFWorkbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}			
		};
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME);
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(rdfSchema, COLUMN_CODE);
		
		RDFInstance rdfInstance = mapper.converRowToRDF(headerRow, row);
		Assert.assertNotNull(rdfInstance);
		
		
		//apply changes
		
		long millis = System.currentTimeMillis();
		String name = "Name: " + millis;
		String code = "Code: " + millis;
		Double age = ((Double)MsExcelUtils.getCellValue(row.getCell(2))).doubleValue() + 1;
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
		Assert.assertTrue(age.equals(MsExcelUtils.getCellValue(row.getCell(2))));
		Assert.assertTrue(sex.equals(MsExcelUtils.getCellValue(row.getCell(3))));
		Assert.assertTrue(ill.equals(MsExcelUtils.getCellValue(row.getCell(4))));
		Assert.assertTrue(dateOnset.getTime() == ((Date)MsExcelUtils.getCellValue(row.getCell(5))).getTime());
	}
	
	@Test
	public void shouldCreateExcelFileFromRDFSchema() throws Exception{

		IMsExcel excel = new IMsExcel(){
			@Override public void flush() {}
			@Override public HSSFWorkbook getWorkbook() {return getDefaultWorkbook();}
			@Override public void setDirty() {}			
		};
		
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, SHEET_NAME);
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(rdfSchema, COLUMN_CODE);
		
		// create new file
		String newFileName = TestHelper.fileName("testRDFExcel"+IdGenerator.INSTANCE.newID()+".xls");
		mapper.createDataSource(newFileName);
		
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(newFileName));
		HSSFSheet sheet = wb.getSheet(SHEET_NAME);
		Assert.assertNotNull(sheet);
		
		HSSFRow row = sheet.getRow(0);
		Assert.assertNotNull(row);
		
		HSSFCell cell = row.getCell(0);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_NAME, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(1);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_CODE, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(2);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_AGE, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(3);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_SEX, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(4);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_ILL, cell.getRichStringCellValue().getString());
		
		cell = row.getCell(5);
		Assert.assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
		Assert.assertEquals(COLUMN_DATE_ONSET, cell.getRichStringCellValue().getString());
	}
	
	protected HSSFWorkbook getDefaultWorkbook() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(SHEET_NAME);
		HSSFRow rowHeader = sheet.createRow(0);
		
		HSSFCell cell = rowHeader.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(COLUMN_NAME));
		
		cell = rowHeader.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(COLUMN_CODE));
		
		cell = rowHeader.createCell(2, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(COLUMN_AGE));
		
		cell = rowHeader.createCell(3, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(COLUMN_SEX));
		
		cell = rowHeader.createCell(4, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(COLUMN_ILL));
		
		cell = rowHeader.createCell(5, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(COLUMN_DATE_ONSET));
	    
		HSSFRow rowData = sheet.createRow(1);
		
		cell = rowData.createCell(0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("juan"));
		
		cell = rowData.createCell(1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("P1"));
		
		cell = rowData.createCell(2, HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(30);
		
		cell = rowData.createCell(3, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString("male"));
		
		cell = rowData.createCell(4, HSSFCell.CELL_TYPE_BOOLEAN);
		cell.setCellValue(true);
		
		cell = rowData.createCell(5, HSSFCell.CELL_TYPE_NUMERIC);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
	    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"));
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(new Date());

		return workbook;
	}
}
