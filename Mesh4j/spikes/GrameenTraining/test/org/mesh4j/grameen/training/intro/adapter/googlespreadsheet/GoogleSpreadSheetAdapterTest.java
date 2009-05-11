package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.GoogleSpreadsheetToPlainXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;

public class GoogleSpreadSheetAdapterTest {
	private IGoogleSpreadSheet spreadsheet;
	private IGoogleSpreadsheetToXMLMapping mapper;

	String userName = "gspreadsheet.test@gmail.com";
	String passWord = "java123456";
	String GOOGLE_SPREADSHEET_FIELD = "peo4fu7AitTo8e3v0D8FCew";
	
	@Before
	public void setUp(){
		String idColumName = "id";
		mapper = new GoogleSpreadsheetToPlainXMLMapping("user",idColumName,null);
		spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
	}
	
	@Test
	public void ShouldSyncTwoWorkSheet(){
		GSWorksheet workSheetSource = spreadsheet.getGSWorksheet(1);//user entity source worksheet
		GSWorksheet workSheetTarget = spreadsheet.getGSWorksheet(2);//user entity target worksheet
		
		SplitAdapter splitAdapterSource = getAdapter(workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SplitAdapter splitAdapterTarget = getAdapter(workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
	}
	
	@Test
	public void ShouldSyncTwoWorkSheetOfTwoSpreadSheetOfTwoDiffUser(){
		String userName = "";
		String passWord = "";
		String spField = "";
		
		
		
		
		userName = "gspreadsheet.run@gmail.com";
		passWord = "java123456";
		spField = "pc5o5hLhHbIhQ9IEZKNLAJQ";
		IGoogleSpreadSheet sourceSpreadSheet = getGoogleSpreadSheet(spField, userName, passWord);
		GSWorksheet workSheetSource = sourceSpreadSheet.getGSWorksheet(1);
		
		userName = "gspreadsheet.test@gmail.com";
		passWord = "java123456";
		spField = "peo4fu7AitTo8e3v0D8FCew";
		IGoogleSpreadSheet targetSpreadSheet = getGoogleSpreadSheet(spField, userName, passWord);
		GSWorksheet workSheetTarget = targetSpreadSheet.getGSWorksheet(1);
		
		SplitAdapter splitAdapterSource = getAdapter(sourceSpreadSheet,workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SplitAdapter splitAdapterTarget = getAdapter(targetSpreadSheet,workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());

	}
	@Test
	public void ShouldSyncTwoWorkSheetOfTwoSpreadSheet(){
		String userName = "";
		String passWord = "";
		String spField = "";
		
		userName = "gspreadsheet.test@gmail.com";
		passWord = "java123456";
		spField = "peo4fu7AitTo8e3v0D8FCew";
		IGoogleSpreadSheet sourceSpreadSheet = getGoogleSpreadSheet(spField, userName, passWord);
		GSWorksheet workSheetSource = sourceSpreadSheet.getGSWorksheet(1);
		
		
		userName = "gspreadsheet.test@gmail.com";
		passWord = "java123456";
		spField = "peo4fu7AitTqkOhMSrecFRA";
		IGoogleSpreadSheet targetSpreadSheet = getGoogleSpreadSheet(spField, userName, passWord);
		GSWorksheet workSheetTarget = targetSpreadSheet.getGSWorksheet(1);
		
		SplitAdapter splitAdapterSource = getAdapter(sourceSpreadSheet,workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		SplitAdapter splitAdapterTarget = getAdapter(targetSpreadSheet,workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	private IGoogleSpreadSheet getGoogleSpreadSheet(String spField,String userName,String passWord){
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(spField,userName,passWord);
		return spreadsheet;
	}
	@Test
	public void ShouldSyncAfterAddedItemInEmptyWorkSheet() throws DocumentException{
		
		GSWorksheet workSheetSource = spreadsheet.getGSWorksheet(1);//user entity source worksheet
		GSWorksheet workSheetTarget = spreadsheet.getGSWorksheet(2);//user entity target worksheet
		
		cleanUp(workSheetSource, workSheetTarget);
		
		SplitAdapter splitAdapterSource = getAdapter(workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterSource.add(getItem1());
		splitAdapterSource.add(getItem2());
		
		SplitAdapter splitAdapterTarget = getAdapter(workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterTarget.add(getItem3());
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	
	
	@Test
	public void ShouldSyncAfterDelete() throws DocumentException{
		
		GSWorksheet workSheetSource = spreadsheet.getGSWorksheet(1);//user entity source worksheet
		GSWorksheet workSheetTarget = spreadsheet.getGSWorksheet(2);//user entity target worksheet
		
		cleanUp(workSheetSource, workSheetTarget);
		
		Item item1 = getItem1();
		Item item2 = getItem2();
		Item item3 = getItem3();
		
		SplitAdapter splitAdapterSource = getAdapter(workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterSource.add(item1);
		splitAdapterSource.add(item2);
		
		Assert.assertEquals(2,splitAdapterSource.getAll().size());
		
		SplitAdapter splitAdapterTarget = getAdapter(workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterTarget.add(item3);
		
		Assert.assertEquals(1,splitAdapterTarget.getAll().size());
		
		splitAdapterSource.delete(item1.getSyncId());
		
		Assert.assertEquals(2,splitAdapterSource.getAll().size());
		
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		
		
	}
	
	@Test
	public void ShouldDeleteItem() throws DocumentException{
		GSWorksheet workSheetSource = spreadsheet.getGSWorksheet(1);//user entity source worksheet
		GSWorksheet workSheetTarget = spreadsheet.getGSWorksheet(2);//user entity target worksheet
		
		cleanUp(workSheetSource, workSheetTarget);
		Item item1 = getItem1();
		Item item2 = getItem2();
		
		SplitAdapter splitAdapterSource = getAdapter(workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterSource.add(item1);
		
		Assert.assertEquals(1,splitAdapterSource.getAll().size());
		
		SplitAdapter splitAdapterTarget = getAdapter(workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterTarget.add(item2);
		
		Assert.assertEquals(1,splitAdapterTarget.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		Assert.assertEquals(2,splitAdapterSource.getAll().size());
		Assert.assertEquals(2,splitAdapterTarget.getAll().size());
		
		List<SyncInfo> listOfSyncInfo = splitAdapterSource.getSyncRepository().getAll("user");
		for(SyncInfo syncInfo : listOfSyncInfo){
			splitAdapterSource.delete(syncInfo.getSyncId());
		}
		
		Assert.assertEquals(2,splitAdapterSource.getAll().size());
		
		List<Item> items = splitAdapterSource.getAll();
		for (Item item : items) {
			Assert.assertTrue(item.isDeleted());
		}
		
	}
	
	private SplitAdapter getAdapter(IGoogleSpreadSheet spreadsheet,GSWorksheet contentWorkSheet,IIdentityProvider identityProvider,IIdGenerator idGenerator){
		GoogleSpreadSheetContentAdapter contentRepo = new GoogleSpreadSheetContentAdapter(spreadsheet,contentWorkSheet,mapper);
		String syncSheetName = contentWorkSheet.getName() + "_sync";
		GoogleSpreadSheetSyncRepository  syncRepo = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,syncSheetName);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo,contentRepo,identityProvider);
		return splitAdapter;
	}
	private SplitAdapter getAdapter(GSWorksheet contentWorkSheet,IIdentityProvider identityProvider,IIdGenerator idGenerator){
		GoogleSpreadSheetContentAdapter contentRepo = new GoogleSpreadSheetContentAdapter(spreadsheet,contentWorkSheet,mapper);
		String syncSheetName = contentWorkSheet.getName() + "_sync";
		GoogleSpreadSheetSyncRepository  syncRepo = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,syncSheetName);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo,contentRepo,identityProvider);
		return splitAdapter;
	}
	private IContent getContent(String id,String title,String description,String rawDataAsXML){
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		return content;
	}
	
	private Item getItem1() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" +
								"<id>"+id+"</id>" +
								"<name>Marcelo</name>" +
								"<age>25</age>" +
								"<city>Buens aires</city>" +
								"<country>Argentina</country>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
	private Item getItem2() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" +
								"<id>"+id+"</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}

	private Item getItem3() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" +
								"<id>"+id+"</id>" +
								"<name>Sharif</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
	
	
	private void cleanUp(GSWorksheet workSheetSource,GSWorksheet workSheetTarget){
		//before start the process deleting all the previous content
		clearContentOfWorkSheet(workSheetSource);
		clearContentOfWorkSheet(workSheetTarget);
		
		//delete the sync worksheet
		GSWorksheet workSheet = null;
		String syncWorkSheet = null;
		
		syncWorkSheet = workSheetSource.getName() + "_sync";
		workSheet = spreadsheet.getGSWorksheet(syncWorkSheet);
		
		if(workSheet != null){
			deleteWorkSheet(workSheet);	
		}
		
		syncWorkSheet = workSheetTarget.getName() + "_sync";
		workSheet = spreadsheet.getGSWorksheet(syncWorkSheet);
		
		if(workSheet != null){
			deleteWorkSheet(workSheet);	
		}
		
		GoogleSpreadsheetUtils.flush(spreadsheet.getSpreadsheetService(), spreadsheet.getGSSpreadsheet());
	}
	
	private void clearContentOfWorkSheet(GSWorksheet<GSRow<GSCell>> workSheet){
		for(Map.Entry<String, GSRow<GSCell>> mapRows : workSheet.getGSRows().entrySet()){
			//We should not delete the first header row
			if(Integer.parseInt(mapRows.getKey()) > 1){
				workSheet.deleteChildElement(mapRows.getKey());	
			}
		}
	}
	
	private void deleteWorkSheet(GSWorksheet<GSRow<GSCell>> workSheet){
		spreadsheet.getGSSpreadsheet().deleteChildElement(workSheet.getElementId());
	}
	
	
}
