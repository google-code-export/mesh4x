package org.mesh4j.meshes;

import java.awt.EventQueue;

import javax.swing.Action;

import org.mesh4j.meshes.action.ToggleFrameAction;
import org.mesh4j.meshes.ui.MainWindow;
import org.mesh4j.meshes.ui.MeshesTray;

public class Meshes {
	
	private Meshes() {}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow mainWindow = new MainWindow();
					Action toggleMainWindow = new ToggleFrameAction(mainWindow);
					new MeshesTray(toggleMainWindow);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
