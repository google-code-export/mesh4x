package org.mesh4j.meshes.ui.wizard;

import javax.swing.JPanel;


public class StepTwoDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_TWO";
	
	private JPanel panel;
	
	public StepTwoDescriptor() {
		this.panel = new CreateMeshStepTwoView(this);
	}

	@Override
	public String getBackPanelDescriptor() {
		return StepOneDescriptor.ID;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getNextPanelDescriptor() {
		return StepThreeDescriptor.ID;
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
