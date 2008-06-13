package com.mesh4j.sync.message.dataset;

import java.util.Hashtable;

import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;

public class DataSetManager implements IDataSetManager{

	// MODEL VARIABLES
	private Hashtable<String, IDataSet> dataSets = new Hashtable<String, IDataSet>();
	
	// BUSINESS METHODS
	
	@Override
	public IDataSet getDataSet(String dataSetId) {
		return dataSets.get(dataSetId);
	}

	public void addDataSet(IDataSet dataSet) {
		dataSets.put(dataSet.getDataSetId(), dataSet);
	}
}

