package org.mesh4j.meshes.ui.wizard;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

@SuppressWarnings("serial")
public abstract class BaseWizardPanel extends JPanel {
	
	private CreateMeshWizardView parentView;
	private CreateMeshWizardController controller;
	
	public void setParentView(CreateMeshWizardView parentView) {
		this.parentView = parentView;
	}
	
	protected CreateMeshWizardView getParentView() {
		return parentView;
	}
	
	public void setController(CreateMeshWizardController controller) {
		this.controller = controller;
	}
	
	protected CreateMeshWizardController getController() {
		return controller;
	}

	public abstract void modelPropertyChange(PropertyChangeEvent evt);

}
