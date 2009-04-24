package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IMsExcelToXMLMapping {

	String getIdColumnName();

	String getLastUpdateColumnName();

	void appliesXMLToRow(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, Element payload);
	
	Element convertRowToXML(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row);
	
	ISchema getSchema();

	void createDataSource(String fileName) throws Exception;

}
