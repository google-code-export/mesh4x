package org.mesh4j.meshes.model;

public class DataSet extends AbstractModel {
	
	public static final String TYPE_PROPERTY = "dataset_type";
	public static final String NAME_PROPERTY = "dataset_name";
	public static final String DESCRIPTION_PROPERTY = "dataset_description";
	
	private String name;
	private String description;
	private DataSetType type;
	
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(NAME_PROPERTY, oldName, name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDescription(String description) {
		String oldDescription = this.description;
		this.description = description;
		firePropertyChange(DESCRIPTION_PROPERTY, oldDescription, description);
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setType(DataSetType type) {
		DataSetType oldType = this.type; 
		this.type = type;
		firePropertyChange(NAME_PROPERTY, oldType, type);
	}
	
	public DataSetType getType() {
		return type;
	}

}
