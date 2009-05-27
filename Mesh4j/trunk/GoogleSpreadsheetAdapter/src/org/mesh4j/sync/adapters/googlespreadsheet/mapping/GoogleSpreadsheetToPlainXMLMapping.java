package org.mesh4j.sync.adapters.googlespreadsheet.mapping;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

import com.google.gdata.client.docs.DocsService;
/**
 * 
 * @author Raju
 * @version 1.0,30/3/2009
 * @see IGoogleSpreadsheetToXMLMapping 
 */
public class GoogleSpreadsheetToPlainXMLMapping implements IGoogleSpreadsheetToXMLMapping{

	//MODEL VARIABLES
	private String idColumnName = "";
	private String lastUpdateColumnName = "";
	private String type = "";
	private String sheetName = "";
	private DocsService docService;

	
	//BUSINESS METHDOS
	public GoogleSpreadsheetToPlainXMLMapping(String type, String idColumnName,String lastUpdateColumnName, String sheetName, DocsService docService){
		Guard.argumentNotNullOrEmptyString(type, "type");
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		if(lastUpdateColumnName != null){
			Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		}
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNull(docService,	"docService");
		
		this.type = type;
		this.idColumnName = idColumnName;
		this.sheetName = sheetName;
		this.docService = docService;
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
	public String getIdColumnName() {
		return idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return lastUpdateColumnName;
	}

	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public String getSheetName() {
		return this.sheetName;
	}
	@Override
	public IRDFSchema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String createDataSource(String fileName) throws Exception {
		//create a msexcel document
		HSSFWorkbook workbook = new HSSFWorkbook();			
		MsExcelUtils.flush(workbook, fileName);
		
		//upload the excel document
		return GoogleSpreadsheetUtils.uploadSpreadsheetDoc(new File(fileName), this.docService);
	}
	
}
