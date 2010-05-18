package org.mesh4j.meshes.model;

public abstract class DataSource extends AbstractModel {
	
	private DataSet dataSet;

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

}
