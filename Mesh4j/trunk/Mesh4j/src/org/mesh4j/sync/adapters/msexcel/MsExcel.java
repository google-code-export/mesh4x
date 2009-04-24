package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcel implements IMsExcel {

	// MODEL VARIABLES
	protected String fileName;
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
	
	@Override
	public HSSFWorkbook getWorkbook() {
		return this.workbook;
	}

	@Override
	public void setDirty() {
		this.dirty = true;		
	}

	@Override
	public void flush() {
		if(this.dirty){
			MsExcelUtils.flush(this.workbook, this.fileName);
			this.dirty = false;
		}	
	}
}
