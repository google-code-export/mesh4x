package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepOneDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_ONE";
	
	private JPanel panel;
	
	public StepOneDescriptor() {
		this.panel = new CreateMeshStepOneView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return null;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getNextPanelDescriptor() {
		return StepTwoDescriptor.ID;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public boolean isFinish() {
		return false;
	}

}
