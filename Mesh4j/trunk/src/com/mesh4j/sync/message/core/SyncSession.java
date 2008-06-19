package com.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.Item;

public class SyncSession implements ISyncSession{

	// MODEL VARIABLES
	private String sessionId;
	private IMessageSyncAdapter syncAdapter;
	private IEndpoint target;
	private Date lastSyncDate;
	private boolean isOpen = false;
	private HashMap<String, Item> snapshot = new HashMap<String, Item>();
	private ArrayList<String> conflicts = new ArrayList<String>();
	private ArrayList<String> acks = new ArrayList<String>();

	// METHODS
	public SyncSession(String sessionId, IMessageSyncAdapter syncAdapter, IEndpoint target) {
		super();
		this.sessionId = sessionId;
		this.syncAdapter = syncAdapter;
		this.target = target;
	}

	@Override
	public boolean isOpen(){
		return isOpen;
	}

	@Override
	public String getSourceId(){
		return this.syncAdapter.getSourceId();
	}
	
	@Override
	public IEndpoint getTarget(){
		return this.target;
	}

	@Override
	public Item get(String syncId){
		return this.snapshot.get(syncId);
	}
	
	@Override
	public void add(Item item){
		this.snapshot.put(item.getSyncId(), item);
	}
	
	@Override
	public void update(Item item){
		this.snapshot.put(item.getSyncId(), item);
	}

	@Override
	public void delete(String syncID, String by, Date when){
		Item item = this.snapshot.get(syncID);
		item.getSync().delete(by, when);
	}

	@Override
	public boolean hasChanged(String syncID){
		if(this.lastSyncDate == null){
			return true;
		}
		
		Item item = this.get(syncID);
		if(item == null || item.getLastUpdate() == null || item.getLastUpdate().getWhen() == null){
			return false;
		} 
		return this.lastSyncDate.compareTo(item.getLastUpdate().getWhen()) <= 0;
	}
	
	@Override
	public void addConflict(String syncID){
		this.conflicts.add(syncID);
	}
	
	@Override
	public List<Item> getAll(){		
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item item : this.snapshot.values()) {
			if(SinceLastUpdateFilter.applies(item, this.lastSyncDate)){
				items.add(item);
			}
		}
		return items;
	}
	
	@Override
	public List<Item> getAllWithOutConflicts(){
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item item : this.snapshot.values()) {
			if(SinceLastUpdateFilter.applies(item, this.lastSyncDate) && !hasConflict(item.getSyncId())){
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public boolean hasConflict(String syncID) {
		return this.conflicts.contains(syncID);
	}

	@Override
	public void beginSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		beginSync();
	}

	@Override
	public void beginSync(){
		this.isOpen = true;
		this.conflicts = new ArrayList<String>();
		this.acks = new ArrayList<String>();
		takeSnapshot();
	}

	private void takeSnapshot() {
		this.snapshot = new HashMap<String, Item>();
		List<Item> items = this.syncAdapter.getAll();
		for (Item item : items) {
			this.snapshot.put(item.getSyncId(), item);
		}
	}
	
	@Override
	public void endSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		this.isOpen = false;
	}
	
	@Override
	public Date getLastSyncDate(){
		return this.lastSyncDate;
	}

	@Override
	public boolean isCompleteSync() {
		return this.acks.isEmpty();
	}

	@Override
	public void notifyAck(String syncId) {
		this.acks.remove(syncId);		
	}

	@Override
	public void waitForAck(String syncId) {
		this.acks.add(syncId);
	}
	
	@Override
	public List<Item> getSnapshot(){
		return new ArrayList<Item>(this.snapshot.values());
	}

	@Override
	public void cancelSync() {
		this.isOpen = false;
		this.conflicts = new ArrayList<String>();
		this.acks = new ArrayList<String>();
		this.snapshot = new HashMap<String, Item>();
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
}
