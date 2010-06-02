package org.mesh4j.meshes.scheduling;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

public class DataSetSyncTask extends Task {
	
	public final static String SYNCHRONIZATION_FAILED = "failed";
	public final static String SYNCHRONIZATION_SUCCEED = "succeed";
	public final static String SYNCHRONIZATION_CONFLICTED = "conflicted";
	public final static String SYNCHRONIZATION_ERROR_CREATING_ADAPTER = "error_creating_adapter";

	private final DataSet dataSet;
	private final String baseDirectory;

	public DataSetSyncTask(DataSet dataSet, String baseDirectory) {
		this.dataSet = dataSet;
		this.baseDirectory = baseDirectory;
	}

	@Override
	public void execute(TaskExecutionContext ctx) throws RuntimeException {
		for(DataSource dataSource : dataSet.getDataSources()) {
			execute(ctx, dataSource);
		}
	}

	private void execute(TaskExecutionContext ctx, DataSource dataSource) {
		ISyncAdapter sourceAdapter = dataSource.createSyncAdapter(dataSet, baseDirectory);
		ISyncAdapter targetAdapter = createTargetAdapter();
		
		System.out.println("Starting sync task for " + dataSource.toString() + "...");
		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		engine.synchronize();
		System.out.println("Sync task ended");
	}

	private ISyncAdapter createTargetAdapter() {
		return HttpSyncAdapterFactory.createSyncAdapter(dataSet.getServerFeedUrl(), 
				new LoggedInIdentityProvider());
	}

}
