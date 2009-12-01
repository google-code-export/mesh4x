package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepSixDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_SIX";
	
	private JPanel panel;
	
	public StepSixDescriptor() {
		this.panel = new CreateMeshStepSixView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return StepFiveDescriptor.ID;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getNextPanelDescriptor() {
		return StepSevenDescriptor.ID;
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
