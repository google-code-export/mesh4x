package com.mesh4j.sync.message.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.ISyncSessionFactory;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.model.Item;

public class SyncSessionFactory implements ISyncSessionFactory {

	// MODEL VARIABLES
	private HashMap<String, SyncSession> sessions = new HashMap<String, SyncSession>();
	private HashMap<String, IMessageSyncAdapter> adapters = new HashMap<String, IMessageSyncAdapter>();
	
	// BUSINESS METHODS
	@Override
	public ISyncSession createSession(String sessionId, String sourceId, IEndpoint target, boolean fullProtocol) {
		IMessageSyncAdapter syncAdapter = getSyncAdapter(sourceId);
		SyncSession session = new SyncSession(sessionId, syncAdapter, target, fullProtocol);
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

	@Override
	public ISyncSession createSession(String sessionId, String sourceId,
			String endpointId, boolean fullProtocol, boolean isOpen, Date lastSyncDate,
			List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot,
			List<String> conflicts, List<String> acks) {
		
		IMessageSyncAdapter syncAdapter = getSyncAdapter(sourceId);
		SyncSession session = new SyncSession(sessionId, syncAdapter, new SmsEndpoint(endpointId), fullProtocol);
		session.setOpen(isOpen);
		session.setLastSyncDate(lastSyncDate);
		
		for (Item item : currentSyncSnapshot) {
			session.add(item);	
		}
		
		for (Item item : lastSyncSnapshot) {
			session.addToSnapshot(item);	
		}
		
		for (String syncId : acks) {
			session.waitForAck(syncId);
		}
		
		for (String syncId : conflicts) {
			session.addConflict(syncId);
		}		
		
		this.sessions.put(sessionId, session);
		return session;
	}

}
