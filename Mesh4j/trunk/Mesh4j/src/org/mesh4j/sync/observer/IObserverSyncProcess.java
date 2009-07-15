package org.mesh4j.sync.observer;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;


public interface IObserverSyncProcess{

	public void notifyBeginSync(SyncEngine syncEngine);
	
	public void notifyGetAll(SyncEngine syncEngine, ISyncAdapter adapter, Date since);
	public void notifyGetAndMergeItem(SyncEngine syncEngine, ISyncAdapter adapter, Item incoming, List<Item> items);
	public void notifyAddItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items);
	public void notifyUpdateItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items);

	public void notifyMerge(SyncEngine syncEngine, ISyncAdapter adapter, List<Item> itemsToMerge);
	
	public void notifyEndSync(SyncEngine syncEngine, List<Item> conflicts);

}
