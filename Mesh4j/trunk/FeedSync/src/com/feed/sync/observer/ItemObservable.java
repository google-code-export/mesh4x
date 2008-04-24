package com.feed.sync.observer;

import java.util.Vector;

import com.feed.sync.model.Item;

public class ItemObservable {
	private Vector<ItemObserver> observers = new Vector<ItemObserver>();

	public void notifyObservers(Item item) {
		ItemObserver[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new ItemObserver[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyItemNovelty(item);
			}
		}
	}

	public synchronized void addObserver(ItemObserver observer) {
		if (observer == null)
			throw new NullPointerException();
		if (!observers.contains(observer)) {
			observers.addElement(observer);
		}
	}

	public synchronized void removeObserver(ItemObserver observer) {
		observers.removeElement(observer);
	}

}
