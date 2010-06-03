package org.mesh4j.meshes.scheduling;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.sync.SyncManager;

public class DataSetSyncTask extends Task {

	private final DataSet dataSet;
	
	public DataSetSyncTask(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	@Override
	public void execute(TaskExecutionContext ctx) throws RuntimeException {
		SyncManager.getInstance().synchronize(dataSet);
	}

}
