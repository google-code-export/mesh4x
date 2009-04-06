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
	private String userName = "saiful.raju@gmail.com";
	private String passWord = "";
	
	@Before
	public void setUp(){
		String idColumName = "id";
		int lastUpdateColumnPosition = 6;
		int idColumnPosition = 1;
		spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw",userName,passWord);
		mapper = new SpreadSheetToXMLMapper(idColumName,idColumnPosition,lastUpdateColumnPosition);
		workSheet = spreadsheet.getGSWorksheet("user");
	}
	
	//@Test
	public void ShouldGetContent(){
		String rawDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Raju</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate/>" +
								"</user>";
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		IContent content = adapter.get("1");
		Assert.assertEquals(content.getId(), "1");
		Assert.assertEquals(rawDataAsXML, content.getPayload().asXML());
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
		
		Assert.assertEquals(3, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		
		Assert.assertEquals(4, adapter.getAll(new Date()).size());
		
		IContent contentFromSpreadSheet = adapter.get("4");
		
		Assert.assertEquals(contentFromSpreadSheet.getPayload().asXML(), rawDataAsXML);
	}
	
	//@Test
	public void ShouldUpdateContent(){
		String id = "4";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(4, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		
		Assert.assertEquals(4, adapter.getAll(new Date()).size());
	}
	
	//@Test
	public void ShouldDeleteContent(){
		String id = "4";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>4</id>" +
								"<name>Javed</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
		Assert.assertEquals(4, adapter.getAll(new Date()).size());
		
		adapter.delete(content);
		
		Assert.assertEquals(3, adapter.getAll(new Date()).size());
	}
}
