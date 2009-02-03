package org.mesh4j.sync.mappings;

public class EndpointMapping {

	// MODEL VARIABLES
	private String alias;
	private String endpoint;
	
	// BUSINESS METHODS
	public EndpointMapping(String alias, String endpoint) {
		super();
		this.alias = alias;
		this.endpoint = endpoint;
	}

	public String getAlias() {
		return alias;
	}

	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public String toString(){
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;		
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;		
	}
}
