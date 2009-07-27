package org.mesh4j.sync.observer;

import java.util.Vector;

import org.mesh4j.sync.model.Item;


public class ObservableItem {
	
	private Vector<IObserverItem> observers = new Vector<IObserverItem>();

	public void notifyObservers(Item item) {
		synchronized (this) {
			for (int i = this.observers.size() - 1; i >= 0; i--){
				this.observers.elementAt(i).notifyItemNovelty(item);
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
