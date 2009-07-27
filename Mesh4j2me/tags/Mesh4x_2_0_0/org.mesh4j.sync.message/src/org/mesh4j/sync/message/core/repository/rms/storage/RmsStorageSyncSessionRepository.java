package org.mesh4j.sync.message.core.repository.rms.storage;

import org.mesh4j.sync.adapters.rms.storage.IRmsStorage;
import org.mesh4j.sync.adapters.rms.storage.RmsStorage;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageContentAdapter;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.repository.AbstractSyncSessionRepository;
import org.mesh4j.sync.message.core.repository.IEndpointFactory;
import org.mesh4j.sync.message.core.repository.IMessageSyncAdapterFactory;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class RmsStorageSyncSessionRepository extends AbstractSyncSessionRepository {

	// CONSTANTS
	private static final String DEFAULT_STORAGE_NAME = "M4X_SYNC_SESSIONS";
	private final static String PREFIX_SNAPSHOT_SYNC = "M4X_SS_";
	private final static String PREFIX_SNAPSHOT_CONTENT = "M4X_SC_";
	private final static String PREFIX_OPEN_SESSION_SYNC = "M4X_OS_";
	private final static String PREFIX_OPEN_SESSION_CONTENT = "M4X_OC_";
	
	// MODEL VARIABLES
	private SyncSessionObjectParser parser;
	private IRmsStorage storage;
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	
	// BUSINESS METHODS
	
	public RmsStorageSyncSessionRepository(IEndpointFactory endpointFactory, IMessageSyncAdapterFactory adapterFactory, IIdentityProvider identityProvider, IIdGenerator idGenerator) {
		super(endpointFactory, adapterFactory);
		
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		
		this.parser = new SyncSessionObjectParser(this);
		this.storage = new RmsStorage(this.parser, DEFAULT_STORAGE_NAME);
	}	
	
	public ISyncSession createSession(String sessionId, int version, String sourceId, IEndpoint endpoint, boolean fullProtocol) {
		
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(endpoint, "endpoint");
		
		IMessageSyncAdapter syncAdapter = getSource(sourceId);
		if(syncAdapter == null){
			return null;
		}
		
		RmsSyncSession session = new RmsSyncSession(this, sessionId, version, syncAdapter, endpoint, fullProtocol);
		session.setDirty();
		return session;

	}

	public void save(ISyncSession session) {
		RmsSyncSession syncSession = (RmsSyncSession) session;
		if(syncSession.isDirty()){
			syncSession.setNoDirty();
			SyncSessionFilterById filter = new SyncSessionFilterById(syncSession.getSessionId(), this.parser);
			this.storage.saveOrUpdate(syncSession, filter);
		}
	}

	public void deleteRecordStorage() {
		this.storage.deleteRecordStorage();		
	}
	
	public void deleteAll() {
		super.deleteAll();
		
//		Vector<RmsSyncSession> sessions = this.storage.getAll(null, null);
//		if(!sessions.isEmpty()){
//			
//			RmsStorageSyncRepository syncRepo;
//			RmsStorageContentAdapter contentAdapter;			
//			String storageName;
//			for (RmsSyncSession syncSession : sessions) {
//				storageName = makeSessionStorageName(syncSession);
//				syncRepo = new RmsStorageSyncRepository(PREFIX_OPEN_SESSION_SYNC + storageName, this.identityProvider, this.idGenerator);
//				syncRepo.deleteRecordStorage();
//				
//				contentAdapter = new RmsStorageContentAdapter(PREFIX_OPEN_SESSION_CONTENT + storageName);
//				contentAdapter.deleteRecordStorage();
//				
//				syncRepo = new RmsStorageSyncRepository(PREFIX_SNAPSHOT_SYNC + storageName, this.identityProvider, this.idGenerator);
//				syncRepo.deleteRecordStorage();
//				
//				contentAdapter = new RmsStorageContentAdapter(PREFIX_SNAPSHOT_CONTENT + storageName);
//				contentAdapter.deleteRecordStorage();
//			}
//			
//			this.storage.deleteAll();
//		}
	}


	public ISyncSession getSession(String sessionId) {
		SyncSessionFilterById filter = new SyncSessionFilterById(sessionId, this.parser);
		RmsSyncSession session = (RmsSyncSession)this.storage.get(filter);
		return session;
	}

	public ISyncSession getSession(String sourceId, String endpointId) {
		SyncSessionFilterBySourceAndEndpoint filter = new SyncSessionFilterBySourceAndEndpoint(sourceId, endpointId, this.parser);
		RmsSyncSession session = (RmsSyncSession)this.storage.get(filter);
		return session;
	}

	public IRmsStorage getStorage() {
		return this.storage;
	}

	// Utils
	private static SplitAdapter makeSplitAdapter(String syncStorageName, String contentStorageName, IIdentityProvider identityProvider, IIdGenerator idGenerator) {
		RmsStorageSyncRepository syncRepo = new RmsStorageSyncRepository(syncStorageName, identityProvider, idGenerator);
		RmsStorageContentAdapter contentAdapter = new RmsStorageContentAdapter(contentStorageName);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		return splitAdapter;
	}

	public SplitAdapter makeOpenSessionSplitAdapter(RmsSyncSession session) {
		String storageName = makeSessionStorageName(session);
		return makeSplitAdapter(PREFIX_OPEN_SESSION_SYNC + storageName, PREFIX_OPEN_SESSION_CONTENT + storageName, this.identityProvider, this.idGenerator);
	}

	public SplitAdapter makeSnapshotSplitAdapter(RmsSyncSession session) {
		String storageName = makeSessionStorageName(session);
		return makeSplitAdapter(PREFIX_SNAPSHOT_SYNC + storageName, PREFIX_SNAPSHOT_CONTENT + storageName, this.identityProvider, this.idGenerator);
	}

	private String makeSessionStorageName(RmsSyncSession session) {  // TODO (JMT) storage name without formName
		String storageName = this.normalizeEndpointId(session.getTarget().getEndpointId()) + "_" + session.getSourceId();
		int max = 32 - PREFIX_SNAPSHOT_SYNC.length();
		if(storageName.length() < max){
			return storageName;
		} else {
			return storageName.substring(0, 31);
		}
	}

}
