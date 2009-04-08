package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.utils.XMLHelper;

public class GoogleSpreadSheetContentAdapterTest {
	private IGoogleSpreadSheet spreadsheet;
	private ISpreadSheetToXMLMapper mapper;
	private GSWorksheet workSheet;
	private String userName = "mesh4x@gmail.com";
	private String passWord = "g@l@xy24";
	
	@Before
	public void setUp(){
		String idColumName = "id";
		int lastUpdateColumnPosition = 6;
		int idColumnPosition = 1;
		spreadsheet = new GoogleSpreadsheet("pLUqch-enpf1-GcqnD6qjSA",userName,passWord);
		mapper = new SpreadSheetToXMLMapper(idColumName,idColumnPosition,lastUpdateColumnPosition);
		workSheet = spreadsheet.getGSWorksheet("user");
	}
	
	//TODO will test later.rignt now some operation is working
	//in Mesh4x GData wrapper layer
	//@Test
	public void ShouldDeleteAllFromPhysicalSpreedSheet(){
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		for(IContent content : adapter.getAll(new Date())){
			adapter.delete(content);
		}
		Assert.assertEquals(adapter.getAll(new Date()).size(), 0);
		adapter.beginSync();
		adapter.endSync();
	}
		
	@Test
	public void ShouldAddContent(){
		String id = "4";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		 
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		IContent contentFromSpreadSheet = adapter.get("4");
		
		Assert.assertEquals(contentFromSpreadSheet.getPayload().asXML(), rawDataAsXML);
	}
	
	@Test
	public void ShouldGetContent(){
		
		
		String id = "4";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		IContent loadedContent = adapter.get("4");
		
		Assert.assertEquals(loadedContent.getId(), "4");
		Assert.assertEquals(rawDataAsXML, loadedContent.getPayload().asXML());
	}
	
	
	@Test
	public void ShouldUpdateContent(){
		String id = "";
		String title = "";
		String description = "";
		String rawDataAsXML = "";
		IContent content1,content2,contentToBeUPdated = null;
		
		id = "1";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Sharif</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content1 = getContent(id, title, description, rawDataAsXML);
		
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content2 = getContent(id, title, description, rawDataAsXML);
		
		//we are changing the age value of the content then updates number 2 entity
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		contentToBeUPdated = getContent(id, title, description, rawDataAsXML);
		
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		adapter.save(content2);
		
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		Assert.assertEquals(adapter.get("2").getId(),"2");
		Assert.assertEquals(adapter.get("1").getId(),"1");
		
		String contentDataAsXMLBeforeUpdate = adapter.get("2").getPayload().asXML();
		//now updating the number 2 entity 
		adapter.save(contentToBeUPdated);
		
		//after update total entity size should not increase 
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		
		
		String contentDataAsXMLAfterUpdate = adapter.get("2").getPayload().asXML();
		
		Assert.assertNotSame(contentDataAsXMLAfterUpdate,contentDataAsXMLBeforeUpdate);
	}
	
	//TODO will test later.rignt now some operation is working
	//in Mesh4x GData wrapper layer
	//@Test
	public void ShouldDeleteContent(){
		
		
		String id = "";
		String title = "";
		String description = "";
		String rawDataAsXML = "";
		IContent content1,content2 = null;
		
		id = "4";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content1 = getContent(id, title, description, rawDataAsXML);
		
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";

		content2 = getContent(id, title, description, rawDataAsXML);
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		adapter.save(content2);
		
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		
		adapter.delete(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
	}
	
	
	@Test
	public void ShouldAddContentToSpreadSheetAfterFinishEndSync(){
		String id = "";
		String title = "";
		String description = "";
		String rawDataAsXML = "";
		IContent content1,content2 = null;
		
		id = "4";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content1 = getContent(id, title, description, rawDataAsXML);
		
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content2 = getContent(id, title, description, rawDataAsXML);
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		adapter.save(content2);
		
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		
		adapter.beginSync();
		adapter.endSync();
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
	}
	
	@Test
	public void ShouldUpdateContentToSpreadSheetAfterFinishEndSync(){
		String id = "";
		String title = "";
		String description = "";
		String rawDataAsXML = "";
		IContent content1,content2,contentToBeUPdated = null;
		
		id = "1";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Sharif</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content1 = getContent(id, title, description, rawDataAsXML);
		
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		content2 = getContent(id, title, description, rawDataAsXML);
		
		//we are changing the age value of the content then updates number 2 entity
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		contentToBeUPdated = getContent(id, title, description, rawDataAsXML);
		
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		adapter.save(content2);
		
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		Assert.assertEquals(adapter.get("2").getId(),"2");
		Assert.assertEquals(adapter.get("1").getId(),"1");
		
		String contentDataAsXMLBeforeUpdate = adapter.get("2").getPayload().asXML();
		//now updating the number 2 entity 
		adapter.save(contentToBeUPdated);
		
		//after update total entity size should not increase 
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		
		
		String contentDataAsXMLAfterUpdate = adapter.get("2").getPayload().asXML();
		
		Assert.assertNotSame(contentDataAsXMLAfterUpdate,contentDataAsXMLBeforeUpdate);
		
		adapter.beginSync();
		adapter.endSync();
	}
	
	@Test
	public void ShouldDeleteContentFromSpreadSheetAfterFinishEndSync(){
		
		String id = "";
		String title = "";
		String description = "";
		String rawDataAsXML = "";
		IContent content1,content2 = null;
		
		id = "4";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		content1 = getContent(id, title, description, rawDataAsXML);
		
		id = "2";
		title = "User Info";
		description = "user Information(id,name,age,city,country)";
		rawDataAsXML = "<user>" +
								"<id>2</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		content2 = getContent(id, title, description, rawDataAsXML);
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		adapter.save(content2);
		
		Assert.assertEquals(2, adapter.getAll(new Date()).size());
		
		adapter.delete(content1);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		adapter.beginSync();
		adapter.endSync();
		//after finish the end sync in google spreadsheet adapter there should be
		//only one content2
	}
	
	private IContent getContent(String id,String title,String description,String rawDataAsXML){
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		return content;
	}
	
}
