package org.mesh4j.ektoo.test;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
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
	public void ShouldSyncRssToRssWithoutRDFAssumeSameSchema(){
		
		String link = "";
		Element element = TestHelper.makeElement("<payload><user><id>SyncId123</id><name>SyncId123</name><pass>123</pass></user></payload>");
		XMLContent content = new XMLContent("SyncId123", "SyncId123", "SyncId123", element);
		Item item = new Item(content, new Sync("SyncId123", "jmt", TestHelper.now(), false));
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		 
		File rssSourceFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_source.xml"));
		ISyncAdapter sourceRSSAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssSourceFile.getAbsolutePath(), RssSyndicationFormat.INSTANCE);
		
		sourceRSSAdapter.add(item);
		
		File rssTargetFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_target.xml"));
		ISyncAdapter targetRSSAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssTargetFile.getAbsolutePath(), RssSyndicationFormat.INSTANCE);
		
		Assert.assertEquals(1, sourceRSSAdapter.getAll().size());
		Assert.assertEquals(0, targetRSSAdapter.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(sourceRSSAdapter,targetRSSAdapter);
		List<Item> listOfConflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(1, sourceRSSAdapter.getAll().size());
		Assert.assertEquals(1, targetRSSAdapter.getAll().size());
		
	}
	
	@Test
	public void ShouldSyncAtomToAtomWithoutRDFAssumeSameSchema(){
		
		String link = "";
		Element element = TestHelper.makeElement("<payload><user><id>SyncId123</id><name>SyncId123</name><pass>123</pass></user></payload>");
		XMLContent content = new XMLContent("SyncId123", "SyncId123", "SyncId123", element);
		Item item = new Item(content, new Sync("SyncId123", "jmt", TestHelper.now(), false));
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		 
		File rssSourceFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_source.xml"));
		ISyncAdapter sourceAtomAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssSourceFile.getAbsolutePath(), AtomSyndicationFormat.INSTANCE);
		
		sourceAtomAdapter.add(item);
		
		File rssTargetFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_target.xml"));
		ISyncAdapter targetAtomAdapter = builder.createFeedAdapter("User", "user Info", link, 
										rssTargetFile.getAbsolutePath(), AtomSyndicationFormat.INSTANCE);
		
		Assert.assertEquals(1, sourceAtomAdapter.getAll().size());
		Assert.assertEquals(0, targetAtomAdapter.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(sourceAtomAdapter,targetAtomAdapter);
		List<Item> listOfConflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(1, sourceAtomAdapter.getAll().size());
		Assert.assertEquals(1, targetAtomAdapter.getAll().size());
		
	}
	
	@Test
	public void ShouldSyncKmlToKmlWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceKamlAdapter = builder.createKMLAdapter(TestHelper.fileName("kmlSyncTestsGround.kml"));
		ISyncAdapter targetKamlAdapter = builder.createKMLAdapter(TestHelper.fileName("kmlSyncTestsPlacemark.kml"));
		
		SyncEngine syncEngine = new SyncEngine(sourceKamlAdapter,targetKamlAdapter);
		List<Item> listOfConflicts = syncEngine.synchronize();
			
		Assert.assertEquals(0, listOfConflicts.size());
		
	}
	
	@Test
	public void ShouldSyncGoogleSpreadSheetToExcelWithoutRDFAssumeSameSchema(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);
		
		
		File contentFile = new File(this.getClass().getResource("content1.xls").getFile());
		ISyncAdapter sourceAsGoogleSpreadSheet = builder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(contentFile.getAbsolutePath(), "user", "id");
		
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
				"user_source",
				"user"
				);
		
		GoogleSpreadSheetInfo spreadSheetInfoTarget = new GoogleSpreadSheetInfo(
				"peo4fu7AitTqkOhMSrecFRA",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
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
		File sourceFile = new File(this.getClass().getResource("content1.xls").getFile());
		File targetFile = new File(this.getClass().getResource("content2.xls").getFile());
		
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(sourceFile.getAbsolutePath(), "user", "id");
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(targetFile.getAbsolutePath(), "user", "id");
		
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
		tableName = "user";

		ISyncAdapter targetAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdbtarget",tableName);
		
		SyncEngine engine = new SyncEngine(sourceAsMySql,targetAsMySql);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	@Test
	public void ShouldSyncMySQLToCloud(){	
			
		String user = "root";
		String password = "test1234";
		String tableName = "user";

		String meshName = "Mysql";
		String feedName = "user";

		ISyncAdapterBuilder builder = new SyncAdapterBuilder(
				new PropertiesProvider());
		ISyncAdapter sourceAsMySql = builder.createMySQLAdapter(user, password,
				"localhost", 3306, "mesh4xdb", tableName);

		SplitAdapter sourceAdapter = (SplitAdapter) sourceAsMySql;

		ISyncAdapter targetAdapter = builder.createHttpSyncAdapter(meshName, feedName);

		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);

		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(sourceAdapter.getAll().size(), targetAdapter
				.getAll().size());
	}	
}
