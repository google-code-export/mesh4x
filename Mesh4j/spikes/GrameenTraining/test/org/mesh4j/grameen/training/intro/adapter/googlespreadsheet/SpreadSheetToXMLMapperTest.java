package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.utils.XMLHelper;

public class SpreadSheetToXMLMapperTest {

	@Test
	public void ShouldConvertRowToXMLPayload(){
		
		String rawDataAsXML = "<user><id>1</id><name>Raju</name><age>18</age><city>Dhaka</city><country>Bangladesh</country><lastupdate>5/20/2009 1:01:01</lastupdate></user>";
		
		SpreadSheetToXMLMapper mapper = new SpreadSheetToXMLMapper("id",1,6);
		
		//put your user name and password
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw","saiful.raju@gmail.com","");
		for(Entry<String, GSWorksheet> spSheet : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			String key = spSheet.getKey();
			GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
			for(Map.Entry<String, GSRow<GSCell>> gsRowMap :workSheet.getGSRows().entrySet()){
				GSRow row = gsRowMap.getValue();
				if(Integer.parseInt(row.getElementId()) > 1){
					Element xmlElement = mapper.convertRowToXML(row, workSheet);
					System.out.println(xmlElement.asXML());
					Assert.assertEquals(xmlElement.asXML(), rawDataAsXML);
					break;
				}
			}
			break;
		}
		
	}
	
	//@Test
	public void ShouldConvertXMLToRow(){
		String rawDataAsXML = "<user><name>Raju</name><age>18</age><city>Dhaka</city><country>Bangladesh</country></user>";
		Element payLoad = XMLHelper.parseElement(rawDataAsXML);
		
		//put your user name and password
		SpreadSheetToXMLMapper mapper = new SpreadSheetToXMLMapper("id",1,6);
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw","saiful.raju@gmail.com","");
		for(Entry<String, GSWorksheet> spSheetMpa : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			GSWorksheet workSheet = spSheetMpa.getValue();
			GSRow row = mapper.convertXMLElementToRow(workSheet,payLoad);
			Element xmlElement = mapper.convertRowToXML(row, workSheet);
			Assert.assertEquals(xmlElement.asXML(), rawDataAsXML);
			break;
		}
	}
	
	
}
