package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public abstract class WizardPanelDescriptor {
	
	private CreateMeshWizardController controller;

	public abstract String getNextPanelDescriptor();
	
	public abstract String getBackPanelDescriptor();
	
	public abstract JPanel getPanel();
	
	public abstract String getId();
	
	public abstract boolean isFinish();
	
	public CreateMeshWizardController getController() {
		return controller;
	}
	
	public void setController(CreateMeshWizardController controller) {
		this.controller = controller;
	}

}
