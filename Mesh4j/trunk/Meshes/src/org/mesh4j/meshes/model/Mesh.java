package org.mesh4j.meshes.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mesh")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mesh extends AbstractModel {
	
	public static final String NAME_PROPERTY = "name";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String SERVER_FEED_URL_PROPERTY = "dataset_server_feed_url";
	
	private String name;
	private String description;

	@XmlElementWrapper(name = "dataSources")
	@XmlElementRefs({
        	@XmlElementRef(type = EpiInfoDataSource.class),
        	@XmlElementRef(type = HibernateDataSource.class)
        })
	@XmlMixed()
	private List<DataSource> dataSources = new ArrayList<DataSource>();

	@XmlElementWrapper(name = "feeds")
	@XmlElement(name = "feed")
	private List<Feed> feeds = new ArrayList<Feed>();
	
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

	public List<DataSource> getDataSources() {
		return dataSources;
	}
	
	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}
	
	public List<Feed> getFeeds() {
		return feeds;
	}
	
	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}
	
	public void accept(MeshVisitor visitor) {
		boolean children = visitor.visit(this);
		if (!children) return;
		
		for(DataSource dataSource : dataSources)
			dataSource.accept(visitor);
	}

	public Mesh copy() {
		Mesh copy = new Mesh();
		copy.name = name;
		copy.description = description;
		return copy;
	}

	public Feed getFeed(String name) {
		for (Feed feed : feeds) {
			if (feed.getName().equals(name))
				return feed;
		}
		return null;
	}
}
