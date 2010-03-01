package org.mesh4j.sync.observer;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;


public class ObservableSyncProcess {
	
	private Vector<IObserverSyncProcess> observers = new Vector<IObserverSyncProcess>();

	public synchronized void addObserver(IObserverSyncProcess observer) {
		if (observer == null)
			throw new NullPointerException();
		if (!observers.contains(observer)) {
			observers.addElement(observer);
		}
	}

	public synchronized void removeObserver(IObserverSyncProcess observer) {
		observers.removeElement(observer);
	}

	public void notifyGetAll(SyncEngine syncEngine, ISyncAdapter adapter, Date since) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyGetAll(syncEngine, adapter, since);
			}
		}
		
	}

	public void notifyMerge(SyncEngine syncEngine, ISyncAdapter adapter, List<Item> itemsToMerge) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyMerge(syncEngine, adapter, itemsToMerge);
			}
		}
	}


	public void notifyGetAndMergeItem(SyncEngine syncEngine, ISyncAdapter adapter, Item incoming, List<Item> items) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyGetAndMergeItem(syncEngine, adapter, incoming, items);
			}
		}
		
	}

	public void notifyAddItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyAddItem(syncEngine, adapter, result, items);
			}
		}
		
	}

	public void notifyUpdateItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyUpdateItem(syncEngine, adapter, result, items);
			}
		}
		
	}

	public void notifyBeginSync(SyncEngine syncEngine) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyBeginSync(syncEngine);
			}
		}
		
	}

	public void notifyEndSync(SyncEngine syncEngine, List<Item> conflicts) {
		IObserverSyncProcess[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverSyncProcess[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyEndSync(syncEngine, conflicts);
			}
		}
		
	}

}
