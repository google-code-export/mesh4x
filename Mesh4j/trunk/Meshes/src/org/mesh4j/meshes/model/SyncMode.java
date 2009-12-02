package org.mesh4j.meshes.model;

public enum SyncMode {
	
	SEND_AND_RECEIVE("Send and receive changes to data"),
	SEND("Only send changes"),
	RECEIVE("Only get changes");
	
	private String displayName;
	
	private SyncMode(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return displayName;
	}

}
