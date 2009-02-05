package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcel {

	// MODEL VARIABLES
	private String fileName;
	private HSSFWorkbook workbook;
	private boolean dirty = false;
	
	// BUSINESS METHODS
	public MsExcel(String fileName) {
		super();
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		
		this.fileName = fileName;
		
		try{
			this.workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(fileName);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public HSSFWorkbook getWorkbook() {
		return this.workbook;
	}

	public HSSFSheet getSheet(String sheetName) {
		return this.workbook.getSheet(sheetName);
	}

	public void setDirty() {
		this.dirty = true;		
	}

	public void flush() {
		if(this.dirty){
			MsExcelUtils.flush(this.workbook, this.fileName);
			this.dirty = false;
		}	
	}
}
