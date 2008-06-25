package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.utils.IdGenerator;

public class MockSyncSession implements ISyncSession{

	private Date sinceDate;
	private Item item;
	private IEndpoint endpoint;
	private String sessionID;
	private ArrayList<Item> all = new ArrayList<Item>();
	private ArrayList<Item> snapshot = new ArrayList<Item>();
	private boolean open = false;
	private boolean beginWasCalled = false;
	private boolean endSyncWasCalled = false;
	private boolean getAllWasCalled = false;
	private Date currentDate = new Date();
	private String ackExpected;
	private boolean hasConflict = false;
	
	public MockSyncSession(Date sinceDate) {
		this(sinceDate, null);
	}

	public MockSyncSession(Date sinceDate, Item item) {
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
		
		this.sessionID = IdGenerator.newID();
	}

	@Override public void add(Item item) {}
	@Override public void addConflict(String syncID) {this.hasConflict = true;}
	@Override public void beginSync() {this.beginWasCalled = true;}
	@Override public void beginSync(Date sinceDate) {this.beginWasCalled=true;}
	@Override public void cancelSync() {}
	@Override public void delete(String syncID, String by, Date when) {}
	@Override public void endSync(Date sinceDate) {
		this.sinceDate = sinceDate;
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
	@Override public boolean hasConflict(String syncId) {return hasConflict;}
	@Override public boolean isOpen() {return open;}
	@Override public void update(Item item) {}
	@Override public boolean isCompleteSync() {return this.ackExpected == null;}
	@Override public void notifyAck(String syncId) {
		if(syncId.equals(this.ackExpected)){
			this.ackExpected = null;
		}
	}	
	@Override public void waitForAck(String syncId) {this.ackExpected = syncId;}

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

	public boolean isPendingAck(String syncId) {
		return syncId.equals(this.ackExpected);
	}
}