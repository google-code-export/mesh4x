package org.mesh4j.sync.mappings;

import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;


public enum SyncMode {
	
	SendAndReceiveChanges(true, true, EpiInfoCompactUITranslator.getLabelSendAndReceiveChanges()),
	SendChangesOnly(true, false, EpiInfoCompactUITranslator.getLabelSendChangesOnly()),
	ReceiveChangesOnly(false, true, EpiInfoCompactUITranslator.getLabelReceiveChangesOnly());

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
