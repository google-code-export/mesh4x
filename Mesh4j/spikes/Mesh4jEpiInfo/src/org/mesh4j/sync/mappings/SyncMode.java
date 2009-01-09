package org.mesh4j.sync.mappings;

public enum SyncMode {
	
	SendAndReceiveChanges(true, true),
	SendChangesOnly(true, false),
	ReceiveChangesOnly(false, true);

	// MODEL VARIABLES
	private boolean shouldSendChanges;
	private boolean shouldReceiveChanges;
	
	// BUSINESS METHODS 
	private SyncMode(boolean shouldSendChanges, boolean shouldReceiveChanges){
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
	}
	
	public boolean shouldSendChanges() {
		return this.shouldSendChanges;
	}

	public boolean shouldReceiveChanges() {
		return this.shouldReceiveChanges;
	}

}
