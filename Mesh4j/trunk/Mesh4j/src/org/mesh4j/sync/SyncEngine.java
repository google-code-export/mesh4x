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

	// BUSINESS METHODS
	public SyncEngine(ISyncAdapter source, ISyncAdapter target) {
		super();

		Guard.argumentNotNull(source, "left");
		Guard.argumentNotNull(target, "right");

		this.source = source;
		this.target = target;
	}

	// / <summary>
	// / Performs a full sync between the two repositories, automatically
	// / incorporating changes in both.
	// / </summary>
	// / <remarks>
	// / Items on the source repository are sent first, and then the
	// / changes from the target repository are incorporated into the source.
	// / </remarks>
	// / <returns>The list of items that had conflicts.</returns>
	public List<Item> synchronize() {
		return synchronize(null, NullPreviewHandler.INSTANCE,
				PreviewBehavior.None);
	}

	// / <summary>
	// / Performs a full sync between the two repositories, optionally calling
	// the
	// / given <paramref name="previewer"/> callback as specified by the
	// <paramref name="behavior"/> argument.
	// / </summary>
	// / <remarks>
	// / Items on the source repository are sent first, and then the
	// / changes from the target repository are incorporated into the source.
	// / </remarks>
	// / <returns>The list of items that had conflicts.</returns>
	public List<Item> synchronize(IPreviewImportHandler previewer,
			PreviewBehavior behavior) {
		return synchronize(null, previewer, behavior);
	}

	// / <summary>
	// / Performs a partial sync between the two repositories since the
	// specified date, automatically
	// / incorporating changes in both.
	// / </summary>
	// / <param name="since">Synchronize changes that happened after this
	// date.</param>
	// / <remarks>
	// / Items on the source repository are sent first, and then the
	// / changes from the target repository are incorporated into the source.
	// / </remarks>
	// / <returns>The list of items that had conflicts.</returns>
	public List<Item> synchronize(Date since) {
		return synchronize(since, NullPreviewHandler.INSTANCE,
				PreviewBehavior.None);
	}

	// / <summary>
	// / Performs a partial sync between the two repositories since the
	// specified date, optionally calling the
	// / given <paramref name="previewer"/> callback as specified by the
	// <paramref name="behavior"/> argument.
	// / </summary>
	// / <param name="since">Synchronize changes that happened after this
	// date.</param>
	// / <remarks>
	// / Items on the source repository are sent first, and then the
	// / changes from the target repository are incorporated into the source.
	// / </remarks>
	// / <returns>The list of items that had conflicts.</returns>
	public List<Item> synchronize(Date since, IPreviewImportHandler previewer,
			PreviewBehavior behavior) {

		Guard.argumentNotNull(previewer, "previewer");

		this.beginSync();
		List<Item> result = null;
		
		this.syncTrace.notifyGetAll(this, source, since);
		//collect all the eligible source items for sync operation
		List<Item> sourceItems = (since == null) ? source.getAll() : source.getAllSince(since);
		//prepare list of outgoing items and notify the related observer if any
		List<Item> outgoingItems = this.enumerateItemsProgress(sourceItems, this.itemSent);
				
		if (target instanceof ISupportMerge) {
			this.syncTrace.notifyMerge(this, target, outgoingItems);
			ISupportMerge targetMerge = (ISupportMerge) target;
			targetMerge.merge(outgoingItems);
		} else {
			//prepare merge result for the outgoing items (w.r.t target repository) that includes new/updated/conflicted items
			List<MergeResult> outgoingToMerge = this.mergeItems(outgoingItems, target);
			if (behavior == PreviewBehavior.Right || behavior == PreviewBehavior.Both) {
				outgoingToMerge = previewer.preview(target, outgoingToMerge);
			}
			//update the target repository with outgoingToMerge result
			this.importItems(outgoingToMerge, target);
		}

		this.syncTrace.notifyGetAll(this, target, since);
		//collect all the eligible target items for sync operation
		List<Item> targetItmes = (since == null) ? target.getAll() : target.getAllSince(since);
		//prepare list of incoming items and notify the related observer if any
		List<Item> incomingItems = this.enumerateItemsProgress(targetItmes, this.itemReceived);
				
		if (source instanceof ISupportMerge) {
			this.syncTrace.notifyMerge(this, source, incomingItems);
			// If repository supports its own SSE merge behavior, don't apply it locally.
			ISupportMerge sourceMerge = (ISupportMerge) source;
			result = sourceMerge.merge(incomingItems);
		} else {
			//prepare merge result for the incoming items (w.r.t source repository) that includes new/updated/conflicted items
			List<MergeResult> incomingToMerge = this.mergeItems(incomingItems, source);
			if (behavior == PreviewBehavior.Left || behavior == PreviewBehavior.Both) {
				incomingToMerge = previewer.preview(source, incomingToMerge);
			}

			//update the source repository with incomingToMerge result
			result = this.importItems(incomingToMerge, source);
		}
		
		this.endSync(result);				
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
