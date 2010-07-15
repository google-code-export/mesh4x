package org.mesh4j.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.observer.IObserverItem;
import org.mesh4j.sync.observer.IObserverSyncProcess;
import org.mesh4j.sync.observer.ObservableItem;
import org.mesh4j.sync.observer.ObservableSyncProcess;
import org.mesh4j.sync.validations.Guard;


/**
 * Main class that performs synchronization between two repositories.
 * 
 * @author jtondato
 */
public class SyncEngine {

	// MODEL VARIABLES
	private ObservableItem itemReceived = new ObservableItem();
	private ObservableItem itemSent = new ObservableItem();
	private ObservableSyncProcess syncTrace = new ObservableSyncProcess();

	private ISyncAdapter source;
	private ISyncAdapter target;
	
	private IPreviewImportHandler previewer = NullPreviewHandler.INSTANCE;
	private PreviewBehavior behavior = PreviewBehavior.None;

	// BUSINESS METHODS
	public SyncEngine(ISyncAdapter source, ISyncAdapter target) {
		super();

		Guard.argumentNotNull(source, "left");
		Guard.argumentNotNull(target, "right");

		this.source = source;
		this.target = target;
	}

	public void setPreviewer(IPreviewImportHandler previewer) {
		this.previewer = previewer;
	}

	public IPreviewImportHandler getPreviewer() {
		return previewer;
	}

	public void setPreviewBehavior(PreviewBehavior behavior) {
		this.behavior = behavior;
	}

	public PreviewBehavior getPreviewBehavior() {
		return behavior;
	}

	/**
	 * Performs a full sync between the two repositories, automatically
	 * incorporating changes in both.
	 * 
	 * Items on the source repository are sent first, and then the
	 * changes from the target repository are incorporated into the source.
	 * 
	 * @return The list of items that had conflicts.
	 */
	public List<Item> synchronize() {
		return synchronize(null);
	}

	/**
	 * Performs a partial sync between the two repositories since the 
	 * specified date.
	 * 
	 * @param since Synchronize changes that happened after this date
	 * @return The list of items that had conflicts.
	 */
	public List<Item> synchronize(Date since) {

		this.beginSync();
		
		// Sync items from source to target
		sync(source, target, since, this.itemSent, behavior == PreviewBehavior.Right || behavior == PreviewBehavior.Both ? previewer : null);
		
		// Sync items from target to source
		List<Item> result = sync(target, source, since, this.itemReceived, behavior == PreviewBehavior.Left || behavior == PreviewBehavior.Both ? previewer : null);
		
		this.endSync(result);				
		return result;
	}
	
	private List<Item> sync(ISyncAdapter from, ISyncAdapter to, Date since, ObservableItem observable, IPreviewImportHandler previewer) {
		
		this.syncTrace.notifyGetAll(this, from, since);
		//collect all the eligible source items for sync operation
		List<Item> sourceItems = (since == null) ? from.getAll() : from.getAllSince(since);
		//prepare list of outgoing items and notify the related observer if any
		List<Item> outgoingItems = this.enumerateItemsProgress(sourceItems, observable);
		List<Item> result;
		
		if (to instanceof ISupportMerge) {
			this.syncTrace.notifyMerge(this, to, outgoingItems);
			ISupportMerge targetMerge = (ISupportMerge) to;
			
			result = targetMerge.merge(outgoingItems);
		} else {
			//prepare merge result for the outgoing items (w.r.t target repository) that includes new/updated/conflicted items
			List<MergeResult> outgoingToMerge = this.mergeItems(outgoingItems, to);
			if (previewer != null) {
				outgoingToMerge = previewer.preview(to, outgoingToMerge);
			}
			//update the target repository with outgoingToMerge result
			result = this.importItems(outgoingToMerge, to);
		}
		return result;
	}

	private List<MergeResult> mergeItems(List<Item> items, ISyncAdapter repository) {
		ArrayList<MergeResult> mergeResult = new ArrayList<MergeResult>();
		for (Item incoming : items) {
			this.syncTrace.notifyGetAndMergeItem(this, repository, incoming, items);
			Item original = repository.get(incoming.getSyncId());
			MergeResult result = MergeBehavior.merge(original, incoming);

			if (!result.isMergeNone()) {
				mergeResult.add(result);
			}
		}
		return mergeResult;
	}

	private List<Item> importItems(List<MergeResult> items, ISyncAdapter repository) {
		// Straight import of data in merged results.
		// Conflicting items are saved and also
		// are returned for conflict resolution by the user or
		// a custom component. MergeBehavior determines
		// the winner element that is saved.
		// Conflicts are returned in a list because we need
		// the full iteration over the merged items to be
		// processed. If we returned an IEnumerable, we would
		// depend on the client iterating it in order to
		// actually import items, which is undesirable.
		ArrayList<Item> conflicts = new ArrayList<Item>();

		for (MergeResult result : items) {			
			if (!result.isMergeNone() && result.getProposed() != null
					&& result.getProposed().hasSyncConflicts()) {
				conflicts.add(result.getProposed());
			}

			if (result.getOperation() == null
					|| result.getOperation().isRemoved()) {
				throw new UnsupportedOperationException();
			} else if (result.getOperation().isAdded()) {
				this.syncTrace.notifyAddItem(this, repository, result, items);
				repository.add(result.getProposed());
			} else if (result.getOperation().isUpdated()
					|| result.getOperation().isConflict()) {
				this.syncTrace.notifyUpdateItem(this, repository, result, items);
				repository.update(result.getProposed());
			}
		}

		return conflicts;
	}

	private List<Item> enumerateItemsProgress(List<Item> items,
			ObservableItem observable) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (Item item : items) {
			result.add(item);
			observable.notifyObservers(item);
		}
		return result;
	}

	public void registerItemReceivedObserver(IObserverItem ... observers) {
		for (IObserverItem itemObserver : observers) {
			this.itemReceived.addObserver(itemObserver);	
		}
	}

	public void removeItemReceivedObserver(IObserverItem ... observers) {
		for (IObserverItem itemObserver : observers) {
			this.itemReceived.removeObserver(itemObserver);
		}
	}

	public void registerItemSentObserver(IObserverItem ... observers) {
		for (IObserverItem itemObserver : observers) {
			this.itemSent.addObserver(itemObserver);
		}
	}

	public void removeItemSentObserver(IObserverItem ... observers) {
		for (IObserverItem itemObserver : observers) {
			this.itemSent.removeObserver(itemObserver);
		}
	}
	
	public void registerSyncTraceObserver(IObserverSyncProcess ... observers) {
		for (IObserverSyncProcess observer : observers) {
			this.syncTrace.addObserver(observer);
		}
	}

	public void removeSyncTraceObserver(IObserverSyncProcess ... observers) {
		for (IObserverSyncProcess observer : observers) {
			this.syncTrace.removeObserver(observer);
		}
	}
	
	private void beginSync(){
		this.syncTrace.notifyBeginSync(this);
		
		if(this.source instanceof ISyncAware){
			((ISyncAware)this.source).beginSync();
		}
		
		if(this.target instanceof ISyncAware){
			((ISyncAware)this.target).beginSync();
		}
	}

	private void endSync(List<Item> conflicts){
		this.syncTrace.notifyEndSync(this, conflicts);
		
		if(this.source instanceof ISyncAware){
			((ISyncAware)this.source).endSync();
		}
		
		if(this.target instanceof ISyncAware){
			((ISyncAware)this.target).endSync();
		}		
	}
	
	public ISyncAdapter getSource(){
		return this.source;
	}

	public ISyncAdapter getTarget(){
		return this.target;
	}

	public boolean isSource(ISyncAdapter adapter) {
		return this.source == adapter;
	}
}
