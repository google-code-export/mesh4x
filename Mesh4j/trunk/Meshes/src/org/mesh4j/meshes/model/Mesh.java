package org.mesh4j.meshes.model;

public class Mesh extends AbstractModel {
	
	public static final String NAME_PROPERTY = "name";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String PASSWORD_PROPERTY = "password";
	
	private String name;
	private String description;
	private String password;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		
		firePropertyChange(NAME_PROPERTY, oldName, name);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		String oldDescription = this.description;
		this.description = description;
		
		firePropertyChange(DESCRIPTION_PROPERTY, oldDescription, description);
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		String oldPassword = this.password;
		this.password = password;
		
		firePropertyChange(PASSWORD_PROPERTY, oldPassword, password);
	}

}
