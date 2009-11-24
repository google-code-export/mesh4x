package org.mesh4j.meshes.controller;

import org.mesh4j.meshes.ui.MainWindow;
import org.mesh4j.meshes.ui.MeshesTray;

public class MeshesController {
	
	private MeshesTray tray;
	private MainWindow main;
	
	public MeshesController(MeshesTray tray, MainWindow main) {
		this.tray = tray;
		this.main = main;
	}
	
	public void showMainWindow() {
		main.setVisible(true);
	}

}
