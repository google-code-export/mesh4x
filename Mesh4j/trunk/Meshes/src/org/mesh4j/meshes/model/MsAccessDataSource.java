package org.mesh4j.meshes.model;

import org.mesh4j.sync.ISyncAdapter;

public class MsAccessDataSource extends DataSource {
	
	public static final String FILE_NAME_PROPERTY = "access_filename";
	public static final String TABLE_NAME_PROPERTY = "access_tablename";
	
	private String fileName;
	private String tableName;
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		String oldFileName = this.fileName;
		this.fileName = fileName;
		firePropertyChange(FILE_NAME_PROPERTY, oldFileName, fileName);
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		String oldTableName = this.tableName;
		this.tableName = tableName;
		firePropertyChange(TABLE_NAME_PROPERTY, oldTableName, tableName);
	}
	
	@Override
	public ISyncAdapter createSyncAdapter(DataSet dataSet, String baseDirectory) {
		// TODO Auto-generated method stub
		return null;
	}

}
