package org.mesh4j.sync.adapters.msexcel;

import org.apache.poi.ss.usermodel.Workbook;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcel implements IMsExcel {

	// MODEL VARIABLES
	private String fileName;
	private Workbook workbook;
	private boolean dirty = false;
	
	// BUSINESS METHODS
	public MsExcel(String fileName) {
		super();
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		
		this.fileName = fileName;
		
		this.getOrCreateWorkbookIfAbsent(fileName);
	}

	private void getOrCreateWorkbookIfAbsent(String fileName) {
		try{
			this.workbook = MsExcelUtils.getOrCreateWorkbookIfAbsent(fileName);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@Override
	public Workbook getWorkbook() {
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
			this.getOrCreateWorkbookIfAbsent(this.fileName);
		}	
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}
}
