package org.mesh4j.ektoo.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.mesh4j.sync.payload.mappings.Mapping;

public abstract class AbstractModel{
	
	// MODEL VARIABLES
	protected PropertyChangeSupport propertyChangeSupport;
	private Mapping mapping = null;

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

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		boolean areDifferent = (oldValue == null && newValue != null) || (oldValue != null && newValue == null) || (oldValue != null && newValue != null && !oldValue.equals(newValue));
		if(areDifferent){
			this.fireEmptyMappingForPropertyChange(propertyName);
		}
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void fireEmptyMappingForPropertyChange(String propertyName) {
		this.mapping = null;
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		if(oldValue != newValue){
			this.fireEmptyMappingForPropertyChange(propertyName);
		}
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void setMapping(Mapping mapping) {
		this.mapping = mapping;		
	}

	public Mapping getMapping() {
		return this.mapping;		
	}
}
