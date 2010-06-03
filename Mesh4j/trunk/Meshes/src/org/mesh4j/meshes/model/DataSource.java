package org.mesh4j.meshes.model;

import org.mesh4j.sync.ISyncAdapter;

public abstract class DataSource extends AbstractModel {
	
	private DataSet dataSet;

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public abstract ISyncAdapter createSyncAdapter(DataSet dataSet, String baseDirectory);

	public abstract void accept(MeshVisitor visitor);

}
