package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepFiveDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_FIVE";
	
	private JPanel panel;
	
	public StepFiveDescriptor() {
		this.panel = new CreateMeshStepFiveView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return StepFourDescriptor.ID;
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
