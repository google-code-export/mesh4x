package com.mesh4j.sync.message.core.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mesh4j.sync.adapters.dom.DOMAdapter;
import com.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import com.mesh4j.sync.message.core.MessageSyncAdapter;
import com.mesh4j.sync.message.core.SyncSession;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public class SyncSessionFactory implements ISyncSessionFactory {

	// MODEL VARIABLES
	private Map<String, SyncSession> sessions = Collections.synchronizedMap(new HashMap<String, SyncSession>());
	private Map<String, IMessageSyncAdapter> adapters = Collections.synchronizedMap(new HashMap<String, IMessageSyncAdapter>());
	private String baseDirectory = "";
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private boolean supportInMemoryAdapter = false;
	
	// BUSINESS METHODS
	public SyncSessionFactory(){
		super();
	}
	
	public SyncSessionFactory(String baseDirectory,
			IIdentityProvider identityProvider) {
		super();
		this.baseDirectory = baseDirectory;
		this.identityProvider = identityProvider;
		
		File fileDir = new File(baseDirectory);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
	}

	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		IMessageSyncAdapter syncAdapter = this.adapters.get(sourceId);
		if(syncAdapter != null){
			return syncAdapter;
		}
		
		if(KMLDOMLoaderFactory.isKML(sourceId)){
			String kmlFileName = this.baseDirectory + sourceId;
			DOMAdapter kmlAdapter = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(kmlFileName, this.identityProvider));
			syncAdapter = new MessageSyncAdapter(sourceId, this.identityProvider, kmlAdapter);
			return syncAdapter;
		} else {
			if(this.supportInMemoryAdapter){
				return new InMemoryMessageSyncAdapter(sourceId);
			} else {
				return null;
			}
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
	public ISyncSession createSession(String sessionId, int version, String sourceId, IEndpoint target, boolean fullProtocol) {
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(target, "target");
		
		IMessageSyncAdapter syncAdapter = getSource(sourceId);
		if(syncAdapter == null){
			return null;
		}
		
		SyncSession session = new SyncSession(sessionId, version, syncAdapter, target, fullProtocol);
		this.sessions.put(sessionId, session);
		return session;
	}

	@Override
	public ISyncSession createSession(String sessionId, int version, String sourceId,
			String endpointId, boolean fullProtocol, boolean isOpen, Date lastSyncDate,
			List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot,
			List<String> conflicts, List<String> acks) {
		
		IMessageSyncAdapter syncAdapter = getSource(sourceId);
		if(syncAdapter == null){
			return null;
		}
		
		SyncSession session = new SyncSession(sessionId, version, syncAdapter, new SmsEndpoint(endpointId), fullProtocol);
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

	public void setSupportInMemoryAdapter(boolean supportInMemoryAdapter) {
		this.supportInMemoryAdapter = supportInMemoryAdapter;
	}
}
