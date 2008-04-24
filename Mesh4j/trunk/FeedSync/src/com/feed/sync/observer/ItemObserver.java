package com.feed.sync.observer;

import com.feed.sync.model.Item;


public interface ItemObserver{

	public void notifyItemNovelty(Item item);
}
