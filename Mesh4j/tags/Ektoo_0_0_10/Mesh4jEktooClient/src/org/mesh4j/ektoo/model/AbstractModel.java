package org.mesh4j.ektoo.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public abstract class AbstractModel 
{

	// MODEL VARIABLES
	protected PropertyChangeSupport propertyChangeSupport;

	// BUSINESS METHODS
	public AbstractModel() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListner(PropertyChangeListener listner) {
		propertyChangeSupport.addPropertyChangeListener(listner);
	}

	public void removePropertyChangeListner(PropertyChangeListener listner) {
		propertyChangeSupport.removePropertyChangeListener(listner);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
}
