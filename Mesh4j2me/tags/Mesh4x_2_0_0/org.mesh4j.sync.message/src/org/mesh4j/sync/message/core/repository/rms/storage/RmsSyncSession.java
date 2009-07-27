package org.mesh4j.sync.message.core.repository.rms.storage;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.filter.CompoundFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.validations.Guard;

// TODO (JMT) Conflicts???
public class RmsSyncSession implements ISyncSession {

	// MODEL VARIABLES
	private String sessionId;
	private int version = 0;
	private IMessageSyncAdapter syncAdapter;
	private IEndpoint target;
	private Date lastSyncDate;
	private boolean open = false;
	private boolean fullProtocol = false;
	private boolean cancelled = false;
	//private RmsStorageSyncSessionRepository repository;
	private SplitAdapter currentOpenSession;
	private SplitAdapter snapshot;
	private Vector<String> acks = new Vector<String>();
	private Vector<String> conflicts = new Vector<String>();
    private boolean dirty = false;
	
	// METHODS
	public RmsSyncSession(RmsStorageSyncSessionRepository repository, String sessionId, int version, IMessageSyncAdapter syncAdapter, IEndpoint target, boolean fullProtocol) {
		Guard.argumentNotNull(repository, "repository");
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(target, "target");
		
		this.sessionId = sessionId;
		this.version = version;
		this.syncAdapter = syncAdapter;
		this.target = target;
		this.fullProtocol = fullProtocol;
		
		this.currentOpenSession = repository.makeOpenSessionSplitAdapter(this);
		this.snapshot =  repository.makeSnapshotSplitAdapter(this);
	}

	public String getSessionId() {
		return sessionId;
	}

	public int getVersion() {
		return version;
	}

	public String getSourceId(){
		return this.syncAdapter.getSourceId();
	}

	public IEndpoint getTarget(){
		return this.target;
	}

	public Date getLastSyncDate(){
		return this.lastSyncDate;
	}
	public void setLastSyncDate(Date lastSyncDate){
		this.lastSyncDate = lastSyncDate;
	}

	public boolean isOpen(){
		return open;
	}
	public void setOpen(boolean isOpen){
		this.open = isOpen;
	}

	public boolean isFullProtocol() {
		return fullProtocol;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.cancelled = isCancelled;
	}

	public void setDirty() {
		this.dirty = true;
	}
	public void setNoDirty() {
		this.dirty = false;
	}

	public boolean isDirty(){
		return dirty;
	}
	
	// SESSION METHODS
	
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
		this.conflicts = new Vector<String>();
		this.acks = new Vector<String>();

		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).beginSync();
		}

		Vector<Item> items = this.syncAdapter.getAll();

		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).endSync();
		}

		this.currentOpenSession.deleteAll();

		for (Item item : items) {
			this.currentOpenSession.add(item);
		}
		this.setDirty();
	}

	public void endSync(Date sinceDate){
		this.lastSyncDate = sinceDate;
		this.open = false;
		this.cancelled = false;
		
		this.acks = new Vector<String>();
		
		this.snapshot.deleteAll();

		Vector<Item> items = this.currentOpenSession.getAll();
		for (Item item : items) {
			this.snapshot.add(item);
		}
		
		this.currentOpenSession.deleteAll();
		this.setDirty();
	}
	

	public void cancelSync() {
		this.cancelled = true;
		this.open = false;
		this.conflicts = new Vector<String>();
		this.acks = new Vector<String>();
		
		this.currentOpenSession.deleteAll();
		
		this.setDirty();
	}	

	public boolean isCompleteSync() {
		return this.acks.isEmpty();
	}

	public Date createSyncDate() {
		return new Date();
	}
	
	// CURRENT OPEN SESSION

	public Item get(String syncId){
		return this.currentOpenSession.get(syncId);
	}
	
	public void add(Item item){
		this.currentOpenSession.add(item);
	}

	public void update(Item item){
		this.currentOpenSession.update(item);
	}

	public void delete(String syncID, String by, Date when){
		Item item = this.currentOpenSession.get(syncID);		
		Item itemDeleted = new Item(new NullContent(syncID), item.getSync().clone().delete(by, when));
		this.currentOpenSession.update(itemDeleted);
	}

	public Vector<Item> getAll(IFilter<Item> filter) {
		if(this.lastSyncDate == null){
			return this.currentOpenSession.getAll(filter);
		} else {
			CompoundFilter cf = new CompoundFilter(new SinceLastUpdateFilter(this.lastSyncDate), filter);
			return this.currentOpenSession.getAll(cf);
		}
	}

	public Vector<Item> getAll(){
		return this.currentOpenSession.getAllSince(this.lastSyncDate); 
	}

	public Vector<Item> getCurrentOpenSession() {
		return this.currentOpenSession.getAll();
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
		this.conflicts.addElement(syncID);
		this.setDirty();
	}

	public void addConflict(Item item){
		this.conflicts.addElement(item.getSyncId());
		this.setDirty();
	}
	
	public boolean hasConflict(String syncID) {
		return this.conflicts.contains(syncID);
	}

	public Vector<String> getConflictsSyncIDs() {
		return this.conflicts;
	}

	public void notifyAck(String syncId) {
		this.acks.removeElement(syncId);
		this.setDirty();
	}

	public void waitForAck(String syncId) {
		this.acks.addElement(syncId);
		this.setDirty();
	}
	
	public Vector<String> getAllPendingACKs() {
		return acks;
	}

	// SNAPSHOT

	public Vector<Item> getSnapshot() {
		return this.snapshot.getAll();
	}

	public Item getSnapshotItem(String syncId) {
		return this.snapshot.get(syncId);
	}
	

}