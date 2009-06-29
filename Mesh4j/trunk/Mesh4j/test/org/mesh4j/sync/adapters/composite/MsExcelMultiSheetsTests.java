package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsExcelMultiSheetsTests {

	@Test
	public void shouldSyncAllMsExcelSheets() throws Exception{

		String excelFileName = createMsExcelFile("composite_MsExcel.xlsx");
		
		// Composite adapter
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		Map<String, String[]> sheets = new HashMap<String, String[]>();
		sheets.put("sheet1", new String[]{"Code"});
		sheets.put("sheet2", new String[]{"Code"});
		sheets.put("sheet3", new String[]{"Code"});;
		
		ISyncAdapter adapterSource = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, adapterOpaque, "http://localhost:8080/mesh4x/feeds");
		
		// Sync example
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
	}

	@Test
	public void ShouldSyncAllSheetsOfTwoExcelFileByRDF() throws Exception{
		
		String sourceExcelFile = createMsExcelFile("composite_MsExcel_source.xlsx");
		String targetExcelFile = createMsExcelFile("composite_MsExcel_target.xlsx");
	
		Map<String, String[]> sheets = new HashMap<String, String[]>();
		sheets.put("sheet1", new String[]{"Code"});
		sheets.put("sheet2", new String[]{"Code"});
		sheets.put("sheet3", new String[]{"Code"});
		
		InMemorySyncAdapter opaqueAdapterSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(sourceExcelFile, NullIdentityProvider.INSTANCE, sheets,opaqueAdapterSource, "http://localhost:8080/mesh4x/feeds");
	
		
		InMemorySyncAdapter opaqueAdapterTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(targetExcelFile, NullIdentityProvider.INSTANCE, sheets,opaqueAdapterTarget, "http://localhost:8080/mesh4x/feeds");
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
	}
	
	
	@Test
	public void ShouldCreateTargetAndSyncAllSheetsOfTwoExcelFileByRDF() throws Exception{
		
		String sourceExcelFile = createMsExcelFile("composite_MsExcel_source.xlsx");
		String targetExcelFile = TestHelper.baseDirectoryForTest() + "composite_MsExcel_target.xls";
	
		Map<String, String[]> sourceSheets = new HashMap<String, String[]>();
		sourceSheets.put("sheet1", new String[]{"Code"});
		sourceSheets.put("sheet2", new String[]{"Code"});
		sourceSheets.put("sheet3", new String[]{"Code"});
		
		List<IRDFSchema> targetSheets = new ArrayList<IRDFSchema>();
		
		InMemorySyncAdapter opaqueAdapterSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(sourceExcelFile, NullIdentityProvider.INSTANCE, sourceSheets, opaqueAdapterSource, "http://localhost:8080/mesh4x/feeds");
	
		for(IIdentifiableSyncAdapter identifiableAdapter :((CompositeSyncAdapter)adapterSource).getAdapters()){
			SplitAdapter adapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			IRDFSchema rdfSchema = (IRDFSchema)((MsExcelContentAdapter)adapter.getContentAdapter()).getSchema();
			targetSheets.add(rdfSchema);
		}
		
		InMemorySyncAdapter opaqueAdapterTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(targetExcelFile, NullIdentityProvider.INSTANCE, opaqueAdapterTarget, targetSheets);
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
	}
	
	@Test
	public void ShouldSyncAllSheetsOfTwoExcelformatByRDF() throws Exception{
		String sourceExcelFile = createMsExcelFile("composite_MsExcel_source.xls");
		String targetExcelFile = createMsExcelFile("composite_MsExcel_target.xlsx");
	
		Map<String, String[]> sheets = new HashMap<String, String[]>();
		sheets.put("sheet1", new String[]{"Code"});
		sheets.put("sheet2", new String[]{"Code"});
		sheets.put("sheet3", new String[]{"Code"});
		
		InMemorySyncAdapter opaqueAdapterSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(sourceExcelFile, NullIdentityProvider.INSTANCE, sheets, opaqueAdapterSource, "http://localhost:8080/mesh4x/feeds");
	
		
		InMemorySyncAdapter opaqueAdapterTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(targetExcelFile, NullIdentityProvider.INSTANCE, sheets, opaqueAdapterTarget, "http://localhost:8080/mesh4x/feeds");
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void ShouldSyncAllSheetOfTwoExceFileByPlainXML() throws Exception{
		String sourceExcelFile = createMsExcelFile("composite_MsExcel_source.xlsx");
		String targetExcelFile = createMsExcelFile("composite_MsExcel_target.xlsx");
		
		Map<String, String> sheets = new HashMap<String, String>();
		sheets.put("sheet1", "Code");
		sheets.put("sheet2", "Code");
		sheets.put("sheet3", "Code");
		
		MsExcelSyncAdapterFactory factory = new MsExcelSyncAdapterFactory();
		
		InMemorySyncAdapter opaqueAdapterSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = factory.createSyncAdapterForMultiSheets(sourceExcelFile, NullIdentityProvider.INSTANCE, sheets,opaqueAdapterSource);
		
		InMemorySyncAdapter opaqueAdapterTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = factory.createSyncAdapterForMultiSheets(targetExcelFile, NullIdentityProvider.INSTANCE, sheets,opaqueAdapterTarget);
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		syncEngine.synchronize();
		TestHelper.assertSync(syncEngine);
	}
	public static String createMsExcelFile(String fileName) throws Exception {
		
		// Make MsExcel file
		File excelFile = TestHelper.makeFileAndDeleteIfExists(fileName);
		String excelFileName = excelFile.getCanonicalPath();
		
		Date dateOnset = new Date();
		
		Workbook workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(excelFileName);
		addHeaderToMsExcel(workbook, "sheet1");
		addDataToMsExcel(workbook, "sheet1", "Juan", "1", 30, "Male", true, dateOnset);
		addDataToMsExcel(workbook, "sheet1", "Marcelo", "2", 25, "Male", false, dateOnset);
		
		addHeaderToMsExcel(workbook, "sheet2");
		addDataToMsExcel(workbook, "sheet2", "Juan", "3", 35, "Male", false, dateOnset);
		addDataToMsExcel(workbook, "sheet2", "Carlos", "4", 57, "Male", false, dateOnset);
		
		addHeaderToMsExcel(workbook, "sheet3");
		addDataToMsExcel(workbook, "sheet3", "Juan", "5", 53, "Male", true, dateOnset);
		addDataToMsExcel(workbook, "sheet3", "Ignacio", "6", 11, "Male", true, dateOnset);
		
		MsExcelUtils.flush(workbook, excelFileName);
		return excelFileName;
	}
	
	public static void addHeaderToMsExcel(Workbook workbook, String sheetName) {

		Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);		
		Row rowData = sheet.createRow(0);
		
		Cell cell = rowData.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "Name"));
		
		cell = rowData.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "Code"));
		
		cell = rowData.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "Age"));
		
		cell = rowData.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "Sex"));
		
		cell = rowData.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "Ill"));
		
		cell = rowData.createCell(5, Cell.CELL_TYPE_STRING);
		cell.setCellValue(MsExcelUtils.getRichTextString(workbook, "DateOnset"));
	}
	
	public static void addDataToMsExcel(Workbook workbook, String sheetName, String name, String code, int age, String sex, boolean ill, Date dateOnset) {

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
}
