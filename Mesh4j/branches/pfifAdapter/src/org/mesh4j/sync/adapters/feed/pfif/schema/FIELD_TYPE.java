package org.mesh4j.sync.adapters.feed.pfif.schema;

public enum FIELD_TYPE {
	
	STRING("string"),
	DATE("date"),
	DATE_TIME("dateTime"),
	BOOLEAN("boolean"),
	INTEGER("integer");
	
	
	private final String name;
	
	FIELD_TYPE(String name){
		this.name = name;
	}
	 
	 public String toString(){
		 return this.name;
	 }

	
}
