package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.GoogleSpreadsheetToPlainXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.utils.XMLHelper;
/**
 * 
 * @author Raju
 */
public class SpreadSheetToXMLMapperTest {

	private IGoogleSpreadSheet spreadsheet;
	private IGoogleSpreadsheetToXMLMapping mapper;
	private GSWorksheet workSheet;
	String userName = "gspreadsheet.test@gmail.com";
	String passWord = "java123456";
	String GOOGLE_SPREADSHEET_FIELD = "peo4fu7AitTo8e3v0D8FCew";

	
	@Before
	public void setUp(){
		String idColumName = "id";
		int lastUpdateColumnPosition = 6;
		int idColumnPosition = 1;
		mapper = new GoogleSpreadsheetToPlainXMLMapping("user",idColumName,idColumnPosition,lastUpdateColumnPosition);
	}
	
	@Test
	public void ShouldConvertRowToXMLPayload(){
		
		emptySpreadSheet();
		
		String id = "1";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Raju</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper);
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		 
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		IContent contentFromSpreadSheet = adapter.get("1");
		
		Assert.assertEquals(contentFromSpreadSheet.getPayload().asXML(), rawDataAsXML);
		
		
		for(Entry<String, GSWorksheet> spSheet : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			
			GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
			for(Map.Entry<String, GSRow<GSCell>> gsRowMap :workSheet.getGSRows().entrySet()){
				GSRow row = gsRowMap.getValue();
				if(Integer.parseInt(row.getElementId()) > 1){
					Element xmlElement = mapper.convertRowToXML(row, workSheet);
					System.out.println(xmlElement.asXML());
					Assert.assertEquals(xmlElement.asXML(), rawDataAsXML);
				}
			}
			break;
		}
		
	}
	
	@Test
	public void ShouldConvertXMLToRow(){
		
		emptySpreadSheet();
		
		String id = "1";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Raju</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper);
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		IContent contentFromSpreadSheet = adapter.get("1");
		
		
		for(Entry<String, GSWorksheet> spSheet : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			
			GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
			for(Map.Entry<String, GSRow<GSCell>> gsRowMap :workSheet.getGSRows().entrySet()){
				GSRow<GSCell> rowFromSpreaSheet = gsRowMap.getValue();
				//ignoring the first row,as first row is row header
				if(Integer.parseInt(rowFromSpreaSheet.getElementId()) > 1){
					
					//GSRow<GSCell> rowFromSpreaSheet = workSheet.createAndAddNewRow(workSheet.getChildElements().size() +1);
					
					mapper.applyXMLElementToRow(workSheet, rowFromSpreaSheet, contentFromSpreadSheet.getPayload());
					Assert.assertEquals(rowFromSpreaSheet.getGSCell("id").getCellValue(),"1");
					Assert.assertEquals(rowFromSpreaSheet.getGSCell("name").getCellValue(),"Raju");
					Assert.assertEquals(rowFromSpreaSheet.getGSCell("age").getCellValue(),"18");
					Assert.assertEquals(rowFromSpreaSheet.getGSCell("city").getCellValue(),"Dhaka");
					Assert.assertEquals(rowFromSpreaSheet.getGSCell("country").getCellValue(),"Bangladesh");
					Assert.assertEquals(rowFromSpreaSheet.getGSCell("lastupdate").getCellValue(),"6/11/2009 1:01:01");
				}
			}
			break;
		}
	}
	
	@Test
	public void ShouldNormalizeRow(){
		
		emptySpreadSheet();
		
		String id = "1";
		String title = "User Info";
		String description = "user Information(id,name,age,city,country)";
		String rawDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Raju</name>" +
								"<age>18</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		//we are planning to update only age column
		String rawUpdatedDataAsXML = "<user>" +
								"<id>1</id>" +
								"<name>Raju</name>" +
								"<age>25</age>" +
								"<city>Dhaka</city>" +
								"<country>Bangladesh</country>" +
								"<lastupdate>6/11/2009 1:01:01</lastupdate>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		Element payLoadToBeUpdated = XMLHelper.parseElement(rawUpdatedDataAsXML);
		
		IContent content = new XMLContent(id,title,description,payload);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper);
		
		Assert.assertEquals(0, adapter.getAll(new Date()).size());
		
		adapter.save(content);
		
		Assert.assertEquals(1, adapter.getAll(new Date()).size());
		
		for(Entry<String, GSWorksheet> spSheet : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
			for(Map.Entry<String, GSRow<GSCell>> gsRowMap :workSheet.getGSRows().entrySet()){
				GSRow<GSCell> rowTobeUPdated = gsRowMap.getValue();
				//ignoring the first row,as first row is row header
				if(Integer.parseInt(rowTobeUPdated.getElementId()) > 1){
					mapper.applyXMLElementToRow(workSheet, rowTobeUPdated, payLoadToBeUpdated);
					
					Assert.assertEquals(rowTobeUPdated.getGSCell("id").getCellValue(),"1");
					Assert.assertEquals(rowTobeUPdated.getGSCell("name").getCellValue(),"Raju");
					Assert.assertEquals(rowTobeUPdated.getGSCell("age").getCellValue(),"25");
					Assert.assertEquals(rowTobeUPdated.getGSCell("city").getCellValue(),"Dhaka");
					Assert.assertEquals(rowTobeUPdated.getGSCell("country").getCellValue(),"Bangladesh");
					Assert.assertEquals(rowTobeUPdated.getGSCell("lastupdate").getCellValue(),"6/11/2009 1:01:01");
				}
			}
			break;
		}
	}
	
	private void emptySpreadSheet(){
		loadSpreadSheet();
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper);
		for(IContent content : adapter.getAll(new Date())){
			adapter.delete(content);	
		}
		adapter.beginSync();
		adapter.endSync();
	}
	
	private void loadSpreadSheet(){
		spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		workSheet = spreadsheet.getGSWorksheet("user");
	}	
	
}
