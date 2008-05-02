package com.mesh4j.sync.observer;

import com.mesh4j.sync.model.Item;


public interface ItemObserver{

	public void notifyItemNovelty(Item item);
}
