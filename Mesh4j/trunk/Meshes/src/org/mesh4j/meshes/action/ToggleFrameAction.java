package org.mesh4j.meshes.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

public class ToggleFrameAction extends AbstractAction {

	private static final long serialVersionUID = -5201501785375148292L;

	private JFrame frame;

	public ToggleFrameAction(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (frame.isVisible()) {
			frame.setVisible(false);
		} else {
			frame.setVisible(true);
			frame.toFront();
		}
	}

}
