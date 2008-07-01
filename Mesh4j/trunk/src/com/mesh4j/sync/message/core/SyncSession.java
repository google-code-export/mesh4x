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
import com.mesh4j.sync.model.NullContent;

public class SyncSession implements ISyncSession{

	// MODEL VARIABLES
	private String sessionId;
	private IMessageSyncAdapter syncAdapter;
	private IEndpoint target;
	private Date lastSyncDate;
	private boolean open = false;
	private HashMap<String, Item> cache = new HashMap<String, Item>();
	private List<Item> snapshot = new ArrayList<Item>();
	private HashMap<String, Item> conflicts = new HashMap<String, Item>();
	private ArrayList<String> acks = new ArrayList<String>();
	private boolean fullProtocol = false;

	// METHODS
	public SyncSession(String sessionId, IMessageSyncAdapter syncAdapter, IEndpoint target, boolean fullProtocol) {
		super();
		this.sessionId = sessionId;
		this.syncAdapter = syncAdapter;
		this.target = target;
		this.fullProtocol = fullProtocol;
	}

	@Override
	public boolean isOpen(){
		return open;
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
		return this.cache.get(syncId);
	}
	
	@Override
	public void add(Item item){
		this.cache.put(item.getSyncId(), item);
	}
	
	@Override
	public void update(Item item){
		this.cache.put(item.getSyncId(), item);
	}

	@Override
	public void delete(String syncID, String by, Date when){
		Item item = this.get(syncID);		
		Item ItemDeleted = new Item(new NullContent(syncID), item.getSync().clone().delete(by, when));
		this.cache.put(syncID, ItemDeleted);
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
		this.conflicts.put(syncID, null);
	}
	
	@Override
	public void addConflict(Item item){
		this.conflicts.put(item.getSyncId(), item);
	}
	
	@Override
	public List<Item> getAll(){		
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item item : this.cache.values()) {
			if(SinceLastUpdateFilter.applies(item, this.lastSyncDate)){
				items.add(item);
			}
		}
		return items;
	}
	
	@Override
	public boolean hasConflict(String syncID) {
		return this.conflicts.keySet().contains(syncID);
	}

	@Override
	public void beginSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		beginSync();
	}

	@Override
	public void beginSync(){
		this.open = true;
		this.conflicts = new HashMap<String, Item>();
		this.acks = new ArrayList<String>();
		this.cache = new HashMap<String, Item>();
		
		List<Item> items = this.syncAdapter.getAll();
		for (Item item : items) {
			this.cache.put(item.getSyncId(), item);
		}
	}

	@Override
	public void endSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		this.open = false;
		
		this.snapshot = new ArrayList<Item>(this.cache.values());
	}
	
	@Override
	public void cancelSync() {
		this.open = false;
		this.conflicts = new HashMap<String, Item>();
		this.acks = new ArrayList<String>();
		this.cache = new HashMap<String, Item>();
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
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public List<Item> getSnapshot() {
		return this.snapshot;
	}

	@Override
	public Date createSyncDate() {
		return new Date();
	}

	@Override
	public boolean isFullProtocol() {
		return fullProtocol;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Item> getCurrentSnapshot() {
		return new ArrayList(this.cache.values());		
	}

	@Override
	public List<String> getAllPendingACKs() {
		return acks;
	}

	@Override
	public List<String> getConflictsSyncIDs() {
		return new ArrayList<String>(this.conflicts.keySet());
	}
	

	public void setOpen(boolean isOpen){
		this.open = isOpen;
	}

	public void setLastSyncDate(Date lastSyncDate){
		this.lastSyncDate = lastSyncDate;
	}

	public void addToSnapshot(Item item) {
		this.snapshot.add(item);		
	}
}
