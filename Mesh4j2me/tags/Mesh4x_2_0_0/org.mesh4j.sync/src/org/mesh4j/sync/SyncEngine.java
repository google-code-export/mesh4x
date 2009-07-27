package org.mesh4j.sync;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.observer.IObserverItem;
import org.mesh4j.sync.observer.ObservableItem;
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
	public Vector<Item> synchronize() {
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
	// / <returns>The Vector of items that had conflicts.</returns>
	public Vector<Item> synchronize(IPreviewImportHandler previewer,
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
	// / <returns>The Vector of items that had conflicts.</returns>
	public Vector<Item> synchronize(Date since) {
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
	// / <returns>The Vector of items that had conflicts.</returns>
	public Vector<Item> synchronize(Date since, IPreviewImportHandler previewer,
			PreviewBehavior behavior) {

		Guard.argumentNotNull(previewer, "previewer");

		this.beginSync();
		Vector<Item> result = null;
		Vector<Item> sourceItems = (since == null) ? source.getAll() : source.getAllSince(since);
		Vector<Item> outgoingItems = this.enumerateItemsProgress(sourceItems, this.itemSent);

		if (target instanceof ISupportMerge) {
			ISupportMerge targetMerge = (ISupportMerge) target;
			targetMerge.merge(outgoingItems);
		} else {
			Vector<MergeResult> outgoingToMerge = this.mergeItems(outgoingItems, target);
			if (behavior == PreviewBehavior.Right || behavior == PreviewBehavior.Both) {
				outgoingToMerge = previewer.preview(target, outgoingToMerge);
			}
			this.importItems(outgoingToMerge, target);
		}

		Vector<Item> targetItmes = (since == null) ? target.getAll() : target.getAllSince(since);
		Vector<Item> incomingItems = this.enumerateItemsProgress(targetItmes, this.itemReceived);

		if (source instanceof ISupportMerge) {
			// If repository supports its own SSE merge behavior, don't apply it locally.
			ISupportMerge sourceMerge = (ISupportMerge) source;
			result = sourceMerge.merge(incomingItems);				
		} else {
			Vector<MergeResult> incomingToMerge = this.mergeItems(incomingItems, source);
			if (behavior == PreviewBehavior.Left || behavior == PreviewBehavior.Both) {
				incomingToMerge = previewer.preview(source, incomingToMerge);
			}

			result = this.importItems(incomingToMerge, source);
		}
		this.endSync();	
			
		return result;
	}

	private Vector<MergeResult> mergeItems(Vector<Item> items,
			ISyncAdapter repository) {

		Vector<MergeResult> mergeResult = new Vector<MergeResult>();
		for (Item incoming : items) {
			Item original = repository.get(incoming.getSyncId());
			MergeResult result = MergeBehavior.merge(original, incoming);

			if (!result.isMergeNone()) {
				mergeResult.addElement(result);
			}
		}
		return mergeResult;
	}

	private Vector<Item> importItems(Vector<MergeResult> items,
			ISyncAdapter repository) {
		// Straight import of data in merged results.
		// Conflicting items are saved and also
		// are returned for conflict resolution by the user or
		// a custom component. MergeBehavior determines
		// the winner element that is saved.
		// Conflicts are returned in a Vector because we need
		// the full iteration over the merged items to be
		// processed. If we returned an IEnumerable, we would
		// depend on the client iterating it in order to
		// actually import items, which is undesirable.
		Vector<Item> conflicts = new Vector<Item>();

		for (MergeResult result : items) {			
			if (!result.isMergeNone() && result.getProposed() != null
					&& result.getProposed().hasSyncConflicts()) {
				conflicts.addElement(result.getProposed());
			}

			if (result.getOperation() == null
					|| result.getOperation().isRemoved()) {
				Guard.throwsException("UnsupportedOperation");						// TODO (JMT) resource bundle UnsupportedOperation
			} else if (result.getOperation().isAdded()) {
				repository.add(result.getProposed());
			} else if (result.getOperation().isUpdated()
					|| result.getOperation().isConflict()) {
				repository.update(result.getProposed());
			}
		}

		return conflicts;
	}

	private Vector<Item> enumerateItemsProgress(Vector<Item> items,
			ObservableItem observable) {
		Vector<Item> result = new Vector<Item>();
		for (Item item : items) {
			result.addElement(item);
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
	
	private void beginSync(){
		if(this.source instanceof ISyncAware){
			((ISyncAware)this.source).beginSync();
		}
		
		if(this.target instanceof ISyncAware){
			((ISyncAware)this.target).beginSync();
		}
	}

	private void endSync(){
		if(this.source instanceof ISyncAware){
			((ISyncAware)this.source).endSync();
		}
		
		if(this.target instanceof ISyncAware){
			((ISyncAware)this.target).endSync();
		}		
	}
}
