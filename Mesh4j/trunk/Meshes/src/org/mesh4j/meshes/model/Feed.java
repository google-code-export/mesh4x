package org.mesh4j.meshes.model;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class Feed {
	
	private String name;
	private String serverFeedUrl;
	
	@XmlTransient
	private Mesh mesh;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getServerFeedUrl() {
		return serverFeedUrl;
	}
	
	public void setServerFeedUrl(String serverFeedUrl) {
		this.serverFeedUrl = serverFeedUrl;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent instanceof Mesh)
			mesh = (Mesh) parent;
	}

	public Feed copy() {
		Feed feed = new Feed();
		feed.name = this.name;
		feed.serverFeedUrl = serverFeedUrl;
		return feed;
	}
	
}
