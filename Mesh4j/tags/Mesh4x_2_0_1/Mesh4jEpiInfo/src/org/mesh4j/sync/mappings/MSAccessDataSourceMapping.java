package org.mesh4j.sync.mappings;

public class MSAccessDataSourceMapping extends DataSourceMapping{

	// MODEL VARIABLES
	private String mdbName;
	private String tableName;
	private String fileName;
	
	// BUSINESS METHODS
	public MSAccessDataSourceMapping(String alias, String mdbName, String tableName, String fileName) {
		super(alias);
		this.mdbName = mdbName;
		this.tableName = tableName;
		this.fileName = fileName;
	}

	public String getMDBName() {
		return mdbName;
	}

	public String getTableName() {
		return tableName;
	}	
	
	public String getFileName() {
		return this.fileName;
	}

	public void setMDBName(String mdbName) {
		this.mdbName = mdbName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
