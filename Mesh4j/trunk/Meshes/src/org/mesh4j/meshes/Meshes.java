package org.mesh4j.meshes;

import java.awt.EventQueue;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.action.ToggleFrameAction;
import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.scheduling.ScheduleManager;
import org.mesh4j.meshes.ui.MainWindow;
import org.mesh4j.meshes.ui.MeshesTray;
import org.mesh4j.meshes.ui.ShowMeshWizard;

public class Meshes {
	
	private final static Logger LOGGER = Logger.getLogger(Meshes.class);
	
	private Meshes() {}

	public static void main(String[] args) {
		initLookAndFeel();
		
		final String fileToImport = args.length > 0 ? args[0] : null;
		
		// First check if there is another instance of the program running
		UniqueApplicationInstance instance = UniqueApplicationInstance.getInstance();
		if (instance.anotherInstanceIsRunning()) {
			if (fileToImport != null) {
				instance.importConfigurationFile(fileToImport);
			} else {
				JOptionPane.showMessageDialog(null, "Another instance of Meshes is already running", "Meshes", JOptionPane.INFORMATION_MESSAGE);
			}
			return;
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow mainWindow = new MainWindow();
					Action toggleMainWindow = new ToggleFrameAction(mainWindow);
					new MeshesTray(toggleMainWindow);
					if (fileToImport == null) {
						new ShowMeshWizard().actionPerformed(null);
					} else {
						ConfigurationManager.getInstance().importFile(fileToImport);
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
		
		try {
			ScheduleManager.getInstance().initialize();
		} catch (Exception e) {
			LOGGER.fatal("Failed to initialize the task scheduler", e);
		}
	}
	
	private static void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
