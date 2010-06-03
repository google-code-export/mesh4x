package org.mesh4j.meshes.sync;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetState;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

public class SyncManager {
	
	private static SyncManager instance = new SyncManager();
	private Set<String> currentSyncs = new HashSet<String>();
	
	private SyncManager() {
	}
	
	public static SyncManager getInstance() {
		return instance;
	}
	
	public void synchronize(Mesh mesh, DataSet dataSet, String baseDirectory) {
		if (isSynchronizing(mesh, dataSet))
			return;
		
		currentSyncs.add(getSyncName(mesh, dataSet));
		try {
			dataSet.setState(DataSetState.SYNC);
			try {
				for(DataSource dataSource : dataSet.getDataSources()) {
					synchronize(mesh, dataSet, dataSource, baseDirectory);
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
		} finally {
			currentSyncs.remove(getSyncName(mesh, dataSet));
		}
	}
	
	public boolean isSynchronizing(Mesh mesh, DataSet dataSet) {
		return currentSyncs.contains(getSyncName(mesh, dataSet));
	}

	private void synchronize(Mesh mesh, DataSet dataSet, DataSource dataSource, String baseDirectory) {
		ISyncAdapter sourceAdapter = dataSource.createSyncAdapter(dataSet, baseDirectory);
		ISyncAdapter targetAdapter = createTargetAdapter(dataSet);
		
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
	
	private ISyncAdapter createTargetAdapter(DataSet dataSet) {
		return HttpSyncAdapterFactory.createSyncAdapter(dataSet.getServerFeedUrl(), 
				new LoggedInIdentityProvider());
	}
	
	private String getSyncName(Mesh mesh, DataSet dataSet) {
		return mesh.getName() + "/" + dataSet.getName();
	}

}
