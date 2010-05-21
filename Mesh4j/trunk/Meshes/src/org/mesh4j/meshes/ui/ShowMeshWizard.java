package org.mesh4j.meshes.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.wizard.WizardView;

public class ShowMeshWizard implements ActionListener {
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WizardView wizard = new WizardView();
		new CreateMeshWizardController(wizard);
		
		wizard.setVisible(true);
	}

}
