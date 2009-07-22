package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.DateHelper;

public class MsExcelRDFSyncXLSXTest {

	@Test
	public void shouldSync() throws Exception{
			
		// schema
		String sheetName = "Oswego";
		String idColumn = "Code";

		RDFSchema schema = new RDFSchema(sheetName, "http://mesh4x/epiinfo/"+sheetName+"#", sheetName);
		schema.addStringProperty("Name", "Name", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("Code", "Code", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addIntegerProperty("AGE", "AGE", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("SEX", "SEX", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addBooleanProperty("ILL", "ILL", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addDateTimeProperty("DateOnset", "DateOnset", IRDFSchema.DEFAULT_LANGUAGE);
		
		schema.setIdentifiablePropertyName(idColumn);
		
		MsExcelToRDFMapping rdfMapping = new MsExcelToRDFMapping(schema);
		
		// source split adapter
		IMsExcel excel1 = new MsExcel(TestHelper.makeFileAndDeleteIfExists("myFileSource.xlsx").getCanonicalPath());
		rdfMapping.createDataSource(excel1);
		Workbook workbookSource = excel1.getWorkbook();
		Date date1 = new Date();
		Date date2 = new Date();
		Date date3 = new Date();
		addData(workbookSource, sheetName, "juan1", "1", 30, "male", true, date1);
		addData(workbookSource, sheetName, "juan2", "2", 35, "female", true, date2);
		addData(workbookSource, sheetName, "juan3", "3", 3, "male", false, date3);
		Assert.assertEquals(3, workbookSource.getSheet(sheetName).getLastRowNum()); 
		
		MockMsExcel excelSource = new MockMsExcel(excel1);
		MsExcelSyncRepository syncRepoSource = new MsExcelSyncRepository(excelSource, sheetName+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		MsExcelContentAdapter contentAdapterSource = new MsExcelContentAdapter(excelSource, rdfMapping);
		ISyncAdapter source = new SplitAdapter(syncRepoSource, contentAdapterSource, NullIdentityProvider.INSTANCE);

		// target split adapter
		IMsExcel excel2 = new MsExcel(TestHelper.makeFileAndDeleteIfExists("myFileSource.xlsx").getCanonicalPath());
		rdfMapping.createDataSource(excel2);
		Workbook workbookTarget = excel2.getWorkbook();
		Date date4 = new Date();
		Date date5 = new Date();
		Date date6 = new Date();
		addData(workbookTarget, sheetName, "juan4", "4", 30, "male", true, date4);
		addData(workbookTarget, sheetName, "juan5", "5", 35, "female", true, date5);
		addData(workbookTarget, sheetName, "juan6", "6", 3, "male", false, date6);
		Assert.assertEquals(3, workbookTarget.getSheet(sheetName).getLastRowNum());
		
		MockMsExcel excelTarget = new MockMsExcel(excel2);			
		MsExcelSyncRepository syncRepoTarget = new MsExcelSyncRepository(excelTarget, sheetName+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		MsExcelContentAdapter contentAdapterTarget = new MsExcelContentAdapter(excelTarget, rdfMapping);
		ISyncAdapter target = new SplitAdapter(syncRepoTarget, contentAdapterTarget, NullIdentityProvider.INSTANCE);
		
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflcts = syncEngine.synchronize();
		
		Assert.assertTrue(conflcts.isEmpty());
		
		Assert.assertTrue(excelSource.flushWasCalled());
		Assert.assertTrue(excelSource.dirtyWasCalled());
		
		List<Item> sourceItems = source.getAll();
		List<Item> targetItems = target.getAll();
		Assert.assertEquals(sourceItems.size(), targetItems.size());
		Assert.assertEquals(6, sourceItems.size());
		
		assertRDFItem(schema, contentAdapterSource, "juan1", "1", 30, "male", true, date1);
		assertRDFItem(schema, contentAdapterSource, "juan2", "2", 35, "female", true, date2);
		assertRDFItem(schema, contentAdapterSource, "juan3", "3", 3, "male", false, date3);
		assertRDFItem(schema, contentAdapterSource, "juan4", "4", 30, "male", true, date4);
		assertRDFItem(schema, contentAdapterSource, "juan5", "5", 35, "female", true, date5);
		assertRDFItem(schema, contentAdapterSource, "juan6", "6", 3, "male", false, date6);

		assertRDFItem(schema, contentAdapterTarget, "juan1", "1", 30, "male", true, date1);
		assertRDFItem(schema, contentAdapterTarget, "juan2", "2", 35, "female", true, date2);
		assertRDFItem(schema, contentAdapterTarget, "juan3", "3", 3, "male", false, date3);
		assertRDFItem(schema, contentAdapterTarget, "juan4", "4", 30, "male", true, date4);
		assertRDFItem(schema, contentAdapterTarget, "juan5", "5", 35, "female", true, date5);
		assertRDFItem(schema, contentAdapterTarget, "juan6", "6", 3, "male", false, date6);
	}
	
	@Test
	public void shouldSyncSameFile() throws DocumentException, IOException{

		String sheetName = "patient";
		String idColumnName = "id";
		
		// create file A
		File fileA = TestHelper.makeFileAndDeleteIfExists("dataAndSyncA_RDF.xlsx");
		Workbook workbookA = new XSSFWorkbook();
		
		Sheet sheetA = MsExcelUtils.getOrCreateSheetIfAbsent(workbookA, sheetName);			
		Row rowHeader = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheetA);						
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbookA,rowHeader, idColumnName);
		MsExcelUtils.getOrCreateCellStringIfAbsent(workbookA,rowHeader, "firstname");

		Row rowData = sheetA.createRow(sheetA.getLastRowNum() + 1);
		
		Cell cellIdValue = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		
		cellIdValue.setCellValue(MsExcelUtils.getRichTextString(workbookA, "1"));
		
		
		Cell cellNameValue = rowData.createCell(1, Cell.CELL_TYPE_STRING);
		cellNameValue.setCellValue(MsExcelUtils.getRichTextString(workbookA, "juan"));
		
		
		rowData = sheetA.createRow(sheetA.getLastRowNum() + 1);
		cellIdValue = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		cellIdValue.setCellValue(MsExcelUtils.getRichTextString(workbookA, "2"));
		
		
		cellNameValue = rowData.createCell(1, Cell.CELL_TYPE_STRING);
		cellNameValue.setCellValue(MsExcelUtils.getRichTextString(workbookA, "jose"));
		
		
		
		workbookA.write(new FileOutputStream(fileA));
		
		// create file B
		
		File fileB = TestHelper.makeFileAndDeleteIfExists("dataAndSyncB_RDF.xlsx");

		// create adapters and sync engine
		
		SplitAdapter adapterA = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileA.getCanonicalPath()), sheetName, new String[]{idColumnName}, null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/feeds");
		RDFSchema rdfSchema = (RDFSchema)((MsExcelContentAdapter)adapterA.getContentAdapter()).getSchema();
		
		SplitAdapter adapterB = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileB.getCanonicalPath()), NullIdentityProvider.INSTANCE, rdfSchema);
		
		// sync
		
		SyncEngine syncEngine = new SyncEngine(adapterA, adapterB);
		
		TestHelper.assertSync(syncEngine);		
		
		// no changes or updates are produced
		TestHelper.assertSync(syncEngine);
		
		adapterA = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileA.getCanonicalPath()), sheetName, new String[]{idColumnName}, null, new LoggedInIdentityProvider(), "http://localhost:8080/mesh4x/feeds");		
		adapterB = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileB.getCanonicalPath()), sheetName, new String[]{idColumnName}, null, new LoggedInIdentityProvider(), "http://localhost:8080/mesh4x/feeds");
		syncEngine = new SyncEngine(adapterA, adapterB);
		
		// no changes or updates are produced
		TestHelper.assertSync(syncEngine);		
		TestHelper.assertSync(syncEngine);
		
		List<Item> items = adapterA.getAll();
		for (Item item : items) {
			Assert.assertEquals(1, item.getSync().getUpdates());
			Assert.assertEquals(1, item.getSync().getUpdatesHistory().size());
			Assert.assertEquals(1, item.getLastUpdate().getSequence());
		}
	}
	
	
	private void assertRDFItem(IRDFSchema schema, MsExcelContentAdapter adapter, String name, String code,
			int age, String sex, boolean ill, Date dateOnset) {

		IContent content = adapter.get(code);
		Assert.assertNotNull(content);
		
		Element payload = content.getPayload();		
		RDFInstance instance = schema.createNewInstanceFromRDFXML(payload);
		
		Assert.assertEquals(name, instance.getPropertyValue("Name"));
		Assert.assertEquals(code, instance.getPropertyValue("Code"));
		Assert.assertEquals(age, instance.getPropertyValue("AGE"));
		Assert.assertEquals(sex, instance.getPropertyValue("SEX"));
		Assert.assertEquals(ill, instance.getPropertyValue("ILL"));
		Assert.assertEquals(DateHelper.formatW3CDateTime(dateOnset), DateHelper.formatW3CDateTime((Date)instance.getPropertyValue("DateOnset")));
		
	}

	private void addData(Workbook workbook, String sheetName, String name,
			String code, int age, String sex, boolean ill, Date dateOnset) {

		Sheet sheet = workbook.getSheet(sheetName);		
		Row rowData = sheet.createRow(sheet.getLastRowNum() + 1);
		
		Cell cell = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, name));
		
		
		cell = rowData.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, code));
		
		
		cell = rowData.createCell(2, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(age);
		
		cell = rowData.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, sex));
		
		
		cell = rowData.createCell(4, Cell.CELL_TYPE_BOOLEAN);
		cell.setCellValue(ill);
		
		cell = rowData.createCell(5, Cell.CELL_TYPE_NUMERIC);
		CellStyle cellStyle = workbook.createCellStyle();
	    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"));
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(dateOnset);	
	}

	private class MockMsExcel implements IMsExcel{

		private IMsExcel excel;
		private boolean flushWasCalled = false;
		private boolean dirtyWasCalled = false;
		
		private MockMsExcel(IMsExcel excel){
			this.excel = excel;
		}
		
		@Override public void flush() {
			this.excel.flush();
			flushWasCalled = true;
		}

		public boolean dirtyWasCalled() {
			return dirtyWasCalled;
		}

		public boolean flushWasCalled() {
			return flushWasCalled;
		}

		@Override
		public Workbook getWorkbook() {
			return this.excel.getWorkbook();
		}

		@Override
		public void setDirty() {
			this.excel.setDirty();
			dirtyWasCalled = true;			
		}

		@Override
		public String getFileName() {
			return this.excel.getFileName();
		}

		@Override
		public void reload() {
			this.excel.reload();
		}

		@Override
		public boolean fileExists() {
			return this.excel.fileExists();
		}

		@Override
		public Sheet getSheet(String sheetName) {
			return this.excel.getSheet(sheetName);
		}
		
	}
}
