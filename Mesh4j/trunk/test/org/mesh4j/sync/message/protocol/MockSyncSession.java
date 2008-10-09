package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;


public class MockSyncSession implements ISyncSession{

	private Date sinceDate;
	private Item item;
	private IEndpoint endpoint;
	private String sessionID;
	private int sessionVersion = 0;
	private ArrayList<Item> all = new ArrayList<Item>();
	private ArrayList<Item> snapshot = new ArrayList<Item>();
	private boolean open = false;
	private boolean beginWasCalled = false;
	private boolean endSyncWasCalled = false;
	private boolean getAllWasCalled = false;
	private Date currentDate = new Date();
	private ArrayList<String> acks = new ArrayList<String>();
	private ArrayList<String> conflicts = new ArrayList<String>();
	private boolean fullProtocol = true;
	private boolean cancelled = false;
	
	public MockSyncSession(Date sinceDate) {
		this(sinceDate, null, IdGenerator.INSTANCE.newID());
	}
	public MockSyncSession(Date sinceDate, Item item) {
		this(sinceDate, item, IdGenerator.INSTANCE.newID());
	}
	public MockSyncSession(Date sinceDate, Item item, String sessionId) {
		super();
		this.sinceDate = sinceDate;
		this.item = item;
		
		if(item != null){
			this.all.add(item);
		}
		
		this.endpoint = new IEndpoint(){
			@Override
			public String getEndpointId() {
				return "sms:1";
			}		
		};
		
		this.sessionID = sessionId;
	}

	@Override public void add(Item item) {}
	@Override public void addConflict(String syncID) {this.conflicts.add(syncID);}
	@Override public void beginSync() {
		this.beginWasCalled = true;
		this.cancelled = false;}
	@Override public void beginSync(Date sinceDate, int version) {
		this.beginWasCalled=true;
		this.cancelled = false;
	}
	@Override public void cancelSync() {
		this.cancelled = true;
	}
	@Override public void delete(String syncID, String by, Date when) {
		this.item.getSync().delete(by, when);
	}	
	@Override public void endSync(Date sinceDate) {
		this.sinceDate = sinceDate;
		this.cancelled = false;
		this.endSyncWasCalled = true;
	}
	@Override public Item get(String syncId) {return item;}
	@Override public List<Item> getAll() {
		this.getAllWasCalled = true;
		return all;
	}
	@Override public Date getLastSyncDate() {return sinceDate;}
	@Override public String getSessionId() {return sessionID;}
	@Override public List<Item> getSnapshot() {return snapshot;}
	@Override public String getSourceId() {return "12345";}
	@Override public IEndpoint getTarget() {return endpoint;}
	@Override public boolean hasChanged(String syncID) {
		for (Item snapshotItem : snapshot) {
			if(snapshotItem.getSyncId().equals(syncID)){
				return this.item.equals(snapshotItem);
			}
		}
		return false;
	}
	@Override public boolean hasConflict(String syncId) {return this.conflicts.contains(syncId);}
	@Override public boolean isOpen() {return open;}
	@Override public void update(Item item) {}
	@Override public boolean isCompleteSync() {return this.acks.isEmpty();}
	@Override public void notifyAck(String syncId) {this.acks.remove(syncId);}	
	@Override public void waitForAck(String syncId) {this.acks.add(syncId);}

	public void setOpen() {
		this.open = true;		
	}

	public boolean beginSyncWasCalled() {
		return beginWasCalled;
	}

	public boolean getAllWasCalled() {
		return getAllWasCalled;
	}

	@Override
	public Date createSyncDate() {
		return currentDate;
	}

	public boolean endSyncWasCalled() {
		return endSyncWasCalled;
	}
	
	public void addToSnapshot(Item item){
		this.snapshot.add(item);
	}
	
	public void addItem(Item item){
		this.snapshot.add(item);
		this.all.add(item);
	}

	public boolean isPendingAck(String syncId) {return this.acks.contains(syncId);}

	@Override
	public void addConflict(Item conflicItem) {this.conflicts.add(conflicItem.getSyncId());}

	@Override
	public boolean isFullProtocol() {
		return fullProtocol;
	}
	
	public void setFullProtocol(boolean fullProtocol) {
		this.fullProtocol = fullProtocol;
	}

	@Override
	public List<Item> getCurrentSnapshot() {
		return all;
	}

	@Override
	public List<String> getAllPendingACKs() {
		return acks;
	}

	@Override
	public List<String> getConflictsSyncIDs() {
		return conflicts;
	}
	public void setClose() {
		this.open = false;		
	}
	@Override
	public int getVersion() {
		return this.sessionVersion;
	}
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
}