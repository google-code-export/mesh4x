package org.mesh4j.meshes.model;

import org.mesh4j.sync.ISyncAdapter;

public class MsExcelDataSource extends DataSource {
	
	public static final String FILE_NAME_PROPERTY = "excel_filename";
	public static final String WORKSHEET_NAME_PROPERTY = "excel_worksheet";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "excel_uniquecolumn";
	
	private String fileName;
	private String worksheetName;
	private String uniqueColumnName;
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		String oldFileName = this.fileName;
		this.fileName = fileName;
		firePropertyChange(FILE_NAME_PROPERTY, oldFileName, fileName);
	}
	
	public String getWorksheetName() {
		return worksheetName;
	}
	
	public void setWorksheetName(String worksheetName) {
		String oldWorksheetName = this.worksheetName;
		this.worksheetName = worksheetName;
		firePropertyChange(WORKSHEET_NAME_PROPERTY, oldWorksheetName, worksheetName);
	}
	
	public String getUniqueColumnName() {
		return uniqueColumnName;
	}
	
	public void setUniqueColumnName(String uniqueColumnName) {
		String oldUniqueColumnName = this.uniqueColumnName;
		this.uniqueColumnName = uniqueColumnName;
		firePropertyChange(UNIQUE_COLUMN_NAME_PROPERTY, oldUniqueColumnName, uniqueColumnName);
	}
	
	@Override
	public ISyncAdapter createSyncAdapter(String baseDirectory) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void accept(MeshVisitor visitor) {
		visitor.visit(this);
	}
	
}
