package org.mesh4j.sync.adapters.googlespreadsheet.mapping;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.payload.schema.AbstractPlainXmlIdentifiableMapping;
import org.mesh4j.sync.validations.Guard;
/**
 * 
 * @author Raju
 * @version 1.0,30/3/2009
 * @see IGoogleSpreadsheetToXMLMapping 
 */
public class GoogleSpreadsheetToPlainXMLMapping extends AbstractPlainXmlIdentifiableMapping implements IGoogleSpreadsheetToXMLMapping{

	//MODEL VARIABLES
	
	//BUSINESS METHDOS
	public GoogleSpreadsheetToPlainXMLMapping(String type, String idColumnName, String lastUpdateColumnName){
		super(type, idColumnName, lastUpdateColumnName, GSCell.G_SPREADSHEET_DATE_FORMAT);
	}
	
	@Override
	public Element convertRowToXML(GSRow<GSCell> gsRow) {
		Guard.argumentNotNull(gsRow, "gsRow");
		
		Element rootElement = DocumentHelper.createElement(this.getType());
		Element childElement ; 
		
		for(Map.Entry<String, GSCell> rowMap :gsRow.getChildElements().entrySet()){
			String columnHeader = rowMap.getKey();
			String columnContent = rowMap.getValue().getCellValue();
			childElement = rootElement.addElement(columnHeader);
			 //TODO need to think about it how we can handle the null value
			 //if a row contains null value in some of its column, then how should handle it??
			 if(columnContent == null || columnContent.equals("")){
				 childElement.setText("");	 
			 }else{
				 childElement.setText(columnContent);
			 }
		}
		return rootElement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void applyXMLElementToRow(GSWorksheet<GSRow<GSCell>> workSheet,
									 GSRow<GSCell> rowTobeUPdated, Element payLoad) {
		Guard.argumentNotNull(workSheet, "workSheet");
		Guard.argumentNotNull(payLoad, "payLoad");
		Guard.argumentNotNull(rowTobeUPdated, "rowTobeUPdated");
		
		for (Iterator<Element> iterator = payLoad.elementIterator(); iterator.hasNext();){
			Element	child = (Element) iterator.next();
			rowTobeUPdated.updateCellValue( child.getText(), child.getName());
		}

	}
		
	@Override
	public String getId(GSRow<GSCell> gsRow) {
		 GSCell cell = gsRow.getGSCell(this.getIdColumnName());
		 if(cell == null){
			 return null;
		 } else {
			 return cell.getCellValue();
		 }
	}


	@Override
	public Date getLastUpdate(GSRow<GSCell> row) {
		GSCell cell = row.getGSCell(this.getLastUpdateColumnName());
		if(cell == null){
			return null;
		} else {
			String dateTimeAsString = cell.getCellValue();
			Date lasUpdateDateTime = GoogleSpreadsheetUtils.normalizeDate(dateTimeAsString, GSCell.G_SPREADSHEET_DATE_FORMAT);
			return lasUpdateDateTime;
		}
	}


	@Override
	public GSRow<GSCell> getRow(GSWorksheet<GSRow<GSCell>> workSheet, String id) {
		return GoogleSpreadsheetUtils.getRow(workSheet, new String[]{this.getIdColumnName()}, new String[]{id});
	}
	
}
