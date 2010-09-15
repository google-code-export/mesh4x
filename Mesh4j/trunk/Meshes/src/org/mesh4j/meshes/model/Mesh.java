package org.mesh4j.meshes.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mesh")
@XmlAccessorType(XmlAccessType.NONE)
public class Mesh extends AbstractModel {
	
	public static final String NAME_PROPERTY = "name";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String PASSWORD_PROPERTY = "password";
	public static final String SERVER_FEED_URL_PROPERTY = "dataset_server_feed_url";
	
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "description")
	private String description;
	@XmlElementWrapper(name = "dataSets")
	@XmlElement(name = "dataSet")
	private List<DataSet> dataSets = new ArrayList<DataSet>(3);
	
	public String getName() {
		return this.name;
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

	public List<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
	public void accept(MeshVisitor visitor) {
		boolean children = visitor.visit(this);
		if (!children) return;
		
		for(DataSet dataSet : dataSets)
			dataSet.accept(visitor);
	}

	public Mesh copy() {
		Mesh copy = new Mesh();
		copy.name = name;
		copy.description = description;
		return copy;
	}

}
