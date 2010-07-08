package org.mesh4j.meshes.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.io.LogMarshaller;
import org.mesh4j.sync.ISyncAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class DataSource extends AbstractModel {
	
	private static final String LASTSYNCDATE_PROPERTY = "lastSyncDate";
	private DataSet dataSet;
	@XmlElement
	private String id;
	@XmlElement
	private Date lastSyncDate;
	
	public DataSource() {
		id = UUID.randomUUID().toString();
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	
	public String getId() {
		return id;
	}

	public abstract ISyncAdapter createSyncAdapter(DataSet dataSet, String baseDirectory);

	public void setLastSyncDate(Date lastSyncDate) {
		Date oldValue = this.lastSyncDate;
		this.lastSyncDate = lastSyncDate;
		firePropertyChange(LASTSYNCDATE_PROPERTY, oldValue, lastSyncDate);
	}

	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent instanceof DataSet)
			dataSet = (DataSet) parent;
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
	
	public abstract void accept(MeshVisitor visitor);
}
