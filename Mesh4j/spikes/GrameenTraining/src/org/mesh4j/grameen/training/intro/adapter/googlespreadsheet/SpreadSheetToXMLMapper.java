package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;
/**
 * 
 * @author Raju
 * @version 1.0,30/3/2009
 * @see ISpreadSheetToXMLMapper 
 */
public class SpreadSheetToXMLMapper implements ISpreadSheetToXMLMapper{

	private String idColumnName = "";
	private String lastUpdateColumnName = "";
	private int lastUpdateColumnPosition = -1;
	private int idColumnPosition = 0;
	
	@Deprecated
	public SpreadSheetToXMLMapper(String idColumnName,String lastUpdateColumnName){
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
	}
	
	@Deprecated
	public SpreadSheetToXMLMapper(String idColumnName,int lastUpdateColumnPosition){
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNull(lastUpdateColumnPosition, "lastUpdateColumnPosition");
		this.idColumnName = idColumnName;
		this.lastUpdateColumnPosition = lastUpdateColumnPosition;
	}
	public SpreadSheetToXMLMapper(String idColumnName,int idColumnPosition,int lastUpdateColumnPosition){
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNull(idColumnPosition, "idColumnPosition");
		Guard.argumentNotNull(lastUpdateColumnPosition, "lastUpdateColumnPosition");
		this.idColumnName = idColumnName;
		this.idColumnPosition = idColumnPosition;
		this.lastUpdateColumnPosition = lastUpdateColumnPosition;
	}
	public SpreadSheetToXMLMapper(){
	}
	
	@Override
	public Element convertRowToXML(GSRow<GSCell> gsRow, GSWorksheet worksheet) {
		Guard.argumentNotNull(gsRow, "gsRow");
		Guard.argumentNotNull(worksheet, "worksheet");
		
		Element rootElement = DocumentHelper.createElement(worksheet.getName());
		Element childElement ; 
		
		for(Map.Entry<String, GSCell> rowMap :gsRow.getChildElements().entrySet()){
			String columnHeader = rowMap.getKey();
			String columnContent = rowMap.getValue().getCellValue();
			childElement = rootElement.addElement(columnHeader);
			 //TODO need to think about it how we can handle the null value
			 //if a row contains null value in some of its column then how should handle it
			 if(columnContent == null || columnContent.equals("")){
				 childElement.setText("");	 
			 }else{
				 childElement.setText(columnContent);
			 }
		}
		
//		 for (String columnHeader : rowEntry.getCustomElements().getTags()){
//			 String columnContent = rowEntry.getCustomElements().getValue(columnHeader);
//			 childElement = rootElement.addElement(columnHeader);
//			 //TODO need to think about it how we can handle the null value
//			 //if a row contains null value in some of its column then how should handle it
//			 if(columnContent == null || columnContent.equals("")){
//				 childElement.setText("");	 
//			 }else{
//				 childElement.setText(columnContent);
//			 }
//		 }
		return rootElement;
	}

	@Override
	public GSRow<GSCell> convertXMLElementToRow(GSWorksheet<GSRow<GSCell>> workSheet,Element element) {
		Guard.argumentNotNull(workSheet, "workSheet");
		Guard.argumentNotNull(element, "element");
		
		LinkedHashMap<String,String> listMap = new LinkedHashMap<String, String>();
		GSRow<GSCell> gsRow = null ;
		
		for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();){
			Element child = (Element) iterator.next();
			listMap.put(child.getName(), child.getText());
		}
	
		
		try {
			gsRow = workSheet.createNewRow(listMap);
		} catch (IOException e) {
			throw new MeshException(e);
		} catch (ServiceException e) {
			throw new MeshException(e);
		}
		return gsRow;
	}

	@Override
	public GSRow<GSCell> normalizeRow(GSWorksheet<GSRow<GSCell>> workSheet,
									  Element payLoad, GSRow<GSCell> rowTobeUPdated) {
		Guard.argumentNotNull(workSheet, "workSheet");
		Guard.argumentNotNull(payLoad, "payLoad");
		Guard.argumentNotNull(rowTobeUPdated, "rowTobeUPdated");
		
		for (Iterator<Element> iterator = payLoad.elementIterator(); iterator.hasNext();){
			Element	child = (Element) iterator.next();
			rowTobeUPdated.updateCellValue( child.getText() ,child.getName());
		}
		return rowTobeUPdated;
	}
	
	
	
	@Override
	public String getIdColumnName() {
		return idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return lastUpdateColumnName;
	}

	@Override
	public int getLastUpdateColumnPosition() {
		return this.lastUpdateColumnPosition;
	}

	@Override
	public int getIdColumnPosition() {
		return this.idColumnPosition;
	}

	

	
	
}
