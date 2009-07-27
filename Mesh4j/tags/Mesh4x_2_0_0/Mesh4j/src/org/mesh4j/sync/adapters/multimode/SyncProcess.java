package org.mesh4j.sync.adapters.multimode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.security.IIdentityProvider;

public class SyncProcess {

	private final static Log LOGGER = LogFactory.getLog(SyncTask.class);

	// MODEL VARIABLES
	private List<ISyncProcessListener> syncProcessListeners = new ArrayList<ISyncProcessListener>();
	private List<SyncTask> syncTasks = new ArrayList<SyncTask>();
	private SyncStatus status = SyncStatus.ReadyToSync;
	
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

	public void synchronize(Date sinceDate) {
		this.status = SyncStatus.Synchronizing;
		boolean error = false;
		boolean failed = false;
		
		for (SyncTask syncTask : this.syncTasks) {
			syncTask.synchronize(sinceDate, this.syncProcessListeners);
			if(syncTask.isError()){
				error = true;
			}else if(syncTask.isFailed()){
				failed = true;
			}
		}
		
		if(error){
			this.status = SyncStatus.Error;
		}else if(failed){
			this.status = SyncStatus.Fail;
		}else{
			this.status = SyncStatus.Successfully;
		}
	}

	public List<SyncTask> getSyncTasks() {
		return Collections.unmodifiableList(this.syncTasks);
	}
	
	// TODO (JMT) Refactoring: Create a Factory class from Factory methods
	public static SyncProcess makeSyncProcessForSyncMsAccessVsHttp(String fileName, String serverURL, String meshGroup, IIdentityProvider identityProvider, String baseDirectory, ISyncProcessListener... syncProcessListeners) {
		SyncProcess syncProcess = new SyncProcess();
		if(syncProcessListeners != null){
			syncProcess.syncProcessListeners.addAll(Arrays.asList(syncProcessListeners));
		}

		Set<String> tables = null;
		try{
			tables = MsAccessHibernateSyncAdapterFactory.getTableNames(fileName);
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			syncProcess.notifyErrorReadingMsAccessTables();
			return null;
		}
		return makeSyncProcessForSyncMsAccessVsHttp(syncProcess, fileName, tables, serverURL, meshGroup, identityProvider, baseDirectory, syncProcessListeners); 
	}
	
	public static SyncProcess makeSyncProcessForSyncMsAccessVsHttp(String fileName, Set<String> tables, String serverURL, String meshGroup, IIdentityProvider identityProvider, String baseDirectory, ISyncProcessListener... syncProcessListeners) {
		SyncProcess syncProcess = new SyncProcess();
		if(syncProcessListeners != null){
			syncProcess.syncProcessListeners.addAll(Arrays.asList(syncProcessListeners));
		}
		
		return makeSyncProcessForSyncMsAccessVsHttp(syncProcess, fileName, tables, serverURL, meshGroup, identityProvider, baseDirectory, syncProcessListeners);
	}
	
	private static SyncProcess makeSyncProcessForSyncMsAccessVsHttp(SyncProcess syncProcess, String fileName, Set<String> tables, String serverURL, String meshGroup, IIdentityProvider identityProvider, String baseDirectory, ISyncProcessListener... syncProcessListeners) {
		syncProcess.notifyDataSources(tables);
		
		for (String tableName : tables) {
			syncProcess.notifyCreatingMsAccessSyncAdapter(tableName);
			
			SplitAdapter source = null;
			try{
				source = MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(fileName, tableName, serverURL, baseDirectory, identityProvider);
			}catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
				syncProcess.notifyErrorCreatingMsAccessAdapter(tableName);
			}
			
			if(source != null){
				HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)source.getContentAdapter();
				String dataSet = hibernateContentAdapter.getType();
				ISchema schema = hibernateContentAdapter.getSchema();
	
				String url = serverURL+"/"+meshGroup+"/"+dataSet;
				syncProcess.notifyCreatingCloudSyncAdapter(tableName, url);
				
				HttpSyncAdapter target =null;
				try{
					target = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverURL, meshGroup, dataSet, identityProvider, schema);
				}catch (Throwable e) {
					LOGGER.error(e.getMessage(), e);
					syncProcess.notifyErrorCreatingHttpAdapter(tableName, url);
					source.endSync();
				}
				
				if(target != null){
					SyncEngine syncEngine = new SyncEngine(source, target);
					SyncTask task = new SyncTask(tableName, syncEngine);
					syncProcess.syncTasks.add(task);
					task.notifyStatus(syncProcess.syncProcessListeners);
				}
			}
		}

		return syncProcess;
	}
	
	public static SyncProcess makeSyncProcessForSyncMySqlVsHttp(String user, String password, String hostName, int portNo, String databaseName, Set<String> tables, String serverURL, String meshGroup, IIdentityProvider identityProvider, String baseDirectory, ISyncProcessListener... syncProcessListeners) {
		SyncProcess syncProcess = new SyncProcess();
		if(syncProcessListeners != null){
			syncProcess.syncProcessListeners.addAll(Arrays.asList(syncProcessListeners));
		}
		return makeSyncProcessForSyncMySqlVsHttp(syncProcess, user, password, hostName, portNo, databaseName, tables, serverURL, meshGroup, identityProvider, baseDirectory, syncProcessListeners);
	}
	
	private static SyncProcess makeSyncProcessForSyncMySqlVsHttp(SyncProcess syncProcess, String user, String password, String hostName, int portNo, String databaseName, Set<String> tables, String serverURL, String meshGroup, IIdentityProvider identityProvider, String baseDirectory, ISyncProcessListener... syncProcessListeners) {
		syncProcess.notifyDataSources(tables);
		
		for (String tableName : tables) {
			syncProcess.notifyCreatingMySqlSyncAdapter(tableName);
			
			SplitAdapter source = null;
			try{
				String connectionURL = "jdbc:mysql://" + hostName + ":" + portNo + "/" + databaseName;
				source = HibernateSyncAdapterFactory.createHibernateAdapter(
					connectionURL, 
					user, 
					password, 
					com.mysql.jdbc.Driver.class,
					org.hibernate.dialect.MySQLDialect.class, 
					tableName, 
					serverURL, 
					baseDirectory, 
					identityProvider, 
					null);
			}catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
				syncProcess.notifyErrorCreatingMySqlAdapter(tableName);
			}
			
			if(source != null){
				HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)source.getContentAdapter();
				String dataSet = hibernateContentAdapter.getType();
				ISchema schema = hibernateContentAdapter.getSchema();
	
				String url = serverURL+"/"+meshGroup+"/"+dataSet;
				syncProcess.notifyCreatingCloudSyncAdapter(tableName, url);

				HttpSyncAdapter target =null;
				try{
					target = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverURL, meshGroup, dataSet, identityProvider, schema);
				}catch (Throwable e) {
					LOGGER.error(e.getMessage(), e);
					syncProcess.notifyErrorCreatingHttpAdapter(tableName, url);
					source.endSync();
				}
				
				if(target != null){
					SyncEngine syncEngine = new SyncEngine(source, target);
					SyncTask task = new SyncTask(tableName, syncEngine);
					syncProcess.syncTasks.add(task);
					task.notifyStatus(syncProcess.syncProcessListeners);
				}
			}
		}

		return syncProcess;
	}

	private void notifyErrorCreatingHttpAdapter(String tableName, String url) {
		this.status = SyncStatus.Error;
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyErrorCreatingHttpAdapter(tableName, url);
			}
		}
	}
	
	private void notifyErrorCreatingMySqlAdapter(String tableName) {
		this.status = SyncStatus.Error;
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyErrorCreatingMySqlAdapter(tableName);
			}
		}
	}
	
	private void notifyErrorCreatingMsAccessAdapter(String tableName) {
		this.status = SyncStatus.Error;
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyErrorCreatingMsAccessAdapter(tableName);
			}
		}
	}
	
	private void notifyErrorReadingMsAccessTables() {
		this.status = SyncStatus.Error;
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyErrorReadingMsAccessTables();
			}
		}
	}

	private void notifyCreatingCloudSyncAdapter(String tableName, String url) {
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyCreatingCloudSyncAdapter(tableName, url);
			}
		}		
	}

	private void notifyCreatingMsAccessSyncAdapter(String tableName) {
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyCreatingMsAccessSyncAdapter(tableName);
			}
		}
	}
	
	private void notifyCreatingMySqlSyncAdapter(String tableName) {
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.notifyCreatingMySqlSyncAdapter(tableName);
			}
		}
	}

	protected void notifyDataSources(Set<String> dataSources) {
		if(this.syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : this.syncProcessListeners) {
				syncProcessListener.syncProcessDataSourcesNotification(dataSources);
			}
		}
	}

	public SyncStatus getStatus() {
		return this.status;
	}

	public void unregisterListener(ISyncProcessListener syncListener) {
		this.syncProcessListeners.remove(syncListener);
	}

	public SyncTask getSyncTask(SyncEngine syncEngine) {
		for (SyncTask syncTask : this.syncTasks) {
			if(syncTask.getSyncEngine().equals(syncEngine)){
				return syncTask;
			}
		}
		return null;
	}
}
