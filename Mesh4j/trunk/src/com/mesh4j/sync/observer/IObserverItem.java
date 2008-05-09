package com.mesh4j.sync.observer;

import com.mesh4j.sync.model.Item;


public interface IObserverItem{

	public void notifyItemNovelty(Item item);
}
