package org.mesh4j.sync.mappings;


public enum SyncMode {
	
	SendAndReceiveChanges(true, true, "Send and Receive Changes"),
	SendChangesOnly(true, false, "Send Changes Only"),
	ReceiveChangesOnly(false, true, "Receive Changes Only");

	// MODEL VARIABLES
	private boolean shouldSendChanges;
	private boolean shouldReceiveChanges;
	private String alias;
	
	// BUSINESS METHODS 
	private SyncMode(boolean shouldSendChanges, boolean shouldReceiveChanges, String alias){
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.alias = alias;
	}
	
	public boolean shouldSendChanges() {
		return this.shouldSendChanges;
	}

	public boolean shouldReceiveChanges() {
		return this.shouldReceiveChanges;
	}
	
	@Override
	public String toString(){
		return this.alias;
	}

}
