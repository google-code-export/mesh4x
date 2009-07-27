package org.mesh4j.sync.adapters.multimode;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.observer.IObserverSyncProcess;

public class SyncTaskSummary implements IObserverSyncProcess{

	// MODEL VARIABLES
	private SyncTask syncTask;
	private List<ISyncProcessListener> syncProcessListeners;
	
	public SyncTaskSummaryStatus status = SyncTaskSummaryStatus.Ready;
	public String currentSyncId;
	public int currentTotal;
	public int currentIndex;
	public boolean source;
	
	public int sourceAdded = 0;
	public int targetAdded = 0;		
	public int sourceDeleted = 0;
	public int targetDeleted = 0;		
	public int sourceUpdated = 0;
	public int targetUpdated = 0;
	public int sourceConflicts = 0;
	public int targetConflicts = 0;
	
	public boolean sourceSupportMerge = false;
	public int sourceTotalMerge = 0;
	
	public boolean targetSupportMerge = false;
	public int targetTotalMerge = 0;
	
	public int totalConflicts = 0;
	
	// BUSINESS METHODS
	public SyncTaskSummary(SyncTask syncTask, List<ISyncProcessListener> syncProcessListeners) {
		this.syncTask = syncTask;
		this.syncProcessListeners = syncProcessListeners;
	}

	@Override
	public void notifyAddItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items) {
		status = SyncTaskSummaryStatus.Add;
		
		currentSyncId = result.getProposed().getSyncId();
		currentTotal = items.size(); 
		currentIndex = items.indexOf(result)+1;
		
		source = syncEngine.isSource(adapter); 
		if(source){
			sourceAdded = sourceAdded +1;
		}else{
			targetAdded = targetAdded +1;
		}
		notifyStatus();
	}

	@Override
	public void notifyBeginSync(SyncEngine syncEngine) {
		status = SyncTaskSummaryStatus.BeginSync;
		currentSyncId = null;
		currentTotal = 0; 
		currentIndex = 0;

		notifyStatus();
	}

	@Override
	public void notifyEndSync(SyncEngine syncEngine, List<Item> conflicts) {
		status = SyncTaskSummaryStatus.EndSync;
		this.totalConflicts = conflicts.size();
		notifyStatus();
	}

	@Override
	public void notifyGetAll(SyncEngine syncEngine, ISyncAdapter adapter, Date since) {
		status = SyncTaskSummaryStatus.GetAll;
		source = syncEngine.isSource(adapter);
		notifyStatus();
	}

	@Override
	public void notifyGetAndMergeItem(SyncEngine syncEngine, ISyncAdapter adapter, Item item, List<Item> items) {
		status = SyncTaskSummaryStatus.GetAndMergeItem;
		currentSyncId = item.getSyncId();
		currentTotal = items.size(); 
		currentIndex = items.indexOf(item)+1;		
		source = syncEngine.isSource(adapter); 
		notifyStatus();
	}

	@Override
	public void notifyMerge(SyncEngine syncEngine, ISyncAdapter adapter, List<Item> items) {
		status = SyncTaskSummaryStatus.Merge;
		currentTotal = 0; 
		currentIndex = 0;
		currentSyncId = "";
		
		source = syncEngine.isSource(adapter); 
		if(source){
			sourceSupportMerge = true;
			sourceTotalMerge = items.size();
		} else {
			targetSupportMerge = true;
			targetTotalMerge = items.size();
		}
		
		notifyStatus();
	}

	@Override
	public void notifyUpdateItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items) {
		status = SyncTaskSummaryStatus.Update;
		currentTotal = items.size(); 
		currentIndex = items.indexOf(result) + 1;
		currentSyncId = result.getProposed().getSyncId();
		source = syncEngine.isSource(adapter); 
		if(source){
			if(result.getProposed().isDeleted()){
				sourceDeleted = sourceDeleted + 1;
			} else {
				if(result.getOperation().isConflict()){
					sourceConflicts = sourceConflicts +1;
				}else{
					sourceUpdated = sourceUpdated +1;
				}
			}
		}else{
			if(result.getProposed().isDeleted()){
				targetDeleted = targetDeleted + 1;
			} else {
				if(result.getOperation().isConflict()){
					targetConflicts = targetConflicts +1;
				} else {
					targetUpdated = targetUpdated +1;
				}
			}
		}
		notifyStatus();
	}

	private void notifyStatus() {
		this.syncTask.notifyStatus(this.syncProcessListeners);
	}

}
