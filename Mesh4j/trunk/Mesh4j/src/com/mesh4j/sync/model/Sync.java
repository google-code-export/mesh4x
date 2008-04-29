package com.mesh4j.sync.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.mesh4j.sync.behavior.Behaviors;
import com.mesh4j.sync.utils.IdGenerator;
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
	public Sync()
	{
		this(IdGenerator.newID());
	}
	
	public Sync(String id, int updates)
	{
		Guard.argumentNotNullOrEmptyString(id, "id");
		this.id = id;
		this.updates = updates;
	}

	public Sync(String id){
		this(id, 0);
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

	public Sync update(String by, Date when)
	{
		return Behaviors.INSTANCE.update(this, by, when, false);
	}

	public Sync update(String by, Date when, boolean deleteItem)
	{
		return Behaviors.INSTANCE.update(this, by, when, deleteItem);
	}

	public Sync addHistory(History history)
	{
		this.getUpdatesHistory().push(history);
		return this;
	}

	/// <summary>
	/// Adds the conflict history immediately after the topmost history.
	/// </summary>
	/// <remarks>Used for conflict resolution only.</remarks>
	public Sync addConflictHistory(History history)
	{
		History topmost = updatesHistory.pop();
		updatesHistory.push(history);
		updatesHistory.push(topmost);
		return this;
	}
	
	public Sync increaseUpdates() {
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
		Sync newSync = new Sync(this.id, this.updates);
		newSync.deleted = this.deleted;

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

}
