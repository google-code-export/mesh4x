package org.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.validations.Guard;


public class SyncSession implements ISyncSession{

	// MODEL VARIABLES
	private String sessionId;
	private int version = 0;
	private IMessageSyncAdapter syncAdapter;
	private IEndpoint target;
	private Date lastSyncDate;
	private boolean open = false;
	private HashMap<String, Item> cache = new HashMap<String, Item>();
	private List<Item> snapshot = new ArrayList<Item>();
	private HashMap<String, Item> conflicts = new HashMap<String, Item>();
	private ArrayList<String> acks = new ArrayList<String>();
	private boolean fullProtocol = false;
	private boolean cancelled = false;

	// METHODS
	public SyncSession(String sessionId, int version, IMessageSyncAdapter syncAdapter, IEndpoint target, boolean fullProtocol) {
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(target, "target");
		
		this.sessionId = sessionId;
		this.version = version;
		this.syncAdapter = syncAdapter;
		this.target = target;
		this.fullProtocol = fullProtocol;

	}

	@Override
	public boolean isOpen(){
		return open;
	}

	public IMessageSyncAdapter getSyncAdapter(){
		return this.syncAdapter;
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
	public void beginSync(Date sinceDate, int version){
		this.lastSyncDate = sinceDate;
		this.beginSync(version);
	}

	@Override
	public void beginSync(){
		this.beginSync(this.version +1);
	}
	
	
	private void beginSync(int version){
		this.version = version;
		this.open = true;
		this.conflicts = new HashMap<String, Item>();
		this.acks = new ArrayList<String>();
		this.cache = new HashMap<String, Item>();
		this.cancelled = false;
		
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).beginSync();
		}
		
		List<Item> items = this.syncAdapter.getAll();
		
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).endSync();
		}
		for (Item item : items) {
			this.cache.put(item.getSyncId(), item);
		}
	}

	@Override
	public void endSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		this.open = false;
		this.cancelled = false;
		
		this.snapshot = new ArrayList<Item>(this.cache.values());
	}
	
	@Override
	public void cancelSync() {
		this.open = false;
		this.cancelled = true;
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
	public int getVersion() {
		return version;
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
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.cancelled = isCancelled;
	}
}
