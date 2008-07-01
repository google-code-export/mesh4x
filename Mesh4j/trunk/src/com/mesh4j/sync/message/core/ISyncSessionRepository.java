package com.mesh4j.sync.message.core;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.ISyncSession;

public interface ISyncSessionRepository {

	ISyncSession createSession(String sessionID, String sourceId, IEndpoint endpoint, boolean fullProtocol);

	ISyncSession getSession(String sessionId);
	
	ISyncSession getSession(String sourceId, String endpointId);
	
	void flush(ISyncSession syncSession);

	void snapshot(ISyncSession syncSession);

	void cancel(ISyncSession syncSession);

}
