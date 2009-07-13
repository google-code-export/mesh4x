package org.mesh4j.sync.adapters.csv;

import java.util.Date;
import java.util.Iterator;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.AbstractPlainXmlIdentifiableMapping;
import org.mesh4j.sync.utils.DateHelper;

public class CSVToPlainXMLMapping extends AbstractPlainXmlIdentifiableMapping implements ICSVToXMLMapping {

	// BUSINESS METHODS
	public CSVToPlainXMLMapping(String type, String idColumnName, String lastUpdateColumnName, String lastUpdateColumnDateTimeFormat) {
		super(type, idColumnName, lastUpdateColumnName, lastUpdateColumnDateTimeFormat);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void appliesXMLToRow(CSVFile csvFile, CSVRow row, Element payload){		
		Element child;
		for (Iterator<Element> iterator = payload.elementIterator(); iterator.hasNext();) {
			child = (Element) iterator.next();
			row.setCellValue(child.getName(), child.getText());			
		}
	}
	
	
	@Override
	public Element convertRowToXML(CSVFile csvFile, CSVRow row){
		Element payload = DocumentHelper.createElement(csvFile.getSheetName());
		
		String columnName;
		String columnValue;
		Element fieldElement;
		
		for (int i = 0; i < row.getCellCount(); i++) {
			
			columnName = row.getHeader(i);
			columnValue = row.getCellValue(i); 
			  
			fieldElement = payload.addElement(columnName);
			
			fieldElement.addText(columnValue);
		}
		return payload;
	}
	
	@Override
	public String getHeader(CSVFile csvFile) {
		return csvFile.getHeader().toString();
	}

	@Override
	public Date getLastUpdate(CSVFile cvsFile, CSVRow row) {
		String cellValue = row.getCellValue(this.getLastUpdateColumnName());
		if(cellValue != null){
			return DateHelper.parseDate(cellValue, this.getLastUpdateColumnDateTimeFormat());
		} else {
			return null;
		}
	}

	@Override
	public String getId(CSVFile csvFile, CSVRow row) {
		String cellValue = row.getCellValue(this.getIdColumnName());
		if(cellValue != null){
			return cellValue;
		} else {
			return null;
		}
	}

}
