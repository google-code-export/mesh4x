package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;

public class MockSyncSession implements ISyncSession{

	// MODEL VARIABLES
	private Date sinceDate;
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
	private boolean shouldSendChanges = true;
	private boolean shouldReceiveChanges = true;
	private int numberOfAddedItems = 0;
	private int numberOfUpdatedItems = 0;
	private int numberOfDeletedItems = 0;
	private String targetSourceType = null;
	private int targetNumberOfAddedItems = 0;
	private int targetNumberOfUpdatedItems = 0;
	private int targetNumberOfDeletedItems = 0;
	private boolean broken = false;
	private int in = 0;
	private int out = 0;
	
	// BUSINESS METHODS
	
	public MockSyncSession(Date sinceDate) {
		this(sinceDate, null, IdGenerator.INSTANCE.newID());
	}
	public MockSyncSession(Date sinceDate, Item item) {
		this(sinceDate, item, IdGenerator.INSTANCE.newID());
	}
	public MockSyncSession(Date sinceDate, Item item, String sessionId) {
		super();
		this.sinceDate = sinceDate;
		
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

	@Override public void add(Item item) {
		this.all.add(item);
		this.numberOfAddedItems = this.numberOfAddedItems +1;
	}
	@Override public void addConflict(String syncID) {this.conflicts.add(syncID);}
	@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {
		this.fullProtocol = fullProtocol;
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.beginWasCalled = true;
		this.cancelled = false;}
	@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, 
			Date sinceDate, int version, String targetsourceType) {
		this.fullProtocol = fullProtocol;
		this.shouldSendChanges = shouldSendChanges;
		this.shouldReceiveChanges = shouldReceiveChanges;
		this.beginWasCalled=true;
		this.cancelled = false;
		this.targetSourceType = targetsourceType;
	}
	@Override public void cancelSync() {
		this.cancelled = true;
	}
	@Override public void delete(String syncID, String by, Date when) {
		Item item = get(syncID);
		if(item != null){
			item.getSync().delete(by, when);
			this.numberOfDeletedItems = this.numberOfDeletedItems +1;
		}
	}	
	@Override public void endSync(Date sinceDate, int in, int out) {
		this.sinceDate = sinceDate;
		this.cancelled = false;
		this.endSyncWasCalled = true;
		this.in = in;
		this.out = out;
	}
	
	@Override public Item get(String syncId) {
		for (Item item : this.all) {
			if(item.getSyncId().equals(syncId)){
				return item;
			}
		}
		return null;
	}
	
	@Override public List<Item> getAll() {
		this.getAllWasCalled = true;
		return all;
	}
	@Override public Date getLastSyncDate() {return sinceDate;}
	@Override public String getSessionId() {return sessionID;}
	@Override public List<Item> getSnapshot() {return snapshot;}
	@Override public String getSourceId() {return "12345";}
	@Override public IEndpoint getTarget() {return endpoint;}

	@Override public boolean hasConflict(String syncId) {return this.conflicts.contains(syncId);}
	@Override public boolean isOpen() {return open;}
	@Override public void update(Item item) {this.numberOfUpdatedItems = this.numberOfUpdatedItems +1;}
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
	
	@Override
	public boolean shouldReceiveChanges() {
		return this.shouldReceiveChanges;
	}
	
	@Override
	public boolean shouldSendChanges() {
		return this.shouldSendChanges;
	}
	
	public void setShouldReceiveChanges(boolean shouldReceiveChanges) {
		this.shouldReceiveChanges = shouldReceiveChanges;
	}
	
	public void setShouldSendChanges(boolean shouldSendChanges) {
		this.shouldSendChanges = shouldSendChanges;
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
		return "mock";
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
	public String getTargetSourceType() {
		return targetSourceType;
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
		return broken;
	}
	@Override
	public void setBroken() {
		this.broken = true;
	}
	@Override
	public int getLastNumberInMessages() {
		return in;
	}
	@Override
	public int getLastNumberOutMessages() {
		return out;
	}
	@Override
	public Date getEndDate() {
		return null;
	}
	@Override
	public Date getStartDate() {
		return null;
	}
}