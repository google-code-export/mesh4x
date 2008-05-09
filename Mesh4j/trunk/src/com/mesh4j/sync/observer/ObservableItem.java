package com.mesh4j.sync.observer;

import java.util.Vector;

import com.mesh4j.sync.model.Item;

public class ObservableItem {
	
	private Vector<IObserverItem> observers = new Vector<IObserverItem>();

	public void notifyObservers(Item item) {
		IObserverItem[] arrLocal;
		synchronized (this) {
			arrLocal = observers.toArray(new IObserverItem[0]);
			for (int i = arrLocal.length - 1; i >= 0; i--){
				(arrLocal[i]).notifyItemNovelty(item);
			}
		}
	}

	public synchronized void addObserver(IObserverItem observer) {
		if (observer == null)
			throw new NullPointerException();
		if (!observers.contains(observer)) {
			observers.addElement(observer);
		}
	}

	public synchronized void removeObserver(IObserverItem observer) {
		observers.removeElement(observer);
	}

}
