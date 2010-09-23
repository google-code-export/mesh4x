package org.mesh4j.meshes.ui.wizard;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.controller.CreateMeshWizardController;

@SuppressWarnings("serial")
public class EpiInfoConfigPanel2 extends ConfigPanel {
	
	private static final Logger LOGGER = Logger.getLogger(EpiInfoConfigPanel2.class);

	private CreateMeshWizardController controller;
	
	public EpiInfoConfigPanel2(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
	}
}
