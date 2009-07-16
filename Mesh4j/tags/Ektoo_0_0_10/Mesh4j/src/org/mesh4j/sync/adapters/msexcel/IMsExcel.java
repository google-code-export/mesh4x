package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface IMsExcel {

	public Workbook getWorkbook();
	
	public void setDirty();
	
	public void flush();

	public String getFileName();

	public void reload();

	public boolean fileExists();

	public Sheet getSheet(String sheetName);
}
