package org.mesh4j.sync.merge;

import java.util.Vector;

import org.mesh4j.sync.model.Item;


public class MergeProcessParameters {

	private Vector<Item> outerCollection = new Vector<Item>();
	private Vector<Item> innerCollection = new Vector<Item>();
	private Vector<Item> mergedCollection = new Vector<Item>();
	private Item winner;

	public Vector<Item> getOuterCollection() {
		return outerCollection;
	}

	public void setOuterCollection(Vector<Item> outerCollection) {
		this.outerCollection = outerCollection;
	}

	public Vector<Item> getInnerCollection() {
		return innerCollection;
	}

	public void setInnerCollection(Vector<Item> innerCollection) {
		this.innerCollection = innerCollection;
	}

	public Item getWinner() {
		return winner;
	}

	public void setWinner(Item winner) {
		this.winner = winner;
	}

	public Vector<Item> getMergedCollection() {
		return mergedCollection;
	}

	public void setMergedCollection(Vector<Item> mergedCollection) {
		this.mergedCollection = mergedCollection;
	}

	public void interchangeInnerWithOuter() {
		Vector<Item> aux = this.getInnerCollection();
		this.setInnerCollection(this.getOuterCollection());
		this.setOuterCollection(aux);

	}

}
