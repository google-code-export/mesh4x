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
	private Date startDate;
	private Date endDate;
	private int lastNumberInMessages = 0;
	private int lastNumberOutMessages = 0;	
	private boolean open = false;
	private HashMap<String, Item> cache = new HashMap<String, Item>();
	private List<Item> snapshot = new ArrayList<Item>();
	private HashMap<String, Item> conflicts = new HashMap<String, Item>();
	private ArrayList<String> acks = new ArrayList<String>();
	private boolean fullProtocol = false;
	private boolean cancelled = false;
	private boolean broken = false;
	private boolean shouldSendChanges = true;
	private boolean shouldReceiveChanges = true;
	private int numberOfAddedItems = 0;
	private int numberOfUpdatedItems = 0;
	private int numberOfDeletedItems = 0;
	private String targetSourceType;
	private int targetNumberOfAddedItems = 0;
	private int targetNumberOfUpdatedItems = 0;
	private int targetNumberOfDeletedItems = 0;

	// METHODS
	public SyncSession(String sessionId, int version, IMessageSyncAdapter syncAdapter, IEndpoint target, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {
		this(sessionId, version, syncAdapter, target, fullProtocol, shouldSendChanges, shouldReceiveChanges, 0, 0, 0, null, 0, 0, 0);
	}
	
	public SyncSession(String sessionId, int version,
			IMessageSyncAdapter syncAdapter, IEndpoint target,
			boolean fullProtocol, boolean shouldSendChanges,
			boolean shouldReceiveChanges, int numberOfAddedItems,
			int numberOfUpdatedItems, int numberOfDeletedItems,
			String targetSourceType, int targetNumberOfAddedItems,
			int targetNumberOfUpdatedItems, int targetNumberOfDeletedItems) {
		
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(target, "target");
		
		this.sessionId = sessionId;
		this.version = version;
		this.syncAdapter = syncAdapter;
		this.target = target;
		this.fullProtocol = fullProtocol;
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.numberOfAddedItems = numberOfAddedItems;
		this.numberOfUpdatedItems = numberOfUpdatedItems;
		this.numberOfDeletedItems = numberOfDeletedItems;
		this.targetSourceType = targetSourceType;
		this.targetNumberOfAddedItems = targetNumberOfAddedItems;
		this.targetNumberOfUpdatedItems = targetNumberOfUpdatedItems;
		this.targetNumberOfDeletedItems = targetNumberOfDeletedItems;
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
		this.numberOfAddedItems = this.numberOfAddedItems + 1;
	}
	
	@Override
	public void update(Item item){
		this.cache.put(item.getSyncId(), item);
		if(item.isDeleted()){
			this.numberOfDeletedItems = this.numberOfDeletedItems + 1;
		} else {
			this.numberOfUpdatedItems = this.numberOfUpdatedItems + 1;
		}
	}

	@Override
	public void delete(String syncID, String by, Date when){
		Item item = this.get(syncID);		
		Item ItemDeleted = new Item(new NullContent(syncID), item.getSync().clone().delete(by, when));
		this.cache.put(syncID, ItemDeleted);
		this.numberOfDeletedItems = this.numberOfDeletedItems + 1;
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
	public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, 
			Date sinceDate, int version, String targetSourceType){
		this.lastSyncDate = sinceDate;
		this.fullProtocol = fullProtocol;
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.beginSync(version, targetSourceType);
	}

	@Override
	public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges){
		this.fullProtocol = fullProtocol;
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.beginSync(this.version +1, null);
	}
		
	private void beginSync(int version, String targetSourceType){
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
		
		this.numberOfAddedItems = 0;
		this.numberOfDeletedItems = 0;
		this.numberOfUpdatedItems = 0;
		
		this.targetSourceType = targetSourceType;
		this.targetNumberOfAddedItems = 0;
		this.targetNumberOfDeletedItems = 0;
		this.targetNumberOfUpdatedItems = 0;
		
		this.lastNumberInMessages = 0;
		this.lastNumberOutMessages = 0;
		
		this.startDate = new Date();
		this.endDate = null;
	}

	@Override
	public void endSync(Date sinceDate, int numberInMessages, int numberOutMessages){
		this.lastSyncDate = sinceDate;
		this.lastNumberInMessages = numberInMessages;
		this.lastNumberOutMessages = numberOutMessages;
		
		this.open = false;
		this.cancelled = false;
		
		this.snapshot = new ArrayList<Item>(this.cache.values());
		this.endDate = new Date();
	}
	
	@Override
	public void cancelSync() {
		this.open = false;
		this.cancelled = true;
		this.conflicts = new HashMap<String, Item>();
		this.acks = new ArrayList<String>();
		this.cache = new HashMap<String, Item>();
		
		this.numberOfAddedItems = 0;
		this.numberOfDeletedItems = 0;
		this.numberOfUpdatedItems = 0;
		
		this.targetNumberOfAddedItems = 0;
		this.targetNumberOfDeletedItems = 0;
		this.targetNumberOfUpdatedItems = 0;
		
		this.broken = false;
		
		this.endDate = new Date();
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

	@Override
	public boolean shouldReceiveChanges() {
		return this.shouldReceiveChanges;
	}

	@Override
	public boolean shouldSendChanges() {
		return this.shouldSendChanges;
	}

	@Override
	public int getNumberOfAddedItems() {
		return this.numberOfAddedItems;
	}

	@Override
	public int getNumberOfDeletedItems() {
		return this.numberOfDeletedItems;
	}

	@Override
	public int getNumberOfUpdatedItems() {
		return this.numberOfUpdatedItems;
	}

	@Override
	public String getSourceType() {
		return this.getSyncAdapter().getSourceType();
	}
	
	@Override
	public int getTargetNumberOfAddedItems() {
		return this.targetNumberOfAddedItems;
	}

	@Override
	public int getTargetNumberOfDeletedItems() {
		return this.targetNumberOfDeletedItems;
	}

	@Override
	public int getTargetNumberOfUpdatedItems() {
		return this.targetNumberOfUpdatedItems;
	}
	
	@Override
	public String getTargetSourceType(){
		return this.targetSourceType;
	}

	@Override
	public void setTargetNumberOfAddedItems(int added) {
		this.targetNumberOfAddedItems = added;		
	}

	@Override
	public void setTargetNumberOfDeletedItems(int deleted) {
		this.targetNumberOfDeletedItems = deleted;
	}

	@Override
	public void setTargetNumberOfUpdatedItems(int updated) {
		this.targetNumberOfUpdatedItems = updated;
	}

	@Override
	public void setTargetSorceType(String targetSourceType) {
		this.targetSourceType = targetSourceType;
	}

	@Override
	public boolean isBroken() {
		return this.broken;
	}

	@Override
	public void setBroken() {
		this.broken = true;
	}

	@Override
	public int getLastNumberInMessages() {
		return this.lastNumberInMessages;
	}
	public void setLastNumberInMessages(int in) {
		this.lastNumberInMessages = in;
	}

	@Override
	public int getLastNumberOutMessages() {
		return this.lastNumberOutMessages;
	}
	public void setLastNumberOutMessages(int out) {
		this.lastNumberOutMessages = out;
	}

	@Override
	public Date getEndDate() {
		return this.endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;		
	}
	
	@Override
	public Date getStartDate() {
		return this.startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;		
	}
}


