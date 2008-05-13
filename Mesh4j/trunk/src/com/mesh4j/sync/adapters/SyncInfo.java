package com.mesh4j.sync.adapters;

import com.mesh4j.sync.model.Sync;

public class SyncInfo {
	
	// MODEL VARIABLES
	private String syncId;
	private String type;
	private String id;
	private int version;
	private Sync sync;
	
	// BUSINESS METHODS

	public SyncInfo(Sync sync, IIdentifiableContent entity) {
		this(sync, entity.getType(), entity.getId(), entity.getVersion());
	}
	
	public SyncInfo(Sync sync, String type, String id, int version) {
		super();
		this.sync = sync;
		this.syncId = sync.getId();
		this.type = type;
		this.id = id;
		this.version = version;
	}
	
	public boolean isDeleted() {
		return this.sync != null && this.sync.isDeleted();
	}
	
	public boolean contentHasChanged(IIdentifiableContent content) {
		return this.version != content.getVersion();
	}

	public String getSyncId() {
		return syncId;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public Sync getSync() {
		return sync;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void updateSync(Sync sync) {
		this.sync = sync;
		this.syncId = sync.getId();
	}
	
}
