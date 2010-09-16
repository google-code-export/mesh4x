package org.mesh4j.meshes.sync;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetState;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.SyncLog;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncDirection;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

public class SyncManager {
	
	private static SyncManager instance = new SyncManager();
	private Set<String> currentSyncs = new HashSet<String>();
	
	private SyncManager() {
	}
	
	public static SyncManager getInstance() {
		return instance;
	}
	
	public void synchronize(DataSet dataSet) {
		if (isSynchronizing(dataSet))
			return;
		
		currentSyncs.add(getSyncName(dataSet));
		try {
			dataSet.setState(DataSetState.SYNC);
			try {
				for(DataSource dataSource : dataSet.getDataSources()) {
					try {
						synchronize(dataSource);
						dataSource.addLog(new SyncLog(true, "Synchronization succeeded"));
					} catch (Exception e) {
						dataSource.addLog(new SyncLog(false, e.getMessage()));
						throw e;
					}
				}
				dataSet.setState(DataSetState.NORMAL);
			} catch (Exception e) {
				dataSet.setState(DataSetState.FAILED);
			}
			
			try {
				ConfigurationManager.getInstance().saveMesh(dataSet.getMesh());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} finally {
			currentSyncs.remove(getSyncName(dataSet));
		}
	}
	
	public boolean isSynchronizing(DataSet dataSet) {
		return currentSyncs.contains(getSyncName(dataSet));
	}

	private void synchronize(DataSource dataSource) {
		ISyncAdapter sourceAdapter = dataSource.createSyncAdapter();
		ISyncAdapter targetAdapter = createTargetAdapter(dataSource.getDataSet());
		
		System.out.println("Starting sync task for " + dataSource.toString() + "...");
		Date syncStart = new Date();
		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		List<Item> conflicts = engine.synchronize(dataSource.getLastSyncDate(), getSyncDirection(dataSource.getDataSet().getSchedule().getSyncMode()));
		if (!conflicts.isEmpty()) {
			dataSource.setHasConflicts(true);
		}

		System.out.println("Sync task ended");
		dataSource.setLastSyncDate(syncStart);
	}
	
	private ISyncAdapter createTargetAdapter(DataSet dataSet) {
		return HttpSyncAdapterFactory.createSyncAdapter(dataSet.getAbsoluteServerFeedUrl(), 
				new LoggedInIdentityProvider());
	}
	
	private String getSyncName(DataSet dataSet) {
		return dataSet.getMesh().getName() + "/" + dataSet.getName();
	}

	private SyncDirection getSyncDirection(SyncMode mode) {
		switch (mode) {
		case RECEIVE:
			return SyncDirection.TargetToSource;
		case SEND:
			return SyncDirection.SourceToTarget;
		default:
			return SyncDirection.Both;
		}
	}
}
