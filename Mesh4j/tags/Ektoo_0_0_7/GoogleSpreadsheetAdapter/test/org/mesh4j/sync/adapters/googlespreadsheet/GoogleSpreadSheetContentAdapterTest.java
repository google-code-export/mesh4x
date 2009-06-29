package org.mesh4j.sync.adapters.googlespreadsheet;

import java.util.Date;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToPlainXMLMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.utils.XMLHelper;
/**
 * 
 * @author Raju
 */

public class GoogleSpreadSheetContentAdapterTest {

	// MODEL VARIABLES
	private IGoogleSpreadSheet spreadsheet;
	private IGoogleSpreadsheetToXMLMapping mapper;
	private String userName = "gspreadsheet.test@gmail.com";
	private String passWord = "java123456";
	private String GOOGLE_SPREADSHEET_NAME  = "testspreadsheet";


	// TEST METHODS
	@Before
	public void init(){
		loadSpreadSheet();
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,mapper);
		for(IContent content : adapter.getAll(new Date())){
			adapter.delete(content);	
		}
		adapter.beginSync();
		adapter.endSync();
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfSpreadSheetIsNull(){
		new GoogleSpreadSheetContentAdapter(null,mapper);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfMapperIsNull(){
		new GoogleSpreadSheetContentAdapter(spreadsheet,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfAddedContentIsNull(){
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,mapper);
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		adapter.save(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfContentIdIsNullOrEmpty(){
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,mapper);
		adapter.get("");
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
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		 
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		IContent contentFromSpreadSheet = adapter.get("4");
		
		Assert.assertEquals( rawDataAsXML,contentFromSpreadSheet.getPayload().asXML());
		
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
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
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
								"</user>";
		
		contentToBeUPdated = getContent(id, title, description, rawDataAsXML);
		
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
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
	
	
	@Test
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
								"</user>";

		content2 = getContent(id, title, description, rawDataAsXML);
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
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
								"</user>";
		
		content2 = getContent(id, title, description, rawDataAsXML);
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
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
								"</user>";
		
		contentToBeUPdated = getContent(id, title, description, rawDataAsXML);
		
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
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
								"</user>";
		content2 = getContent(id, title, description, rawDataAsXML);
		
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet, mapper);
		
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
	
	private void loadSpreadSheet(){
		spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_NAME,userName, passWord);
		//GSWorksheet workSheet = spreadsheet.getGSWorksheet(1);
		//workSheet.getName(), 
		mapper = new GoogleSpreadsheetToPlainXMLMapping("user", "id", null, spreadsheet.getDocsService());
	}		
	
}
