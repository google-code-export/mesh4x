package org.mesh4j.meshes.ui.wizard;



public class StepFourDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_FOUR";
	
	private BaseWizardPanel panel;
	
	public StepFourDescriptor() {
		this.panel = new CreateMeshStepFourView();
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
		return StepFiveDescriptor.ID;
	}

	@Override
	public BaseWizardPanel getPanel() {
		return panel;
	}
	
}
