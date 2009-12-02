package org.mesh4j.meshes.ui.wizard;



public class StepFiveDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_FIVE";
	
	private BaseWizardPanel panel;
	
	public StepFiveDescriptor() {
		this.panel = new CreateMeshStepFiveView();
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
		return StepSixDescriptor.ID;
	}

	@Override
	public BaseWizardPanel getPanel() {
		return panel;
	}

}
