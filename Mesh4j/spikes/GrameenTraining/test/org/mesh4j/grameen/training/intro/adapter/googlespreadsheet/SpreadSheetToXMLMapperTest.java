package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Map;

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
		
		String rawDataAsXML = "<user><name>Raju</name><age>18</age><city>Dhaka</city><country>Bangladesh</country></user>";
//		String user_Marcelo = "<user><name>Marcelo</name><age>19</age><city>Bandaras</city><country>Arjentian</country></user>";
//		String user_Sharif = "<user><name>Sharif</name><age>18</age><city>Dhaka</city><country>Bangladesh</country></user>";
		
		SpreadSheetToXMLMapper mapper = new SpreadSheetToXMLMapper("id","lastupdateColumnName");
		
		//put your user name and password
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw","saiful.raju@gmail.com","");
		for(Map.Entry<String, GSWorksheet<GSRow<GSCell>>> spSheet : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			String key = spSheet.getKey();
			GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
			for(Map.Entry<String, GSRow<GSCell>> gsRowMap :workSheet.getGSRows().entrySet()){
				GSRow row = gsRowMap.getValue();
//				System.out.println("id "+row.getId());
//				System.out.println("index "+ row.getRowIndex());
				Element xmlElement = mapper.convertRowToXML(row, workSheet);
				Assert.assertEquals(xmlElement.asXML(), rawDataAsXML);
				System.out.println(xmlElement.asXML());
				break;
			}
		}
		
	}
	
	//@Test
	public void ShouldConvertXMLToRow(){
		String rawDataAsXML = "<user><name>Raju</name><age>18</age><city>Dhaka</city><country>Bangladesh</country></user>";
		Element payLoad = XMLHelper.parseElement(rawDataAsXML);
		
		//put your user name and password
		SpreadSheetToXMLMapper mapper = new SpreadSheetToXMLMapper("id","lastupdateColumnName");
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw","saiful.raju@gmail.com","");
		for(Map.Entry<String, GSWorksheet<GSRow<GSCell>>> spSheetMpa : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()){
			GSWorksheet workSheet = spSheetMpa.getValue();
			GSRow row = mapper.convertXMLElementToRow(payLoad, 1);
			Element xmlElement = mapper.convertRowToXML(row, workSheet);
			Assert.assertEquals(xmlElement.asXML(), rawDataAsXML);
			break;
		}
	}
	
	
}
