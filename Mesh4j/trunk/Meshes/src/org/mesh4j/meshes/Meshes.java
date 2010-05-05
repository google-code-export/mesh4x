package org.mesh4j.meshes;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.controller.WizardController;
import org.mesh4j.meshes.ui.wizard.WizardView;

public class Meshes {
	
	private final static Logger LOGGER = Logger.getLogger(Meshes.class);
	
	private Meshes() {}

	public static void main(String[] args) {
		
		initLookAndFeel();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//					MainWindow mainWindow = new MainWindow();
//					Action toggleMainWindow = new ToggleFrameAction(mainWindow);
//					new MeshesTray(toggleMainWindow);
					
					WizardView wizard = new WizardView();
					WizardController controller = new CreateMeshWizardController(wizard);
					
					wizard.setVisible(true);
					wizard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
