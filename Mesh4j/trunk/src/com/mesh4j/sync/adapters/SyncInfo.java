package com.mesh4j.sync.adapters;

import java.util.Date;

import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.IIdentityProvider;

public class SyncInfo {
	
	// MODEL VARIABLES
	private String syncId;
	private String type;
	private String id;
	private int version;
	private Sync sync;
	
	// BUSINESS METHODS

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
	
	public boolean contentHasChanged(IContent content) {
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
	
	public boolean updateSyncIfChanged(IContent content, IIdentityProvider identityProvider){		
		Sync sync = this.getSync();
		if (content == null && sync != null){
			if (!sync.isDeleted()){
				sync.delete(identityProvider.getAuthenticatedUser(), new Date());
				return true;
			}
		}else{
			if (!this.isDeleted() && this.contentHasChanged(content)){
				sync.update(identityProvider.getAuthenticatedUser(), new Date(), sync.isDeleted());
				this.setVersion(content.getVersion());
				return true;
			}
		}
		return false;
	}
}
