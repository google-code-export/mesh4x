package org.mesh4j.meshes.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.mesh4j.sync.ISyncAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class DataSource extends AbstractModel {
	
	private static final String LASTSYNCDATE_PROPERTY = "lastSyncDate";
	private DataSet dataSet;
	@XmlElement
	private Date lastSyncDate;

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
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

	public abstract void accept(MeshVisitor visitor);
}
