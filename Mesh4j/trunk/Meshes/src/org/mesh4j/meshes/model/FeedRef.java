package org.mesh4j.meshes.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.io.LogMarshaller;
import org.mesh4j.sync.ISyncAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class FeedRef extends AbstractModel {
	
	private String feedName;
	private String localName;
	private Date lastSyncDate;
	private SyncState state;
	private boolean hasConflicts;
	@XmlTransient
	private DataSource dataSource;
	
	public String getFeedName() {
		return feedName;
	}
	
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}
	
	public String getLocalName() {
		return localName;
	}
	
	public void setLocalName(String localName) {
		String oldValue = this.localName;
		this.localName = localName;
		firePropertyChange("localName", oldValue, localName);
	}
	
	public void setHasConflicts(boolean hasConflicts) {
		boolean oldValue = this.hasConflicts;
		this.hasConflicts = hasConflicts;
		firePropertyChange("hasConflicts", oldValue, hasConflicts);
	}
	
	public boolean hasConflicts() {
		return hasConflicts;
	}
	
	public void setLastSyncDate(Date lastSyncDate) {
		Date oldValue = this.lastSyncDate;
		this.lastSyncDate = lastSyncDate;
		firePropertyChange("lastSyncDate", oldValue, lastSyncDate);
	}

	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public SyncState getState() {
		return state;
	}
	
	public void setState(SyncState state) {
		SyncState oldValue = this.state;
		this.state = state;
		firePropertyChange("state", oldValue, state);
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent instanceof DataSource)
			dataSource = (DataSource) parent;
	}
	
	public void addLog(SyncLog log) {
		File dir = ConfigurationManager.getInstance().getRuntimeDirectory(this);
		File logFile = new File(dir, "synclog.xml");
		
		List<SyncLog> logItems;
		if (logFile.exists()) {
			logItems = LogMarshaller.load(logFile);
		} else {
			logItems = new ArrayList<SyncLog>();
		}
		
		logItems.add(log);
		LogMarshaller.save(logItems, logFile);
	}
	
	public SyncLog[] getLogEntries() {
		File dir = ConfigurationManager.getInstance().getRuntimeDirectory(this);
		File logFile = new File(dir, "synclog.xml");
		
		if (!logFile.exists())
			return new SyncLog[0];
		
		List<SyncLog> logItems = LogMarshaller.load(logFile);
		return (SyncLog[]) logItems.toArray(new SyncLog[logItems.size()]);
	}

	public Feed getTargetFeed() {
		return dataSource.getMesh().getFeed(feedName);
	}

	public ISyncAdapter createSyncAdapter() {
		return dataSource.createSyncAdapter(this);
	}

	public FeedRef copy() {
		FeedRef feedRef = new FeedRef();
		feedRef.feedName = this.feedName;
		feedRef.localName = this.localName;
		return feedRef;
	}
}
