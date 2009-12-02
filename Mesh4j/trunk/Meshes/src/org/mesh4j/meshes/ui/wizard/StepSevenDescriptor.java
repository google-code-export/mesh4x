package org.mesh4j.meshes.ui.wizard;



public class StepSevenDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_SEVEN";
	
	private BaseWizardPanel panel;
	
	public StepSevenDescriptor() {
		this.panel = new CreateMeshStepSevenView();
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
	public BaseWizardPanel getPanel() {
		return panel;
	}

}
