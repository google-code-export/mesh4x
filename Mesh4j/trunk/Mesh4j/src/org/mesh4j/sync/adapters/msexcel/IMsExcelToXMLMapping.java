package org.mesh4j.sync.adapters.msexcel;

import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IMsExcelToXMLMapping {

	String getIdColumnName();

	String getLastUpdateColumnName();

	void appliesXMLToRow(Workbook wb, Sheet sheet, Row row, Element payload);
	
	Element convertRowToXML(Workbook wb, Sheet sheet, Row row);
	
	ISchema getSchema();

	Workbook createDataSource(String fileName) throws Exception;

	String getIdColumnValue(Sheet sheet, Row row);

	Date getLastUpdateColumnValue(Sheet sheet, Row row);

}
