package org.mesh4j.sync.message.core.repository;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.Iterator;

public class InMemorySyncSession implements ISyncSession{

	// MODEL VARIABLES
	private String sessionId;
	private int version = 0;
	private IMessageSyncAdapter syncAdapter;
	private IEndpoint target;
	private Date lastSyncDate;
	private boolean open = false;
	private HashMap cache = new HashMap();
	private Vector<Item> snapshot = new Vector<Item>();
	private HashMap conflicts = new HashMap();
	private Vector<String> acks = new Vector<String>();
	private boolean fullProtocol = false;
	private boolean cancelled = false;

	// METHODS
	public InMemorySyncSession(String sessionId, int version, IMessageSyncAdapter syncAdapter, IEndpoint target, boolean fullProtocol) {
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(target, "target");
		
		this.sessionId = sessionId;
		this.version = version;
		this.syncAdapter = syncAdapter;
		this.target = target;
		this.fullProtocol = fullProtocol;
	}


	public boolean isOpen(){
		return open;
	}


	public String getSourceId(){
		return this.syncAdapter.getSourceId();
	}
	

	public IEndpoint getTarget(){
		return this.target;
	}


	public Item get(String syncId){
		return (Item) this.cache.get(syncId);
	}
	

	public void add(Item item){
		this.cache.put(item.getSyncId(), item);
	}
	

	public void update(Item item){
		this.cache.put(item.getSyncId(), item);
	}


	public void delete(String syncID, String by, Date when){
		Item item = this.get(syncID);		
		Item ItemDeleted = new Item(new NullContent(syncID), item.getSync().clone().delete(by, when));
		this.cache.put(syncID, ItemDeleted);
	}


	public boolean hasChanged(String syncID){
		if(this.lastSyncDate == null){
			return true;
		}
		
		Item item = this.get(syncID);
		if(item == null || item.getLastUpdate() == null || item.getLastUpdate().getWhen() == null){
			return false;
		} 
		return this.lastSyncDate.getTime() <= item.getLastUpdate().getWhen().getTime();
	}
	

	public void addConflict(String syncID){
		this.conflicts.put(syncID, this.get(syncID));
	}
	

	public void addConflict(Item item){
		this.conflicts.put(item.getSyncId(), item);
	}
	

	public Vector<Item> getAll(){		
		Vector<Item> items = new Vector<Item>();
		int length = this.cache.values().length;
		
		Item item = null;
		for (int i = 0; i < length; i++) {
			item = (Item) this.cache.values()[i];
			if(SinceLastUpdateFilter.applies(item, this.lastSyncDate)){
				items.addElement(item);
			}
		}
		return items;
	}
	

	public boolean hasConflict(String syncID) {
		return this.conflicts.containsKey(syncID);
	}


	public void beginSync(Date sinceDate, int version){
		this.lastSyncDate = sinceDate;
		this.beginSync(version);
	}


	public void beginSync(){
		this.beginSync(this.version +1);
	}
	
	
	private void beginSync(int version){
		this.version = version;
		this.open = true;
		this.cancelled = false;
		this.conflicts = new HashMap();
		this.acks = new Vector<String>();
		this.cache = new HashMap();
		
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).beginSync();
		}
		
		Vector<Item> items = this.syncAdapter.getAll();
		
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).endSync();
		}
		for (Item item : items) {
			this.cache.put(item.getSyncId(), item);
		}
	}


	public void endSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		this.open = false;
		this.cancelled = false;
		this.snapshot = new Vector<Item>();
		int length = this.cache.values().length;
		Item item = null;
		for (int i = 0; i < length; i++) {
			item = (Item) this.cache.values()[i];
			this.snapshot.addElement(item);
		}
	}
	

	public void cancelSync() {
		this.cancelled = true;
		this.open = false;
		this.conflicts = new HashMap();
		this.acks = new Vector<String>();
		this.cache = new HashMap();
	}	

	public Date getLastSyncDate(){
		return this.lastSyncDate;
	}

	public boolean isCompleteSync() {
		return this.acks.isEmpty();
	}

	public void notifyAck(String syncId) {
		this.acks.removeElement(syncId);		
	}

	public void waitForAck(String syncId) {
		this.acks.addElement(syncId);
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public int getVersion() {
		return version;
	}
	
	public Vector<Item> getSnapshot() {
		return this.snapshot;
	}

	public Date createSyncDate() {
		return new Date();
	}

	public boolean isFullProtocol() {
		return fullProtocol;
	}

	public Vector<Item> getCurrentSnapshot() {
		Vector<Item> result = new Vector<Item>();
		
		int length = this.cache.values().length;
		Item item = null;
		for (int i = 0; i < length; i++) {
			item = (Item) this.cache.values()[i];
			result.addElement(item);
		}
		return result;		
	}

	public Vector<String> getAllPendingACKs() {
		return acks;
	}

	public Vector<String> getConflictsSyncIDs() {
		Vector<String> result = new Vector<String>();
		
		Iterator<String> it = this.conflicts.keysIterator();
		while(it.hasNext()){
			result.addElement(it.next());
		}
		return result;
	}
	
	public void setOpen(boolean isOpen){
		this.open = isOpen;
	}

	public void setLastSyncDate(Date lastSyncDate){
		this.lastSyncDate = lastSyncDate;
	}

	public void addToSnapshot(Item item) {
		this.snapshot.addElement(item);		
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public Vector<Item> getAll(IFilter<Item> filter) {
		Vector<Item> result = new Vector<Item>();
		Vector<Item> items = getAll();
		for (Item item : items) {
			if(filter.applies(item)){
				result.addElement(item);
			}			
		}
		return result;
	}

	public Item getSnapshotItem(String syncId) {
		Vector<Item> items = getSnapshot();
		for (Item item : items) {
			if(item.getSyncId().equals(syncId)){
				return item;
			}			
		}
		return null;
	}

//	public void flush() {
//		if(!this.isOpen()){
//			Guard.throwsException("INVALID_FLUSH_SYNC_SESSION");
//		}
//		// nothing to do		
//	}
//
//	public void snapshot() {
//		if(this.getLastSyncDate() == null || this.isOpen()){
//			Guard.throwsException("INVALID_SNAPSHOT_SYNC_SESSION");
//		}
//		// nothing to do	
//	}
}
