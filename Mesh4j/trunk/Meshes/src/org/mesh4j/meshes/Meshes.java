package org.mesh4j.meshes;

import java.awt.EventQueue;

import javax.swing.Action;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.action.ToggleFrameAction;
import org.mesh4j.meshes.ui.MainWindow;
import org.mesh4j.meshes.ui.MeshesTray;
import org.mesh4j.meshes.ui.ShowMeshWizard;

public class Meshes {
	
	private final static Logger LOGGER = Logger.getLogger(Meshes.class);
	
	private Meshes() {}

	public static void main(String[] args) {
		
		initLookAndFeel();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow mainWindow = new MainWindow();
					Action toggleMainWindow = new ToggleFrameAction(mainWindow);
					new MeshesTray(toggleMainWindow);
					new ShowMeshWizard().actionPerformed(null);
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
