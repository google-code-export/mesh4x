package org.mesh4j.ektoo.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.test.utils.TestHelper;

public class InterRepositorySyncTest {
	
//	@Test
//	public void ShouldSyncExcelToMySqlWithoutRDFAssumeSameSchema(){
//		String user = "root";
//		String password = "test1234";
//		String tableName = "user";
//		
//		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
//		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", "user", "id");
//		
//		ISyncAdapter targetAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
//		
//		SyncEngine engine = new SyncEngine(sourceAsExcel,targetAsMySql);
//		List<Item> listOfConflicts = engine.synchronize();
//		Assert.assertEquals(0, listOfConflicts.size());
//	}
	

//	@Test
//	public void ShouldSyncGoogleSpreadSheetToMySQLWithoutRDFAssumeSameSchema(){
//		String user = "root";
//		String password = "test1234";
//		String tableName = "user_info";
//	
//		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
//				"peo4fu7AitTqkOhMSrecFRA",
//				"gspreadsheet.test@gmail.com",
//				"java123456",
//				"id",
//				1,
//				6,
//				"user_source",
//				"user"
//				);
//		
//		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
//		ISyncAdapter targetAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
//		ISyncAdapter sourceAsGoogleSpreadSheet = builder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
//		
//		SyncEngine engine = new SyncEngine(sourceAsGoogleSpreadSheet,targetAsMySql);
//		List<Item> listOfConflicts = engine.synchronize();
//		Assert.assertEquals(0, listOfConflicts.size());
//	}
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToExcelWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTqkOhMSrecFRA",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				1,
				6,
				"user_source",
				"user"
				);
		
		ISyncAdapter sourceAsGoogleSpreadSheet = builder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", "user", "id");
		
		SyncEngine engine = new SyncEngine(sourceAsGoogleSpreadSheet,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToGoogleSpreadSheetWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		GoogleSpreadSheetInfo spreadSheetInfoSource = new GoogleSpreadSheetInfo(
				"peo4fu7AitTqkOhMSrecFRA",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				1,
				6,
				"user_source",
				"user"
				);
		
		GoogleSpreadSheetInfo spreadSheetInfoTarget = new GoogleSpreadSheetInfo(
				"peo4fu7AitTqkOhMSrecFRA",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				1,
				6,
				"user_target",
				"user"
				);
		
		ISyncAdapter sourceAsGoogleSpreadSheet = builder.createGoogleSpreadSheetAdapter(spreadSheetInfoSource);
		
		ISyncAdapter targetAsGoogleSpreadSheet = builder.createGoogleSpreadSheetAdapter(spreadSheetInfoTarget);
		
		
		SyncEngine engine = new SyncEngine(sourceAsGoogleSpreadSheet,targetAsGoogleSpreadSheet);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	
	@Test
	public void ShouldSyncExcelToExcelWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "sourceContentFile.xls", "user", "id");
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "targetContentFile.xls", "user", "id");
		
		SyncEngine engine = new SyncEngine(sourceAsExcel,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncAccessToAccessWithoutRDFAssumeSameSchema(){
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsAccess = builder.createMsAccessAdapter(TestHelper.baseDirectoryForTest() +"aktoo_source.mdb" , "aktoo");
		ISyncAdapter targetAsAccess = builder.createMsAccessAdapter(TestHelper.baseDirectoryForTest() +"aktoo_target.mdb" , "aktoo");
		
		SyncEngine engine = new SyncEngine(sourceAsAccess,targetAsAccess);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncMysqlToMysqlWithoutRDFAssumeSameSchema(){
		String user = "root";
		String password = "test1234";
		String tableName = "user";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);

		user = "root";
		password = "test1234";
		tableName = "user_target";

		ISyncAdapter targetAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
		
		SyncEngine engine = new SyncEngine(sourceAsMySql,targetAsMySql);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
}
