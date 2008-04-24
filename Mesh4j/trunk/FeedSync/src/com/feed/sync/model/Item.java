package com.feed.sync.model;

import com.feed.sync.validations.Guard;

public class Item implements Cloneable{
	
	// MODEL VARIABLES
	private IModelItem modelItem;
	private Sync sync;

	// BUSINESS METHODS
	public Item(IModelItem modelItem, Sync sync)
	{
		Guard.argumentNotNull(sync, "sync");

		if (modelItem == null)
			this.modelItem = new NullModelItem(sync.getId());
		else
			this.modelItem = modelItem;

		this.sync = sync;
	}

	public Sync getSync()
	{
		return sync;
	}

	public IModelItem getModelItem()
	{
		return this.modelItem;
	}


	public String getSyncId() {
		return this.getSync().getId();
	}

	public boolean hasSyncConflicts() {
		return this.getSync().getConflicts().size() > 0;
	}

	public History getLastUpdate(){
		return this.getSync().getLastUpdate();
	}

	public boolean isSubsumedBy(Item item) {
		History Hx = this.getLastUpdate();
		for(History Hy : item.getSync().getUpdatesHistory())
		{
			if (Hx.IsSubsumedBy(Hy))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj != null)
		{
			if(obj instanceof Item){
				Item otherItem = (Item) obj;
				
				// If both model items are null, we check for sync equality.
				if (this.getModelItem() == null && otherItem.getModelItem() == null)
				{
					return this.getSync().equals(otherItem.getSync());
				}
				else if (this.getModelItem() != null && otherItem.getModelItem() !=  null)
				{
					return 
						this.getModelItem().equals(otherItem.getModelItem()) &&
						this.getSync().equals(otherItem.getSync());
				}
			}
		}

		return false;
	}

	public int hashCode()
	{
		int hash = ((modelItem == null) ? 0 : modelItem.hashCode());
		hash = ((sync == null) ? hash : hash ^ sync.hashCode());
		return hash;
	}

	public Item clone() {
		IModelItem cloneModelItem = null;
		Sync cloneSync = null;
		if (modelItem != null) {
			cloneModelItem = modelItem.clone();
		}
		if (sync != null) {
			cloneSync = sync.clone();
		}

		return new Item(cloneModelItem, cloneSync);
	}


}
