package com.mesh4j.sync.merge;

import com.mesh4j.sync.model.Item;

public class MergeResult {

	// MODEL VARIABLES
	private Item original;
	private Item incoming;
	private Item proposed;
	private MergeOperation operation;

	// BUSINESS METHODS
	public MergeResult(Item original, Item incoming, Item proposed,
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
		return MergeOperation.None == this.operation;
	}

}
