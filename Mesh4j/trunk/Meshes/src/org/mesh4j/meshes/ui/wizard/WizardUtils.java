package org.mesh4j.meshes.ui.wizard;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.mesh4j.meshes.controller.WizardController;

public class WizardUtils {
	
	public static void nextWhenEnterPressedOn(final WizardController controller, Component ... components) {
		for(Component component : components) {
			component.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == 10)
						controller.nextButtonPressed();
				}
			});
		}
	}

}
