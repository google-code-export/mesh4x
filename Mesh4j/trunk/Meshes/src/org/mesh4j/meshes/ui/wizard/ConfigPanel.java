package org.mesh4j.meshes.ui.wizard;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class ConfigPanel extends JPanel {
	
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
	public String getErrorMessage() {
		return null;
	}

}
