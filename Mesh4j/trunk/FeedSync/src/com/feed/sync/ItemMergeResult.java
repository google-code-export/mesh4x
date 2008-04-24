package com.feed.sync;

import com.feed.sync.model.Item;

public class ItemMergeResult {

	// MODEL VARIABLES
	private Item original;
	private Item incoming;
	private Item proposed;
	private MergeOperation operation;

	// BUSINESS METHODS
	public ItemMergeResult(Item original, Item incoming, Item proposed,
			MergeOperation operation) {
		this.original = original;
		this.incoming = incoming;
		this.proposed = proposed;
		this.operation = operation;
	}

	public Item getOriginal() {
		return original;
	}

	public Item getIncoming() {
		return incoming;
	}

	public Item getProposed() {
		return proposed;
	}

	public MergeOperation getOperation() {
		return operation;
	}

	public boolean isMergeNone() {
		return MergeOperation.None != this.operation;
	}

}
