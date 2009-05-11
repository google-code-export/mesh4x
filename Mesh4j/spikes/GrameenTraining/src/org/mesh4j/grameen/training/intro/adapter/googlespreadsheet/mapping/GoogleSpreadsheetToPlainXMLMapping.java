package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping;

import java.util.Iterator;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;
/**
 * 
 * @author Raju
 * @version 1.0,30/3/2009
 * @see IGoogleSpreadsheetToXMLMapping 
 */
public class GoogleSpreadsheetToPlainXMLMapping implements IGoogleSpreadsheetToXMLMapping{

	private String idColumnName = "";
	private String lastUpdateColumnName = "";
	private String type = "";
	private int lastUpdateColumnPosition = -1;
	private int idColumnPosition = 0;
	
		
	public GoogleSpreadsheetToPlainXMLMapping(String type, String idColumnName,
			int idColumnPosition, int lastUpdateColumnPosition) {
		Guard.argumentNotNullOrEmptyString(type, "type");
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNull(idColumnPosition, "idColumnPosition");
		Guard.argumentNotNull(lastUpdateColumnPosition,
				"lastUpdateColumnPosition");

		this.type = type;
		this.idColumnName = idColumnName;
		this.idColumnPosition = idColumnPosition;
		this.lastUpdateColumnPosition = lastUpdateColumnPosition;
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

//	@Override
//	public GSRow<GSCell> convertXMLElementToRow(GSWorksheet<GSRow<GSCell>> workSheet,Element element) {
//		Guard.argumentNotNull(workSheet, "workSheet");
//		Guard.argumentNotNull(element, "element");
//		
//		LinkedHashMap<String,String> listMap = new LinkedHashMap<String, String>();
//		GSRow<GSCell> gsRow = null ;
//		
//		for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();){
//			Element child = (Element) iterator.next();
//			listMap.put(child.getName(), child.getText());
//		}
//		
//		try {
//			gsRow = workSheet.createNewRow(listMap);
//		} catch (IOException e) {
//			throw new MeshException(e);
//		} catch (ServiceException e) {
//			throw new MeshException(e);
//		}
//		return gsRow;
//	}

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

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public IRDFSchema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
