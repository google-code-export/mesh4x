package org.mesh4j.sync.adapters.multimode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.security.IIdentityProvider;

public class SyncProcess {

	// MODEL VARIABLES
	List<SyncTask> syncTasks = new ArrayList<SyncTask>();
	
	// BUSINESS METHODS
	
	public SyncTask getSyncTask(String datasource) {
		for (SyncTask syncTask : this.syncTasks) {
			String rdfName = syncTask.getDataSource();
			if(rdfName.equals(datasource)){
				return syncTask;
			}
		}
		return null;
	}

	public void synchronize() {
		for (SyncTask syncTask : this.syncTasks) {
			syncTask.synchronize();
		}		
	}

	public List<SyncTask> getSyncTasks() {
		return Collections.unmodifiableList(this.syncTasks);
	}
	
	// Factory methods
	
	public static SyncProcess makeSyncProcessForSyncMsAccessVsHttp(String fileName, String serverURL, String meshGroup, IIdentityProvider identityProvider, String baseDirectory) throws IOException {
		SyncProcess syncProcess = new SyncProcess();
		
		Set<String> tables = MsAccessHibernateSyncAdapterFactory.getTableNames(fileName);
		for (String tableName : tables) {
			SplitAdapter target = MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(fileName, tableName, serverURL, baseDirectory, identityProvider);
			HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)target.getContentAdapter();
			String dataSet = hibernateContentAdapter.getType();
			ISchema schema = hibernateContentAdapter.getSchema();
			
			HttpSyncAdapter source = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverURL, meshGroup, dataSet, identityProvider, schema);
			
			SyncEngine syncEngine = new SyncEngine(source, target);
			SyncTask task = new SyncTask(dataSet, syncEngine);
			syncProcess.syncTasks.add(task);
		}

		return syncProcess;
	}

}
