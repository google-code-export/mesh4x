package org.mesh4j.sync.mappings;

import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;

public enum SyncMode {
	
	SendAndReceiveChanges(true, true, MeshCompactUITranslator.getLabelSendAndReceiveChanges()),
	SendChangesOnly(true, false, MeshCompactUITranslator.getLabelSendChangesOnly()),
	ReceiveChangesOnly(false, true, MeshCompactUITranslator.getLabelReceiveChangesOnly());

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
	
	public static SyncMode getSyncMode(boolean shouldSendChanges, boolean shouldReceiveChanges) {
		if(shouldSendChanges && shouldReceiveChanges){
			return SendAndReceiveChanges;
		}else if(shouldSendChanges){
			return SendChangesOnly;
		} else {
			return ReceiveChangesOnly;
		}
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
