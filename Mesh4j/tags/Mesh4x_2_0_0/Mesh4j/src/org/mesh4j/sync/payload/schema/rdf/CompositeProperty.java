package org.mesh4j.sync.payload.schema.rdf;

import java.util.HashMap;
import java.util.List;

public class CompositeProperty {
	
	// MODEL VARIABLE
	private String compositeName;
	private List<String> propertyNames;
	private HashMap<String, Object> properties;
	
	// BUSINESS METHODS
	public CompositeProperty(String compositeName, List<String> propertyNames) {
		super();
		this.compositeName = compositeName;
		this.properties = new HashMap<String, Object>();
		this.propertyNames = propertyNames;
		
		for (String propertyName : this.propertyNames) {
			this.properties.put(propertyName, null);
		}
	}	
	
	public boolean isCompleted() {
		for (Object propertyValue : this.properties.values()) {
			if(propertyValue == null){
				return false;
			}			
		}
		return true;
	}
	
	public List<String> getPropertyNames() {
		return this.propertyNames;
	}
	
	public Object getPropertyValue(String propertyName) {
		return this.properties.get(propertyName);
	}
	
	public void setPropertyValue(String propertyName, Object value) {
		if(this.containsPropery(propertyName)){
			this.properties.put(propertyName, value);
		}
	}
	
	public String getCompositeName() {
		return this.compositeName;
	}
	
	public boolean containsPropery(String propertyName) {
		return this.propertyNames.contains(propertyName);
	}	
}
