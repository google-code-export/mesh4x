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
	public ISyncSession createSession(String sessionId, String sourceId, IEndpoint target) {
		IMessageSyncAdapter syncAdapter = getSyncAdapter(sourceId);
		SyncSession session = new SyncSession(sessionId, syncAdapter, target);
		this.sessions.put(sessionId, session);
		return session;
	}

	private IMessageSyncAdapter getSyncAdapter(String sourceId) {
		return this.adapters.get(sourceId);
	}

	@Override
	public ISyncSession get(String sourceId, String targetId) {
		for (ISyncSession syncSession : this.sessions.values()) {
			if(syncSession.getSourceId().equals(sourceId)
				&& syncSession.getTarget().getEndpointId().equals(targetId)){
					return syncSession;
				}
		}
		return null;
	}
	
	@Override
	public ISyncSession get(String sessionId) {
		return this.sessions.get(sessionId);
	}

	@Override
	public void registerSource(IMessageSyncAdapter adapter) {
		this.adapters.put(adapter.getSourceId(), adapter);
	}

}
