package org.mesh4j.meshes.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "epiInfoDataSource")
@XmlAccessorType(XmlAccessType.NONE)
public class EpiInfoDataSource extends DataSource {
	
	public static final String LOCATION_PROPERTY = "epiinfo_location";
	
	@XmlElement
	private String location;

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		String oldLocation = this.location;
		this.location = location;
		firePropertyChange(LOCATION_PROPERTY, oldLocation, location);
	}

}
