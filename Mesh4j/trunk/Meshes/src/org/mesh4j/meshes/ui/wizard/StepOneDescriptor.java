package org.mesh4j.meshes.ui.wizard;



public class StepOneDescriptor extends WizardPanelDescriptor {
	
	public static final String ID = "STEP_ONE";
	
	private BaseWizardPanel panel;
	
	public StepOneDescriptor() {
		this.panel = new CreateMeshStepOneView();
	}

	@Override
	public String getBackPanelDescriptor() {
		return null;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getNextPanelDescriptor() {
		return StepTwoDescriptor.ID;
	}

	@Override
	public BaseWizardPanel getPanel() {
		return panel;
	}

}
