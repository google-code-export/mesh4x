package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface IMsExcel {

	public HSSFWorkbook getWorkbook();
	
	public void setDirty();
	
	public void flush();
}
