package org.mesh4j.meshes.scheduling;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.sync.SyncManager;

public class DataSourceSyncTask extends Task {

	private final DataSource dataSource;
	
	public DataSourceSyncTask(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void execute(TaskExecutionContext ctx) throws RuntimeException {
		SyncManager.getInstance().synchronize(dataSource);
	}

}
