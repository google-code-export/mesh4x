package org.mesh4j.sync.model;

import org.mesh4j.sync.validations.Guard;

public class Item implements Cloneable{
	
	// MODEL VARIABLES
	private IContent content;
	private Sync sync;

	// BUSINESS METHODS	
	public Item(IContent content, Sync sync)
	{
		Guard.argumentNotNull(sync, "sync");

		if (content == null)
			this.content = new NullContent(sync.getId());
		else
			this.content = content;

		this.sync = sync;
	}

	public Sync getSync()
	{
		return sync;
	}

	public IContent getContent()
	{
		return this.content;
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

	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj != null)
		{
			if(obj instanceof Item){
				Item otherItem = (Item) obj;
				
				// If both model items are null, we check for sync equality.
				if (this.getContent() == null && otherItem.getContent() == null)
				{
					return this.getSync().equals(otherItem.getSync());
				}
				else if (this.getContent() != null && otherItem.getContent() !=  null)
				{
					return 
						this.getContent().equals(otherItem.getContent()) &&
						this.getSync().equals(otherItem.getSync());
				}
			}
		}

		return false;
	}

	public int hashCode()
	{
		int hash = ((content == null) ? 0 : content.hashCode());
		hash = ((sync == null) ? hash : hash ^ sync.hashCode());
		return hash;
	}

	public Item clone() {
		IContent contentClone = null;
		Sync cloneSync = null;
		if (content != null) {
			contentClone = content.clone();
		}
		if (sync != null) {
			cloneSync = sync.clone();
		}

		return new Item(contentClone, cloneSync);
	}

	public boolean isDeleted() {
		return this.getSync() != null && this.getSync().isDeleted();
	}
}
