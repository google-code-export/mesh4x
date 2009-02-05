package org.mesh4j.sync.mappings;

public class DataSourceMapping {

	// MODEL VARIABLES
	private String alias;
	
	// BUSINESS METHODS
	public DataSourceMapping(String alias) {
		super();
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public String toString(){
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
