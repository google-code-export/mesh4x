package org.mesh4j.meshes.scheduling;

import java.io.IOException;
import java.util.Date;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetState;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

public class DataSetSyncTask extends Task {
	
	public final static String SYNCHRONIZATION_FAILED = "failed";
	public final static String SYNCHRONIZATION_SUCCEED = "succeed";
	public final static String SYNCHRONIZATION_CONFLICTED = "conflicted";
	public final static String SYNCHRONIZATION_ERROR_CREATING_ADAPTER = "error_creating_adapter";

	private final Mesh mesh;
	private final DataSet dataSet;
	private final String baseDirectory;

	public DataSetSyncTask(Mesh mesh, DataSet dataSet, String baseDirectory) {
		this.mesh = mesh;
		this.dataSet = dataSet;
		this.baseDirectory = baseDirectory;
	}

	@Override
	public void execute(TaskExecutionContext ctx) throws RuntimeException {
		dataSet.setState(DataSetState.SYNC);
		try {
			for(DataSource dataSource : dataSet.getDataSources()) {
				execute(ctx, dataSource);
			}
			dataSet.setState(DataSetState.NORMAL);
		} catch (RuntimeException e) {
			dataSet.setState(DataSetState.FAILED);
			throw e;
		}
		
		try {
			ConfigurationManager.getInstance().saveMesh(mesh);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void execute(TaskExecutionContext ctx, DataSource dataSource) {
		ISyncAdapter sourceAdapter = dataSource.createSyncAdapter(dataSet, baseDirectory);
		ISyncAdapter targetAdapter = createTargetAdapter();
		
		System.out.println("Starting sync task for " + dataSource.toString() + "...");
		Date syncStart = new Date();
		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		if (dataSource.getLastSyncDate() != null)
			engine.synchronize(dataSource.getLastSyncDate());
		else
			engine.synchronize();
		System.out.println("Sync task ended");
		
		dataSource.setLastSyncDate(syncStart);
	}

	private ISyncAdapter createTargetAdapter() {
		return HttpSyncAdapterFactory.createSyncAdapter(dataSet.getServerFeedUrl(), 
				new LoggedInIdentityProvider());
	}

}
