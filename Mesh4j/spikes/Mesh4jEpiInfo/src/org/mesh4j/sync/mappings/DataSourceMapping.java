package org.mesh4j.sync.mappings;

public class DataSourceMapping {

	// MODEL VARIABLES
	private String alias;
	private String mdbName;
	private String tableName;
	
	// BUSINESS METHODS
	public DataSourceMapping(String alias, String mdbName, String tableName) {
		super();
		this.alias = alias;
		this.mdbName = mdbName;
		this.tableName = tableName;
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
}
