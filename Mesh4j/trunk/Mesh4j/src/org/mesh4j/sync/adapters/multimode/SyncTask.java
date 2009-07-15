package org.mesh4j.sync.adapters.multimode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class SyncTask {

	private final static Log LOGGER = LogFactory.getLog(SyncTask.class);
	
	// MODEL VARIABLES
	private String dataSource;
	private SyncEngine syncEngine;
	private SyncStatus status = SyncStatus.ReadyToSync;
	private List<Item> conflicts = new ArrayList<Item>(); 
	private Throwable error;
	private SyncTaskSummary syncSummary;
	
	// BUSINESS METHODS
	protected SyncTask(String dataSource, SyncEngine syncEngine){
		Guard.argumentNotNull(syncEngine, "syncEngine");
		Guard.argumentNotNullOrEmptyString(dataSource, "dataSource");
		
		this.syncEngine = syncEngine;
		this.dataSource = dataSource;
	}
	
	public ISyncAdapter getSource() {
		return this.syncEngine.getSource();
	}

	public ISyncAdapter getTarget() {
		return this.syncEngine.getTarget();
	}

	public SyncStatus getStatus() {
		return this.status;
	}

	public SyncEngine getSyncEngine() {
		return this.syncEngine;
	}

	public List<Item> getConflicts() {
		return this.conflicts;
	}

	protected void synchronize(Date sinceDate, List<ISyncProcessListener> syncProcessListeners){
		this.status = SyncStatus.Synchronizing;
		this.syncSummary = new SyncTaskSummary(this, syncProcessListeners);
		try{
			this.syncEngine.registerSyncTraceObserver(this.syncSummary);
			this.conflicts = this.syncEngine.synchronize(sinceDate);
			if(this.conflicts.isEmpty()){
				this.status = SyncStatus.Successfully;
			} else {
				this.status = SyncStatus.Fail;
			}
		} catch(Throwable e){
			this.status = SyncStatus.Error;
			this.error = e;
			LOGGER.error(e.getMessage(), e);
		}finally{
			this.syncEngine.removeSyncTraceObserver(this.syncSummary);
		}
		this.notifyStatus(syncProcessListeners);
	}
	
	protected void notifyStatus(List<ISyncProcessListener> syncProcessListeners) {
		if(syncProcessListeners != null){
			for (ISyncProcessListener syncProcessListener : syncProcessListeners) {
				syncProcessListener.syncProcessChangeStatusNotification(this);
			}
		}
	}

	public Throwable getError(){
		return this.error;
	}

	public String getDataSource() {
		return this.dataSource;
	}

	public boolean isFailed() {
		return this.status.isFailed();
	}

	public boolean isError() {
		return this.status.isError();
	}

	public boolean isSuccessfully() {
		return this.status.isSuccessfully();
	}

	public boolean isReadyToSync() {
		return this.status.isReadyToSync();
	}

	public SyncTaskSummary getSummary() {
		return this.syncSummary;
	}
	
}
