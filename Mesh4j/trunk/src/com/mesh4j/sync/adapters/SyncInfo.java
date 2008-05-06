package com.mesh4j.sync.adapters;

import com.mesh4j.sync.model.Sync;

public class SyncInfo {
	
	// MODEL VARIABLES
	private String syncId;
	private String entityName;
	private String entityId;
	private int entityVersion;
	private Sync sync;
	
	// BUSINESS METHODS

	public SyncInfo(Sync sync, EntityContent entity) {
		this(sync, entity.getEntityName(), entity.getEntityId(), entity.getEntityVersion());
	}
	
	public SyncInfo(Sync sync, String entityName, String entityId, int entityVersion) {
		super();
		this.sync = sync;
		this.syncId = sync.getId();
		this.entityName = entityName;
		this.entityId = entityId;
		this.entityVersion = entityVersion;
	}
	
	public boolean isDeleted() {
		return this.sync != null && this.sync.isDeleted();
	}
	
	public boolean contentHasChanged(EntityContent entity) {
		return this.entityVersion != entity.getEntityVersion();
	}

	public String getSyncId() {
		return syncId;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getEntityId() {
		return entityId;
	}

	public int getEntityVersion() {
		return entityVersion;
	}

	public Sync getSync() {
		return sync;
	}

	public void setEntityVersion(int entityVersion) {
		this.entityVersion = entityVersion;
	}

	public void updateSync(Sync sync) {
		this.sync = sync;
		this.syncId = sync.getId();
	}
	
}
