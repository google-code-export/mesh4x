package org.mesh4j.meshes.ui.wizard;



public class StepTwoDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_TWO";
	
	private BaseWizardPanel panel;
	
	public StepTwoDescriptor() {
		this.panel = new CreateMeshStepTwoView();
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
	public BaseWizardPanel getPanel() {
		return panel;
	}

}
