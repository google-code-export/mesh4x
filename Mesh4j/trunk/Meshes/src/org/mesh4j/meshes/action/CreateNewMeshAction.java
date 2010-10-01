package org.mesh4j.meshes.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.wizard.WizardView;

public class CreateNewMeshAction extends AbstractAction {
	
	private static final long serialVersionUID = 285418254328317209L;
	
	public CreateNewMeshAction() {
		super("Create new mesh...");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		WizardView wizard = new WizardView();
		new CreateMeshWizardController(wizard);
		wizard.setVisible(true);
	}

}
