package org.mesh4j.meshes.ui.wizard;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public class WizardConfigureDataSourceStep2 extends WizardConfigureDataSourceStep {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_DATA_SOURCE_CONFIG_2";
	
	public WizardConfigureDataSourceStep2(CreateMeshWizardController controller) {
		super(controller);
	}
	
	protected ConfigPanel newConfigPanel(DataSourceType type) {
		switch(type) {
		case EPI_INFO:
			return new EpiInfoConfigPanel2(controller);
		case DATABASE:
			return new DatabaseConfigPanel2(controller);
		default:
			throw new IllegalStateException("No ConfigPanel for DataSourceType " + type.name());
		}
	}

	@Override
	public String getId() {
		return ID;
	}
	
}
