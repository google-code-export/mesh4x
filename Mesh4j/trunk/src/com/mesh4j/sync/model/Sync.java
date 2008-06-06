package com.mesh4j.sync.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.mesh4j.sync.validations.Guard;

public class Sync implements Cloneable{

	// MODEL VARIABLES
	private String id;
	private boolean deleted = false;
	private int updates = 0;
	private boolean noConflicts = false;
	private Stack<History> updatesHistory = new Stack<History>();
	private List<Item> conflicts = new ArrayList<Item>();
	
	// BUSINESS METHODS	
	
	public Sync(String id)
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		this.id = id;
		this.updates = 0;
	}
	
	public Sync(String id, String by, Date when, boolean deleteItem) 
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		if (by == null && when == null){
			Guard.throwsArgumentException("MustProvideWhenOrBy");
		}
			
		this.id = id; 
		this.basicUpdate(by, when, deleteItem);
	}


	public String getId()
	{
		return id;
	}

	public int getUpdates()
	{
		return updates;
	}

	public boolean isDeleted()
	{
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isNoConflicts()
	{
		return noConflicts;
	}

	public History getLastUpdate()
	{
		return this.getUpdatesHistory().size() > 0 ? getUpdatesHistory().peek() : null;
	}

	public Stack<History> getUpdatesHistory()
	{
		return updatesHistory;
	}

	public List<Item> getConflicts()
	{
		return conflicts;
	}

	private Sync increaseUpdates() {
		this.updates = this.updates + 1;
		return this;
	}
	
	public Sync removeConflict(Item item) {
		this.conflicts.remove(item);	
		return this;
	}

	public Sync markWithoutConflicts() {
		this.noConflicts = true;
		return this;
	}

	public Sync markWithConflicts() {
		this.noConflicts = false;
		return this;
	}
	
	public boolean equals(Object obj){
		
		if (this == obj) return true;
		if (obj != null)
		{
			if(obj instanceof Sync){
				Sync otherSync = (Sync) obj;
				return this.getId().equals(otherSync.getId()) &&
					(this.getUpdates() == otherSync.getUpdates()) &&
					(this.isDeleted() == otherSync.isDeleted()) &&
					(this.isNoConflicts() == otherSync.isNoConflicts()) &&					
					areEqualsHistories(this.getUpdatesHistory(), otherSync.getUpdatesHistory());
			}
		}
		return false;
	}
	
	private static boolean areEqualsHistories(Stack<History> updatesHistory, Stack<History> updatesHistory1){
		if (updatesHistory == updatesHistory1) return true;
		if (updatesHistory != null && updatesHistory1 != null)
		{
			if (updatesHistory.size() != updatesHistory1.size()) return false;

			for (int i = 0; i < updatesHistory.size(); i++)
			{
				History history = updatesHistory.elementAt(i);
				History history1 = updatesHistory1.elementAt(i);
				if (history == null) return false;
				if (!history.equals(history1)) return false;
			}

			return true;
		}
		return false;
	}
	
	public int hashCode()
	{
		int hash = 0;
		hash = hash ^ this.id.hashCode();
		hash = hash ^ this.updates;
		hash = hash ^ new Boolean(this.deleted).hashCode();
		hash = hash ^ new Boolean(this.noConflicts).hashCode();
		hash = hash ^ this.updatesHistory.hashCode();
		return hash;
	}
	
	public Sync clone(){
		Sync newSync = new Sync(this.id);
		newSync.deleted = this.deleted;
		newSync.updates = this.updates;

		ArrayList<History> newHistory = new ArrayList<History>();
		newHistory.addAll(this.updatesHistory);
		
		for (History history : newHistory)
		{
			newSync.updatesHistory.push(history.clone());
		}

		for (Item conflict : this.conflicts)
		{
			newSync.conflicts.add(conflict.clone());
		}

		return newSync;
	}
	
	// BEHAVIORs
	public Sync delete(String by, Date when)
	{
		Guard.argumentNotNullOrEmptyString(by, "by");
		Guard.argumentNotNull(when, "when");
		
		//Deleted attribute set to true because it is a deletion (3.2.4 from spec)
		return this.update(by, when, true);
	}

	// 3.2
	// 3.2
	public Sync update(String by, Date when, int sequence)
	{
		// 3.2.1
		this.increaseUpdates();

		// 3.2.2 & 3.2.2.a.i
		History history = new History(by, when, sequence); 

		// 3.2.3
		this.getUpdatesHistory().push(history);

		return this;
	}
	
	public Sync update(String by, Date when)
	{
		return this.update(by, when, false);
	}
	
	public Sync update(String by, Date when, boolean deleteItem)
	{
		//Sync sync = this.clone();
		Sync sync = this;
		sync.basicUpdate(by, when, deleteItem);
		return sync;
	}
	
	private void basicUpdate(String by, Date when, boolean deleteItem)
	{
		// 3.2.1
		this.increaseUpdates();

		// 3.2.2 & 3.2.2.a.i
		History history = new History(by, when, this.getUpdates()); 

		// 3.2.3
		this.getUpdatesHistory().push(history);

		// 3.2.4
		this.setDeleted(deleteItem);
	}
	
	public boolean isSubsumedBy(Sync sync) {
		History Hx = this.getLastUpdate(); 
		for(History Hy : sync.getUpdatesHistory())
		{
			if (Hx.IsSubsumedBy(Hy))
			{
				return true;
			}
		}
		return false;
	}

	public Sync addConflict(Item item) {
		this.getConflicts().add(item);
		return this;
	}

	public boolean purgue() {
		if(this.updates > 1){
			History lastUpdate = getLastUpdate();
			this.updatesHistory.clear();
			this.updatesHistory.push(lastUpdate);
			this.updates = 1;
			return true;
		} else {
			return false;
		}
	}

}
