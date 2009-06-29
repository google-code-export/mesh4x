package org.mesh4j.sync.message.core;

import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.NullPreviewHandler;
import org.mesh4j.sync.PreviewBehavior;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


public class MessageSyncAdapter implements IMessageSyncAdapter, ISyncAware {

	// MODEL VARIABLES
	private String sourceId;
	private String sourceType;
	private ISyncAdapter syncAdapter;
	private ISyncAdapterFactory syncAdapterFactory;
	private IIdentityProvider identityProvider;
	
	// BUSINESS METHODS
	
	public MessageSyncAdapter(String sourceId, String sourceType, IIdentityProvider identityProvider, ISyncAdapter syncAdapter, ISyncAdapterFactory syncAdapterFactory) {
		super();
		
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNullOrEmptyString(sourceType, "sourceType");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.sourceId = sourceId;
		this.sourceType = sourceType;
		this.syncAdapter = syncAdapter;
		this.syncAdapterFactory = syncAdapterFactory;
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

	@Override
	public String getSourceType() {
		return this.sourceType;
	}

	public ISyncAdapterFactory getSyncAdapterFactory() {
		return this.syncAdapterFactory;
	}
}
