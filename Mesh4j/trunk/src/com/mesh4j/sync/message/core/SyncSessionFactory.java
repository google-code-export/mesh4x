package com.mesh4j.sync.message.core;

import java.util.HashMap;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.ISyncSessionFactory;

public class SyncSessionFactory implements ISyncSessionFactory {

	// MODEL VARIABLES
	private HashMap<String, SyncSession> sessions = new HashMap<String, SyncSession>();
	private HashMap<String, IMessageSyncAdapter> adapters = new HashMap<String, IMessageSyncAdapter>();
	
	// BUSINESS METHODS
	@Override
	public ISyncSession createSession(String sourceId, IEndpoint target) {
		IMessageSyncAdapter syncAdapter = getSyncAdapter(sourceId);
		
		String key = makeKey(sourceId, target.getEndpointId());
		SyncSession session = new SyncSession(syncAdapter, target);
		this.sessions.put(key, session);
		return session;
	}

	private String makeKey(String sourceId, String endpointId) {
		return sourceId+"-"+endpointId;
	}

	private IMessageSyncAdapter getSyncAdapter(String sourceId) {
		return this.adapters.get(sourceId);
	}

	@Override
	public ISyncSession get(String sourceId, String targetId) {
		String key = makeKey(sourceId, targetId);
		return this.sessions.get(key);
	}

	@Override
	public void registerSource(IMessageSyncAdapter adapter) {
		this.adapters.put(adapter.getSourceId(), adapter);
	}

}
