package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepSevenDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_SEVEN";
	
	private JPanel panel;
	
	public StepSevenDescriptor() {
		this.panel = new CreateMeshStepSevenView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return StepSixDescriptor.ID;
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
