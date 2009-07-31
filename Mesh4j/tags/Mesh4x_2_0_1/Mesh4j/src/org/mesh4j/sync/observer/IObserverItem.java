package org.mesh4j.sync.observer;

import org.mesh4j.sync.model.Item;


public interface IObserverItem{

	public void notifyItemNovelty(Item item);

}
