package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Element;

public interface IMsExcelToXMLMapping {

	public String getIdColumnName();

	public String getLastUpdateColumnName();

	public void appliesXMLToRow(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, Element payload);
	
	public Element convertRowToXML(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row);

}
