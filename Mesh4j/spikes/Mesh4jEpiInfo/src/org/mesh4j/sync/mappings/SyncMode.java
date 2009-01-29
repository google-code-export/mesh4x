package org.mesh4j.sync.mappings;

import org.mesh4j.sync.PreviewBehavior;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;

public enum SyncMode {
	
	SendAndReceiveChanges(true, true, PreviewBehavior.None, MeshCompactUITranslator.getLabelSendAndReceiveChanges()),
	SendChangesOnly(true, false, PreviewBehavior.Right, MeshCompactUITranslator.getLabelSendChangesOnly()),
	ReceiveChangesOnly(false, true, PreviewBehavior.Left, MeshCompactUITranslator.getLabelReceiveChangesOnly());

	// MODEL VARIABLES
	private boolean shouldSendChanges;
	private boolean shouldReceiveChanges;
	private String alias;
	private PreviewBehavior behavior;
	
	// BUSINESS METHODS 
	private SyncMode(boolean shouldSendChanges, boolean shouldReceiveChanges, PreviewBehavior behavior, String alias){
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.alias = alias;
		this.behavior = behavior;
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
	
	public PreviewBehavior getBehavior(){
		return behavior;
	}
	
	@Override
	public String toString(){
		return this.alias;
	}
}
