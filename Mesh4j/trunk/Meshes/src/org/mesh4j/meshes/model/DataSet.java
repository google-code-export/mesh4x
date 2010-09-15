package org.mesh4j.meshes.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.NONE)
public class DataSet extends AbstractModel {
	
	public static final String TYPE_PROPERTY = "dataset_type";
	public static final String NAME_PROPERTY = "dataset_name";
	public static final String SERVER_FEED_URL_PROPERTY = "dataset_server_feed_url";
	public static final String SCHEDULE_PROPERTY = "dataset_schedule";
	public static final String STATE_PROPERTY = "state";
	
	@XmlElement
	private DataSetType type;
	@XmlElement
	private String name;
	@XmlElement
	private String serverFeedUrl;
	@XmlElement
	private Schedule schedule;
	@XmlElementWrapper(name = "dataSources")
	@XmlElementRefs({
        	@XmlElementRef(type = EpiInfoDataSource.class),
        	@XmlElementRef(type = HibernateDataSource.class)
        })
	@XmlMixed()
	private List<DataSource> dataSources = new ArrayList<DataSource>(3);
	
	@XmlTransient
	private DataSetState state = DataSetState.NORMAL;
	@XmlTransient
	private Mesh mesh;
		
	public void setSchedule(Schedule schedule) {
		Schedule oldSchedule = this.schedule;
		this.schedule = schedule;
		firePropertyChange(SCHEDULE_PROPERTY, oldSchedule, schedule);
	}
	
	public Schedule getSchedule() {
		return this.schedule;
	}
		
	public void setType(DataSetType type) {
		DataSetType oldType = this.type; 
		this.type = type;
		firePropertyChange(TYPE_PROPERTY, oldType, type);
	}
	
	public DataSetType getType() {
		return this.type;
	}
	
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(NAME_PROPERTY, oldName, name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setServerFeedUrl(String serverFeedUrl) {
		String oldUrl = this.serverFeedUrl;
		this.serverFeedUrl = serverFeedUrl;
		firePropertyChange(SERVER_FEED_URL_PROPERTY, oldUrl, serverFeedUrl);
	}
	
	public String getServerFeedUrl() {
		return serverFeedUrl;
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public DataSetState getState() {
		return state;
	}
	
	public void setState(DataSetState state) {
		DataSetState oldState = this.state;
		this.state = state;
		
		firePropertyChange(STATE_PROPERTY, oldState, state);
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public void accept(MeshVisitor visitor) {
		boolean children = visitor.visit(this);
		if (!children) return;
		
		for(DataSource dataSource : dataSources)
			dataSource.accept(visitor);
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent instanceof Mesh)
			mesh = (Mesh) parent;
	}
	
	public String getAbsoluteServerFeedUrl()
	{
		URL url;
		try {
			url = new URL(getServerFeedUrl());
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
		return url.toString();
	}

	public DataSet copy() {
		DataSet copy = new DataSet();
		copy.type = type;
		copy.name = name;
		copy.serverFeedUrl = serverFeedUrl;
		copy.schedule = new Schedule();
		copy.schedule.setSchedulingOption(schedule.getSchedulingOption());
		copy.schedule.setSyncMode(schedule.getSyncMode());
		return copy;
	}
}
