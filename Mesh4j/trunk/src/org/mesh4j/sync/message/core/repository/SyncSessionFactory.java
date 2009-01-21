package org.mesh4j.sync.message.core.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.SyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class SyncSessionFactory implements ISyncSessionFactory {

	// MODEL VARIABLES
	private Map<String, SyncSession> sessions = Collections.synchronizedMap(new HashMap<String, SyncSession>());
	private Map<String, IMessageSyncAdapter> adapters = Collections.synchronizedMap(new HashMap<String, IMessageSyncAdapter>());
	private String baseDirectory = "";
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private IEndpointFactory endpointFactory;
	private IMessageSyncAdapterFactory adapterFactory;
	
	// BUSINESS METHODS
	
	public SyncSessionFactory(IEndpointFactory endpointFactory, IMessageSyncAdapterFactory adapterFactory, String baseDirectory,
			IIdentityProvider identityProvider) {

		Guard.argumentNotNull(endpointFactory, "endpointFactory");
		Guard.argumentNotNull(adapterFactory, "adapterFactory");
		Guard.argumentNotNullOrEmptyString(baseDirectory, "baseDirectory");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.adapterFactory = adapterFactory;
		this.endpointFactory = endpointFactory;
		this.baseDirectory = baseDirectory;
		this.identityProvider = identityProvider;
		
		File fileDir = new File(this.baseDirectory);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
	}

	public SyncSessionFactory(IEndpointFactory endpointFactory, IMessageSyncAdapterFactory adapterFactory) {
		Guard.argumentNotNull(endpointFactory, "endpointFactory");
		Guard.argumentNotNull(adapterFactory, "adapterFactory");
		
		this.endpointFactory = endpointFactory;
		this.adapterFactory = adapterFactory;
	}

	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		IMessageSyncAdapter syncAdapter = (IMessageSyncAdapter) this.adapters.get(sourceId);
		return syncAdapter;
	}
	
	@Override
	public IMessageSyncAdapter getSourceOrCreateIfAbsent(String sourceId) {
		IMessageSyncAdapter syncAdapter = (IMessageSyncAdapter) this.adapters.get(sourceId);
		if(syncAdapter != null){
			return syncAdapter;
		}
		
		syncAdapter = this.adapterFactory.createSyncAdapter(sourceId, this.identityProvider);
		if(syncAdapter != null){
			this.adapters.put(sourceId, syncAdapter);
			return syncAdapter;
		} else {
			return null;
		}
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
	public void registerSource(IMessageSyncAdapter source) {
		this.adapters.put(source.getSourceId(), source);
	}
	
	public List<ISyncSession> getAll() {
		return new ArrayList<ISyncSession>(this.sessions.values());		
	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter source) {
		if(this.adapters.get(source.getSourceId()) == null){
			this.adapters.put(source.getSourceId(), source);
		}
	}

	@Override
	public ISyncSession createSession(String sessionId, int version, String sourceId, IEndpoint target, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(target, "target");
		
		IMessageSyncAdapter syncAdapter = getSourceOrCreateIfAbsent(sourceId);
		if(syncAdapter == null){
			return null;
		}
		
		SyncSession session = new SyncSession(sessionId, version, syncAdapter, target, fullProtocol, shouldSendChanges, shouldReceiveChanges);
		this.sessions.put(sessionId, session);
		return session;
	}

	@Override
	public ISyncSession createSession(String sessionId, int version, String sourceId,
			String endpointId, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, 
			boolean isOpen, boolean isBroken, boolean isCancelled, Date lastSyncDate, int lastIn, int lastOut,
			List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot,
			List<String> conflicts, List<String> acks,
			int numberOfAddedItems, int numberOfUpdatedItems, int numberOfDeletedItems,
			String targetSourceType, int targetNumberOfAddedItems, int targetNumberOfUpdatedItems, int targetNumberOfDeletedItems) {
		
		Guard.argumentNotNull(currentSyncSnapshot, "currentSyncSnapshot");
		Guard.argumentNotNull(lastSyncSnapshot, "lastSyncSnapshot");
		Guard.argumentNotNull(acks, "acks");
		Guard.argumentNotNull(conflicts, "conflicts");

		
		IMessageSyncAdapter syncAdapter = getSourceOrCreateIfAbsent(sourceId);
		if(syncAdapter == null){
			return null;
		}
		
		SyncSession session = new SyncSession(
				sessionId, 
				version, 
				syncAdapter, 
				this.endpointFactory.makeIEndpoint(endpointId), 
				fullProtocol, 
				shouldSendChanges, 
				shouldReceiveChanges, 
				numberOfAddedItems, 
				numberOfUpdatedItems, 
				numberOfDeletedItems,
				targetSourceType, 
				targetNumberOfAddedItems, 
				targetNumberOfUpdatedItems, 
				targetNumberOfDeletedItems);
		
		session.setOpen(isOpen);
		session.setLastSyncDate(lastSyncDate);
		session.setLastNumberInMessages(lastIn);
		session.setLastNumberOutMessages(lastOut);
		session.setCancelled(isCancelled);
		
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
		
		if(isBroken){
			session.setBroken();
		}
		
		this.sessions.put(sessionId, session);
		return session;
	}
}
