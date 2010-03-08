package org.mesh4j.sync.adapters.feed.pfif.schema;

public enum PFIF_ENTITY {
	
	PERSON("person"),
	NOTE("note");
	
	
	private final String name;
	
	PFIF_ENTITY(String name){
		this.name = name;
	}
	 
	 public String toString(){
		 return this.name;
	 }
}
