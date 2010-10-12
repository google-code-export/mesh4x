package org.mesh4j.meshes.model;

public enum SchedulingOption {
	
	MANUALLY("On request only"),
	
	//AUTOMATIC("Do one automatic try when connecting"),
	
	FIVE_MINUTES("Every 5 minutes"),
	
	TEN_MINUTES("Every 10 minutes"),
	
	ONE_HOUR("Every 1 hour"),
	
	ONE_DAY("Every Day");
	
	private String displayName;
	
	private SchedulingOption(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
	
}
