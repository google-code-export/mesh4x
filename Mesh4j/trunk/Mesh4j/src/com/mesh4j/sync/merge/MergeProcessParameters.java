package com.mesh4j.sync.merge;

import java.util.ArrayList;

import com.mesh4j.sync.model.Item;

public class MergeProcessParameters {

	private ArrayList<Item> outerCollection = new ArrayList<Item>();
	private ArrayList<Item> innerCollection = new ArrayList<Item>();
	private ArrayList<Item> mergedCollection = new ArrayList<Item>();
	private Item winner;

	public ArrayList<Item> getOuterCollection() {
		return outerCollection;
	}

	public void setOuterCollection(ArrayList<Item> outerCollection) {
		this.outerCollection = outerCollection;
	}

	public ArrayList<Item> getInnerCollection() {
		return innerCollection;
	}

	public void setInnerCollection(ArrayList<Item> innerCollection) {
		this.innerCollection = innerCollection;
	}

	public Item getWinner() {
		return winner;
	}

	public void setWinner(Item winner) {
		this.winner = winner;
	}

	public ArrayList<Item> getMergedCollection() {
		return mergedCollection;
	}

	public void setMergedCollection(ArrayList<Item> mergedCollection) {
		this.mergedCollection = mergedCollection;
	}

	public void interchangeInnerWithOuter() {
		ArrayList<Item> aux = this.getInnerCollection();
		this.setInnerCollection(this.getOuterCollection());
		this.setOuterCollection(aux);

	}

}
