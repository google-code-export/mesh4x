package org.mesh4j.meshes.ui.wizard;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class BaseWizardPanel extends JPanel {

	public abstract void modelPropertyChange(PropertyChangeEvent evt);
	
	public abstract String getId();
	
	// Return null if there are no error messages
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
