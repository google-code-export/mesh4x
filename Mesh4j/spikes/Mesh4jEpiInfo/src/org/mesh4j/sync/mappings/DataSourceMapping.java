package org.mesh4j.sync.mappings;

import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;

public class DataSourceMapping {


	// MODEL VARIABLES
	private String alias;
	private String mdbName;
	private String tableName;
	private String fileName;
	
	// BUSINESS METHODS
	public DataSourceMapping(String alias, String mdbName, String tableName, String fileName) {
		super();
		this.alias = alias;
		this.mdbName = mdbName;
		this.tableName = tableName;
		this.fileName = fileName;
	}

	public String getAlias() {
		return alias;
	}

	public String getMDBName() {
		return mdbName;
	}

	public String getTableName() {
		return tableName;
	}	
	
	@Override
	public String toString(){
		return this.alias;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setMDBName(String mdbName) {
		this.mdbName = mdbName;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSourceId() {
		return MsAccessSyncAdapterFactory.createSourceId(this.alias);
	}
}
