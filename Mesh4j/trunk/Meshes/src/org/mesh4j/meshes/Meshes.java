package org.mesh4j.meshes;

import java.awt.EventQueue;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.ui.wizard.CreateMeshWizardView;
import org.mesh4j.meshes.ui.wizard.StepFourDescriptor;
import org.mesh4j.meshes.ui.wizard.StepOneDescriptor;
import org.mesh4j.meshes.ui.wizard.StepThreeDescriptor;
import org.mesh4j.meshes.ui.wizard.StepTwoDescriptor;
import org.mesh4j.meshes.ui.wizard.WizardPanelDescriptor;

public class Meshes {
	
	private final static Log LOGGER = LogFactory.getLog(Meshes.class);
	
	private Meshes() {}

	public static void main(String[] args) {
		
		initLookAndFeel();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//MainWindow mainWindow = new MainWindow();
					//Action toggleMainWindow = new ToggleFrameAction(mainWindow);
					//new MeshesTray(toggleMainWindow);
					
					CreateMeshWizardView wizard = new CreateMeshWizardView(new CreateMeshWizardController(new Mesh()));
					
					WizardPanelDescriptor desc1 = new StepOneDescriptor();
					WizardPanelDescriptor desc2 = new StepTwoDescriptor();
					WizardPanelDescriptor desc3 = new StepThreeDescriptor();
					WizardPanelDescriptor desc4 = new StepFourDescriptor();
					wizard.registerWizardPanel(desc1);
					wizard.registerWizardPanel(desc2);
					wizard.registerWizardPanel(desc3);
					wizard.registerWizardPanel(desc4);
					
					wizard.setCurrentPanel(desc1.getId());
					
					wizard.setVisible(true);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}
	
	private static void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
