package org.mesh4j.meshes;

import java.awt.EventQueue;

import org.mesh4j.meshes.ui.MeshesTray;

public class Meshes {
	
	private Meshes() {}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MeshesTray();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
