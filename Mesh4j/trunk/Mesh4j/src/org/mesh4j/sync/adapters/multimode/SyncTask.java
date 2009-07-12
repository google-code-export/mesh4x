package org.mesh4j.sync.adapters.multimode;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class SyncTask {

	// MODEL VARIABLES
	private String dataSource;
	private SyncEngine syncEngine;
	private SyncTaskStatus status = SyncTaskStatus.ReadyToSync;
	private List<Item> conflicts = new ArrayList<Item>(); 
	private Throwable error;
	
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

	public SyncTaskStatus getStatus() {
		return this.status;
	}

	public SyncEngine getSyncEngine() {
		return this.syncEngine;
	}

	public List<Item> getConflicts() {
		return this.conflicts;
	}

	protected void synchronize(){
		this.status = SyncTaskStatus.Synchronizing;
		try{
			this.conflicts = this.syncEngine.synchronize();
			if(this.conflicts.isEmpty()){
				this.status = SyncTaskStatus.Successfully;
			} else {
				this.status = SyncTaskStatus.Fail;
			}
		} catch(Throwable e){
			this.status = SyncTaskStatus.Error;
			this.error = e;
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
}
