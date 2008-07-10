package com.mesh4j.sync.message.core;

import java.util.List;

import com.mesh4j.sync.ISyncAdapter;
import com.mesh4j.sync.ISyncAware;
import com.mesh4j.sync.NullPreviewHandler;
import com.mesh4j.sync.PreviewBehavior;
import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.InMemorySyncAdapter;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public class MessageSyncAdapter implements IMessageSyncAdapter, ISyncAware {

	// MODEL VARIABLES
	private String sourceId;
	private ISyncAdapter syncAdapter;
	private IIdentityProvider identityProvider;
	
	// BUSINESS METHODS
	
	public MessageSyncAdapter(String sourceId, IIdentityProvider identityProvider, ISyncAdapter syncAdapter) {
		super();
		
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.sourceId = sourceId;
		this.syncAdapter = syncAdapter;
		this.identityProvider = identityProvider;
	}

	@Override
	public List<Item> getAll() {
		return this.syncAdapter.getAll();
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public List<Item> synchronizeSnapshot(ISyncSession syncSession) {
		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(this.sourceId, this.identityProvider, syncSession.getSnapshot());
		SyncEngine syncEngineA = new SyncEngine(this.syncAdapter, inMemoryAdapter);
		List<Item> conflicts = syncEngineA.synchronize(NullPreviewHandler.INSTANCE, PreviewBehavior.Left);
		return conflicts;
	}
	
	public ISyncAdapter getSyncAdapter() {
		return this.syncAdapter;
	}

	@Override
	public void beginSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).beginSync();
		}
	}

	@Override
	public void endSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).endSync();
		}
	}
}
