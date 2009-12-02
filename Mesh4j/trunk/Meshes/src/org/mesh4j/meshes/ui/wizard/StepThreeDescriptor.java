package org.mesh4j.meshes.ui.wizard;



public class StepThreeDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_THREE";
	
	private BaseWizardPanel panel;
	
	public StepThreeDescriptor() {
		this.panel = new CreateMeshStepThreeView();
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
	public BaseWizardPanel getPanel() {
		return panel;
	}

}
