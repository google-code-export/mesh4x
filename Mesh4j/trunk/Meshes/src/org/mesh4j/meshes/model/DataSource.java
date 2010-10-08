package org.mesh4j.meshes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.ISchema;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DataSource extends AbstractModel {
	
	@XmlTransient
	private Mesh mesh;
	private String id;
	private Schedule schedule;
	private SyncState state;
	@XmlElement(name = "feedRef")
	private List<FeedRef> feeds = new ArrayList<FeedRef>();
	
	public DataSource() {
		id = UUID.randomUUID().toString();
	}

	public Mesh getMesh() {
		return mesh;
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public List<FeedRef> getFeeds() {
		return feeds;
	}
	
	public void setFeeds(List<FeedRef> feeds) {
		this.feeds = feeds;
	}
	
	public SyncState getState() {
		return state;
	}
	
	public void setState(SyncState state) {
		SyncState oldValue = this.state;
		this.state = state;
		firePropertyChange("state", oldValue, state);
	}
	
	public String getId() {
		return id;
	}
	
	public String getRdfSchemaBaseUri() {
		return "urn:uuid:" + id;
	}
	
	public ISyncAdapter createSyncAdapter(FeedRef feedRef) {
		return createSyncAdapter(null, feedRef);
	}
	
	public ISyncAdapter createSyncAdapter(ISchema schema, FeedRef feedRef) {
		return createSyncAdapter(schema, ConfigurationManager.getInstance().getRuntimeDirectory(feedRef).getAbsolutePath(), feedRef);
	}

	public abstract ISyncAdapter createSyncAdapter(ISchema schema, String baseDirectory, FeedRef feedRef);
		
	public boolean hasConflicts() {
		for (FeedRef feedRef : feeds) {
			if (feedRef.hasConflicts())
				return true;
		}
		return false;
	}

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent instanceof Mesh)
			mesh = (Mesh) parent;
	}
	
	public Schedule getSchedule() {
		return schedule;
	}
	
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	public abstract void accept(MeshVisitor visitor);

	public DataSource copy() {
		try {
			DataSource copy = getClass().newInstance();
			copy.id = id;
			copy.schedule = schedule;
			return copy;
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	
	
}
