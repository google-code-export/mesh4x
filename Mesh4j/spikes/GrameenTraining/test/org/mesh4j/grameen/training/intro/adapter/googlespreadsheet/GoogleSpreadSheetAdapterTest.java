package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.SyncEngine;
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

import com.google.gdata.util.ServiceException;

public class GoogleSpreadSheetAdapterTest {
	private IGoogleSpreadSheet spreadsheet;
	private ISpreadSheetToXMLMapper mapper;

	String userName = "gspreadsheet.test@gmail.com";
	String passWord = "java123456";
	String GOOGLE_SPREADSHEET_FIELD = "peo4fu7AitTo8e3v0D8FCew";
	
	@Before
	public void setUp(){
		String idColumName = "id";
		int lastUpdateColumnPosition = 6;
		int idColumnPosition = 1;
		mapper = new SpreadSheetToXMLMapper(idColumName,idColumnPosition,lastUpdateColumnPosition);
		spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
	}
	
	//@Test
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
	public void ShouldSyncAfterAddedItemInEmptyWorkSheet() throws DocumentException{
		
		
		
		
		GSWorksheet workSheetSource = spreadsheet.getGSWorksheet(1);//user entity source worksheet
		GSWorksheet workSheetTarget = spreadsheet.getGSWorksheet(2);//user entity target worksheet
		
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
		
		GoogleSpreadsheetUtils.flush(spreadsheet.getService(), spreadsheet.getGSSpreadsheet());
		
		SplitAdapter splitAdapterSource = getAdapter(workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterSource.add(getItem1());
		splitAdapterSource.add(getItem2());
		
		SplitAdapter splitAdapterTarget = getAdapter(workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterTarget.add(getItem3());
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	//@Test
	public void ShouldSyncAfterDelete() throws DocumentException{
		
		GSWorksheet workSheetSource = spreadsheet.getGSWorksheet(1);//user entity source worksheet
		GSWorksheet workSheetTarget = spreadsheet.getGSWorksheet(2);//user entity target worksheet
		
		cleanUP(workSheetSource.getName());
		cleanUP(workSheetTarget.getName());
		
		SplitAdapter splitAdapterSource = getAdapter(workSheetSource, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterSource.add(getItem1());
		splitAdapterSource.add(getItem2());
		
		
		SplitAdapter splitAdapterTarget = getAdapter(workSheetTarget, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		splitAdapterTarget.add(getItem3());
		
		
		Assert.assertEquals(2,splitAdapterSource.getAll().size());
		splitAdapterSource.delete(getItem1().getSyncId());
		Assert.assertEquals(1,splitAdapterSource.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource,splitAdapterTarget);
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
	}
	
	private SplitAdapter getAdapter(GSWorksheet contentWorkSheet,IIdentityProvider identityProvider,IIdGenerator idGenerator){
		GoogleSpreadSheetContentAdapter contentRepo = new GoogleSpreadSheetContentAdapter(spreadsheet,contentWorkSheet,mapper,"user");
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
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
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
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
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
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
	private void cleanUP(String sheetName){
		GSWorksheet<GSRow<GSCell>> workSheet = null;
		
		workSheet = spreadsheet.getGSWorksheet(sheetName);
		//clear the content worksheet
		clearContentOfWorkSheet(workSheet);
		
		//delete the sync worksheet
		String syncWorkSheet = sheetName + "_sync";
		workSheet = spreadsheet.getGSWorksheet(syncWorkSheet);
		if(workSheet != null){
			deleteWorkSheet(workSheet);	
		}
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
