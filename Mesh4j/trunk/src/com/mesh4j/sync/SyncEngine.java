package com.mesh4j.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.merge.MergeBehavior;
import com.mesh4j.sync.merge.MergeResult;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.observer.ItemObservable;
import com.mesh4j.sync.observer.ItemObserver;
import com.mesh4j.sync.validations.Guard;

/**
 * Main class that performs synchronization between two repositories.
 * 
 * @author jtondato
 */
public class SyncEngine {

	// MODEL VARIABLES
	private ItemObservable itemReceivedObservable = new ItemObservable();
	private ItemObservable itemSentObservable = new ItemObservable();

	private RepositoryAdapter source;
	private RepositoryAdapter target;

	// BUSINESS METHODS
	public SyncEngine(RepositoryAdapter source, RepositoryAdapter target) {   // TODO (JMT) spike SyncEngine<T>
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
	public List<Item> synchronize(PreviewImportHandler previewer,
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
	public List<Item> synchronize(Date since, PreviewImportHandler previewer,
			PreviewBehavior behavior) {

		Guard.argumentNotNull(previewer, "previewer");

		List<Item> sourceItems = (since == null) ? source.getAll() : source.getAllSince(since);
		List<Item> outgoingItems = this.enumerateItemsProgress(sourceItems, this.itemSentObservable);

		if (!target.supportsMerge()) {
			List<MergeResult> outgoingToMerge = this.mergeItems(outgoingItems, target);
			if (behavior == PreviewBehavior.Right || behavior == PreviewBehavior.Both) {
				outgoingToMerge = previewer.preview(target, outgoingToMerge);
			}
			this.importItems(outgoingToMerge, target);
		} else {
			target.merge(outgoingItems);
		}

		List<Item> targetItmes = (since == null) ? target.getAll() : target.getAllSince(since);
		List<Item> incomingItems = this.enumerateItemsProgress(targetItmes, this.itemReceivedObservable);

		if (!source.supportsMerge()) {
			List<MergeResult> incomingToMerge = this.mergeItems(incomingItems, source);
			if (behavior == PreviewBehavior.Left || behavior == PreviewBehavior.Both) {
				incomingToMerge = previewer.preview(source, incomingToMerge);
			}

			return this.importItems(incomingToMerge, source);
		} else {
			// If repository supports its own SSE merge behavior, don't apply it
			// locally.
			return new ArrayList<Item>(source.merge(incomingItems));
		}
	}

	private List<MergeResult> mergeItems(List<Item> items,
			RepositoryAdapter repository) {

		ArrayList<MergeResult> mergeResult = new ArrayList<MergeResult>();
		for (Item incoming : items) {
			Item original = repository.get(incoming.getSyncId());
			MergeResult result = MergeBehavior.merge(original, incoming);

			if (result.isMergeNone()) {
				mergeResult.add(result);
			}
		}
		return mergeResult;
	}

	private List<Item> importItems(List<MergeResult> items,
			RepositoryAdapter repository) {
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
			if (result.isMergeNone() && result.getProposed() != null
					&& result.getProposed().hasSyncConflicts()) {
				conflicts.add(result.getProposed());
			}

			if (result.getOperation() == null
					|| result.getOperation().isRemoved()) {
				throw new UnsupportedOperationException();
			} else if (result.getOperation().isAdded()) {
				repository.add(result.getProposed());
			} else if (result.getOperation().isUpdated()
					|| result.getOperation().isConflict()) {
				repository.update(result.getProposed());
			}
		}

		return conflicts;
	}

	private List<Item> enumerateItemsProgress(List<Item> items,
			ItemObservable observable) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (Item item : items) {
			result.add(item);
			observable.notifyObservers(item);
		}
		return result;
	}

	public void registerItemReceivedObserver(ItemObserver ... observers) {
		for (ItemObserver itemObserver : observers) {
			this.itemReceivedObservable.addObserver(itemObserver);	
		}
	}

	public void removeItemReceivedObserver(ItemObserver ... observers) {
		for (ItemObserver itemObserver : observers) {
			this.itemReceivedObservable.removeObserver(itemObserver);
		}
	}

	public void registerItemSentObserver(ItemObserver ... observers) {
		for (ItemObserver itemObserver : observers) {
			this.itemSentObservable.addObserver(itemObserver);
		}
	}

	public void removeItemSentObserver(ItemObserver ... observers) {
		for (ItemObserver itemObserver : observers) {
			this.itemSentObservable.removeObserver(itemObserver);
		}
	}

}
