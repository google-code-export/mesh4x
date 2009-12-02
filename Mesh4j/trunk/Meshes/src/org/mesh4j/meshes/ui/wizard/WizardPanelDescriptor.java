package org.mesh4j.meshes.ui.wizard;


public abstract class WizardPanelDescriptor {
	
	public abstract String getNextPanelDescriptor();
	
	public abstract String getBackPanelDescriptor();
	
	public abstract BaseWizardPanel getPanel();
	
	public abstract String getId();

}
