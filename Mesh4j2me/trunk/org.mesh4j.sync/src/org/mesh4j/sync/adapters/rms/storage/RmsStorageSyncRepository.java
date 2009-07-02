package org.mesh4j.sync.adapters.rms.storage;

import java.util.Vector;

import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class RmsStorageSyncRepository implements ISyncRepository{

	private static final String DEFAULT_STORAGE_NAME = "M4X_SYNC_REPOSITORY";
	
	// MODEL VARIABLES
	private IRmsStorage storage;
	private IIdGenerator idGenerator;
	private SyncInfoObjectParser parser;
	
	// BUSINESS METHODS
	public RmsStorageSyncRepository(IIdentityProvider identityProvider, IIdGenerator idGenerator){
		this(DEFAULT_STORAGE_NAME, identityProvider, idGenerator);
	}
	
	public RmsStorageSyncRepository(String storageName, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		Guard.argumentNotNull(idGenerator, "idGenerator");
		
		this.parser = new SyncInfoObjectParser(AtomSyndicationFormat.INSTANCE, identityProvider, idGenerator);
		this.storage = new RmsStorage(parser, storageName);
		this.idGenerator = idGenerator;
	}
	
	public SyncInfo get(String syncId) {
		SyncIdRecordFilter idFilter = new SyncIdRecordFilter(this.parser, syncId);
		return (SyncInfo) this.storage.get(idFilter);
	}

	public Vector<SyncInfo> getAll(String entityName) {
		SyncInfoSinceEntityNameRecordFilter filter = new SyncInfoSinceEntityNameRecordFilter(this.parser, entityName);
		return this.storage.getAll(filter, null);
	}

	public String newSyncID(IContent content) {
		return this.idGenerator.newID();
	}

	public void save(SyncInfo syncInfo) {
		SyncIdRecordFilter idFilter = new SyncIdRecordFilter(this.parser, syncInfo.getSyncId());
		this.storage.saveOrUpdate(syncInfo, idFilter);		
	}

	public void deleteRecordStorage() {
		this.storage.deleteRecordStorage();		
	}

	public void deleteAll() {
		this.storage.deleteRecordStorage();		
	}
}