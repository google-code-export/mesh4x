package org.mesh4j.meshes.ui.wizard;



public class StepSixDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_SIX";
	
	private BaseWizardPanel panel;
	
	public StepSixDescriptor() {
		this.panel = new CreateMeshStepSixView();
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
	public BaseWizardPanel getPanel() {
		return panel;
	}

}
