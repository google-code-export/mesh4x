package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepFourDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_FOUR";
	
	private JPanel panel;
	
	public StepFourDescriptor() {
		this.panel = new CreateMeshStepFourView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return StepThreeDescriptor.ID;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getNextPanelDescriptor() {
		return null;
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
