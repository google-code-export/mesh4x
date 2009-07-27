package org.mesh4j.sync.message.core.repository;

import java.util.Vector;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.HashMap;

public class InMemorySyncSessionRepository extends AbstractSyncSessionRepository {
	
	// MODEL VARIANBLES
	private HashMap sessions = new HashMap();
	
	// BUSINESS METHODS
	public InMemorySyncSessionRepository(IEndpointFactory endpointFactory, IMessageSyncAdapterFactory adapterFactory) {
		super(endpointFactory, adapterFactory);
	}

	public ISyncSession getSession(String sourceId, String targetId) {
		
		ISyncSession syncSession = null;
		for (int i = 0; i < this.sessions.values().length; i++) {
			syncSession = (ISyncSession)this.sessions.values()[i];
			
			if(syncSession.getSourceId().equals(sourceId)
				&& syncSession.getTarget().getEndpointId().equals(targetId)){
					return syncSession;
			}
		}
		return null;
	}

	public ISyncSession getSession(String sessionId) {
		return (ISyncSession)this.sessions.get(sessionId);
	}
	
	public Vector<ISyncSession> getAll() {
		Vector<ISyncSession> result = new Vector<ISyncSession>();
		ISyncSession syncSession = null;
		for (int i = 0; i < this.sessions.values().length; i++) {
			syncSession = (ISyncSession)this.sessions.values()[i];
			result.addElement(syncSession);
		}
		return result;		
	}

	protected void deleteSession(ISyncSession syncSession) {
		this.sessions.remove(syncSession.getSessionId());
	}

	public void deleteAll() {
		this.sessions.clear();		
	}
	
	public ISyncSession createSession(String sessionId, int version, String sourceId, IEndpoint target, boolean fullProtocol) {
		Guard.argumentNotNullOrEmptyString(sessionId, "sessionId");
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(target, "target");
		
		IMessageSyncAdapter syncAdapter = getSource(sourceId);
		if(syncAdapter == null){
			return null;
		}
		
		InMemorySyncSession session = new InMemorySyncSession(sessionId, version, syncAdapter, target, fullProtocol);
		
		this.save(session);
		return session;
	}

	public void save(ISyncSession syncSession) {
		this.sessions.put(syncSession.getSessionId(), syncSession);	
	}
}
