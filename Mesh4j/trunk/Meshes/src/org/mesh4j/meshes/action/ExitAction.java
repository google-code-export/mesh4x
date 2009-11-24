package org.mesh4j.meshes.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ExitAction extends AbstractAction {

	private static final long serialVersionUID = -4854302601630372692L;

	public void actionPerformed(ActionEvent evt) {
		System.exit(0);
	}
}
