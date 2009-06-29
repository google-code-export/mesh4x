package org.mesh4j.sync.adapters.googlespreadsheet;

public enum SyncColumn {

	sync_id, entity_name, entity_id, entity_version, sync_data;
	
	/** 
	 * return the name without '_', which is the tag for google spreadsheet column  
	 */
	public String toString(){
		return this.name().replace("_", "");
	}
	
}
