package org.mesh4j.sync.adapters.msexcel;

import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IMsExcelToXMLMapping extends IIdentifiableMapping {

	void appliesXMLToRow(Workbook wb, Sheet sheet, Row row, Element payload);
	
	Element convertRowToXML(Workbook wb, Sheet sheet, Row row);
	
	ISchema getSchema();

	String getId(Sheet sheet, Row row);

	Date getLastUpdate(Sheet sheet, Row row);

	void initializeHeaderRow(Workbook wb, Sheet sheet, Row row);

	Row getRow(Sheet sheet, String id);

	Sheet getSheet(Workbook workbook);

}
