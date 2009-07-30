package org.mesh4j.ektoo.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.mesh4j.sync.payload.mappings.Mapping;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public abstract class AbstractModel 
{

	// MODEL VARIABLES
	protected PropertyChangeSupport propertyChangeSupport;
	private Mapping mappings = null;

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
	
	public void setMappings(Mapping mappings) {
		this.mappings = mappings;		
	}

	public Mapping getMappings() {
		return this.mappings;		
	}
}
