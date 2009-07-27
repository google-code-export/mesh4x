package org.mesh4j.sync.message.core;

import java.util.Vector;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.NullPreviewHandler;
import org.mesh4j.sync.PreviewBehavior;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


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

	public Vector<Item> getAll() {
		return this.syncAdapter.getAll();
	}

	public String getSourceId() {
		return sourceId;
	}

	public Vector<Item> synchronizeSnapshot(ISyncSession syncSession) {
		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(this.sourceId, this.identityProvider, syncSession.getSnapshot());
		SyncEngine syncEngineA = new SyncEngine(this.syncAdapter, inMemoryAdapter);
		Vector<Item> conflicts = syncEngineA.synchronize(NullPreviewHandler.INSTANCE, PreviewBehavior.Left);
		return conflicts;
	}
	
	public ISyncAdapter getSyncAdapter() {
		return this.syncAdapter;
	}

	public void beginSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).beginSync();
		}
	}

	public void endSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).endSync();
		}
	}
}
