package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepThreeDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_THREE";
	
	private JPanel panel;
	
	public StepThreeDescriptor() {
		this.panel = new CreateMeshStepThreeView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return StepTwoDescriptor.ID;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getNextPanelDescriptor() {
		return StepFourDescriptor.ID;
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
