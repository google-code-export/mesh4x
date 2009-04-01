package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Iterator;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.validations.Guard;

import com.google.gdata.data.spreadsheet.ListEntry;
/**
 * 
 * @author Raju
 * @version 1.0,30/3/2009
 * @see ISpreadSheetToXMLMapper 
 */
public class SpreadSheetToXMLMapper implements ISpreadSheetToXMLMapper{

	private String idColumnName = "";
	private String lastUpdateColumnName = "";
	
	public SpreadSheetToXMLMapper(String idColumnName,String lastUpdateColumnName){
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
	}
	
	@Override
	public Element convertRowToXML(GSRow gsRow, GSWorksheet worksheet) {
		
		Element rootElement = DocumentHelper.createElement(worksheet.getName());
		
		ListEntry rowEntry = gsRow.getRowEntry();
		Element childElement ; 
		
		 for (String columnHeader : rowEntry.getCustomElements().getTags()){
			 String columnContent = rowEntry.getCustomElements().getValue(columnHeader);
			 childElement = rootElement.addElement(columnHeader);
			 //TODO need to think about it how we can handle the null value
			 //if a row contains null value in some of its column then how should handle it
			 if(columnContent == null || columnContent.equals("")){
				 childElement.setText("");	 
			 }else{
				 childElement.setText(columnContent);
			 }
		 }
		return rootElement;
	}

	@Override
	public GSRow convertXMLElementToRow(Element element, int rowIndex) {
		
		Element child;
		ListEntry newRowEntry = new ListEntry();
		
		for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();){
			child = (Element) iterator.next();
			newRowEntry.getCustomElements().setValueLocal(child.getName(), child.getText());
		}
		GSRow gsRow = new GSRow(newRowEntry,rowIndex);
		return gsRow;
	}

	@Override
	public String getIdColumnName() {
		return idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return lastUpdateColumnName;
	}

	
	
}
