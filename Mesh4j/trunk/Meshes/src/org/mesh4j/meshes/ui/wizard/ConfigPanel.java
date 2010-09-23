package org.mesh4j.meshes.ui.wizard;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class ConfigPanel extends JPanel {
	
	public void showInWizard() {
	}
	
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
	public String getErrorMessage() {
		return null;
	}
	
	public boolean needsValidationBeforeLeave() {
		return false;
	}

	public String getErrorMessageBeforeLeave() {
		return null;
	}

}
