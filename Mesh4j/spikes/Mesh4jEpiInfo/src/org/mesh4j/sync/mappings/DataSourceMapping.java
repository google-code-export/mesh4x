package org.mesh4j.sync.mappings;

public class DataSourceMapping {

	// MODEL VARIABLES
	private String sourceId;
	private String mdbName;
	private String tableName;
	
	// BUSINESS METHODS
	public DataSourceMapping(String sourceId, String mdbName, String tableName) {
		super();
		this.sourceId = sourceId;
		this.mdbName = mdbName;
		this.tableName = tableName;
	}

	public String getSourceId() {
		return sourceId;
	}

	public String getMDBName() {
		return mdbName;
	}

	public String getTableName() {
		return tableName;
	}	
	
	@Override
	public String toString(){
		return this.sourceId;
	}
}
